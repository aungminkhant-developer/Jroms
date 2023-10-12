package gp.pyinsa.jroms.auditing;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import gp.pyinsa.jroms.models.AppUserDetails;
import gp.pyinsa.jroms.models.User;
import gp.pyinsa.jroms.repositories.UserRepository;

public class AuditorAwareImpl implements AuditorAware<User> {

    @Autowired
    UserRepository userRepository;

    @Override
    public Optional<User> getCurrentAuditor() {

        try {
            AppUserDetails userDetails = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            return Optional.of(userDetails.getUser());
        } catch (Exception exception) {
            // return Optional.of(userRepository.findByRoleName("ROLE_ADMIN").get(0));
            User user = new User();
            user.setId("U001");
            return Optional.of(user);
        }
    }

}