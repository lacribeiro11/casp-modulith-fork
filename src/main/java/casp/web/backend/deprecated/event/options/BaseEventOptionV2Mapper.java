package casp.web.backend.deprecated.event.options;

import casp.web.backend.data.access.layer.event.options.DailyEventOption;
import casp.web.backend.data.access.layer.event.options.WeeklyEventOption;
import casp.web.backend.data.access.layer.event.options.WeeklyEventOptionRecurrence;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@Mapper
public interface BaseEventOptionV2Mapper {
    BaseEventOptionV2Mapper BASE_EVENT_OPTION_V2_MAPPER = Mappers.getMapper(BaseEventOptionV2Mapper.class);

    WeeklyEventOptionRecurrence toWeeklyEventOptionRecurrence(casp.web.backend.deprecated.event.options.WeeklyEventOptionRecurrence weeklyEventOptionRecurrence);

    List<WeeklyEventOptionRecurrence> toWeeklyEventOptionRecurrenceList(List<casp.web.backend.deprecated.event.options.WeeklyEventOptionRecurrence> weeklyEventOptionRecurrenceList);

    @Mapping(target = "optionType", ignore = true)
    WeeklyEventOption toWeeklyEventOption(casp.web.backend.deprecated.event.options.WeeklyEventOption weeklyEventOption);

    @Mapping(target = "optionType", ignore = true)
    DailyEventOption toDailyEventOption(casp.web.backend.deprecated.event.options.DailyEventOption dailyEventOption);
}
