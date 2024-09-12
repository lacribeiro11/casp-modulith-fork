package casp.web.backend.presentation.layer.dog;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.dog.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.dog.DogRepository;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.participants.BaseParticipantRepository;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.presentation.layer.MvcMapper;
import casp.web.backend.presentation.layer.dtos.dog.DogDto;
import casp.web.backend.presentation.layer.dtos.dog.DogHasHandlerDto;
import casp.web.backend.presentation.layer.dtos.member.MemberDto;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Set;
import java.util.UUID;

import static casp.web.backend.presentation.layer.dtos.dog.DogHasHandlerMapper.DOG_HAS_HANDLER_MAPPER;
import static casp.web.backend.presentation.layer.dtos.dog.DogMapper.DOG_MAPPER;
import static casp.web.backend.presentation.layer.dtos.member.MemberMapper.MEMBER_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DogHasHandlerRestControllerTest {
    private static final String DOG_HAS_HANDLER_URL_PREFIX = "/dog-has-handler";
    private static final TypeReference<Set<DogHasHandlerDto>> DOG_HAS_HANDLER_SET_TYPE_REFERENCE = new TypeReference<>() {
    };
    private static final TypeReference<Set<String>> STRING_SET_TYPE_REFERENCE = new TypeReference<>() {
    };

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private DogHasHandlerRepository dogHasHandlerRepository;
    @Autowired
    private DogRepository dogRepository;
    @Autowired
    private BaseParticipantRepository baseParticipantRepository;

    private DogHasHandlerDto dogHasHandler;
    private MemberDto member;
    private DogDto dog;

    @BeforeEach
    void setUp() {
        baseParticipantRepository.deleteAll();
        dogHasHandlerRepository.deleteAll();
        memberRepository.deleteAll();
        dogRepository.deleteAll();

        var dogHasHandlerDocument = TestFixture.createValidDogHasHandler();
        var memberDocument = dogHasHandlerDocument.getMember();
        var dogDocument = dogHasHandlerDocument.getDog();
        var space = TestFixture.createValidSpace();
        space.setMemberOrHandlerId(dogHasHandlerDocument.getId());

        member = MEMBER_MAPPER.toDto(memberRepository.save(memberDocument));
        dog = DOG_MAPPER.toDto(dogRepository.save(dogDocument));
        dogHasHandler = DOG_HAS_HANDLER_MAPPER.toDto(dogHasHandlerRepository.save(dogHasHandlerDocument));
        baseParticipantRepository.save(space);
    }

    private ResultActions getDogHasHandlerById(final UUID id) throws Exception {
        return mockMvc.perform(get(DOG_HAS_HANDLER_URL_PREFIX + "/{id}", id));
    }

    @Test
    void getDogHasHandlerByDogId() throws Exception {
        var mvcResult = mockMvc.perform(get(DOG_HAS_HANDLER_URL_PREFIX + "/by-dog-id/{dogId}", dog.getId()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, DOG_HAS_HANDLER_SET_TYPE_REFERENCE)).containsOnly(dogHasHandler);
    }

    @Test
    void getDogHasHandlerByMemberId() throws Exception {
        var mvcResult = mockMvc.perform(get(DOG_HAS_HANDLER_URL_PREFIX + "/by-member-id/{memberId}", member.getId()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, DOG_HAS_HANDLER_SET_TYPE_REFERENCE)).containsOnly(dogHasHandler);
    }

    @Test
    void getDogsByMemberId() throws Exception {
        TypeReference<Set<DogDto>> typeReference = new TypeReference<>() {
        };
        var mvcResult = mockMvc.perform(get(DOG_HAS_HANDLER_URL_PREFIX + "/dogs-by-member-id/{memberId}", member.getId()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, typeReference)).containsOnly(dog);
    }

    @Test
    void getMembersByDogId() throws Exception {
        TypeReference<Set<MemberDto>> typeReference = new TypeReference<>() {
        };
        var mvcResult = mockMvc.perform(get(DOG_HAS_HANDLER_URL_PREFIX + "/members-by-dog-id/{dogId}", dog.getId()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, typeReference)).containsOnly(member);
    }

    @Test
    void deleteDogHasHandlersByDogId() throws Exception {
        mockMvc.perform(delete(DOG_HAS_HANDLER_URL_PREFIX + "/by-dog-id/{dogId}", dog.getId()))
                .andExpect(status().isNoContent());

        assertThat(dogHasHandlerRepository.findAll()).allSatisfy(dh -> assertSame(EntityStatus.DELETED, dh.getEntityStatus()));
        assertThat(baseParticipantRepository.findAll()).allSatisfy(p -> assertSame(EntityStatus.DELETED, p.getEntityStatus()));
    }

    @Test
    void deleteDogHasHandlersByMemberId() throws Exception {
        mockMvc.perform(delete(DOG_HAS_HANDLER_URL_PREFIX + "/by-member-id/{memberId}", member.getId()))
                .andExpect(status().isNoContent());

        assertThat(dogHasHandlerRepository.findAll()).allSatisfy(dh -> assertSame(EntityStatus.DELETED, dh.getEntityStatus()));
        assertThat(baseParticipantRepository.findAll()).allSatisfy(p -> assertSame(EntityStatus.DELETED, p.getEntityStatus()));
    }

    @Test
    void searchByName() throws Exception {
        var mvcResult = mockMvc.perform(get(DOG_HAS_HANDLER_URL_PREFIX + "/search-by-name")
                        .param("name", dog.getName()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, DOG_HAS_HANDLER_SET_TYPE_REFERENCE)).containsOnly(dogHasHandler);
    }

    @Test
    void getAllDogHasHandler() throws Exception {
        var mvcResult = mockMvc.perform(get(DOG_HAS_HANDLER_URL_PREFIX))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, DOG_HAS_HANDLER_SET_TYPE_REFERENCE)).containsOnly(dogHasHandler);
    }

    @Test
    void getDogHasHandlersByHandlerIds() throws Exception {
        var mvcResult = mockMvc.perform(get(DOG_HAS_HANDLER_URL_PREFIX + "/by-ids")
                        .param("ids", dogHasHandler.getId().toString()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, DOG_HAS_HANDLER_SET_TYPE_REFERENCE)).containsOnly(dogHasHandler);
    }

    @Test
    void getDogHasHandlerIdsByMemberId() throws Exception {
        var mvcResult = mockMvc.perform(get(DOG_HAS_HANDLER_URL_PREFIX + "/dog-has-handler-ids-by-member-id/{memberId}", member.getId()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, STRING_SET_TYPE_REFERENCE)).containsOnly(dogHasHandler.getId().toString());
    }

    @Test
    void getDogHasHandlerIdsByDogId() throws Exception {
        var mvcResult = mockMvc.perform(get(DOG_HAS_HANDLER_URL_PREFIX + "/dog-has-handler-ids-by-dog-id/{dogId}", dog.getId()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, STRING_SET_TYPE_REFERENCE)).containsOnly(dogHasHandler.getId().toString());
    }

    @Test
    void getMembersEmailByIds() throws Exception {
        var mvcResult = mockMvc.perform(get(DOG_HAS_HANDLER_URL_PREFIX + "/get-emails-by-ids")
                        .param("ids", dogHasHandler.getId().toString()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, STRING_SET_TYPE_REFERENCE)).containsOnly(member.getEmail());
    }

    @Nested
    class SaveDogHasHandler {
        @Test
        void dogHasHandlerIsAlwaysAsActiveSaved() throws Exception {
            dogHasHandler.setEntityStatus(EntityStatus.DELETED);

            var mvcResult = performPost(dogHasHandler)
                    .andExpect(status().isOk())
                    .andReturn();

            assertThat(MvcMapper.toObject(mvcResult, DogHasHandlerDto.class).getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
        }

        @Test
        void itIsInvalid() throws Exception {
            dogHasHandler.setDog(null);
            dogHasHandler.setDogId(null);
            performPost(dogHasHandler)
                    .andExpect(status().isBadRequest());
        }

        private ResultActions performPost(final DogHasHandlerDto dogHasHandlerDto) throws Exception {
            return mockMvc.perform(post(DOG_HAS_HANDLER_URL_PREFIX)
                    .content(MvcMapper.toString(dogHasHandlerDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    class GetDogHasHandlerById {
        @Test
        void itExist() throws Exception {
            var mvcResult = getDogHasHandlerById(dogHasHandler.getId())
                    .andExpect(status().isOk())
                    .andReturn();

            assertThat(MvcMapper.toObject(mvcResult, DogHasHandlerDto.class)).isEqualTo(dogHasHandler);
        }

        @Test
        void doesNotExist() throws Exception {
            getDogHasHandlerById(UUID.randomUUID())
                    .andExpect(status().isBadRequest());
        }
    }
}
