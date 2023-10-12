package gp.pyinsa.jroms.services;

import java.util.List;

import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.NotificationMessage;

public interface NotificationMessageService {
    NotificationMessage saveNotification(NotificationMessage notificationMessage);

    List<NotificationMessage> getCandidateApplicationNotifications();
    
    List<NotificationMessage> getInterviewNotifications();

    List<NotificationMessage> getTop50CandidateNotifications();

    List<JobDetail> getTop50ActiveJobs();

    List<Candidate> getCandidatesInJobDetailByDateDesc(JobDetail jobDetail);

    List<NotificationMessage> getTop50InterviewNotificationsForInterviewer(String username);
}
