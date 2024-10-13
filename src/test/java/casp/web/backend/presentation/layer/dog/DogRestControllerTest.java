package casp.web.backend.presentation.layer.dog;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.dog.DogDto;
import casp.web.backend.business.logic.layer.dog.DogHasHandlerDto;
import casp.web.backend.business.logic.layer.dog.DogService;
import casp.web.backend.common.DogHasHandlerReferenceRepository;
import casp.web.backend.common.EntityStatus;
import casp.web.backend.data.access.layer.dog.Dog;
import casp.web.backend.data.access.layer.dog.DogHasHandler;
import casp.web.backend.data.access.layer.dog.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.dog.DogRepository;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.deprecated.event.participants.BaseParticipantRepository;
import casp.web.backend.deprecated.reference.MemberReferenceRepository;
import casp.web.backend.presentation.layer.MvcMapper;
import casp.web.backend.presentation.layer.RestResponsePage;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Set;
import java.util.UUID;

import static casp.web.backend.business.logic.layer.dog.DogMapper.DOG_MAPPER;
import static casp.web.backend.deprecated.dog.DogHasHandlerV2Mapper.DOG_HAS_HANDLER_V2_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DogRestControllerTest {
    private static final String DOG_URL_PREFIX = "/dog";
    private static final String DOG_NOT_FOUND_MSG = "Dog with id %s not found or it isn't active.";
    private static final TypeReference<RestResponsePage<DogDto>> DOG_PAGE_RESPONSE = new TypeReference<>() {
    };

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DogRepository dogRepository;
    @Autowired
    private DogHasHandlerRepository dogHasHandlerRepository;
    @Autowired
    private BaseParticipantRepository baseParticipantRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    @Autowired
    private MemberReferenceRepository memberReferenceRepository;

    @SpyBean
    private DogService dogService;

    private DogDto charlie;
    private DogDto bonsai;
    private DogDto inactive;

    @BeforeEach
    void setUp() {
        baseParticipantRepository.deleteAll();
        dogHasHandlerRepository.deleteAll();
        dogRepository.deleteAll();
        memberRepository.deleteAll();

        charlie = createDog("Charlie", EntityStatus.ACTIVE);
        bonsai = createDog("Bonsai", EntityStatus.ACTIVE);
        inactive = createDog("INACTIVE", EntityStatus.INACTIVE);
    }

    @Test
    void getDogs() throws Exception {
        var mvcResult = mockMvc.perform(get(DOG_URL_PREFIX)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,desc"))
                .andExpect(status().isOk())
                .andReturn();

        var dogDtoPage = MvcMapper.toObject(mvcResult, DOG_PAGE_RESPONSE);
        assertThat(dogDtoPage.getContent()).containsExactly(charlie, bonsai);
    }

    @Test
    void register() throws Exception {
        var mvcResult = mockMvc.perform(post(DOG_URL_PREFIX + "/register"))
                .andExpect(status().isOk())
                .andReturn();

        var dogDtoPage = MvcMapper.toObject(mvcResult, DOG_PAGE_RESPONSE);
        assertThat(dogDtoPage.getContent()).containsExactly(charlie, bonsai);
    }

    private DogDto createDog(final String name, final EntityStatus entityStatus) {
        var dog = TestFixture.createDog();
        dog.setEntityStatus(entityStatus);
        dog.setName(name);
        dog.setChipNumber(UUID.randomUUID().toString());

        var member = TestFixture.createMember();
        member.setEntityStatus(entityStatus);
        memberRepository.save(member);

        var dogHasHandler = new DogHasHandler();
        memberReferenceRepository.findById(member.getId()).ifPresent(dogHasHandler::setMember);
        dogHasHandler.setDog(DOG_HAS_HANDLER_V2_MAPPER.toDogReference(dog));
        dogHasHandler.setEntityStatus(entityStatus);
        dogHasHandlerRepository.save(dogHasHandler);
        var space = TestFixture.createSpace();
        space.setMemberOrHandlerId(dogHasHandler.getId());
        space.setEntityStatus(entityStatus);
        var examParticipant = TestFixture.createExamParticipant();
        examParticipant.setMemberOrHandlerId(dogHasHandler.getId());
        examParticipant.setEntityStatus(entityStatus);
        baseParticipantRepository.saveAll(Set.of(examParticipant, space));

        var dto = DOG_MAPPER.toTarget(dogRepository.save(dog));
        var dogHasHandlerReferences = dogHasHandlerReferenceRepository.findAllByDogId(dog.getId());
        dto.setDogHasHandlerSet(DOG_MAPPER.toDogHasHandlerDtoSet(dogHasHandlerReferences));
        return dto;
    }

    private ResultActions getDogById(final UUID dogId) throws Exception {
        return mockMvc.perform(get(DOG_URL_PREFIX + "/{id}", dogId));
    }

    private void assertDogHasHandler(final DogDto actual) {
        assertThat(actual.getDogHasHandlerSet())
                .singleElement()
                .satisfies(dh -> {
                    assertEquals(getDogHasHandler().getId(), dh.getId());
                    assertEquals(getDogHasHandler().getMemberId(), dh.getMemberId());
                    assertEquals(getDogHasHandler().getFirstName(), dh.getFirstName());
                    assertEquals(getDogHasHandler().getLastName(), dh.getLastName());
                });
    }

    private DogHasHandlerDto getDogHasHandler() {
        return charlie.getDogHasHandlerSet().stream().findAny().orElseThrow();
    }

    @Test
    void getDogByChipNumber() throws Exception {
        var mvcResult = mockMvc.perform(get(DOG_URL_PREFIX + "/by-chip-number/{chipNumber}", charlie.getChipNumber()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, Dog.class)).
                usingRecursiveAssertion()
                .isEqualTo(DOG_MAPPER.toSource(charlie));
    }

    @Nested
    class DeleteDogById {
//    FIXME    @Test
//        void cascadeDelete() throws Exception {
//            var dogHasHandlerId = getDogHasHandler().getId();
//            deleteDog(charlie.getId())
//                    .andExpect(status().isNoContent());
//
//            getDogById(charlie.getId()).andExpect(status().isBadRequest());
//
//            var dogHasHandlerList = dogHasHandlerRepository.findAll()
//                    .stream()
//                    .filter(dh -> charlie.getId().equals(dh.getDog().getId()))
//                    .toList();
//            var baseParticipantList = baseParticipantRepository.findAll()
//                    .stream()
//                    .filter(p -> dogHasHandlerId.equals(p.getMemberOrHandlerId()))
//                    .toList();
//            assertThat(dogHasHandlerList).isNotEmpty().allSatisfy(dh -> assertSame(EntityStatus.DELETED, dh.getEntityStatus()));
//            assertThat(baseParticipantList).isNotEmpty().allSatisfy(p -> assertSame(EntityStatus.DELETED, p.getEntityStatus()));
//        }

        @Test
        void dogNotFound() throws Exception {
            deleteDog(inactive.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(DOG_NOT_FOUND_MSG.formatted(inactive.getId())));

            verify(dogService).deleteDogById(inactive.getId());
        }

        private ResultActions deleteDog(final UUID dogId) throws Exception {
            return mockMvc.perform(delete(DOG_URL_PREFIX + "/{id}", dogId));
        }
    }

    @Nested
    class SaveDog {
        @Test
        void dogIsAlwaysAsActiveSaved() throws Exception {
            charlie.setEntityStatus(EntityStatus.DELETED);
            performPost(DOG_MAPPER.toSource(charlie))
                    .andExpect(status().isOk());

            var mvcResult = getDogById(charlie.getId())
                    .andExpect(status().isOk())
                    .andReturn();
            assertThat(MvcMapper.toObject(mvcResult, DogDto.class))
                    .satisfies(dogDto -> {
                        assertSame(EntityStatus.ACTIVE, dogDto.getEntityStatus());
                        assertDogHasHandler(dogDto);
                    });
        }

        @Test
        void bodyIsInvalid() throws Exception {
            var exception = performPost(new DogDto())
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .satisfies(e -> assertThat(e.getMessage()).contains("NotBlank.ownerName", "NotBlank.ownerAddress", "NotBlank.name"));
        }

        private ResultActions performPost(final Dog dog) throws Exception {
            return mockMvc.perform(post(DOG_URL_PREFIX)
                    .content(MvcMapper.toString(dog))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    class GetDogsByNameOrOwnerName {

        private static final TypeReference<RestResponsePage<Dog>> TYPE_REFERENCE = new TypeReference<>() {
        };
        private static final String URL_TEMPLATE = DOG_URL_PREFIX + "/by-dog-name-or-owner-name";


        @Test
        void foundDogByNameAndOwnerName() throws Exception {
            var mvcResult = mockMvc.perform(get(URL_TEMPLATE)
                            .param("name", charlie.getName())
                            .param("ownerName", charlie.getOwnerName()))
                    .andExpect(status().isOk())
                    .andReturn();

            var dogPage = MvcMapper.toObject(mvcResult, TYPE_REFERENCE);
            assertThat(dogPage.getContent())
                    .singleElement()
                    .usingRecursiveAssertion()
                    .isEqualTo(DOG_MAPPER.toSource(charlie));
        }

        @Test
        void withoutParameters() throws Exception {
            var mvcResult = mockMvc.perform(get(URL_TEMPLATE))
                    .andExpect(status().isOk())
                    .andReturn();

            var dogPage = MvcMapper.toObject(mvcResult, TYPE_REFERENCE);
            assertThat(dogPage.getContent())
                    .allSatisfy(actual -> assertThat(Set.of(charlie, bonsai))
                            .anySatisfy(expected -> assertThat(actual)
                                    .usingRecursiveAssertion()
                                    .isEqualTo(DOG_MAPPER.toSource(expected))));
        }
    }

    @Nested
    class GetDogById {
        @Test
        void dogFound() throws Exception {
            var mvcResult = getDogById(charlie.getId())
                    .andExpect(status().isOk())
                    .andReturn();

            var dogDto = MvcMapper.toObject(mvcResult, DogDto.class);
            assertThat(dogDto).isEqualTo(charlie);
            assertDogHasHandler(dogDto);
        }

        @Test
        void dogNotFound() throws Exception {
            getDogById(DogRestControllerTest.this.inactive.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(DOG_NOT_FOUND_MSG.formatted(inactive.getId())));
        }
    }
}
