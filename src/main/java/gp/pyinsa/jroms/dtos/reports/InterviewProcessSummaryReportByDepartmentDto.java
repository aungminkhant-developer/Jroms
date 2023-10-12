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
public class InterviewProcessSummaryReportByDepartmentDto extends InterviewProcessSummaryReportDto {
    private String departmentId;
    private String departmentName;
}
