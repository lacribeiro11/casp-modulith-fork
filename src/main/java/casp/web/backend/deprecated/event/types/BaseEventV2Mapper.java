package casp.web.backend.deprecated.event.types;

import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.data.access.layer.event.types.Exam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@Mapper
public interface BaseEventV2Mapper {
    BaseEventV2Mapper BASE_EVENT_V2_MAPPER = Mappers.getMapper(BaseEventV2Mapper.class);

    @Mapping(target = "eventType", ignore = true)
    @Mapping(target = "version", ignore = true)
    Course toCourse(casp.web.backend.deprecated.event.types.Course course);

    @Mapping(target = "eventType", ignore = true)
    @Mapping(target = "version", ignore = true)
    Event toEvent(casp.web.backend.deprecated.event.types.Event event);

    @Mapping(target = "eventType", ignore = true)
    @Mapping(target = "version", ignore = true)
    Exam toExam(casp.web.backend.deprecated.event.types.Exam exam);
}
