package casp.web.backend.presentation.layer.dog;

import casp.web.backend.business.logic.layer.dog.DogDto;
import casp.web.backend.common.base.BaseViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DogWriteMapper extends BaseViewMapper<DogDto, DogWrite> {
    DogWriteMapper WRITE_MAPPER = Mappers.getMapper(DogWriteMapper.class);
}
