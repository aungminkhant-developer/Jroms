package gp.pyinsa.jroms.services;

import java.util.List;

import gp.pyinsa.jroms.models.JobType;

public interface JobTypeService {
    List<JobType> getActiveJobTypes();

    void addNewJobType(JobType jobType);

    void updateJobType(JobType jobType);

    void deleteJobTypeById(short id);

    JobType getJobTypeById(short id);
}
