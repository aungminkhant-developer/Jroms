package gp.pyinsa.jroms.services;


import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import gp.pyinsa.jroms.models.EmailTemplate;

@Service
public class MailServiceImpls implements MailService{

    @Autowired
    private JavaMailSender mailSender;

    public MailServiceImpls(JavaMailSender mailSender){
        this.mailSender=mailSender;
    }

    @Override
    public void sendMail(EmailTemplate emailTemplate, String adminMail, String candidateMail,
            List<MultipartFile> attachmets,String nullAttachment) throws MessagingException, IOException  {
            System.out.println("test :"+nullAttachment);
            MimeMessage message=mailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message, true);
            helper.setFrom(adminMail, "ACE company");
            helper.setTo(candidateMail);
            helper.setSubject(emailTemplate.getSubject());
            helper.setText(emailTemplate.getBodyText(), true);

            if(nullAttachment.equals("NotNull")){
                for(MultipartFile file: attachmets){
                    InputStreamSource inputStreamSource=new ByteArrayResource(file.getBytes());
                    helper.addAttachment(file.getOriginalFilename(), inputStreamSource);
                }
            }
            mailSender.send(message);
       
    }

    @Override
    public void jobAcceptMail(String emailTemplate, String candidateMail,String departmentMail,String teamMail)
            throws MessagingException, IOException {
        String[] toMailList={"acecompany771@gmail.com",departmentMail,teamMail};
        MimeMessage message=mailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(message);
        helper.setFrom(candidateMail);
        helper.setTo(toMailList);
        helper.setSubject("Job accepted from candidate");
        helper.setText(emailTemplate, true);
        mailSender.send(message);
    }

    
}
