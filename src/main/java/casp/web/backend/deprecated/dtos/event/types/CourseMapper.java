package casp.web.backend.deprecated.dtos.event.types;

import casp.web.backend.deprecated.event.types.Course;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@Mapper
public interface CourseMapper extends BaseEventMapper<Course, CourseDto> {
    CourseMapper COURSE_MAPPER = Mappers.getMapper(CourseMapper.class);
}
