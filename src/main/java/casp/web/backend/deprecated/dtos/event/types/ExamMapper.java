package casp.web.backend.deprecated.dtos.event.types;

import casp.web.backend.deprecated.event.types.Exam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@Mapper
public interface ExamMapper extends BaseEventMapper<Exam, ExamDto> {
    ExamMapper EXAM_MAPPER = Mappers.getMapper(ExamMapper.class);
}
