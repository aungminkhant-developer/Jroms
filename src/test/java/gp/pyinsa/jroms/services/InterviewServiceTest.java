package gp.pyinsa.jroms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import gp.pyinsa.jroms.constants.Interview_Result;
import gp.pyinsa.jroms.constants.Interview_Status;
import gp.pyinsa.jroms.constants.Mail_Status;
import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.Interview;
import gp.pyinsa.jroms.models.InterviewSchedule;
import gp.pyinsa.jroms.models.InterviewStage;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.JobLevel;
import gp.pyinsa.jroms.models.JobTitle;
import gp.pyinsa.jroms.models.WorkSchedule;
import gp.pyinsa.jroms.repositories.InterviewRepository;

@SpringBootTest
public class InterviewServiceTest {

    @Mock
    private InterviewRepository interviewRepository;

    @InjectMocks
    private InterviewServiceImpl interviewServiceImpl;

    @Test
    void getAllInterview_WhenSetByCandidateId_ShouldGetAllInterviewsUnderCandidate(){
        //setup
        Candidate candidate=new Candidate((long)1, "Thet Wai Aung", "09757129299", "twa453354@gmail.com", "Yangon", null, null, "TU", "M", "Java,Javascript", "English", "Java", "Mid", "Mid", null, null, null, null, null, null,false);
        Interview interview1=new Interview((long)1, Interview_Status.ONGOING, Interview_Result.PENDING, Mail_Status.NOT_SEND, candidate, new InterviewStage(), new InterviewSchedule(), new JobDetail(), null);
        Interview interview2=new Interview((long)2, Interview_Status.FINISHED, Interview_Result.PASSED, Mail_Status.SEND, candidate, new InterviewStage(), new InterviewSchedule(), new JobDetail(), null);
        List<Interview> interviewsList=List.of(interview1,interview2);
        
        //mock
        when(interviewRepository.findAllByCandidate_Id(candidate.getId())).thenReturn(interviewsList);

        //assert
        List<Interview> newInterviewsList=interviewServiceImpl.getAllInterviewByCandidateId(candidate.getId());
        verify(interviewRepository, times(1)).findAllByCandidate_Id(candidate.getId());
        assertEquals(newInterviewsList.size(),interviewsList.size());
    }

    @Test
    void getInterview_WhenSetInterviewId_ShouldGetInterview(){
        //setup
        Interview interview=new Interview((long)1, Interview_Status.ONGOING, Interview_Result.PENDING, Mail_Status.NOT_SEND, null, new InterviewStage(), new InterviewSchedule(), new JobDetail(), null);
        long interviewId=1;

        //mock
        when(interviewRepository.findById(interviewId)).thenReturn(Optional.of(interview));

        //assert
        Interview newInterview=interviewServiceImpl.getInterviewById(interviewId);
        verify(interviewRepository, times(1)).findById(interviewId);
        assertEquals(newInterview.getStatus(),interview.getStatus());
        assertEquals(newInterview,interview);
    }

    @Test
    void addNewInterview_WhenAdd_ShouldSaveNewInterview(){
        //setup
        Candidate candidate=new Candidate((long)1, "Thet Wai Aung", "09757129299", "twa453354@gmail.com", "Yangon", null, null, "TU", "M", "Java,Javascript", "English", "Java", "Mid", "Mid", null, null, null, null, null, null,false);
        Interview interview=new Interview((long)1, Interview_Status.ONGOING, Interview_Result.PENDING, Mail_Status.NOT_SEND, candidate, new InterviewStage(), new InterviewSchedule(), new JobDetail(), null);
    
        //assert
        interviewServiceImpl.saveInterview(interview);
        verify(interviewRepository, times(1)).saveAndFlush(interview);
        
    }

    @Test
    void existsByCandidateAndMailStatus_WhenValid_ReturnTrue(){
        //setup
        Candidate candidate=new Candidate((long)1, "Thet Wai Aung", "09757129299", "twa453354@gmail.com", "Yangon", null, null, "TU", "M", "Java,Javascript", "English", "Java", "Mid", "Mid", null, null, null, null, null, null,false);
        boolean status=true;

        //mock
        when(interviewRepository.existsByCandidateAndMailStatus(candidate, Mail_Status.NOT_SEND)).thenReturn(true);

        //assert
        boolean returnStatus=interviewServiceImpl.existsByCandidateAndMailStatus(candidate, Mail_Status.NOT_SEND);
        verify(interviewRepository, times(1)).existsByCandidateAndMailStatus(candidate, Mail_Status.NOT_SEND);
        assertEquals(status,returnStatus);
    }

