package gp.pyinsa.jroms.services;

import java.util.List;

import gp.pyinsa.jroms.models.InterviewType;

public interface IntrerviewTypeService {

    List<InterviewType> getAllInterviewTypes();

    InterviewType getInterviewTypeById(short id);
    
}
