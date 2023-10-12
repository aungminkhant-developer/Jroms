package gp.pyinsa.jroms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import gp.pyinsa.jroms.models.EmailTemplate;
import gp.pyinsa.jroms.models.EmailType;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate,Short>{
    
    boolean existsByEmailType(EmailType emailType);
}
