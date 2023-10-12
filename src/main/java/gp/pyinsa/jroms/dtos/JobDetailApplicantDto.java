package gp.pyinsa.jroms.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JobDetailApplicantDto {
    private Long jobDetailId;
    private Long applicantCount;
    private String lastAppliedDate;
    private String jobPosition;
    private String message;

    public JobDetailApplicantDto(Long jobDetailId, Long applicantCount, String lastAppliedDate) {
        this.jobDetailId = jobDetailId;
        this.applicantCount = applicantCount;
        this.lastAppliedDate = lastAppliedDate;
    }

}
