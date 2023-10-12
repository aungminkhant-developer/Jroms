package gp.pyinsa.jroms.models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;

import gp.pyinsa.jroms.constants.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "notification_messages")
public class NotificationMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(max = 255, message = "{notification.valid.message.length}")
    @NotBlank(message = "{notification.valid.message.notblank}")
    private String message;

    @Column(name = "type")
    private NotificationType notificationType;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate = new Date();

    @ManyToOne
    @JoinColumn(name = "job_details_id", nullable = false)
    private JobDetail jobDetail;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "notifications_users",
        joinColumns = @JoinColumn(name = "notification_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> interviewers;

    @Column(name = "candidate_id")
    private Long candidateId;
    
}
