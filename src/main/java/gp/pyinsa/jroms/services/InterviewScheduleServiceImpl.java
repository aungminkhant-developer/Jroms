package gp.pyinsa.jroms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.models.InterviewSchedule;
import gp.pyinsa.jroms.repositories.InterviewScheduleRepository;

@Service
public class InterviewScheduleServiceImpl implements InterviewScheduleService{

    @Autowired
    private InterviewScheduleRepository interviewScheduleRepository;

    @Override
    public void saveInterviewSchedule(InterviewSchedule schedule) {
        interviewScheduleRepository.save(schedule);
    }
    
}
