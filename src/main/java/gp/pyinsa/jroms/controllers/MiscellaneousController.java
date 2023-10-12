package gp.pyinsa.jroms.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.models.InterviewStage;
import gp.pyinsa.jroms.models.JobLevel;
import gp.pyinsa.jroms.models.JobTitle;
import gp.pyinsa.jroms.models.JobType;
import gp.pyinsa.jroms.services.InterviewStageService;
import gp.pyinsa.jroms.services.JobLevelService;
import gp.pyinsa.jroms.services.JobTitleService;
import gp.pyinsa.jroms.services.JobTypeService;

@Controller
@RequestMapping("/mng")
@PreAuthorize("hasRole('ADMIN') or hasRole('HR_SENIOR') or hasRole('DEPARTMENT_HEAD') or hasRole('TEAM_LEADER')")
public class MiscellaneousController {

    @Autowired
    private JobTitleService jobTitleService;

    @Autowired
    private JobLevelService jobLevelService;

    @Autowired
    private JobTypeService jobTypeService;

    @Autowired
    private InterviewStageService interviewStageService;

    @GetMapping("/miscellaneous")
    public String miscellaneousPage() {
        return "miscellaneous";
    }

    // Job Title Management [CUD]
    @PostMapping("/miscellaneous/job-titles/add")
    public String addJobTitle(@Valid @ModelAttribute("newJobTitle") JobTitle newJobTitle, BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "miscellaneous";
        }

        try {
            jobTitleService.addNewJobTitle(newJobTitle);
        } catch (ResourceAlreadyExistsException e) {
            result.addError(new ObjectError("newJobTitle", e.getMessage()));
            return "miscellaneous";
        }

