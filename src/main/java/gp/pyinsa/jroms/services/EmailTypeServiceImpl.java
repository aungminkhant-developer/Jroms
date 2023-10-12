package gp.pyinsa.jroms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.constants.EmailRole;
import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.models.EmailType;
import gp.pyinsa.jroms.repositories.EmailTypeRepository;

@Service
public class EmailTypeServiceImpl implements EmailTypeService{

    @Autowired
    private EmailTypeRepository emailTypeRepository;

    @Override
    public void saveEmailType(EmailType type) {
        if(emailTypeRepository.existsByNameAndStatusAndRole(type.getName(),Status.ACTIVE,type.getRole())){
            throw new ResourceAlreadyExistsException("Email type already exists.");
        }
        emailTypeRepository.save(type);
    }

    @Override
    public List<EmailType> getAllEmailTypeByActive(Status status) {
        return emailTypeRepository.findAllByStatus(status);
    }

    @Override
    public void deleteEmailTypeByStatus(short id) {
       EmailType type=emailTypeRepository.findById(id).get();
       type.setStatus(Status.DELETED);
       emailTypeRepository.save(type);
    }

    @Override
    public void editEmailType(EmailType type) {
        EmailType emailType=emailTypeRepository.findById(type.getId()).get();
        if(emailTypeRepository.existsByNameAndStatusAndRole(type.getName(), Status.ACTIVE,type.getRole())){
            throw new ResourceAlreadyExistsException("Email Type already exists");
        }
        emailType.setName(type.getName());
        emailType.setRole(type.getRole());
        emailTypeRepository.save(emailType);
    }

    @Override
    public EmailType getEmailType(short id) {
       return emailTypeRepository.findByIdAndStatus(id, Status.ACTIVE);
    }

    @Override
    public List<EmailType> getAllEmailTypeByActiveAndEmailRole(Status status, EmailRole role) {
        return emailTypeRepository.findAllByStatusAndAndRole(status, role);
    }
    
}
