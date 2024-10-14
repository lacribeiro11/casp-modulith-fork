package casp.web.backend.presentation.layer.dog;

import casp.web.backend.business.logic.layer.dog.DogHasHandlerDto;
import casp.web.backend.common.base.BaseViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DogHasHandlerWriteMapper extends BaseViewMapper<DogHasHandlerDto, DogHasHandlerWrite> {
    DogHasHandlerWriteMapper WRITE_MAPPER = Mappers.getMapper(DogHasHandlerWriteMapper.class);
}
