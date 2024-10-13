package casp.web.backend.business.logic.layer.member;


import casp.web.backend.business.logic.layer.dog.DogHasHandlerService;
import casp.web.backend.business.logic.layer.event.participants.BaseParticipantObserver;
import casp.web.backend.business.logic.layer.event.types.BaseEventObserver;
import casp.web.backend.common.DogHasHandlerReferenceRepository;
import casp.web.backend.common.EntityStatus;
import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.deprecated.event.types.BaseEvent;
import casp.web.backend.deprecated.member.CardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.business.logic.layer.member.MemberMapper.MEMBER_MAPPER;
import static casp.web.backend.deprecated.member.MemberV2Mapper.MEMBER_V2_MAPPER;

@Service
class MemberServiceImpl implements MemberService {
    private static final Logger LOG = LoggerFactory.getLogger(MemberServiceImpl.class);
    private static final String EMAIL_FORMAT_IF_DELETED = "%s---%s";

    private final MemberRepository memberRepository;
    private final DogHasHandlerService dogHasHandlerService;
    private final BaseParticipantObserver baseParticipantObserver;
    private final BaseEventObserver baseEventObserver;
    private final CardRepository cardRepository;
    private final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;

    @Autowired
    MemberServiceImpl(final MemberRepository memberRepository,
                      final DogHasHandlerService dogHasHandlerService,
                      final BaseParticipantObserver baseParticipantObserver,
                      final BaseEventObserver baseEventObserver,
                      final CardRepository cardRepository,
                      final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository) {
        this.memberRepository = memberRepository;
        this.dogHasHandlerService = dogHasHandlerService;
        this.baseParticipantObserver = baseParticipantObserver;
        this.baseEventObserver = baseEventObserver;
        this.cardRepository = cardRepository;
        this.dogHasHandlerReferenceRepository = dogHasHandlerReferenceRepository;
    }

    @Override
    public Page<Member> getMembersByFirstNameAndLastName(final String firstName, final String lastName, final Pageable pageable) {
        return memberRepository.findAllByFirstNameAndLastName(firstName, lastName, pageable);
    }

    @Override
    public Page<Member> getMembersByEntityStatus(final EntityStatus entityStatus, final Pageable pageable) {
        return memberRepository.findAllByEntityStatus(entityStatus, pageable);
    }

    @Override
    public MemberDto getMemberById(final UUID id) {
        var memberDto = MEMBER_MAPPER.toTarget(memberRepository.findByIdAndEntityStatusCustom(id, EntityStatus.ACTIVE));
        var dogHasHandlerSet = dogHasHandlerReferenceRepository.findAllByMemberId(id);
        memberDto.setDogHasHandlerSet(MEMBER_MAPPER.toDogHasHandlerDtoSet(dogHasHandlerSet));
        return memberDto;
    }

    @Override
    public MemberDto saveMember(final Member member) {
        memberRepository.findMemberByEmail(member.getEmail())
                .ifPresent(m -> {
                    if (!member.equals(m)) {
                        var msg = "Member with email %s already exists.".formatted(m.getEmail());
                        LOG.error(msg);
                        throw new IllegalStateException(msg);
                    }
                });
        memberRepository.save(member);
        return getMemberById(member.getId());
    }

    @Override
    public void deleteMemberById(final UUID id) {
        var member = memberRepository.findByIdAndEntityStatusCustom(id, EntityStatus.ACTIVE);
        dogHasHandlerService.deleteDogHasHandlersByMemberId(id);
        baseParticipantObserver.deleteParticipantsByMemberOrHandlerId(id);
        baseEventObserver.deleteBaseEventsByMemberId(id);
        member.setEmail(EMAIL_FORMAT_IF_DELETED.formatted(member.getEmail(), id));
        member.setEntityStatus(EntityStatus.DELETED);
        memberRepository.save(member);
    }

    @Override
    public Member deactivateMember(final UUID id) {
        var member = memberRepository.findByIdAndEntityStatusCustom(id, EntityStatus.ACTIVE);
        dogHasHandlerService.deactivateDogHasHandlersByMemberId(id);
        baseParticipantObserver.deactivateParticipantsByMemberOrHandlerId(id);
        baseEventObserver.deactivateBaseEventsByMemberId(id);
        member.setEntityStatus(EntityStatus.INACTIVE);
        return memberRepository.save(member);
    }

    @Override
    public MemberDto activateMember(final UUID id) {
        var member = memberRepository.findByIdAndEntityStatusCustom(id, EntityStatus.INACTIVE);
        dogHasHandlerService.activateDogHasHandlersByMemberId(id);
        baseParticipantObserver.activateParticipantsByMemberOrHandlerId(id);
        baseEventObserver.activateBaseEventsByMemberId(id);
        member.setEntityStatus(EntityStatus.ACTIVE);
        memberRepository.save(member);
        return getMemberById(id);
    }

    @Override
    public Page<Member> getMembersByName(final String name, final Pageable pageable) {
        return memberRepository.findAllByValue(name, pageable);
    }

    @Override
    public Set<String> getMembersEmailByIds(final Set<UUID> membersId) {
        return memberRepository.findAllByIdInAndEntityStatus(membersId, EntityStatus.ACTIVE)
                .stream()
                .map(Member::getEmail)
                .collect(Collectors.toSet());
    }

    @Override
    public void setActiveMemberToBaseEvent(final BaseEvent baseEvent) {
        if (baseEvent.getMember() == null) {
            memberRepository.findByIdAndEntityStatus(baseEvent.getMemberId(), EntityStatus.ACTIVE)
                    .ifPresentOrElse(baseEvent::setMember,
                            () -> LOG.warn("No active member found with id: {}", baseEvent.getMemberId()));
        }
    }

    @Override
    public void migrateDataToV2() {
        memberRepository.findAll().forEach(mv1 -> {
            var cardV1Set = cardRepository.findAllByMemberId(mv1.getId());
            mv1.setCards(MEMBER_V2_MAPPER.toCardV2Set(cardV1Set));
            memberRepository.save(mv1);
        });
    }
}
