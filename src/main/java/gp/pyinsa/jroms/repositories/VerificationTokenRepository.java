package gp.pyinsa.jroms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import gp.pyinsa.jroms.models.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long>{

    VerificationToken findByToken(String token);
    
}
