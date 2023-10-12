package gp.pyinsa.jroms.models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@Table(name = "teams")
public class Team extends Auditable {

    @Id
    @NotBlank(message = "{team.valid.name.notblank}")
    @Length(max = 8, message = "{team.valid.name.length}")
    private String id;

    @NotBlank(message = "{team.valid.name.notblank}")
    @Length(max = 100, message = "{team.valid.name.length}")
    private String name;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_leader_id", referencedColumnName = "id", nullable = true)
    private User teamLeader;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "departments_id", referencedColumnName = "id")
    // @NotNull(message = "{team.valid.department.notnull}")
    private Department department;

    private Status status = Status.ACTIVE; 
}
