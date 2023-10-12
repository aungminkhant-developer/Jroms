package gp.pyinsa.jroms.controllers;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import gp.pyinsa.jroms.constants.EmailRole;
import gp.pyinsa.jroms.constants.Mail_Status;
import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.dtos.EmailTemplateDto;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.EmailTemplate;
import gp.pyinsa.jroms.models.EmailType;
import gp.pyinsa.jroms.models.Interview;
import gp.pyinsa.jroms.services.CandidateService;
import gp.pyinsa.jroms.services.EmailTemplateService;
import gp.pyinsa.jroms.services.EmailTypeService;
import gp.pyinsa.jroms.services.InterviewService;
import gp.pyinsa.jroms.services.MailService;
import gp.pyinsa.jroms.services.VerificationTokenService;

@Controller
@RequestMapping("/mng/mail")
public class MailController {

    @Autowired
    private EmailTypeService emailTypeService;

    @Autowired
    private EmailTemplateService emailTemplateService;

    @Autowired
    private MailService mailService;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @GetMapping("/create")
    public String emailTemplate(Model model){
        model.addAttribute("emailTemplateBtn", "Create");
        return "emailTemplate";
    }

    // add email type 
    @PostMapping("/addEmailType")
    public String addEmailType(@Valid @ModelAttribute("emailType")EmailType emailType,
                                BindingResult result, Model model,RedirectAttributes redirect,
                                @RequestParam("emailRole") short emailRole){
        if(emailRole == 0){
            emailType.setRole(EmailRole.OTHERS);
        }if (emailRole == 1 ) {
            emailType.setRole(EmailRole.INTERVIEW_INVITATION);
        } if(emailRole == 2) {
            emailType.setRole(EmailRole.JOB_OFFER);
        }
        model.addAttribute("btnValue", "Add");
        model.addAttribute("emailTemplateBtn", "Create");
        if(result.hasErrors()){
            model.addAttribute("selectEmailRole", emailRole);
            return "emailTemplate";
        }
        try {
            emailTypeService.saveEmailType(emailType);
        } catch (ResourceAlreadyExistsException e) {
            model.addAttribute("selectEmailRole", emailRole);
            model.addAttribute("emailTypeObjectError", e.getMessage());
            return "emailTemplate";
        }catch(Exception e){
            model.addAttribute("selectEmailRole", emailRole);
            model.addAttribute("emailTypeObjectError", "Fail.Please try again.");
            return "emailTemplate";
        }                            
        redirect.addFlashAttribute("addEmailTypeSuccess", "Email type have created successfully");
        return "redirect:/mng/mail/create";
    }

    //delete email type 
    @PostMapping("/deleteEmailType")
    public String deleteType(@RequestParam short id,RedirectAttributes redirect){
        emailTypeService.deleteEmailTypeByStatus(id);
        redirect.addFlashAttribute("addEmailTypeSuccess", "Email type have deleted successfully");
        return "redirect:/mng/mail/create";
    }

    //to edit email type
    @PostMapping("/editEmailType")
    public String editEmailType(@Valid @ModelAttribute("emailType")EmailType emailType,
                                @RequestParam short id,BindingResult result,Model model,RedirectAttributes redirect,
                                @RequestParam("emailRole") short emailRole){
        
        emailType.setId(id);
        if(emailRole == 0){
            emailType.setRole(EmailRole.OTHERS);
        }if (emailRole == 1 ) {
            emailType.setRole(EmailRole.INTERVIEW_INVITATION);
        } if(emailRole == 2) {
            emailType.setRole(EmailRole.JOB_OFFER);
        }
        model.addAttribute("btnValue", "Update");
        model.addAttribute("emailTemplateBtn", "Create");
        if(result.hasErrors()){
            model.addAttribute("selectEmailRole", emailRole);
            return "emailTemplate";
        }
        try {
            emailTypeService.editEmailType(emailType);
        } catch (ResourceAlreadyExistsException e) {
            model.addAttribute("selectEmailRole", emailRole);
            model.addAttribute("emailTypeObjectError", e.getMessage());
            return "emailTemplate";
        }catch (Exception e){
            model.addAttribute("selectEmailRole", emailRole);
            model.addAttribute("emailTypeObjectError", "Fail.Please try again.");
            return "emailTemplate";
        } 
        redirect.addFlashAttribute("addEmailTypeSuccess", "Email type have updated successfully");                          
        return "redirect:/mng/mail/create";
    }

