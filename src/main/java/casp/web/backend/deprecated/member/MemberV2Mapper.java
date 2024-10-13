package casp.web.backend.deprecated.member;

import casp.web.backend.common.member.Card;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@Mapper
public interface MemberV2Mapper {
    MemberV2Mapper MEMBER_V2_MAPPER = Mappers.getMapper(MemberV2Mapper.class);

    Card toCardV2(casp.web.backend.deprecated.member.Card card);

    Set<Card> toCardV2Set(Set<casp.web.backend.deprecated.member.Card> cardSet);
}
