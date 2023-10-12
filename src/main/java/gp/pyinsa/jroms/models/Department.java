package gp.pyinsa.jroms.models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

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
@Table(name = "departments")
public class Department extends Auditable {

    @Id
    @NotBlank(message = "{department.valid.id.notblank}")
    @Length(max = 8, message = "{department.valid.id.length}")
    private String id;

    @NotBlank(message = "{department.valid.name.notblank}")
    @Length(max = 100, message = "{department.valid.name.length}")
    private String name;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_head_id", referencedColumnName = "id")
    private User departmentHead;

    private Status status = Status.ACTIVE;

}
