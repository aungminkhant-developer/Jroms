package gp.pyinsa.jroms.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import gp.pyinsa.jroms.auditing.AuditorAwareImpl;
import gp.pyinsa.jroms.models.User;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {
    @Bean
    AuditorAware<User> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
