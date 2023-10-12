package gp.pyinsa.jroms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.models.EmailTemplate;
import gp.pyinsa.jroms.repositories.EmailTemplateRepository;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService{

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    @Override
    public EmailTemplate saveEmailTemplate(EmailTemplate emailTemplate) {
        if(emailTemplateRepository.existsByEmailType(emailTemplate.getEmailType())){
            throw new ResourceAlreadyExistsException("Email template has already exists");
        }
        emailTemplate.setBodyText(HtmlUtils.htmlEscape(emailTemplate.getBodyText()));
        return emailTemplateRepository.save(emailTemplate);
    }

    @Override
    public void editEmailTemplate(EmailTemplate emailTemplate) {
       EmailTemplate eTemplate=emailTemplateRepository.findById(emailTemplate.getId()).get();
       eTemplate.setSubject(emailTemplate.getSubject());
       eTemplate.setBodyText(HtmlUtils.htmlEscape(emailTemplate.getBodyText()));
       emailTemplateRepository.save(eTemplate);
    }
    
}
