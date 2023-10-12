package gp.pyinsa.jroms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.Email;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import gp.pyinsa.jroms.constants.EmailRole;
import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.models.EmailTemplate;
import gp.pyinsa.jroms.models.EmailType;
import gp.pyinsa.jroms.repositories.EmailTypeRepository;

@SpringBootTest
public class EmailTypeServiceTest {
    
    @Mock
    private EmailTypeRepository emailTypeRepository;

    @InjectMocks
    private EmailTypeServiceImpl emailTypeServiceImpl;

    @Test
    void addNewEmailType_WhenEmailTypeIsValid_ShouldSaveEmailType(){
        //setup
        EmailTemplate emailTemplate=new EmailTemplate((short)1, "Offering", "Dear Jhon", Status.ACTIVE, new EmailType());
        EmailType emailType=new EmailType((short)1, "Job Offering Mail", Status.ACTIVE,emailTemplate,EmailRole.OTHERS);

        //mock
        when(emailTypeRepository.existsByNameAndStatusAndRole(emailType.getName(), emailType.getStatus(),emailType.getRole())).thenReturn(false);

        //Assert
        emailTypeServiceImpl.saveEmailType(emailType);
        verify(emailTypeRepository, times(1)).existsByNameAndStatusAndRole(emailType.getName(), Status.ACTIVE,emailType.getRole());
        verify(emailTypeRepository, times(1)).save(emailType);
    }

    @Test
    void getActiveEmailType_WhenCalled_ShouldReturnActiveEmailTypeList(){
        //setup
        EmailTemplate emailTemplate=new EmailTemplate((short)1, "Offering", "Dear Jhon", Status.ACTIVE, new EmailType());
        EmailType emailType1=new EmailType((short)1, "Job Offering", Status.ACTIVE, emailTemplate,EmailRole.OTHERS);
        EmailType emailType2=new EmailType((short)2, "Invitation", Status.ACTIVE, emailTemplate,EmailRole.OTHERS);
        List<EmailType> emailTypesList=List.of(emailType1,emailType2);

        //mock
        when(emailTypeRepository.findAllByStatus(Status.ACTIVE)).thenReturn(emailTypesList);

        //assert
        List<EmailType> emailTypes=emailTypeServiceImpl.getAllEmailTypeByActive(Status.ACTIVE);
        assertEquals(emailTypes.size(), emailTypesList.size());
        verify(emailTypeRepository, times(1)).findAllByStatus(Status.ACTIVE);
    }

    @Test
    void deleteEmailTypeBySorting_ShouldChangeStatusToDeleted(){
        //setup
        EmailType emailType=new EmailType((short)1, "Job Offer", Status.ACTIVE, new EmailTemplate(),EmailRole.OTHERS);
        short emailTypeId=1;

        //mock
        when(emailTypeRepository.findById(emailTypeId)).thenReturn(Optional.of(emailType));

        //assert
        emailTypeServiceImpl.deleteEmailTypeByStatus(emailTypeId);
        verify(emailTypeRepository, times(1)).findById(emailTypeId);
        verify(emailTypeRepository, times(1)).save(emailType);
        assertEquals(Status.DELETED, emailType.getStatus());
    }

    @Test
    void updateEmailType_WhenEmailTypeIsValid_ShouldUpdateEmailType(){
        //setup
        EmailType emailType=new EmailType((short)1, "Job Offering Mail", Status.ACTIVE, new EmailTemplate(),EmailRole.OTHERS);
        //for update emailType
        EmailType newEmailType=new EmailType((short)1, "Interview Invitation Mail", Status.ACTIVE, new EmailTemplate(),EmailRole.OTHERS);

        //mock
        when(emailTypeRepository.existsByNameAndStatusAndRole(emailType.getName(), Status.ACTIVE,emailType.getRole())).thenReturn(true);
        when(emailTypeRepository.findById(newEmailType.getId())).thenReturn(Optional.of(emailType));

        //assert
        emailTypeServiceImpl.editEmailType(newEmailType);
        verify(emailTypeRepository, times(1)).existsByNameAndStatusAndRole(emailType.getName(), Status.ACTIVE,emailType.getRole());
        verify(emailTypeRepository, times(1)).findById(newEmailType.getId());
        verify(emailTypeRepository, times(1)).save(emailType);
    }

    @Test
    void updateEmailType_WhenEmailTypeNameExists_ShouldThrowAlreadyExistsException(){
        //setup
        EmailType emailType=new EmailType((short)1, "Job Offering Mail", Status.ACTIVE, new EmailTemplate(),EmailRole.OTHERS);

        //mock
        when(emailTypeRepository.existsByNameAndStatusAndRole(emailType.getName(), Status.ACTIVE,emailType.getRole())).thenReturn(true);
        when(emailTypeRepository.findById(emailType.getId())).thenReturn(Optional.of(emailType));
        //assert
        assertThrows(ResourceAlreadyExistsException.class, () -> emailTypeServiceImpl.editEmailType(emailType));
        verify(emailTypeRepository, times(1)).existsByNameAndStatusAndRole(emailType.getName(), Status.ACTIVE,emailType.getRole());
        verify(emailTypeRepository, times(1)).findById(emailType.getId());
        verify(emailTypeRepository, times(0)).save(emailType);
    }

    @Test
    void getEmailTypeByIdAndStatus_WhenSetIdAndStatus_ShouldGetEmailType(){
        //setup
        EmailType emailType=new EmailType((short)1, "Job Offering Mail", Status.ACTIVE, new EmailTemplate(),EmailRole.OTHERS);
        short emailTypeId=1;

        //mock
        when(emailTypeRepository.findByIdAndStatus(emailTypeId, Status.ACTIVE)).thenReturn(emailType);

        //assert
        EmailType newEmailType=emailTypeServiceImpl.getEmailType(emailTypeId);
        verify(emailTypeRepository, times(1)).findByIdAndStatus(emailTypeId, Status.ACTIVE);
        assertEquals(newEmailType,emailType);

    }
}
