package casp.web.backend.presentation.layer.dtos.dog;

import casp.web.backend.common.BaseMapper;
import casp.web.backend.data.access.layer.dog.Dog;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DogMapper extends BaseMapper<Dog, DogDto> {
    DogMapper DOG_MAPPER = Mappers.getMapper(DogMapper.class);
}
