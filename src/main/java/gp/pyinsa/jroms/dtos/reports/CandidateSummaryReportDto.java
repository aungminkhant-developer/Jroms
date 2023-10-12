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
public class CandidateSummaryReportDto {
    private String name;
    private String applyDate;
    private String cvStatus;
    private String interviewDate;
    private String interviewStage;
    private String interviewStatus;
    private String dob;
    private String sex;
    private String phoneNumber;
    private String email;
    private String education;
    private String technicalSkills;
    private String languageSkills;
    private String appliedPosition;
    private String level;
    private String mainTechnology;
    private String relatedExperienceTime;
    private String expectedSalary;
}
