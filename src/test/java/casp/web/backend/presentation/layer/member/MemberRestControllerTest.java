package casp.web.backend.presentation.layer.member;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.member.MemberDto;
import casp.web.backend.business.logic.layer.member.MemberService;
import casp.web.backend.common.EntityStatus;
import casp.web.backend.common.Role;
import casp.web.backend.data.access.layer.dog.DogRepository;
import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.deprecated.dog.DogHasHandlerOldRepository;
import casp.web.backend.deprecated.event.participants.BaseParticipantRepository;
import casp.web.backend.deprecated.event.types.BaseEventRepository;
import casp.web.backend.presentation.layer.MvcMapper;
import casp.web.backend.presentation.layer.RestResponsePage;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static casp.web.backend.business.logic.layer.member.MemberMapper.MEMBER_MAPPER;
import static casp.web.backend.presentation.layer.member.MemberReadMapper.READ_MAPPER;
import static casp.web.backend.presentation.layer.member.MemberWriteMapper.WRITE_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberRestControllerTest {
    private static final String MEMBER_URL_PREFIX = "/member";
    private static final TypeReference<RestResponsePage<MemberRead>> PAGE_TYPE_REFERENCE = new TypeReference<>() {
    };
    private static final String MEMBER_NOT_FOUND_MESSAGE = "Member with id %s not found or it isn't %s.";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private DogHasHandlerOldRepository dogHasHandlerOldRepository;
    @Autowired
    private BaseParticipantRepository baseParticipantRepository;
    @Autowired
    private BaseEventRepository baseEventRepository;
    @Autowired
    private DogRepository dogRepository;

    @SpyBean
    private MemberService memberService;

    private MemberDto john;
    private MemberDto zephyr;
    private Member inactive;
    private Set<MemberRead> expectedActiveMembers;

    @BeforeEach
    void setUp() {
        baseParticipantRepository.deleteAll();
        baseEventRepository.deleteAll();
        dogHasHandlerOldRepository.deleteAll();
        memberRepository.deleteAll();
        dogRepository.deleteAll();

        var johnDocument = memberRepository.save(TestFixture.createMember());
        john = MEMBER_MAPPER.toTarget(johnDocument);
        zephyr = MEMBER_MAPPER.toTarget(memberRepository.save(TestFixture.createMember("Zephyr", "Starling")));
        inactive = TestFixture.createMember("INACTIVE", "INACTIVE");
        inactive.setEntityStatus(EntityStatus.INACTIVE);
        memberRepository.save(inactive);
        var bonsaiDocument = TestFixture.createDog();
        dogRepository.save(bonsaiDocument);
        var eventParticipant = TestFixture.createEventParticipant();
        eventParticipant.setMemberOrHandlerId(johnDocument.getId());
        var event = eventParticipant.getBaseEvent();
        event.setMember(johnDocument);
        event.setMemberId(johnDocument.getId());
        var coTrainer = TestFixture.createCoTrainer();
        coTrainer.setMemberOrHandlerId(johnDocument.getId());
        var course = coTrainer.getBaseEvent();
        course.setMember(johnDocument);
        course.setMemberId(johnDocument.getId());
        baseEventRepository.saveAll(Set.of(event, course));
        baseParticipantRepository.saveAll(Set.of(eventParticipant, coTrainer));
        expectedActiveMembers = READ_MAPPER.toTargetSet(Set.of(john, zephyr));
    }

    @Test
    void searchMembersByFirstNameOrLastName() throws Exception {
        var mvcResult = mockMvc.perform(get(MEMBER_URL_PREFIX + "/search-members-by-name")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        var memberDtoPage = MvcMapper.toObject(mvcResult, PAGE_TYPE_REFERENCE);
        assertThat(memberDtoPage).containsExactlyInAnyOrderElementsOf(expectedActiveMembers);
    }

    @Test
    void getMemberRoles() throws Exception {
        TypeReference<List<Role>> typeReference = new TypeReference<>() {
        };
        var mvcResult = mockMvc.perform(get(MEMBER_URL_PREFIX + "/roles"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, typeReference)).containsExactlyElementsOf(Role.getAllRolesSorted());
    }

    @Test
    void getMembersEmailByIds() throws Exception {
        TypeReference<List<String>> typeReference = new TypeReference<>() {
        };
        var mvcResult = mockMvc.perform(get(MEMBER_URL_PREFIX + "/emails-by-ids")
                        .param("membersId", john.getId().toString(), zephyr.getId().toString(), inactive.getId().toString()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, typeReference)).containsExactlyInAnyOrder(john.getEmail(), zephyr.getEmail());
    }

    @Test
    void migrateDataToV2() throws Exception {
        mockMvc.perform(post(MEMBER_URL_PREFIX + "/migrate-data"))
                .andExpect(status().isNoContent());

        verify(memberService).migrateDataToV2();
    }

    private ResultActions performPost(final MemberWrite member) throws Exception {
        return mockMvc.perform(post(MEMBER_URL_PREFIX)
                .content(MvcMapper.toString(member))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions getMemberById(final UUID id) throws Exception {
        return mockMvc.perform(get(MEMBER_URL_PREFIX + "/{id}", id));
    }

    private ResultActions deactivateMember(final UUID memberId) throws Exception {
        return mockMvc.perform(post(MEMBER_URL_PREFIX + "/{id}/deactivate", memberId));
    }

    @Nested
    class GetMembers {
        @Test
        void entityStatusIsValid() throws Exception {
            var mvcResult = getMemberPage(EntityStatus.ACTIVE)
                    .andExpect(status().isOk())
                    .andReturn();

            var memberDtoPage = MvcMapper.toObject(mvcResult, PAGE_TYPE_REFERENCE);
            assertThat(memberDtoPage).containsExactlyInAnyOrderElementsOf(expectedActiveMembers);
        }

        @Test
        void entityStatusIsInvalid() throws Exception {
            var exception = getMemberPage(EntityStatus.DELETED)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .satisfies(e -> assertThat(e.getMessage()).contains("Failed to convert value", "DELETE"));

        }

        private ResultActions getMemberPage(final EntityStatus entityStatus) throws Exception {
            return mockMvc.perform(get(MEMBER_URL_PREFIX)
                    .param("entityStatusParam", entityStatus.name())
                    .param("page", "0")
                    .param("size", "10"));
        }
    }

    @Nested
    class GetMembersByFirstNameAndLastName {
        private static final String URL = MEMBER_URL_PREFIX + "/search-members-by-firstname-and-lastname";

        @Test
        void validRequest() throws Exception {
            var mvcResult = performGet(john.getFirstName(), john.getLastName())
                    .andExpect(status().isOk())
                    .andReturn();

            var membersPage = MvcMapper.toObject(mvcResult, PAGE_TYPE_REFERENCE);
            assertThat(membersPage).containsExactly(READ_MAPPER.toTarget(john));
        }

        @Test
        void requestWithoutParameters() throws Exception {
            var exception = mockMvc.perform(get(URL))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .satisfies(e -> assertThat(e.getMessage()).contains("Required request parameter"));
        }

        @Test
        void requestWithBadParameters() throws Exception {
            var exception = performGet(" ", " ")
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .satisfies(e -> assertThat(e.getMessage()).contains("firstName: must not be blank", "lastName: must not be blank"));
        }

        private ResultActions performGet(final String firstName, final String lastName) throws Exception {
            return mockMvc.perform(get(URL)
                    .param("firstName", firstName)
                    .param("lastName", lastName));
        }
    }

    @Nested
    class ActivateMember {
        @Test
        void cascadeActivateMember() throws Exception {
            deactivateMember(john.getId())
                    .andExpect(status().isOk());

            var mvcResult = activateMember(john.getId())
                    .andExpect(status().isOk())
                    .andReturn();

            assertThat(MvcMapper.toObject(mvcResult, MemberRead.class))
                    .usingRecursiveAssertion()
                    .isEqualTo(READ_MAPPER.toTarget(john));
            assertThat(dogHasHandlerOldRepository.findAll()).allSatisfy(dh -> assertSame(EntityStatus.ACTIVE, dh.getEntityStatus()));
            assertThat(baseParticipantRepository.findAll()).allSatisfy(p -> assertSame(EntityStatus.ACTIVE, p.getEntityStatus()));
            assertThat(baseEventRepository.findAll()).allSatisfy(e -> assertSame(EntityStatus.ACTIVE, e.getEntityStatus()));
        }

        @Test
        void memberNotFound() throws Exception {
            activateMember(john.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value(MEMBER_NOT_FOUND_MESSAGE.formatted(john.getId(), EntityStatus.INACTIVE)));

            verify(memberService).activateMember(john.getId());
        }

        private ResultActions activateMember(final UUID memberId) throws Exception {
            return mockMvc.perform(post(MEMBER_URL_PREFIX + "/{id}/activate", memberId));
        }
    }

    @Nested
    class DeactivateMethod {
        @Test
        void cascadeDeactivate() throws Exception {
            var mvcResult = deactivateMember(john.getId())
                    .andExpect(status().isOk())
                    .andReturn();

            assertThat(MvcMapper.toObject(mvcResult, MemberRead.class)).satisfies(dto -> {
                assertSame(EntityStatus.INACTIVE, dto.getEntityStatus());
            });
            assertThat(dogHasHandlerOldRepository.findAll()).allSatisfy(dh -> assertSame(EntityStatus.INACTIVE, dh.getEntityStatus()));
            assertThat(baseParticipantRepository.findAll()).allSatisfy(p -> assertSame(EntityStatus.INACTIVE, p.getEntityStatus()));
            assertThat(baseEventRepository.findAll()).allSatisfy(e -> assertSame(EntityStatus.INACTIVE, e.getEntityStatus()));
        }

        @Test
        void memberNotFound() throws Exception {
            deactivateMember(inactive.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value(MEMBER_NOT_FOUND_MESSAGE.formatted(inactive.getId(), EntityStatus.ACTIVE)));

            verify(memberService).deactivateMember(inactive.getId());
        }
    }

    @Nested
    class DeleteMember {
        @Test
        void cascadeDelete() throws Exception {
            deleteMember(john.getId())
                    .andExpect(status().isNoContent());

            getMemberById(john.getId()).andExpect(status().isBadRequest());
            assertThat(dogHasHandlerOldRepository.findAll()).allSatisfy(dh -> assertSame(EntityStatus.DELETED, dh.getEntityStatus()));
            assertThat(baseParticipantRepository.findAll()).allSatisfy(p -> assertSame(EntityStatus.DELETED, p.getEntityStatus()));
            assertThat(baseEventRepository.findAll()).allSatisfy(e -> assertSame(EntityStatus.DELETED, e.getEntityStatus()));
        }

        @Test
        void memberNotFound() throws Exception {
            deleteMember(inactive.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value(MEMBER_NOT_FOUND_MESSAGE.formatted(inactive.getId(), EntityStatus.ACTIVE)));

            verify(memberService).deleteMemberById(inactive.getId());
        }

        private ResultActions deleteMember(final UUID memberId) throws Exception {
            return mockMvc.perform(delete(MEMBER_URL_PREFIX + "/{id}", memberId));
        }
    }

    @Nested
    class SaveMember {
        @Captor
        private ArgumentCaptor<MemberDto> memberCaptor;

        @Test
        void memberIsAlwaysAsActiveSaved() throws Exception {
            john.setEntityStatus(EntityStatus.DELETED);
            var mvcResult = performPost(WRITE_MAPPER.toTarget(john))
                    .andExpect(status().isOk())
                    .andReturn();

            assertThat(MvcMapper.toObject(mvcResult, MemberRead.class))
                    .usingRecursiveAssertion()
                    .isEqualTo(READ_MAPPER.toTarget(john));
        }

        @Test
        void newMemberWithExistingEMail() throws Exception {
            var member = MEMBER_MAPPER.toTarget(TestFixture.createMember());
            member.setEmail(john.getEmail());

            performPost(WRITE_MAPPER.toTarget(member))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Member with email %s already exists." .formatted(john.getEmail())));

            verify(memberService).saveMember(memberCaptor.capture());
            assertEquals(member.getId(), memberCaptor.getValue().getId());
        }

        @Test
        void newMemberIsInvalid() throws Exception {
            var exception = performPost(new MemberWrite())
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .satisfies(e -> assertThat(e.getMessage())
                            .contains("NotBlank.lastName", "NotBlank.firstName", "NotNull.email"));
        }

        @Test
        void saveNewMember() throws Exception {
            var memberDto = MEMBER_MAPPER.toTarget(TestFixture.createMember());
            var memberWrite = WRITE_MAPPER.toTarget(memberDto);
            memberWrite.setId(null);
            memberWrite.setVersion(null);

            var mvcResult = performPost(memberWrite)
                    .andExpect(status().isOk())
                    .andReturn();

            var actual = MvcMapper.toObject(mvcResult, MemberRead.class);
            assertNotNull(actual.getId());
            assertEquals(1, actual.getVersion());

            var expected = READ_MAPPER.toTarget(memberDto);
            expected.setId(actual.getId());
            expected.setVersion(actual.getVersion());
            assertThat(actual).usingRecursiveAssertion().isEqualTo(expected);

        }
    }

    @Nested
    class GetDataById {
        @Test
        void memberExist() throws Exception {
            var mvcResult = getMemberById(john.getId())
                    .andExpect(status().isOk())
                    .andReturn();

            var memberDto = MvcMapper.toObject(mvcResult, MemberRead.class);
            assertThat(memberDto)
                    .usingRecursiveAssertion()
                    .isEqualTo(READ_MAPPER.toTarget(john));
        }

        @Test
        void memberDoesNotExist() throws Exception {
            getMemberById(inactive.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value(MEMBER_NOT_FOUND_MESSAGE.formatted(inactive.getId(), EntityStatus.ACTIVE)));

            verify(memberService).getMemberById(inactive.getId());
        }
    }

    @Nested
    class ChangesOfCardRestController {

        private Member johnDomain;

        private static casp.web.backend.data.access.layer.member.Card createCard(final int balance) {
            var cardV2 = new casp.web.backend.data.access.layer.member.Card();
            cardV2.setCode(UUID.randomUUID().toString());
            cardV2.setBalance(balance);
            return cardV2;
        }

        private static void assertCard(final casp.web.backend.data.access.layer.member.Card expectedCard, final casp.web.backend.data.access.layer.member.Card actualCard) {
            assertEquals(expectedCard.getCode(), actualCard.getCode());
            assertEquals(expectedCard.getBalance(), actualCard.getBalance());
        }

        @BeforeEach
        void setUp() {
            johnDomain = memberRepository.findById(john.getId()).orElseThrow();
            johnDomain.setCards(Set.of(createCard(30), createCard(20)));
            johnDomain = memberRepository.save(johnDomain);
        }

        @Test
        void saveCard() throws Exception {
            var johnDto = MEMBER_MAPPER.toTarget(johnDomain);
            var cardV2 = createCard(10);
            johnDto.setCards(Set.of(cardV2));

            var mvcResult = performPost(WRITE_MAPPER.toTarget(johnDto))
                    .andExpect(status().isOk())
                    .andReturn();

            var actualCards = MvcMapper.toObject(mvcResult, MemberRead.class).getCards();
            assertThat(actualCards)
                    .singleElement()
                    .satisfies(card -> assertCard(cardV2, card));
        }

        @Test
        void getCards() throws Exception {
            var mvcResult = getMemberById(johnDomain.getId())
                    .andExpect(status().isOk())
                    .andReturn();

            var actualCards = MvcMapper.toObject(mvcResult, MemberRead.class).getCards();
            assertThat(actualCards)
                    .hasSize(2)
                    .allSatisfy(actualCard -> assertThat(johnDomain.getCards())
                            .anySatisfy(expectedCard -> assertCard(expectedCard, actualCard)));
        }
    }
}
