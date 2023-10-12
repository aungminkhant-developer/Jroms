package gp.pyinsa.jroms.constants;

public enum InterviewProcessSummaryGroup {
    JOB_OFFER("Job Offer"),
    JOB_POSITION("Job Position"),
    DEPARTMENT("Department"),
    TEAM("Team");

    private final String displayValue;

    private InterviewProcessSummaryGroup(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
