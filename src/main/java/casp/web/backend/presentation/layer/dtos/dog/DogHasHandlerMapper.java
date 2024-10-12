package casp.web.backend.presentation.layer.dtos.dog;

import casp.web.backend.common.BaseMapper;
import casp.web.backend.deprecated.dog.DogHasHandler;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DogHasHandlerMapper extends BaseMapper<DogHasHandler, DogHasHandlerDto> {
    DogHasHandlerMapper DOG_HAS_HANDLER_MAPPER = Mappers.getMapper(DogHasHandlerMapper.class);

    @Mapping(target = "member", ignore = true)
    @Mapping(target = "dog", ignore = true)
    @Override
    DogHasHandler toDocument(DogHasHandlerDto dto);
}
