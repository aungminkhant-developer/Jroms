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
public class InterviewProcessSummaryReportDto {
    private String fromDate;
    private String toDate;
    private Integer totalReceivedCandidates;
    private Integer interviewCandidates;
    private Integer passedCandidates;
    private Integer pendingCandidates;
    private Integer cancelCandidates;
    private Integer notInterviewCandidates;
    private Integer offerMailLetter;
    private Integer acceptedCandidates;
}
