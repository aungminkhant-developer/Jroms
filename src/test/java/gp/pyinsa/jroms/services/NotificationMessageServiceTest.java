package gp.pyinsa.jroms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import gp.pyinsa.jroms.constants.NotificationType;
import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.JobTitle;
import gp.pyinsa.jroms.models.NotificationMessage;
import gp.pyinsa.jroms.repositories.CandidateRepository;
import gp.pyinsa.jroms.repositories.JobDetailRepository;
import gp.pyinsa.jroms.repositories.NotificationMessageRepository;

@SpringBootTest
public class NotificationMessageServiceTest {

    @Mock
    private NotificationMessageRepository notificationMessageRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private JobDetailRepository jobDetailRepository;

    @InjectMocks
    private NotificationMessageServiceImpl notificationMessageService;

    @Test
    void saveNotification_WhenNoCandidate_ShouldSave() {
        NotificationMessage msg = new NotificationMessage();
        msg.setId(1L);
        msg.setCandidateId(2L);
        msg.setCreatedDate(new Date());
        msg.setMessage("This is a message.");
        msg.setInterviewers(List.of());
        JobDetail jobDetail = new JobDetail();
        jobDetail.setCreatedDate(new Date());
        jobDetail.setJobTitle(new JobTitle((short) 1, "Java Developer", Status.ACTIVE));
        msg.setJobDetail(jobDetail);
        msg.setNotificationType(NotificationType.CANDIDATE_APPLICATION);

        when(candidateRepository.findByJobDetailOrderByCreatedDateDesc(msg.getJobDetail())).thenReturn(List.of());
        when(notificationMessageRepository.saveAndFlush(msg)).thenReturn(msg);

        NotificationMessage savedNotification = notificationMessageService.saveNotification(msg);
        assertEquals(msg.getId(), savedNotification.getId());
        verify(candidateRepository, times(1)).findByJobDetailOrderByCreatedDateDesc(msg.getJobDetail());
        verify(notificationMessageRepository, times(1)).saveAndFlush(msg);
    }

    @Test
    void saveNotification_WhenCandidateExists_ShouldSave() {
        NotificationMessage msg = new NotificationMessage();
        msg.setId(1L);
        msg.setCandidateId(2L);
        msg.setCreatedDate(new Date());
        msg.setMessage("This is a message.");
        msg.setInterviewers(List.of());
        JobDetail jobDetail = new JobDetail();
        jobDetail.setCreatedDate(new Date());
        jobDetail.setJobTitle(new JobTitle((short) 1, "Java Developer", Status.ACTIVE));
        msg.setJobDetail(jobDetail);
        msg.setNotificationType(NotificationType.CANDIDATE_APPLICATION);

        Candidate cand = new Candidate();
        cand.setId(1L);

        when(candidateRepository.findByJobDetailOrderByCreatedDateDesc(msg.getJobDetail())).thenReturn(List.of(cand));
        when(notificationMessageRepository.saveAndFlush(msg)).thenReturn(msg);

        NotificationMessage savedNotification = notificationMessageService.saveNotification(msg);
        assertEquals(msg.getId(), savedNotification.getId());
        assertEquals(cand.getId(), savedNotification.getCandidateId());
        verify(candidateRepository, times(1)).findByJobDetailOrderByCreatedDateDesc(msg.getJobDetail());
        verify(notificationMessageRepository, times(1)).saveAndFlush(msg);
    }

}
