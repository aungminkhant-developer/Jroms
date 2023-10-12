package gp.pyinsa.jroms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import gp.pyinsa.jroms.models.InterviewType;
import gp.pyinsa.jroms.repositories.InterviewTypeRepository;

@Service
public class InterviewTypeServiceImpl implements IntrerviewTypeService{

    @Autowired
    private InterviewTypeRepository interviewTypeRepository;

    @Override
    public List<InterviewType> getAllInterviewTypes() {
       return interviewTypeRepository.findAll();
    }

    @Override
    public InterviewType getInterviewTypeById(short id) {
       return interviewTypeRepository.findById(id).get();
    }

  

    
}
