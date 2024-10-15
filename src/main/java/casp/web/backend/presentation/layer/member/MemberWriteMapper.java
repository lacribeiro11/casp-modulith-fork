package casp.web.backend.presentation.layer.member;


import casp.web.backend.business.logic.layer.member.MemberDto;
import casp.web.backend.common.base.BaseViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface MemberWriteMapper extends BaseViewMapper<MemberDto, MemberWrite> {
    MemberWriteMapper WRITE_MAPPER = Mappers.getMapper(MemberWriteMapper.class);
}
