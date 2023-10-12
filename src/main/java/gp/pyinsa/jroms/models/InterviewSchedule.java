package gp.pyinsa.jroms.models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

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
@Table(name = "interview_schedules")
public class InterviewSchedule extends Auditable{

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name="interview_date")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date interviewDate;

        @Column(name = "start_time")
        private String startTime;

        @Column(name = "end_time")
        private String endTime;
        
        @Column(name = "interview_location")
        private String interviewLocation;

        @ManyToOne(cascade= {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},
                  targetEntity = InterviewType.class)
        @JoinColumn(name="interview_types_id")
        private InterviewType interviewType;
        
       
}
