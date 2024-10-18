package casp.web.backend.deprecated.dtos.event.types;

import casp.web.backend.common.base.BaseMapper;
import casp.web.backend.deprecated.event.types.BaseEvent;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

import java.util.Optional;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface BaseEventMapper<E extends BaseEvent, D extends BaseEventDto<?>> extends BaseMapper<E, D> {
    @Override
    D toTarget(E document);

    @AfterMapping
    default void afterToDto(@MappingTarget D dto, E document) {
        Optional.ofNullable(document.getDailyOption()).ifPresent(dto::setOption);
        Optional.ofNullable(document.getWeeklyOption()).ifPresent(dto::setOption);
    }
}
