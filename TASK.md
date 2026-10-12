I can’t directly write the file from here because I don’t have a file-edit tool available in this session, but you can
create a new `TASK.md` at the project root with this content:

```markdown
# Task: Migrate MongoDB References from `@DBRef` to `@DocumentReference`

## Goal

Migrate the project from Spring Data MongoDB `@DBRef` references to `@DocumentReference`.

The migration will be executed **once**, verified, and then the temporary migration code will be removed.

---

## Background

Spring Data MongoDB `@DBRef` stores references in MongoDB like this:
```

javascript
member: {
"$ref": "member",
"$id": UUID ("...")
}

```
Spring Data MongoDB `@DocumentReference` stores references by default as the referenced document id directly:
```

javascript
member: UUID ("...")

```
Therefore, after migration, the old DBRef object should disappear and the field should contain only the referenced id.

This is expected and correct:
```

javascript
member: UUID ("...")

```
---

## Important Rule

Do **not** delete only `$id`.

Wrong:
```

javascript
member: {
"$ref": "member"
}

```
Wrong:
```

javascript
member: {
"$id": UUID ("...")
}

```
Correct:
```

javascript
member: UUID ("...")

```
The migration must replace the whole reference field with the value from `field.$id`.

Conceptually:
```

javascript
field = field.$id

```
---

## Migration Strategy

Use a temporary one-time migration runner.

After successful migration and verification:

1. Remove the migration runner.
2. Remove the migration property.
3. Keep the `@DocumentReference` changes.
4. Keep the query path changes.

Do **not** keep a runtime listener unless mixed old/new data must be supported temporarily.

For this task, the preferred approach is:

- no `AfterLoadEvent` listener;
- no permanent compatibility layer;
- one controlled migration run;
- cleanup afterwards.

---

## Required Code Changes

### 1. Replace `@DBRef` with `@DocumentReference`

All Mongo reference fields currently using `@DBRef` should be changed to `@DocumentReference`.

Example:
```

java
@DocumentReference
private MemberReference member;

```
Instead of:
```

java
@DBRef
private MemberReference member;

```
### 2. Update custom query paths

After migration, references are stored as scalar ids.

Therefore, old DBRef paths must be changed.

Before:
```

java
"member.$id"

```
After:
```

java
"member"

```
Before:
```

java
"participants.member.$id"

```
After:
```

java
"participants.member"

```
Before:
```

java
"participants.dogHasHandler.$id"

```
After:
```

java
"participants.dogHasHandler"

```
Before:
```

java
"coTrainers.member.$id"

```
After:
```

java
"coTrainers.member"

```
### 3. Review QueryDSL reference queries

Queries like this may need validation after migration:
```

java
DOG_HAS_HANDLER.member.id.eq (memberId)

```
Depending on Spring Data MongoDB and QueryDSL behavior, this may or may not generate the expected scalar-id query.

If needed, replace fragile QueryDSL reference-id filters with explicit MongoDB criteria such as:
```

java
Criteria.where ("member").is (memberId)

```
---

## Temporary Migration Runner

Create this class temporarily:
```

java
package casp.web.backend.configuration;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty (prefix = "casp.migration",
name = "dbref-to-document-reference-enabled",
havingValue = "true"
)
class DbRefToDocumentReferenceMigrationRunner implements ApplicationRunner {

    private final MongoTemplate mongoTemplate;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting one-time DBRef to DocumentReference migration");

        migrateSingleRefField("dogHasHandler", "member");
        migrateSingleRefField("dogHasHandler", "dog");

        migrateSingleRefField("course", "member");
        migrateSingleRefField("event", "member");
        migrateSingleRefField("exam", "member");

        migrateNestedArrayRefField("course", "coTrainers", "member");
        migrateNestedArrayRefField("course", "participants", "dogHasHandler");
        migrateNestedArrayRefField("event", "participants", "member");
        migrateNestedArrayRefField("exam", "participants", "dogHasHandler");

        log.info("Finished one-time DBRef to DocumentReference migration");
    }

    private void migrateSingleRefField(String collectionName, String fieldName) {
        var filter = new Document(fieldName + ".$id", new Document("$exists", true));
        var pipeline = List.of(
                new Document("$set", new Document(fieldName, "$" + fieldName + ".$id"))
        );

        UpdateResult result = mongoTemplate.getCollection(collectionName)
                .updateMany(filter, pipeline);

        logResult(collectionName, fieldName, result);
    }

    private void migrateNestedArrayRefField(String collectionName, String arrayFieldName, String refFieldName) {
        var filter = new Document(arrayFieldName + "." + refFieldName + ".$id", new Document("$exists", true));
        var pipeline = List.of(
                new Document("$set", new Document(arrayFieldName,
                        new Document("$map", new Document()
                                .append("input", "$" + arrayFieldName)
                                .append("as", "item")
                                .append("in", new Document("$mergeObjects", List.of(
                                        "$$item",
                                        new Document(refFieldName, "$$item." + refFieldName + ".$id")
                                )))
                        )
                ))
        );

        UpdateResult result = mongoTemplate.getCollection(collectionName)
                .updateMany(filter, pipeline);

        logResult(collectionName, arrayFieldName + "." + refFieldName, result);
    }

    private void logResult(String collectionName, String fieldName, UpdateResult result) {
        log.info(
                "DBRef migration result for {}.{}: matched={}, modified={}",
                collectionName,
                fieldName,
                result.getMatchedCount(),
                result.getModifiedCount()
        );
    }

}

```
---

## Temporary Migration Property

Enable the migration for one run only:
```

yaml
casp:
migration:
dbref-to-document-reference-enabled: true

