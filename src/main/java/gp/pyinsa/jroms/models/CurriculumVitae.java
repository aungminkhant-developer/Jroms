package gp.pyinsa.jroms.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import gp.pyinsa.jroms.constants.CVStatus;
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
@Table(name = "curriculum_vitae")
public class CurriculumVitae extends AuditableTriangle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(max = 255, message = "{cv.valid.cv_url.length}")
    @NotBlank(message = "{cv.valid.cv_url.notblank}")
    @Column(name = "cv_url")
    private String cvurl;

    private CVStatus status = CVStatus.RECEIVED;

}
