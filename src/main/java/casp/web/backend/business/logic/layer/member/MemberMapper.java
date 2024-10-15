package casp.web.backend.business.logic.layer.member;

import casp.web.backend.common.BaseDtoMapper;
import casp.web.backend.common.member.DogHasHandler;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.data.access.layer.member.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;

// FIXME Remove public
@Mapper
public interface MemberMapper extends BaseDtoMapper<Member, MemberDto> {
    MemberMapper MEMBER_MAPPER = Mappers.getMapper(MemberMapper.class);

    @Mapping(target = "entityStatus", ignore = true)
    @Override
    Member toSource(MemberDto source);

    @Mapping(target = "dogId", source = "dogHasHandler.dog.id")
    @Mapping(target = "dogName", source = "dogHasHandler.dog.name")
    DogHasHandler toDogHasHandlerDto(DogHasHandlerReference dogHasHandler);

    Set<DogHasHandler> toDogHasHandlerDtoSet(Set<DogHasHandlerReference> dogHasHandlerSet);
}
