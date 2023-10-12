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
public class InterviewProcessSummaryReportByTeamDto extends InterviewProcessSummaryReportDto {
    private String teamId;
    private String teamName;
}
