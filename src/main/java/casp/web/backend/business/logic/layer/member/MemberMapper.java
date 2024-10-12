package casp.web.backend.business.logic.layer.member;

import casp.web.backend.common.BaseMapper;
import casp.web.backend.common.DogHasHandlerReference;
import casp.web.backend.data.access.layer.member.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;

// FIXME Remove public
@Mapper
public interface MemberMapper extends BaseMapper<Member, MemberDto> {
    MemberMapper MEMBER_MAPPER = Mappers.getMapper(MemberMapper.class);

    @Mapping(target = "dogId", source = "dogHasHandler.dog.id")
    @Mapping(target = "dogName", source = "dogHasHandler.dog.name")
    DogHasHandlerDto toDogHasHandlerDto(DogHasHandlerReference dogHasHandler);

    Set<DogHasHandlerDto> toDogHasHandlerDtoSet(Set<DogHasHandlerReference> dogHasHandlerSet);
}
