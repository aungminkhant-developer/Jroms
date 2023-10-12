package gp.pyinsa.jroms.services;

import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.VerificationToken;

public interface VerificationTokenService {
    
    void saveVerificationToken(String token,Candidate candidate);

    String validateToken(String token);

    VerificationToken getVerificationToken(String token);
}