    @Test
    void getAllInterviews_ShouldGetAllInterviews(){
        //setup
        Candidate candidate=new Candidate((long)1, "Thet Wai Aung", "09757129299", "twa453354@gmail.com", "Yangon", null, null, "TU", "M", "Java,Javascript", "English", "Java", "Mid", "Mid", null, null, null, null, null, null,false);
        Candidate candidate2=new Candidate((long)2, "Mary", "09950909977","mary@gmail.com", "Yangon", null, null, "YTU", "F", "Java", "Japanese", "Java", "Mid", null, null, null, null, null, null, null,false);
        Interview interview=new Interview((long)1, Interview_Status.ONGOING, Interview_Result.PENDING, Mail_Status.NOT_SEND, candidate, new InterviewStage(), new InterviewSchedule(), new JobDetail(), null);
        Interview interview2=new Interview((long)2, Interview_Status.FINISHED, Interview_Result.PASSED, Mail_Status.SEND, candidate2, new InterviewStage(), new InterviewSchedule(), new JobDetail(), null);
        List<Interview> interviewsList=List.of(interview,interview2);

        //mock
        when(interviewRepository.findAll()).thenReturn(interviewsList);

        //assert
        List<Interview> newInterviewsList=interviewServiceImpl.getAllInterviews();
        verify(interviewRepository, times(1)).findAll();
        assertEquals(newInterviewsList.size(),interviewsList.size());
        assertNotEquals(newInterviewsList.get(0).getCandidate(), newInterviewsList.get(1).getCandidate());       
    }

    @Test
    void getAllInterviewByJobDetailId_WhenSetJobDetailId_ShouldGetInterviewsUnderJobDetail(){
        //setup
        Candidate candidate=new Candidate((long)1, "Thet Wai Aung", "09757129299", "twa453354@gmail.com", "Yangon", null, null, "TU", "M", "Java,Javascript", "English", "Java", "Mid", "Mid", null, null, null, null, null, null,false);
        JobDetail jobDetail=new JobDetail((long)1, new JobTitle(), new JobLevel(), (short)2,"300000", new WorkSchedule(), null, null, null, null, null, null, null, null, null, null);
        Interview interview=new Interview((long)1, Interview_Status.ONGOING, Interview_Result.PENDING, Mail_Status.NOT_SEND, candidate, new InterviewStage(), new InterviewSchedule(), jobDetail, null);
        Interview interview2=new Interview((long)2, Interview_Status.FINISHED, Interview_Result.PASSED, Mail_Status.SEND, candidate, new InterviewStage(), new InterviewSchedule(),jobDetail, null);
        List<Interview> interviewList=List.of(interview,interview2);

        //mock
        when(interviewRepository.findAllByJobDetail_Id(jobDetail.getId())).thenReturn(interviewList);

        //assert
        List<Interview> newInterviewList=interviewServiceImpl.getAllInterviewByJobDetailId(jobDetail.getId());
        verify(interviewRepository, times(1)).findAllByJobDetail_Id(jobDetail.getId());
        assertEquals(newInterviewList.size(),interviewList.size());
        assertEquals(newInterviewList.get(0).getJobDetail().getId(),interviewList.get(0).getJobDetail().getId());
    }

    @Test
    void existsByCandidateAndInterviewStage_WhenIsValid_ReturnTrue(){
        //setup
        InterviewStage interviewStage=new InterviewStage((short)1, "First Interview", Status.ACTIVE);
        Candidate candidate=new Candidate((long)1, "Thet Wai Aung", "09757129299", "twa453354@gmail.com", "Yangon", null, null, "TU", "M", "Java,Javascript", "English", "Java", "Mid", "Mid", null, null, null, null, null, null,false);
        boolean status=true;
        //mock
        when(interviewRepository.existsByCandidateAndInterviewStage(candidate, interviewStage)).thenReturn(true);
    
        //assert
        boolean returnStatus=interviewServiceImpl.existsByCandidateAndInterviewStage(candidate, interviewStage);
        verify(interviewRepository, times(1)).existsByCandidateAndInterviewStage(candidate, interviewStage);
        assertEquals(status,returnStatus);
    }

    @Test
    void existsByCandidateAndResult_WhenIsValid_ReturnTrue(){
        //setup
        Candidate candidate=new Candidate((long)1, "Thet Wai Aung", "09757129299", "twa453354@gmail.com", "Yangon", null, null, "TU", "M", "Java,Javascript", "English", "Java", "Mid", "Mid", null, null, null, null, null, null,false);  
        boolean status=true;
        //mock
        when(interviewRepository.existsByCandidateAndResult(candidate, Interview_Result.PASSED)).thenReturn(true);

        //assert
        boolean returnStatus=interviewServiceImpl.existsByCandidateAndResult(candidate, Interview_Result.PASSED);
        verify(interviewRepository, times(1)).existsByCandidateAndResult(candidate, Interview_Result.PASSED);
        assertEquals(returnStatus,status);
    }
    
}
