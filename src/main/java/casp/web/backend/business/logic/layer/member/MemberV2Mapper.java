package casp.web.backend.business.logic.layer.member;

import casp.web.backend.datav2.member.Card;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

// TODO #16 Delete after the data migration
@Mapper
public interface MemberV2Mapper {
    MemberV2Mapper MEMBER_V2_MAPPER = Mappers.getMapper(MemberV2Mapper.class);

    Card toCardV2(casp.web.backend.data.access.layer.member.Card card);

    Set<Card> toCardV2Set(Set<casp.web.backend.data.access.layer.member.Card> cardSet);
}
