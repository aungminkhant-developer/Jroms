package gp.pyinsa.jroms.dtos.reports;

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
public class InterviewProcessSummaryReportByJobOfferDto extends InterviewProcessSummaryReportDto {
    private Long jobOfferId;
    private String jobOffer;
    private String openDate;
    private String expireDate;
    private String departmentId;
    private String teamId;
}
