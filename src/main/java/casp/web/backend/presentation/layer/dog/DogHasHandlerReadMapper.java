package casp.web.backend.presentation.layer.dog;

import casp.web.backend.business.logic.layer.dog.DogHasHandlerDto;
import casp.web.backend.common.base.BaseViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DogHasHandlerReadMapper extends BaseViewMapper<DogHasHandlerDto, DogHasHandlerRead> {
    DogHasHandlerReadMapper READ_MAPPER = Mappers.getMapper(DogHasHandlerReadMapper.class);
}
