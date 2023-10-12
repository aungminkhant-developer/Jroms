package gp.pyinsa.jroms.dtos;

import java.util.List;

import gp.pyinsa.jroms.constants.Interview_Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InterviewEditDto {

    private Long id;
    private String startDate;
    private String startTime;
    private String endTime;
    private String startTimeFormat;
    private String endTimeFormat;
    private String interviewType;
    private String typeValue;
    private List<String> interviewerList;
    private String interviewStage;
    private List<String> interviewerNameList;
    private List<String> interviewerIdList;
    private Interview_Result result;
    private String departHeadName;
    private String teamLeaderName;
    private String candidateName;
    private String jobDetailName;
    private String jobLevelName;
}
