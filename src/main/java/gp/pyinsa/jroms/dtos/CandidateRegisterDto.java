package gp.pyinsa.jroms.dtos;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateRegisterDto {
    @Id
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

    private String expected_salary;
    
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
    private String appliedPosition;

    @Length(max = 45, message = "{candidate.valid.level.length}")
    @NotBlank(message = "{candidate.valid.level.notblank}")
    private String level;
    //need to add constraint so that dob cannot be lesser than 16 years old
    private String dob;
}
