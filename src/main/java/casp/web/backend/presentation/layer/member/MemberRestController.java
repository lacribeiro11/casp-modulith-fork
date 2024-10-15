package casp.web.backend.presentation.layer.member;

import casp.web.backend.business.logic.layer.member.MemberService;
import casp.web.backend.common.enums.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static casp.web.backend.presentation.layer.member.MemberReadMapper.READ_MAPPER;
import static casp.web.backend.presentation.layer.member.MemberWriteMapper.WRITE_MAPPER;

@RestController
@RequestMapping("member")
@Validated
class MemberRestController {

    private final MemberService memberService;

    @Autowired
    MemberRestController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    ResponseEntity<Page<MemberRead>> getMembers(final @RequestParam EntityStatusParam entityStatusParam,
                                                final @ParameterObject Pageable pageable) {
        var memberDtoPage = memberService.getMembersByEntityStatus(entityStatusParam.getEntityStatus(), pageable);
        return ResponseEntity.ok(READ_MAPPER.toTargetPage(memberDtoPage));
    }

    @GetMapping("{id}")
    ResponseEntity<MemberRead> getMemberById(final @PathVariable UUID id) {
        var memberDto = memberService.getMemberById(id);
        return ResponseEntity.ok(READ_MAPPER.toTarget(memberDto));
    }

    @GetMapping("search-members-by-firstname-and-lastname")
    ResponseEntity<Page<MemberRead>> getMemberByFirstNameAndLastName(final @RequestParam @NotBlank String firstName,
                                                                     final @RequestParam @NotBlank String lastName,
                                                                     final @ParameterObject Pageable pageable) {
        var memberDtoPage = memberService.getMembersByFirstNameAndLastName(firstName, lastName, pageable);
        return ResponseEntity.ok(READ_MAPPER.toTargetPage(memberDtoPage));
    }

    @PostMapping
    ResponseEntity<MemberRead> saveMember(final @RequestBody @Valid MemberWrite memberWrite) {
        var memberDto = WRITE_MAPPER.toSource(memberWrite);
        memberDto = memberService.saveMember(memberDto);
        return ResponseEntity.ok(READ_MAPPER.toTarget(memberDto));
    }

    @DeleteMapping("{id}")
    ResponseEntity<Void> deleteMember(final @PathVariable UUID id) {
        memberService.deleteMemberById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/deactivate")
    ResponseEntity<MemberRead> deactivateMember(final @PathVariable UUID id) {
        var memberDto = memberService.deactivateMember(id);
        return ResponseEntity.ok(READ_MAPPER.toTarget(memberDto));
    }

    @PostMapping("{id}/activate")
    ResponseEntity<MemberRead> activateMember(final @PathVariable UUID id) {
        var memberDto = memberService.activateMember(id);
        return ResponseEntity.ok(READ_MAPPER.toTarget(memberDto));
    }

    @GetMapping("search-members-by-name")
    ResponseEntity<Page<MemberRead>> searchMembersByFirstNameOrLastName(final @RequestParam(required = false, defaultValue = "") String name,
                                                                        final @ParameterObject Pageable pageable) {
        var memberDtoPage = memberService.getMembersByName(name, pageable);
        return ResponseEntity.ok(READ_MAPPER.toTargetPage(memberDtoPage));
    }

    @GetMapping("roles")
    ResponseEntity<List<Role>> getMemberRoles() {
        return ResponseEntity.ok(Role.getAllRolesSorted());
    }

    @GetMapping("emails-by-ids")
    ResponseEntity<Set<String>> getMembersEmailByIds(final @RequestParam @Size(min = 1) Set<UUID> membersId) {
        return ResponseEntity.ok(memberService.getMembersEmailByIds(membersId));
    }

    /**
     * @deprecated It will be removed in #3.
     */
    @Deprecated(forRemoval = true, since = "0.0.0")
    @PostMapping("migrate-data")
    ResponseEntity<Void> migrateDataToV2() {
        memberService.migrateDataToV2();
        return ResponseEntity.noContent().build();
    }
}
