package gp.pyinsa.jroms.services;

import gp.pyinsa.jroms.models.EmailTemplate;

public interface EmailTemplateService {
    
    EmailTemplate saveEmailTemplate(EmailTemplate emailTemplate);

    void editEmailTemplate(EmailTemplate emailTemplate);
}
