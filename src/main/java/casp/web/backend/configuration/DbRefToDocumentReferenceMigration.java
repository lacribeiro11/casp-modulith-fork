package casp.web.backend.configuration;

import com.mongodb.DBRef;
import com.mongodb.client.model.Filters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * One-time migration: converts stored DBRefs ({$ref, $id}) into plain ids, as expected by @DocumentReference.
 * Remove this class (and its property) after it has run in production.
 *
 * @deprecated Temporary migration code. Delete this class and the
 * {@code casp.migration.dbref-to-document-reference.enabled} property from {@code application.yml}
 * once the migration has run in production.
 */
@Deprecated(since = "0.0.14", forRemoval = true)
@Slf4j
@Component
@ConditionalOnProperty(name = "casp.migration.dbref-to-document-reference.enabled", havingValue = "true")
@RequiredArgsConstructor
class DbRefToDocumentReferenceMigration implements InitializingBean {
    private static final List<String> COLLECTIONS = List.of("dogHasHandler", "course", "event", "exam");
    private static final String REF = "$ref";
    private static final String ID = "$id";

    private final MongoOperations mongoOperations;

    @Override
    public void afterPropertiesSet() {
        log.info("Starting DBRef -> DocumentReference migration");
        COLLECTIONS.forEach(this::migrateCollection);
        log.info("Finished DBRef -> DocumentReference migration");
    }

    /**
     * Migrates all documents of a single collection from the {@code @DBRef} storage format to the
     * {@code @DocumentReference} storage format.
     * <p>
     * Every document is read through the raw driver collection, so no entity mapping is involved.
     * Each top-level field goes through {@link #convert(Object)}. Only fields that actually contained a DBRef
     * (at any nesting depth) are written back, using a single {@code $set} update matched by {@code _id}.
     * Fields without DBRefs are never touched, and documents without any DBRef produce no write.
     * <p>
     * The migration can safely be run more than once. Once all DBRefs have been converted, another run finds
     * nothing to change and performs no writes.
     * <p>
     * Ids are {@link java.util.UUID}s. They are read and written with the same UUID representation configured on
     * the {@code MongoClient}, so migrated references keep matching the {@code _id}s of the referenced documents.
     *
     * @param collectionName name of the MongoDB collection to migrate
     */
    private void migrateCollection(String collectionName) {
        var collection = mongoOperations.getCollection(collectionName);
        var scanned = 0;
        var migrated = 0;

        try (var cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                var document = cursor.next();
                scanned++;

                var updates = new Document();
                for (var entry : document.entrySet()) {
                    var original = entry.getValue();
                    var converted = convert(original);
                    if (converted != original) {
                        updates.append(entry.getKey(), converted);
                    }
                }

                if (!updates.isEmpty()) {
                    collection.updateOne(Filters.eq("_id", document.get("_id")), new Document("$set", updates));
                    migrated++;
                }
            }
        }
        log.info("Collection '{}': scanned {}, migrated {}", collectionName, scanned, migrated);
    }

    /**
     * Recursively replaces every DBRef inside the given BSON value with its plain id.
     * <p>
     * Supported cases:
     * <ul>
     *     <li>{@link DBRef}: replaced by {@link DBRef#getId()}.</li>
     *     <li>{@link Document} containing both {@code $ref} and {@code $id}: treated as a raw DBRef and replaced by
     *     the value of {@code $id}. {@code $ref} and an optional {@code $db} are dropped, because
     *     {@code @DocumentReference} gets the target collection from the entity mapping.</li>
     *     <li>Any other {@link Document}: each field value is converted recursively. Field order is kept.</li>
     *     <li>{@link List}: each element is converted recursively. Element order is kept.</li>
     *     <li>Any other value (e.g. {@link String}, {@link Number}, {@link java.util.UUID}, dates, {@code null}):
     *     returned unchanged.</li>
     * </ul>
     * <p>
     * <b>Identity rule:</b> if the value contains no DBRef, the <em>same instance</em> is returned. If it does,
     * a converted copy is returned and the original is left untouched. Callers can therefore find out whether
     * anything changed with a cheap reference comparison ({@code converted != original}) instead of
     * {@code equals}.
     *
     * @param value any BSON value as decoded by the driver; may be {@code null}
     * @return the same instance if nothing was converted, otherwise a converted copy (or the extracted id)
     */
    private static Object convert(Object value) {
        if (value instanceof DBRef dbRef) {
            return dbRef.getId();
        }
        if (value instanceof Document document) {
            if (document.containsKey(REF) && document.containsKey(ID)) {
                return document.get(ID);
            }
            var changed = false;
            var result = new Document();
            for (var entry : document.entrySet()) {
                var converted = convert(entry.getValue());
                changed |= converted != entry.getValue();
                result.append(entry.getKey(), converted);
            }
            return changed ? result : document;
        }
        if (value instanceof List<?> list) {
            var changed = false;
            var result = new ArrayList<>(list.size());
            for (var item : list) {
                var converted = convert(item);
                changed |= converted != item;
                result.add(converted);
            }
            return changed ? result : list;
        }
        return value;
    }
}
