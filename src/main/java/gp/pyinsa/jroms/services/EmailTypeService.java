package gp.pyinsa.jroms.services;

import java.util.List;

import gp.pyinsa.jroms.constants.EmailRole;
import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.models.EmailType;

public interface EmailTypeService {
    
    void saveEmailType(EmailType type);

    void editEmailType(EmailType type);

    void deleteEmailTypeByStatus( short id);

    List<EmailType> getAllEmailTypeByActive(Status status);

    EmailType getEmailType(short id);

    List<EmailType> getAllEmailTypeByActiveAndEmailRole(Status status,EmailRole role);
}
