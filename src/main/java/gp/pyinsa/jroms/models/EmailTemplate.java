package gp.pyinsa.jroms.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "email_templates")
@Entity
public class EmailTemplate extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private short id;

    @Column(name = "subject_text")
    @Length(max = 200,message = "Subject must have within 200 characters")
    @NotBlank(message = "Subject is required")
    private String subject;

    @Column(name = "body_text")
    @NotBlank(message = "Message is required")
    private String bodyText;

    private Status status=Status.ACTIVE;

    @OneToOne
    @JoinColumn(name = "email_types_id")
    private EmailType emailType;
    
}
