package casp.web.backend.deprecated.dtos.dog;

import casp.web.backend.common.base.BaseMapper;
import casp.web.backend.deprecated.dog.DogHasHandler;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@Mapper
public interface DogHasHandlerMapper extends BaseMapper<DogHasHandler, DogHasHandlerDto> {
    DogHasHandlerMapper DOG_HAS_HANDLER_MAPPER = Mappers.getMapper(DogHasHandlerMapper.class);

    @Mapping(target = "member", ignore = true)
    @Mapping(target = "dog", ignore = true)
    @Override
    DogHasHandler toSource(DogHasHandlerDto dto);
}
