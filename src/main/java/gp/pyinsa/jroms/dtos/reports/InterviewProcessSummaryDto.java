package gp.pyinsa.jroms.dtos.reports;

import java.util.Date;

import javax.validation.constraints.NotNull;

import gp.pyinsa.jroms.constants.InterviewProcessSummaryGroup;
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
public class InterviewProcessSummaryDto {
    @NotNull
    Date fromDate;

    @NotNull
    Date toDate;

    @NotNull
    InterviewProcessSummaryGroup groupBy;
}
