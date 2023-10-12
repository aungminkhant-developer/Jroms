package gp.pyinsa.jroms.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.constants.NotificationType;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.NotificationMessage;
import gp.pyinsa.jroms.repositories.CandidateRepository;
import gp.pyinsa.jroms.repositories.JobDetailRepository;
import gp.pyinsa.jroms.repositories.NotificationMessageRepository;

@Service
public class NotificationMessageServiceImpl implements NotificationMessageService {

    @Autowired
    private NotificationMessageRepository notificationMessageRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private JobDetailRepository jobDetailRepository;

    @Override
    public NotificationMessage saveNotification(NotificationMessage notificationMessage) {
        List<Candidate> candidates = candidateRepository.findByJobDetailOrderByCreatedDateDesc(notificationMessage.getJobDetail());
        if(candidates.size() > 0) {
            notificationMessage.setCandidateId(candidates.get(0).getId());
        }
        NotificationMessage savedNotification = notificationMessageRepository.saveAndFlush(notificationMessage);
        return savedNotification;
    }

    @Override
    public List<NotificationMessage> getCandidateApplicationNotifications() {
        return notificationMessageRepository.findByNotificationType(NotificationType.CANDIDATE_APPLICATION);
    }

    @Override
    public List<NotificationMessage> getInterviewNotifications() {
        return notificationMessageRepository.findByNotificationType(NotificationType.INTERVIEW);
    }

    @Override
    public List<NotificationMessage> getTop50CandidateNotifications() {
        return notificationMessageRepository
        .findTop50ByNotificationTypeOrderByCreatedDateDesc(NotificationType.CANDIDATE_APPLICATION);
    }

    @Override
    public List<JobDetail> getTop50ActiveJobs() {
        return jobDetailRepository.findTop50ByExpireDateGreaterThan(new Date());
    }

    @Override
    public List<Candidate> getCandidatesInJobDetailByDateDesc(JobDetail jobDetail) {
        return candidateRepository.findByJobDetailOrderByCreatedDateDesc(jobDetail);
    }

    @Override
    public List<NotificationMessage> getTop50InterviewNotificationsForInterviewer(String username) {
        return notificationMessageRepository
                .findTop50ByNotificationTypeAndInterviewersUsernameOrderByCreatedDateDesc(NotificationType.INTERVIEW,
                        username);
    }

}
