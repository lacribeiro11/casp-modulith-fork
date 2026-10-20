package casp.web.backend.member;

import casp.web.backend.MongoIntegrationTest;
import casp.web.backend.common.exception.MemberEMailConflictException;
import casp.web.backend.member.data.Member;
import casp.web.backend.member.data.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static casp.web.backend.member.MemberMapper.MEMBER_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@MongoIntegrationTest
@ExtendWith(OutputCaptureExtension.class)
class MemberServiceImplIntTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;

    @Nested
    class EmailUniqueness {

        private Member member;

        @BeforeEach
        void setUp() {
            memberRepository.deleteAll();
            member = memberRepository.save(TestFixture.createMember());
        }

        @Test
        void doesNotFail() {
            memberService.deleteMemberById(member.getId());

            assertDoesNotThrow(() -> memberService.saveMember(MEMBER_MAPPER.toTarget(TestFixture.createMember())));
        }

        @Test
        void doesFail(CapturedOutput output) {
            var exception = assertThrowsExactly(MemberEMailConflictException.class, () -> memberService.saveMember(MEMBER_MAPPER.toTarget(TestFixture.createMember())));

            assertThat(output).contains(LogLevel.ERROR.name(), exception.getMessage());
        }
    }
}
