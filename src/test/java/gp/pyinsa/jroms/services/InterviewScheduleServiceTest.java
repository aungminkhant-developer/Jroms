package gp.pyinsa.jroms.services;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import gp.pyinsa.jroms.models.InterviewSchedule;
import gp.pyinsa.jroms.models.InterviewType;
import gp.pyinsa.jroms.repositories.InterviewScheduleRepository;

@SpringBootTest
public class InterviewScheduleServiceTest {

    @Mock
    private InterviewScheduleRepository interviewScheduleRepository;

    @InjectMocks
    private InterviewScheduleServiceImpl interviewScheduleServiceImpl;

    @Test
    void addNewInterviewSchedule_WhenAdd_ShouldSaveInterviewSchedule(){
        //setup
        InterviewSchedule interviewSchedule=new InterviewSchedule((long)1, new Date(), "01:00PM", "02:00PM", "MICT Park,Hlaing,Yangon", new InterviewType());

       //assert
       verify(interviewScheduleRepository, times(0)).save(interviewSchedule);
       
    }
    
}
