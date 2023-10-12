package gp.pyinsa.jroms.services;

import java.util.List;

import gp.pyinsa.jroms.models.InterviewStage;

public interface InterviewStageService {
    List<InterviewStage> getActiveInterviewStages();
    void addNewInterviewStage(InterviewStage interviewStage);
    void updateInterviewStage(InterviewStage interviewStage);
    void deleteInterviewStageById(short id);
    InterviewStage getInterviewStageById(short id);
    
}