    @PostMapping("/addEmailTemplate")
    public String addEmailTemplate(@Valid @ModelAttribute("emailTemplate")EmailTemplate emailTemplate,
                                    @RequestParam short emailTypeId,BindingResult result,Model model){
        model.addAttribute("emailTemplateBtn", "Create");
        model.addAttribute("emailTypeId", emailTypeId);                                
        if(result.hasErrors()){
            return "emailTemplate";
        }
        EmailType emailType=emailTypeService.getEmailType(emailTypeId);
        emailTemplate.setEmailType(emailType);
        try {
            EmailTemplate emailTemplate2=emailTemplateService.saveEmailTemplate(emailTemplate);
            emailTemplate2.setBodyText(HtmlUtils.htmlUnescape(emailTemplate2.getBodyText()));
            model.addAttribute("emailTemplate",emailTemplate2);
        } catch (ResourceAlreadyExistsException e) {
            model.addAttribute("emailTemplateObjectError", e.getMessage());
            return "emailTemplate";
        } catch(Exception e){
            model.addAttribute("emailTemplateObjectError", e.getMessage());
            return "emailTemplate";
        }
        model.addAttribute("emailTemplateBtn", "Update");
        model.addAttribute("addTemplateSuccess", "Email template have created successfully");
        return "emailTemplate";
    }

    @PostMapping("/editEmailTemplate")
    public String editEmailTemplate(@Valid @ModelAttribute("emailTemplate")EmailTemplate emailTemplate,
                                     @RequestParam short emailTypeId,BindingResult result,Model model){
        model.addAttribute("emailTemplateBtn", "Update");
        model.addAttribute("emailTypeId", emailTypeId);
        if(result.hasErrors()){
            return "emailTemplate";
        }
        emailTemplateService.editEmailTemplate(emailTemplate);
        model.addAttribute("addTemplateSuccess", "Email template have updated successfully");
        return "emailTemplate";
    }

    //to send mail for job offer
    @PostMapping("/sendJobOfferMail")
    public String jobOfferMailSent(@ModelAttribute("emailTemplate") EmailTemplate emailTemplate,
            @RequestParam String adminEmail, String candidateEmail, long candidateId, Model model,
            @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments,
            @RequestParam int redirectPage,
            String nullAttachment, RedirectAttributes redirect){
            Candidate candidate=candidateService.getCandidateById(candidateId);
                String page=null;
                if(redirectPage == 0){
                    page="redirect:/mng/candidates";
                }if(redirectPage == 1){
                    page="redirect:/mng/jobOffer/"+candidate.getJobDetail().getId();
                }
            String verificationToken=UUID.randomUUID().toString();  
            String url="http://localhost:8080/accept?token="+verificationToken;
            String addLink=emailTemplate.getBodyText()+"<p><strong><em>To accept job, click below link.This link will be expired after one day.</em></p><p><em><a href=\""+url+"\">click here</a></em></strong></p>";
            emailTemplate.setBodyText(addLink);
        try {
            mailService.sendMail(emailTemplate, adminEmail, candidateEmail, attachments, nullAttachment);
        } catch (SocketTimeoutException | ConnectException e) {
            redirect.addFlashAttribute("mailException", "Connection fail !!");
            return page;
        } catch ( MessagingException|IOException e) {
            redirect.addFlashAttribute("mailException", "Failed to send email. Please try again");
            return page;
        }catch (Exception e) {
            redirect.addFlashAttribute("mailException", "Failed to send email. Please try again");
            return page;
        }
        Interview interview=interviewService.getInterviewByCandidateAndMailStatus(candidate, Mail_Status.NOT_SEND);
        if( interview != null){
            interviewService.deleteInterviewById(interview.getId());
        }
        Interview interviewNextMail=interviewService.getInterviewByCandidateAndMailStatus(candidate, Mail_Status.SEND);
        if(interviewNextMail != null){
            interviewNextMail.setMailStatus(Mail_Status.NEXT_MAIL_SEND);
            interviewService.saveInterview(interviewNextMail);
        }
        candidate.setJobOfferMailSent(true);
        candidateService.save(candidate);

        //to save jobAccept token with expire date
        verificationTokenService.saveVerificationToken(verificationToken, candidate);
        redirect.addFlashAttribute("mailSuccess", "Mail is successfully sent");
        return page;
    }

    //to get email type when change select box by fetch method
    @PostMapping("/getEmailType")
    public ResponseEntity<EmailTemplateDto> getEmailType(@RequestBody short typeId){
        EmailType emailType=emailTypeService.getEmailType(typeId);
        if(emailType.getEmailTemplate() == null){
             return ResponseEntity.ok(new EmailTemplateDto());
        }
        else{
             EmailTemplateDto eTemplateDto=new EmailTemplateDto(emailType.getEmailTemplate().getId(),
                                                emailType.getEmailTemplate().getSubject(), 
                                                HtmlUtils.htmlUnescape(emailType.getEmailTemplate().getBodyText()),
                                                emailType.getName());
             return ResponseEntity.ok(eTemplateDto);
        }      
    }

    @ModelAttribute("emailType")
    public EmailType sendEmailType(){
        return new EmailType();
    }

    @ModelAttribute("emailTemplate")
    public EmailTemplate newEmailTemplate(){
        return new EmailTemplate();
    }

    @ModelAttribute("emailTypeList")
    public List<EmailType> getAllEmailTypes(){
        return emailTypeService.getAllEmailTypeByActive(Status.ACTIVE);
    }

   
    
}
