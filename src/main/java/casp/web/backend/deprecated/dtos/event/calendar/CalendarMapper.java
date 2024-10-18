package casp.web.backend.deprecated.dtos.event.calendar;

import casp.web.backend.deprecated.event.calendar.Calendar;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@Mapper
public interface CalendarMapper {
    CalendarMapper CALENDAR_MAPPER = Mappers.getMapper(CalendarMapper.class);

    Calendar toDocument(CalendarDto dto);

    CalendarDto toDto(Calendar document);

    List<CalendarDto> toDtoList(List<Calendar> documents);

    List<Calendar> toDocumentList(List<CalendarDto> dtoList);
}
