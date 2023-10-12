package gp.pyinsa.jroms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gp.pyinsa.jroms.constants.NotificationType;
import gp.pyinsa.jroms.models.NotificationMessage;

@Repository
public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, Long> {
    List<NotificationMessage> findByJobDetail_Id(Long jobDetailId);

    List<NotificationMessage> findByNotificationType(NotificationType type);

    List<NotificationMessage> findTop50ByNotificationTypeOrderByCreatedDateDesc(NotificationType type);

    List<NotificationMessage> findTop50ByNotificationTypeAndInterviewersUsernameOrderByCreatedDateDesc(
            NotificationType interview, String username);

    @Query("SELECT n.jobDetail, COUNT(n.jobDetail), MAX(n.createdDate) AS latest_date FROM NotificationMessage AS n WHERE n.notificationType = 0 GROUP BY n.jobDetail ORDER BY latest_date DESC")
    List<Object[]> findApplicantNotificationsGroupByJobDetail();
}
