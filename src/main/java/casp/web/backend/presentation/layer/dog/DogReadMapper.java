package casp.web.backend.presentation.layer.dog;

import casp.web.backend.business.logic.layer.dog.DogDto;
import casp.web.backend.common.BaseViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DogReadMapper extends BaseViewMapper<DogDto, DogRead> {
    DogReadMapper READ_MAPPER = Mappers.getMapper(DogReadMapper.class);
}
