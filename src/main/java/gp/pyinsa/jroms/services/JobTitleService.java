package gp.pyinsa.jroms.services;

import java.util.List;

import gp.pyinsa.jroms.models.JobTitle;

public interface JobTitleService {

    JobTitle getById(short id);

    List<JobTitle> getActiveJobTitles();

    List<JobTitle> getAllJobTitles();

    void addNewJobTitle(JobTitle jobTitle);

    void updateJobTitle(JobTitle jobTitle);

    void deleteJobTitleById(short id);
}
