package gp.pyinsa.jroms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.exceptions.ResourceNotFoundException;
import gp.pyinsa.jroms.models.InterviewStage;
import gp.pyinsa.jroms.repositories.InterviewStageRepository;
import gp.pyinsa.jroms.utils.CustomBeanUtils;

@Service
public class InterviewStageServiceImpl implements InterviewStageService {

    @Autowired
    private InterviewStageRepository interviewStageRepository;

    @Override
    public List<InterviewStage> getActiveInterviewStages() {
        return interviewStageRepository.findByStatus(Status.ACTIVE);
    }

    @Override
    public void addNewInterviewStage(InterviewStage interviewStage) {
        if(interviewStageRepository.existsByNameAndStatus(interviewStage.getName(), Status.ACTIVE)) {
            throw new ResourceAlreadyExistsException("Interview stage already exists.");
        }

        interviewStageRepository.saveAndFlush(interviewStage);    
    }

    @Override
    public void updateInterviewStage(InterviewStage interviewStage) {
        if(interviewStageRepository.existsByNameAndStatus(interviewStage.getName(), Status.ACTIVE)) {
            throw new ResourceAlreadyExistsException("Interview stage already exists.");
        }

        InterviewStage oldinterviewStage = interviewStageRepository.findById(interviewStage.getId()).orElseThrow(() -> new ResourceNotFoundException("Interview stage not found."));
        CustomBeanUtils.copyNonNullProperties(interviewStage, oldinterviewStage, "id");
        interviewStageRepository.saveAndFlush(oldinterviewStage);
    }

    @Override
    public void deleteInterviewStageById(short id) {
        InterviewStage interviewStage = interviewStageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Interview stage not found."));
        interviewStage.setStatus(Status.DELETED);
        interviewStageRepository.saveAndFlush(interviewStage);
    }

    @Override
    public InterviewStage getInterviewStageById(short id) {
       return interviewStageRepository.findById(id).get();
    }

   

}
