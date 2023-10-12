package gp.pyinsa.jroms.models;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
// import javax.persistence.JoinColumn;
// import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import gp.pyinsa.jroms.constants.Interview_Result;
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
@Table(name = "candidates")
public class Candidate extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(max = 45, message = "{candidate.valid.name.length}")
    @NotBlank(message = "{candidate.valid.name.notblank}")
    private String name;

    @Length(max = 15, message = "{candidate.valid.phone_number.length}")
    @NotBlank(message = "{candidate.valid.phone_number.notblank}")
    private String phone_number;

    @Length(max = 255, message = "{candidate.valid.email.length}")
    @NotBlank(message = "{candidate.valid.email.notblank}")
    @Email(message = "{candidate.valid.email.format}")
    private String email;

    @Length(max = 255, message = "{candidate.valid.address.length}")
    @NotBlank(message = "{candidate.valid.address.notblank}")
    private String address;

    private BigDecimal expected_salary;

    @Length(max = 65535, message = "{candidate.valid.experience.length}")
    @NotBlank(message = "{candidate.valid.experience.notblank}")
    private String experience;

    @Length(max = 65535, message = "{candidate.valid.education.length}")
    @NotBlank(message = "{candidate.valid.education.notblank}")
    private String education;

    @Length(max = 1, message = "{candidate.valid.sex.length}")
    @NotBlank(message = "{candidate.valid.sex.notblank}")
    private String sex;

    @Length(max = 400, message = "{candidate.valid.technical_skills.length}")
    @NotBlank(message = "{candidate.valid.technical_skills.notblank}")
    private String technical_skills;

    @Length(max = 255, message = "{candidate.valid.language_skills.length}")
    private String language_skills;

    @Length(max = 255, message = "{candidate.valid.main_skill.length}")
    @NotBlank(message = "{candidate.valid.main_skill.notblank}")
    private String main_skill;

    @Length(max = 255, message = "{candidate.valid.applied_position.length}")
    @NotBlank(message = "{candidate.valid.applied_position.notblank}")
    @Column(name = "applied_position")
    private String appliedPosition;

    @Length(max = 45, message = "{candidate.valid.level.length}")
    @NotBlank(message = "{candidate.valid.level.notblank}")
    private String level;

    // need to add constraint so that dob cannot be lesser than 16 years old
    private Date dob;

    // need to add max length
    @ColumnDefault(value = "0")
    private Byte status = 0;

    @Column(name = "job_offer_mail_sent")
    private Boolean jobOfferMailSent = false;

    //add new field interview_finale result
    @Column(name = "interview_final_result")
    private Interview_Result finalResult=Interview_Result.NOT_REACHED;
    
    @ManyToOne
    @JoinColumn(name = "job_details_id", nullable = false)
    @Fetch(FetchMode.JOIN)
    private JobDetail jobDetail;

    
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "curriculum_vitae_id")
    @NotNull(message = "{cv.valid.candidates_id.notnull}")
    private CurriculumVitae curriculumVitae;
    
    @Column(name = "job_accepted")
    private boolean jobAccepted=false;
}
