package gp.pyinsa.jroms.constants;

public enum CandidateReportBy {
    DATE_RANGE("Date Range"),
    JOB_OFFER("Job Offer");

    private final String displayValue;

    private CandidateReportBy(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
