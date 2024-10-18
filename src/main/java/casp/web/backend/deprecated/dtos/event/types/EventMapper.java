package casp.web.backend.deprecated.dtos.event.types;

import casp.web.backend.deprecated.event.types.Event;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@Mapper
public interface EventMapper extends BaseEventMapper<Event, EventDto> {
    EventMapper EVENT_MAPPER = Mappers.getMapper(EventMapper.class);
}
