package gp.pyinsa.jroms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.exceptions.ResourceNotFoundException;
import gp.pyinsa.jroms.models.JobLevel;
import gp.pyinsa.jroms.repositories.JobLevelRepository;
import gp.pyinsa.jroms.utils.CustomBeanUtils;

@Service
public class JobLevelServiceImpl implements JobLevelService {

    @Autowired
    private JobLevelRepository jobLevelRepository;

    @Override
    public List<JobLevel> getAllJobLevels() {
        return jobLevelRepository.findAll();
    }

    @Override
    public void addNewJobLevel(JobLevel jobLevel) {
        if(jobLevelRepository.existsByNameAndStatus(jobLevel.getName(), Status.ACTIVE)) {
            throw new ResourceAlreadyExistsException("Job level already exists.");
        }

        jobLevelRepository.saveAndFlush(jobLevel);
    }

    @Override
    public void updateJobLevel(JobLevel jobLevel) {
        if(jobLevelRepository.existsByNameAndStatus(jobLevel.getName(), Status.ACTIVE)) {
            throw new ResourceAlreadyExistsException("Job level already exists.");
        }

        JobLevel oldJobLevel = jobLevelRepository.findById(jobLevel.getId()).orElseThrow(() -> new ResourceNotFoundException("Job level not found."));
        CustomBeanUtils.copyNonNullProperties(jobLevel, oldJobLevel, "id");
        jobLevelRepository.saveAndFlush(oldJobLevel);
    }

    @Override
    public void deleteJobLevelById(short id) {
        JobLevel jobLevel = jobLevelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job level not found."));
        jobLevel.setStatus(Status.DELETED);
        jobLevelRepository.saveAndFlush(jobLevel);
    }

    @Override
    public List<JobLevel> getActiveJobLevels() {
        return jobLevelRepository.findByStatus(Status.ACTIVE);
    }
    
}
