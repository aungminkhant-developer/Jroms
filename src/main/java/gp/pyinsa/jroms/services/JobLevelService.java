package gp.pyinsa.jroms.services;

import java.util.List;

import gp.pyinsa.jroms.models.JobLevel;

public interface JobLevelService {
    List<JobLevel> getActiveJobLevels();
    List<JobLevel> getAllJobLevels();
    void addNewJobLevel(JobLevel jobLevel);
    void updateJobLevel(JobLevel jobLevel);
    void deleteJobLevelById(short id);
}
