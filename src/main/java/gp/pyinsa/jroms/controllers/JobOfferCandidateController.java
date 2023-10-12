package gp.pyinsa.jroms.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import gp.pyinsa.jroms.constants.EmailRole;
import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.CurriculumVitae;
import gp.pyinsa.jroms.models.EmailTemplate;
import gp.pyinsa.jroms.models.EmailType;
import gp.pyinsa.jroms.models.Interview;
import gp.pyinsa.jroms.services.CVService;
import gp.pyinsa.jroms.services.CandidateService;
import gp.pyinsa.jroms.services.EmailTypeService;
import gp.pyinsa.jroms.services.InterviewService;

@Controller
@RequestMapping("/mng/jobOffer")
public class JobOfferCandidateController {
    @Autowired
    private CandidateService candidateService;

    @Autowired
    private CVService cVService;

    @Autowired
    private EmailTypeService emailTypeService;
    
    @GetMapping("/{jobDetailId}")
    public String getCandidatesByJobDetailId(@PathVariable Long jobDetailId, Model model) {
        List<Candidate> candidates = candidateService.getAllCandidateByJobDetailId(jobDetailId);
        model.addAttribute("candidates", candidates);
        model.addAttribute("jobDetailId", jobDetailId);
        return "job_offer_candidate"; // Return the name of the view template
    }

    @PostMapping("/addCVStatus")
    public ResponseEntity<String> addCVStatus(@RequestBody CurriculumVitae curriculumVitae) {
        CurriculumVitae curriculumVitae1 = cVService.getCurriculumVitaeById(curriculumVitae.getId());
        curriculumVitae1.setStatus(curriculumVitae.getStatus());
        cVService.save(curriculumVitae1);
        return ResponseEntity.ok("success");
    }

 @ModelAttribute("emailTemplate")
    public EmailTemplate getNewEmailTemplate() {
        return new EmailTemplate();
    }

    @ModelAttribute("emailTypeListByJobOffer")
    public List<EmailType> getEmailTypesByJobOffer(){
        return emailTypeService.getAllEmailTypeByActiveAndEmailRole(Status.ACTIVE, EmailRole.JOB_OFFER);
    }
}