        redirectAttributes.addFlashAttribute("jobTitleAddSuccess", "Job title is added successfully.");
        return "redirect:/mng/miscellaneous";
    }

    @PostMapping("/miscellaneous/job-titles/update")
    public String updateJobTitle(@Valid @ModelAttribute("updateJobTitle") JobTitle updateJobTitle,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "miscellaneous";
        }

        try {
            jobTitleService.updateJobTitle(updateJobTitle);
        } catch (ResourceAlreadyExistsException e) {
            result.addError(new ObjectError("updateJobTitle", e.getMessage()));
            return "miscellaneous";
        }

        redirectAttributes.addFlashAttribute("jobTitleUpdateSuccess", "Job title is updated successfully.");
        return "redirect:/mng/miscellaneous";
    }

    @PostMapping("/miscellaneous/job-titles/delete")
    public String deleteJobTitle(@RequestParam("id") short id, RedirectAttributes redirectAttributes) {
        jobTitleService.deleteJobTitleById(id);
        redirectAttributes.addFlashAttribute("jobTitleDeleteSuccess", "Job title is deleted successfully.");
        return "redirect:/mng/miscellaneous";
    }

    // Job Level Management [CUD]
    @PostMapping("/miscellaneous/job-levels/add")
    public String addJobLevel(@Valid @ModelAttribute("newJobLevel") JobLevel newJobLevel, BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "miscellaneous";
        }

        try {
            jobLevelService.addNewJobLevel(newJobLevel);
        } catch (ResourceAlreadyExistsException e) {
            result.addError(new ObjectError("newJobLevel", e.getMessage()));
            return "miscellaneous";
        }

        redirectAttributes.addFlashAttribute("jobLevelAddSuccess", "Job level is added successfully.");
        return "redirect:/mng/miscellaneous";
    }

    @PostMapping("/miscellaneous/job-levels/update")
    public String updateJobLevel(@Valid @ModelAttribute("updateJobLevel") JobLevel updateJobLevel,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "miscellaneous";
        }

        try {
            jobLevelService.updateJobLevel(updateJobLevel);
        } catch (ResourceAlreadyExistsException e) {
            result.addError(new ObjectError("updateJobLevel", e.getMessage()));
            return "miscellaneous";
        }

        redirectAttributes.addFlashAttribute("jobLevelUpdateSuccess", "Job level is updated successfully.");
        return "redirect:/mng/miscellaneous";
    }

    @PostMapping("/miscellaneous/job-levels/delete")
    public String deleteJobLevel(@RequestParam("id") short id, RedirectAttributes redirectAttributes) {
        jobLevelService.deleteJobLevelById(id);
        redirectAttributes.addFlashAttribute("jobLevelDeleteSuccess", "Job level is deleted successfully.");
        return "redirect:/mng/miscellaneous";
    }

    // Job Type Management [CUD]
    @PostMapping("/miscellaneous/job-types/add")
    public String addJobType(@Valid @ModelAttribute("newJobType") JobType newJobType, BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "miscellaneous";
        }

        try {
            jobTypeService.addNewJobType(newJobType);
        } catch (ResourceAlreadyExistsException e) {
            result.addError(new ObjectError("newJobType", e.getMessage()));
            return "miscellaneous";
        }

        redirectAttributes.addFlashAttribute("jobTypeAddSuccess", "Job type is added successfully.");
        return "redirect:/mng/miscellaneous";
    }

    @PostMapping("/miscellaneous/job-types/update")
    public String updateJobType(@Valid @ModelAttribute("updateJobType") JobType updateJobType,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "miscellaneous";
        }

        try {
            jobTypeService.updateJobType(updateJobType);
        } catch (ResourceAlreadyExistsException e) {
            result.addError(new ObjectError("updateJobType", e.getMessage()));
            return "miscellaneous";
        }

        redirectAttributes.addFlashAttribute("jobTypeUpdateSuccess", "Job type is updated successfully.");
        return "redirect:/mng/miscellaneous";
    }

    @PostMapping("/miscellaneous/job-types/delete")
    public String deleteJobType(@RequestParam("id") short id, RedirectAttributes redirectAttributes) {
        jobTypeService.deleteJobTypeById(id);
        redirectAttributes.addFlashAttribute("jobTypeDeleteSuccess", "Job type is deleted successfully.");
        return "redirect:/mng/miscellaneous";
    }

    // Interview stage Management [CUD]
    @PostMapping("/miscellaneous/interview-stages/add")
    public String addInterviewStage(@Valid @ModelAttribute("newInterviewStage") InterviewStage newInterviewStage,
            BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "miscellaneous";
        }

        try {
            interviewStageService.addNewInterviewStage(newInterviewStage);
        } catch (ResourceAlreadyExistsException e) {
            result.addError(new ObjectError("newInterviewStage", e.getMessage()));
            return "miscellaneous";
        }

        redirectAttributes.addFlashAttribute("interviewStageAddSuccess", "Interview stage is added successfully.");
        return "redirect:/mng/miscellaneous";
    }

    @PostMapping("/miscellaneous/interview-stages/update")
    public String updateInterviewStage(
            @Valid @ModelAttribute("updateInterviewStage") InterviewStage updateInterviewStage,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "miscellaneous";
        }

        try {
            interviewStageService.updateInterviewStage(updateInterviewStage);
        } catch (ResourceAlreadyExistsException e) {
            result.addError(new ObjectError("updateInterviewStage", e.getMessage()));
            return "miscellaneous";
        }

        redirectAttributes.addFlashAttribute("interviewStageUpdateSuccess", "Interview stage is updated successfully.");
        return "redirect:/mng/miscellaneous";
    }

    @PostMapping("/miscellaneous/interview-stages/delete")
    public String deleteInterviewStage(@RequestParam("id") short id, RedirectAttributes redirectAttributes) {
        interviewStageService.deleteInterviewStageById(id);
        redirectAttributes.addFlashAttribute("interviewStageDeleteSuccess", "Interview stage is deleted successfully.");
        return "redirect:/mng/miscellaneous";
    }

    // Load the data for miscellaneous and models for create and update
    @ModelAttribute("newJobTitle")
    public JobTitle newJobTitle() {
        return new JobTitle();
    }

    @ModelAttribute("updateJobTitle")
    public JobTitle updateJobTitle() {
        return new JobTitle();
    }

    @ModelAttribute("jobTitles")
    public List<JobTitle> jobTitles() {
        return jobTitleService.getActiveJobTitles();
    }

    @ModelAttribute("newJobLevel")
    public JobLevel newJobLevel() {
        return new JobLevel();
    }

    @ModelAttribute("updateJobLevel")
    public JobLevel updateJobLevel() {
        return new JobLevel();
    }

    @ModelAttribute("jobLevels")
    public List<JobLevel> jobLevels() {
        return jobLevelService.getActiveJobLevels();
    }

    @ModelAttribute("newJobType")
    public JobType newJobType() {
        return new JobType();
    }

    @ModelAttribute("updateJobType")
    public JobType updateJobType() {
        return new JobType();
    }

    @ModelAttribute("jobTypes")
    public List<JobType> jobTypes() {
        return jobTypeService.getActiveJobTypes();
    }

    @ModelAttribute("newInterviewStage")
    public InterviewStage newInterviewStage() {
        return new InterviewStage();
    }

    @ModelAttribute("updateInterviewStage")
    public InterviewStage updateInterviewStage() {
        return new InterviewStage();
    }

    @ModelAttribute("interviewStages")
    public List<InterviewStage> interviewStages() {
        return interviewStageService.getActiveInterviewStages();
    }
}
