package gp.pyinsa.jroms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.exceptions.ResourceNotFoundException;
import gp.pyinsa.jroms.models.JobType;
import gp.pyinsa.jroms.repositories.JobTypeRepository;
import gp.pyinsa.jroms.utils.CustomBeanUtils;

@Service
public class JobTypeServiceImpl implements JobTypeService {

    @Autowired
    private JobTypeRepository jobTypeRepository;

    @Override
    public List<JobType> getActiveJobTypes() {
        return jobTypeRepository.findByStatus(Status.ACTIVE);
    }

    @Override
    public void addNewJobType(JobType jobType) {
        if(jobTypeRepository.existsByNameAndStatus(jobType.getName(), Status.ACTIVE)) {
            throw new ResourceAlreadyExistsException("Job type already exists.");
        }

        jobTypeRepository.saveAndFlush(jobType);
    }

    @Override
    public void updateJobType(JobType jobType) {
        if(jobTypeRepository.existsByNameAndStatus(jobType.getName(), Status.ACTIVE)) {
            throw new ResourceAlreadyExistsException("Job type already exists.");
        }

        JobType oldJobType = jobTypeRepository.findById(jobType.getId()).orElseThrow(() -> new ResourceNotFoundException("Job type not found."));
        CustomBeanUtils.copyNonNullProperties(jobType, oldJobType, "id");
        jobTypeRepository.saveAndFlush(oldJobType);
    }

    @Override
    public void deleteJobTypeById(short id) {
        JobType jobType = jobTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job type not found."));
        jobType.setStatus(Status.DELETED);
        jobTypeRepository.saveAndFlush(jobType);
    }

    @Override
    public JobType getJobTypeById(short id) {
        // TODO Auto-generated method stub
        return jobTypeRepository.findById(id).get();
    }
    
}
