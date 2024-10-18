package casp.web.backend.deprecated.dtos.event.participants;

import casp.web.backend.common.base.BaseDocument;
import org.mapstruct.Mapping;

import java.util.Set;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface BaseParticipantMapper<E extends BaseDocument, D extends BaseParticipantDto> {
    D toDto(E document);

    @Mapping(target = "id", ignore = true)
    E toDocument(D dto);

    Set<D> toDtoSet(Set<E> documentSet);

    Set<E> toDocumentSet(Set<D> dtoSet);
}
