package gp.pyinsa.jroms.services;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.web.multipart.MultipartFile;

import gp.pyinsa.jroms.models.EmailTemplate;

public interface MailService {
    
    public void sendMail(EmailTemplate emailTemplate,String adminMail,
                         String candidateMail,List<MultipartFile> attachmets,String nullAttachment)throws MessagingException, IOException  ;


    public void jobAcceptMail(String emailTemplate,
                                String candidateMail,String departmentMail,String teamMail)throws MessagingException, IOException  ;
 }

