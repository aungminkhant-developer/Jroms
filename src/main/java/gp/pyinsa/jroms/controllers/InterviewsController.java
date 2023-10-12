package gp.pyinsa.jroms.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import gp.pyinsa.jroms.exceptions.InterviewByCandidateNotFoundException;
import gp.pyinsa.jroms.models.Interview;
import gp.pyinsa.jroms.models.InterviewStage;
import gp.pyinsa.jroms.models.InterviewType;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.services.InterviewService;
import gp.pyinsa.jroms.services.InterviewStageService;
import gp.pyinsa.jroms.services.IntrerviewTypeService;
import gp.pyinsa.jroms.services.JobDetailService;

@Controller
@RequestMapping("/mng/interviews")
public class InterviewsController {
    @Autowired
	private JobDetailService jobDetailsService;

    @Autowired
    private InterviewService interviewService;

    @Autowired 
    private InterviewStageService interviewStageService;

    @Autowired
    private IntrerviewTypeService intrerviewTypeService;


    @GetMapping("/{jobDetailId}")
    public String getInterviewsByJobDetailId(@PathVariable Long jobDetailId, Model model) {
        List<Interview> interviewList = interviewService.getAllInterviewByJobDetailId(jobDetailId);
        List<InterviewType> interviewType=intrerviewTypeService.getAllInterviewTypes();
        List<InterviewStage> interviewStages=interviewStageService.getActiveInterviewStages();
        JobDetail jobDetail = jobDetailsService.getById(jobDetailId);
        String jobPosition = "";
        if(jobDetail.getJobLevel() != null) {
            jobPosition += jobDetail.getJobLevel().getName() + " ";
        }
        jobPosition += jobDetail.getJobTitle().getName();

        if (interviewList.isEmpty()) {
            model.addAttribute("EmptyInterviewList", true);
            throw new InterviewByCandidateNotFoundException("Interviews have not found in job detail Id " + jobDetailId);
        }

        model.addAttribute("jobPosition", jobPosition);
        model.addAttribute("interviewList", interviewList);
        model.addAttribute("interviewType", interviewType);
        model.addAttribute("interviewStages", interviewStages);
        return "jobDetail_Interview"; // Return the view name (template name) to be rendered
    }
    
}
