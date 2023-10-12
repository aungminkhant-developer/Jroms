package gp.pyinsa.jroms.dtos.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewProcessSummaryReportByJobPositionDto extends InterviewProcessSummaryReportDto {
    private String vacantPosition;
}
