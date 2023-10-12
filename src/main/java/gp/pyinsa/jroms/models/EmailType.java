package gp.pyinsa.jroms.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import gp.pyinsa.jroms.constants.EmailRole;
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
@Table(name = "email_types")
@Entity
public class EmailType extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Length(max = 45,message = "Email type name cannot exceed 45 characters")
    @NotBlank(message = "Email type name is required")
    private String name;

    private Status status=Status.ACTIVE;

    @OneToOne(mappedBy = "emailType")
    private EmailTemplate emailTemplate;

    private EmailRole role;

    
}
