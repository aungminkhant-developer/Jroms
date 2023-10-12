package gp.pyinsa.jroms.dtos.reports;

import java.util.Date;

import gp.pyinsa.jroms.constants.CandidateReportBy;
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
public class CandidateSummaryDto {
    Date fromDate;

    Date toDate;

    Long jobOfferId;

    CandidateReportBy reportBy = CandidateReportBy.DATE_RANGE;
}
