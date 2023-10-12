package gp.pyinsa.jroms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import gp.pyinsa.jroms.constants.EmailRole;
import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.models.EmailTemplate;
import gp.pyinsa.jroms.models.EmailType;
import gp.pyinsa.jroms.repositories.EmailTemplateRepository;

@SpringBootTest
public class EmailTemplateServiceTest {

    @Mock
    private EmailTemplateRepository emailTemplateRepository;

    @InjectMocks
    private EmailTemplateServiceImpl emailTemplateServiceImpl;

    @Test
    void addNewEmailTemplate_WhenEmailTemplateIsValid_ShouldSaveEmailTemplate(){
        //setup
        EmailType emailType=new EmailType((short)1, "Job Offering Mail", Status.ACTIVE, null,EmailRole.OTHERS);
        EmailTemplate emailTemplate=new EmailTemplate((short)1, "Job Offering", "Dear Ko Zin", Status.ACTIVE, emailType);

        //mock
        when(emailTemplateRepository.existsByEmailType(emailTemplate.getEmailType())).thenReturn(false);

        //assert
        emailTemplateServiceImpl.saveEmailTemplate(emailTemplate);
        verify(emailTemplateRepository, times(1)).existsByEmailType(emailTemplate.getEmailType());
        verify(emailTemplateRepository, times(1)).save(emailTemplate);
    }

     @Test
    void addNewEmailTemplate_WhenEmailTypeIsDuplicated_ShouldThrowResourceAlreadyExistException(){
        //setup
        EmailType emailType=new EmailType((short)1, "Job Offering Mail", Status.ACTIVE, new EmailTemplate(),EmailRole.OTHERS);
        EmailTemplate emailTemplate=new EmailTemplate((short)1, "Job Offering", "Dear Ko Zin", Status.ACTIVE, emailType);

        //mock
        when(emailTemplateRepository.existsByEmailType(emailTemplate.getEmailType())).thenReturn(true);

        //assert
        assertThrows(ResourceAlreadyExistsException.class,()-> emailTemplateServiceImpl.saveEmailTemplate(emailTemplate));
        verify(emailTemplateRepository, times(1)).existsByEmailType(emailTemplate.getEmailType());
    }

    @Test
    void updateEmailTemplate_ShouldUpdateEmailTemplate(){
        //setup
        EmailType emailType=new EmailType((short) 1,"Job Offering Mail",Status.ACTIVE,new EmailTemplate(),EmailRole.INTERVIEW_INVITATION);
        EmailTemplate emailTemplate=new EmailTemplate((short)1,"Job Offering","Dear Ko Zin",Status.ACTIVE,emailType);
        EmailTemplate newEmailTemplate=new EmailTemplate((short)1, "Offering", "Hello Ko Zin", Status.ACTIVE,emailType);

        //mock
        when(emailTemplateRepository.findById(newEmailTemplate.getId())).thenReturn(Optional.of(emailTemplate));

        //Assert
        emailTemplateServiceImpl.editEmailTemplate(newEmailTemplate);
        verify(emailTemplateRepository, times(1)).findById(newEmailTemplate.getId());
        verify(emailTemplateRepository, times(1)).save(emailTemplate);
        assertEquals(newEmailTemplate.getSubject()  , emailTemplate.getSubject());
        assertEquals(newEmailTemplate.getBodyText(), emailTemplate.getBodyText());
        assertEquals(newEmailTemplate.getEmailType(),emailTemplate.getEmailType());
    }
    
}
