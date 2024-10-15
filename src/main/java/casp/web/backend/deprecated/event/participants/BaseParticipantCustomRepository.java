package casp.web.backend.deprecated.event.participants;


import casp.web.backend.common.enums.EntityStatus;

import java.util.Set;
import java.util.UUID;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface BaseParticipantCustomRepository {
    <P extends BaseParticipant> Set<P> findAllByMemberOrHandlerIdAndEntityStatus(UUID memberOrHandlerId, EntityStatus entityStatus, final String participantType);

    <P extends BaseParticipant> Set<P> findAllByMemberOrHandlerIdAndEntityStatusNot(UUID memberOrHandlerId, EntityStatus entityStatus, final String participantType);
}
