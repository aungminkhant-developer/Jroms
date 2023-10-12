package gp.pyinsa.jroms.models;

import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import gp.pyinsa.jroms.constants.Interview_Result;
import gp.pyinsa.jroms.constants.Interview_Status;
import gp.pyinsa.jroms.constants.Mail_Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "interviews")
public class Interview extends Auditable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "status")
        private Interview_Status status = Interview_Status.ONGOING;

        @Column(name = "result")
        private Interview_Result result = Interview_Result.NOT_REACHED;

        @Column(name = "mail_status")
        private Mail_Status mailStatus = Mail_Status.NOT_SEND;

        @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
                        CascadeType.REFRESH }, fetch = FetchType.EAGER)
        @JoinColumn(name = "candidates_id", referencedColumnName = "id")
        private Candidate candidate;

        @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
                        CascadeType.REFRESH }, fetch = FetchType.EAGER)
        @JoinColumn(name = "interview_stages_id", referencedColumnName = "id")
        private InterviewStage interviewStage;

        @OneToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "interview_schedule_id", referencedColumnName = "id")
        private InterviewSchedule interviewSchedule;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "job_details_id", referencedColumnName = "id")
        private JobDetail jobDetail;

        @ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
                        CascadeType.REFRESH }, fetch = FetchType.LAZY)
        @JoinTable(name = "interviewer", joinColumns = { @JoinColumn(name = "interviews_id") }, inverseJoinColumns = {
                        @JoinColumn(name = "interviewer_id") })
        private List<User> interviewerList;

}
