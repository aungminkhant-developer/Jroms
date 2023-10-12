package gp.pyinsa.jroms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.exceptions.ResourceNotFoundException;
import gp.pyinsa.jroms.models.JobTitle;
import gp.pyinsa.jroms.repositories.JobTitleRepository;
import gp.pyinsa.jroms.utils.CustomBeanUtils;

@Service
public class JobTitleServiceImpl implements JobTitleService {
    
    @Autowired
    private JobTitleRepository jobTitleRepository;

    @Override
    public List<JobTitle> getActiveJobTitles() {
        return jobTitleRepository.findByStatus(Status.ACTIVE);
    }

    @Override
    public void addNewJobTitle(JobTitle jobTitle) {
        if(jobTitleRepository.existsByNameAndStatus(jobTitle.getName(), Status.ACTIVE)) {
            throw new ResourceAlreadyExistsException("Job Title already exists.");
        }

        jobTitleRepository.saveAndFlush(jobTitle);
    }

    @Override
    public void updateJobTitle(JobTitle jobTitle) {
        if(jobTitleRepository.existsByNameAndStatus(jobTitle.getName(), Status.ACTIVE)) {
            throw new ResourceAlreadyExistsException("Job Title already exists.");
        }

        JobTitle oldJobTitle = jobTitleRepository.findById(jobTitle.getId()).orElseThrow(() -> new ResourceNotFoundException("Job title not found."));
        CustomBeanUtils.copyNonNullProperties(jobTitle, oldJobTitle, "id");
        jobTitleRepository.saveAndFlush(oldJobTitle);
    }

    @Override
    public void deleteJobTitleById(short id) {
        JobTitle jobTitle = jobTitleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job title not found."));
        jobTitle.setStatus(Status.DELETED);
        jobTitleRepository.saveAndFlush(jobTitle);
    }

    @Override
    public List<JobTitle> getAllJobTitles() {
        return jobTitleRepository.findAll();
    }

    @Override
    public JobTitle getById(short id) {
       return jobTitleRepository.findById(id).get();
    }

}
