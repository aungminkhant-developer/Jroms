package gp.pyinsa.jroms.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.util.HtmlUtils;

import gp.pyinsa.jroms.constants.Status;
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
@Table(name = "job_details")
public class JobDetail extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_titles_id", referencedColumnName = "id")
    @Fetch(FetchMode.JOIN)
    @NotNull(message = "{job_details.valid.jobtitle.notnull}")
    private JobTitle jobTitle;

    @ManyToOne
    @JoinColumn(name = "job_levels_id", referencedColumnName = "id")
    private JobLevel jobLevel;

    @Min(value = 1, message = "{job_details.valid.posts.min}")
    private Short posts;

    @Length(max = 30, message = "{job_details.valid.salary.length}")
    @NotBlank(message = "{job_details.valid.salary.notblank}")
    private String salary;

    @ManyToOne
    @JoinColumn(name = "work_schedules_id", referencedColumnName = "id")
    @NotNull(message = "{job_details.valid.workschedule.notnull}")
    private WorkSchedule workSchedule;

    @ManyToOne
    @JoinColumn(name = "job_locations_id", referencedColumnName = "id")
    @NotNull(message = "{job_details.valid.location.notnull}")
    private Location location;

    @ManyToOne
    @JoinColumn(name = "job_types_id", referencedColumnName = "id")
    @NotNull(message = "{job_details.valid.jobtype.notnull}")
    private JobType jobType;

    @ManyToOne
    @JoinColumn(name = "teams_id", referencedColumnName = "id")
    @NotNull(message = "{job_details.valid.team.notnull}")
    private Team team;

    @Length(max = 21000, message = "{job_details.valid.description.length}")
    @NotBlank(message = "{job_details.valid.description.notblank}")
    private String description;

    @Length(max = 21000, message = "{job_details.valid.responsibilities.length}")
    @NotBlank(message = "{job_details.valid.responsibilities.notblank}")
    private String responsibilities;

    @Length(max = 21000, message = "{job_details.valid.requirement.length}")
    @NotBlank(message = "{job_details.valid.requirement.notblank}")
    private String requirement;

    @Length(max = 21000, message = "{job_details.valid.preferences.length}")
    private String preferences;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "open_date")
    @NotNull(message = "{job_details.valid.opendate.notnull}")
    private Date openDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expire_date")
    @NotNull(message = "{job_details.valid.expiredate.notnull}")
    private Date expireDate;

    private Status status = Status.ACTIVE;


    public void escapeHTML() {
        this.description = HtmlUtils.htmlEscape(description);
        this.responsibilities = HtmlUtils.htmlEscape(responsibilities);
        this.requirement = HtmlUtils.htmlEscape(requirement);

        if(this.preferences != null) {
            this.preferences = HtmlUtils.htmlEscape(preferences);
        }
    }

    public void unescapeHTML() {
        this.description = HtmlUtils.htmlUnescape(description);
        this.responsibilities = HtmlUtils.htmlUnescape(responsibilities);
        this.requirement = HtmlUtils.htmlUnescape(requirement);

        if(this.preferences != null) {
            this.preferences = HtmlUtils.htmlUnescape(preferences);
        }
    }

    public JobDetail(Long id, @NotNull(message = "{job_details.valid.jobtitle.notnull}") JobTitle jobTitle,
            JobLevel jobLevel, @Min(value = 1, message = "{job_details.valid.posts.min}") Short posts,
            @Length(max = 30, message = "{job_details.valid.salary.length}") @NotBlank(message = "{job_details.valid.salary.notblank}") String salary,
            WorkSchedule workSchedule, Location location, JobType jobType, Team team,
            @Length(max = 21000, message = "{job_details.valid.description.length}") @NotBlank(message = "{job_details.valid.description.notblank}") String description,
            @Length(max = 21000, message = "{job_details.valid.responsibilities.length}") @NotBlank(message = "{job_details.valid.responsibilities.notblank}") String responsibilities,
            @Length(max = 21000, message = "{job_details.valid.requirement.length}") @NotBlank(message = "{job_details.valid.requirement.notblank}") String requirement,
            @Length(max = 21000, message = "{job_details.valid.preferences.length}") String preferences, Date openDate,
            Date expireDate) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.jobLevel = jobLevel;
        this.posts = posts;
        this.salary = salary;
        this.workSchedule = workSchedule;
        this.location = location;
        this.jobType = jobType;
        this.team = team;
        this.description = description;
        this.responsibilities = responsibilities;
        this.requirement = requirement;
        this.preferences = preferences;
        this.openDate = openDate;
        this.expireDate = expireDate;
    }

    

}
