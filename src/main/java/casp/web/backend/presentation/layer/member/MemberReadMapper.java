package casp.web.backend.presentation.layer.member;

import casp.web.backend.business.logic.layer.member.MemberDto;
import casp.web.backend.common.BaseViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface MemberReadMapper extends BaseViewMapper<MemberDto, MemberRead> {
    MemberReadMapper READ_MAPPER = Mappers.getMapper(MemberReadMapper.class);
}