```
After the migration is completed and verified, remove this property.

---

## Why Nested Array Migration Uses `$mergeObjects`

Some references are nested inside array elements.

Example before migration:
```

javascript
participants: [
{
participantType: "SPACE",
dogHasHandler: {
"$ref": "dogHasHandler",
"$id": UUID ("...")
},
paidPrice: 1.0
}
]

```
The migration must preserve the participant object and only replace the nested reference.

Correct result:
```

javascript
participants: [
{
participantType: "SPACE",
dogHasHandler: UUID ("..."),
paidPrice: 1.0
}
]

```
Do **not** map the whole array item to only the reference id, because that would destroy the other participant fields.

---

## Manual MongoDB Migration Examples

### Single Reference
```

javascript
db.course.updateMany ({ "member.$id": { $exists: true } },
[
{
$set: {
member: "$member.$id"
}
}
]
)

```
### Nested Reference in Array
```

javascript
db.course.updateMany ({ "participants.dogHasHandler.$id": { $exists: true } },
[
{
$set: {
participants: {
$map: {
input: "$participants",
as: "participant",
in: {
$mergeObjects: [
"$$participant",
{
dogHasHandler: "$$participant.dogHasHandler.$id"
}
]
}
}
}
}
}
]
)

```
---

## Pre-Migration Checklist

- [ ] Create a MongoDB backup.
- [ ] Identify all `@DBRef` usages.
- [ ] Replace `@DBRef` with `@DocumentReference`.
- [ ] Update all raw MongoDB paths from `.$id` to scalar id paths.
- [ ] Add the temporary migration runner.
- [ ] Add the temporary migration property.
- [ ] Build the application.
- [ ] Run integration tests.
- [ ] Plan a deployment window if production data is involved.

---

## Verification Queries

After the migration, all of these should return `0`.
```

javascript
db.dogHasHandler.countDocuments ({ "member.$id": { $exists: true } })
db.dogHasHandler.countDocuments ({ "dog.$id": { $exists: true } })

db.course.countDocuments ({ "member.$id": { $exists: true } })
db.course.countDocuments ({ "coTrainers.member.$id": { $exists: true } })
db.course.countDocuments ({ "participants.dogHasHandler.$id": { $exists: true } })

db.event.countDocuments ({ "member.$id": { $exists: true } })
db.event.countDocuments ({ "participants.member.$id": { $exists: true } })

db.exam.countDocuments ({ "member.$id": { $exists: true } })
db.exam.countDocuments ({ "participants.dogHasHandler.$id": { $exists: true } })

```
---

## Expected Data Shape After Migration

### `dogHasHandler`

Before:
```

javascript
{
member: {
"$ref": "member",
"$id": UUID ("...")
},
dog: {
"$ref": "dog",
"$id": UUID ("...")
}
}

```
After:
```

javascript
{
member: UUID ("..."),
dog: UUID ("...")
}

```
### `course`

Before:
```

javascript
{
member: {
"$ref": "member",
"$id": UUID ("...")
},
coTrainers: [
{
member: {
"$ref": "member",
"$id": UUID ("...")
}
}
],
participants: [
{
dogHasHandler: {
"$ref": "dogHasHandler",
"$id": UUID ("...")
}
}
]
}

```
After:
```

javascript
{
member: UUID ("..."),
coTrainers: [
{
member: UUID ("...")
}
],
participants: [
{
dogHasHandler: UUID ("...")
}
]
}

```
### `event`

Before:
```

javascript
{
member: {
"$ref": "member",
"$id": UUID ("...")
},
participants: [
{
member: {
"$ref": "member",
"$id": UUID ("...")
}
}
]
}

```
After:
```

javascript
{
member: UUID ("..."),
participants: [
{
member: UUID ("...")
}
]
}

```
### `exam`

Before:
```

javascript
{
member: {
"$ref": "member",
"$id": UUID ("...")
},
participants: [
{
dogHasHandler: {
"$ref": "dogHasHandler",
"$id": UUID ("...")
}
}
]
}

```
After:
```

javascript
{
member: UUID ("..."),
participants: [
{
dogHasHandler: UUID ("...")
}
]
}

```
---

## Functional Verification

After migration, verify that the application can still load referenced objects.

For example, a document stored as:
```

javascript
member: UUID ("...")

```
should still map to:
```

java
@DocumentReference
private MemberReference member;

```
Spring Data MongoDB should resolve the UUID to the referenced document.

Verify behavior that accesses nested referenced fields, such as:
```

java
dogHasHandler.getMember ().getEmail ()

```
and:
```

java
dogHasHandler.getDog ().getName ()

```
---

## Test Checklist

Run the full test suite:
```

bash
mvn test

```
Pay special attention to tests covering:

- member deletion/deactivation/activation;
- dog deletion;
- dog-handler queries by member id and dog id;
- event/course/exam participant queries;
- calendar aggregation;
- fee statistics aggregation;
- any QueryDSL repository methods involving references.

---

## Post-Migration Cleanup

After successful verification:

- [ ] Remove `DbRefToDocumentReferenceMigrationRunner`.
- [ ] Remove `casp.migration.dbref-to-document-reference-enabled`.
- [ ] Confirm no `@DBRef` imports remain.
- [ ] Confirm no `".$id"` reference paths remain in custom queries.
- [ ] Commit only the final clean code.
- [ ] Keep the database backup until production validation is complete.

---

## Final Expected State

The project should use:
```

java
@DocumentReference
private MemberReference member;

```
MongoDB should store:
```

javascript
member: UUID ("...")

```
MongoDB should no longer store:
```

javascript
member: {
"$ref": "member",
"$id": UUID ("...")
}

```
This is the correct final state for default Spring Data MongoDB `@DocumentReference`.
```
