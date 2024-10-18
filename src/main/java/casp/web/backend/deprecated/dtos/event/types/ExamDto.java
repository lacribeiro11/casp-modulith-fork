package casp.web.backend.deprecated.dtos.event.types;

import casp.web.backend.deprecated.dtos.event.participants.ExamParticipantDto;
import casp.web.backend.deprecated.event.types.Exam;
import jakarta.validation.constraints.NotBlank;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public class ExamDto extends BaseEventDto<ExamParticipantDto> {
    @NotBlank
    private String judgeName;

    public ExamDto() {
        super(Exam.EVENT_TYPE);
    }

    public String getJudgeName() {
        return judgeName;
    }

    public void setJudgeName(final String judgeName) {
        this.judgeName = judgeName;
    }
}
