package gp.pyinsa.jroms.services;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.VerificationToken;
import gp.pyinsa.jroms.repositories.VerificationTokenRepository;

@Service
public class VerificationTokenServiceImpls implements VerificationTokenService{

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Override
    public void saveVerificationToken(String token,Candidate candidate) {
       VerificationToken verificationToken=new VerificationToken(token, candidate);
       verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateToken(String token) {
       VerificationToken verificationToken=verificationTokenRepository.findByToken(token);
       if(token == null){
        return "Invalid verification";
       }
       Calendar calendar=Calendar.getInstance();
       if( verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0){
         return "Job accept date is expired";
       }
       return "success";
    }

   @Override
   public VerificationToken getVerificationToken(String token) {
     return verificationTokenRepository.findByToken(token);
   }
    
}
