package gp.pyinsa.jroms.controllers;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gp.pyinsa.jroms.constants.Constants;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.JobLevel;
import gp.pyinsa.jroms.models.JobTitle;
import gp.pyinsa.jroms.models.JobType;
import gp.pyinsa.jroms.models.Location;
import gp.pyinsa.jroms.models.Team;
import gp.pyinsa.jroms.models.WorkSchedule;
import gp.pyinsa.jroms.services.CandidateService;
import gp.pyinsa.jroms.services.DepartmentService;
import gp.pyinsa.jroms.services.JobDetailService;
import gp.pyinsa.jroms.services.JobLevelService;
import gp.pyinsa.jroms.services.JobTitleService;
import gp.pyinsa.jroms.services.JobTypeService;
import gp.pyinsa.jroms.services.LocationService;
import gp.pyinsa.jroms.services.TeamService;
import gp.pyinsa.jroms.services.WorkScheduleService;
import gp.pyinsa.jroms.utils.DateFormatUtil;

@Controller
@RequestMapping("/mng")
public class JobDetailController {

    @Autowired
    private JobDetailService jobDetailService;

    @Autowired
    private JobTitleService jobTitleService;

    @Autowired
    private JobLevelService jobLevelService;

    @Autowired
    private WorkScheduleService workScheduleService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private JobTypeService jobTypeService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/your-job-delete-id/{id}")
    public String deleteJobOffers(@PathVariable String id, ModelMap model,RedirectAttributes redirect){
        try {
            String message = jobDetailService.updateStatus(id);
            redirect.addFlashAttribute("error", message);
            return "redirect:/mng/jobs";
        } catch (Exception e) {
            // TODO: handle exception
            redirect.addFlashAttribute("error", "error occurred while deleting");
            return "redirect:/mng/jobs";
        }
    }

    @GetMapping("/job-offers")
    public String viewJobOffers(ModelMap model) {
        List<JobDetail> jobDetail = jobDetailService.findAllJobDetails();
        List<JobTitle> jobTitles = jobTitleService.getAllJobTitles();
        model.addAttribute("jobDetail", jobDetail);
        model.addAttribute("jobTitle", jobTitles);
        return "adminViewOffer";
    }

    @GetMapping("/delete-offer")
    public String deleteJobOffer(@RequestParam Long id) {
        jobDetailService.deleteById(id);
        return "adminViewOffer";
    }

    @GetMapping("/job-details/add")
    public String addJobDetailsForm(ModelMap model) {
        JobDetail newJobDetail = new JobDetail();
        newJobDetail.setPosts((short) 1);
        // Set expire date
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, Constants.JOB_OFFER_DURATION_DAYS);
        newJobDetail.setExpireDate(cal.getTime());

        // Set min expire date
        Calendar minCal = Calendar.getInstance();
        minCal.add(Calendar.DAY_OF_MONTH, 1);
        String minExpireDate = DateFormatUtil.formatDateToString(minCal.getTime(), "yyyy-MM-dd");
        model.addAttribute("minExpireDate", minExpireDate);
        model.addAttribute("newJobDetail", newJobDetail);
        return "add-job-offer";
    }

    @PostMapping("/job-details/add")
    public String addJobDetail(ModelMap model, @Valid @ModelAttribute("newJobDetail") JobDetail newJobDetail,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "add-job-offer";
        }

        Date expireDate = newJobDetail.getExpireDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(expireDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        newJobDetail.setExpireDate(cal.getTime());

        if(newJobDetail.getPreferences().isEmpty()) {
            newJobDetail.setPreferences(null);
        }
        jobDetailService.addNewJobDetail(newJobDetail);

        redirectAttributes.addFlashAttribute("jobDetailAddSuccess", "Job offer is added successfully.");
        return "redirect:/mng/job-details/add";
    }

    @GetMapping("/job-details/{id}")
    public String updateJobDetailForm(ModelMap model, @PathVariable("id") Long id) {
        JobDetail updateJobDetail = null;
        try {
            updateJobDetail = jobDetailService.getById(id);
        } catch (Exception e) {
            // If id is invalid, go to error page
            return "redirect:/mng/job-details/add";
        }
        updateJobDetail.unescapeHTML();

        // Check if the job is closed
        Date now = new Date();
        boolean isJobClosed = true;
        if (updateJobDetail.getExpireDate().getTime() > now.getTime()) {
            isJobClosed = false;
        }
        model.addAttribute("isJobClosed", isJobClosed);

        // Set min expire date
        Calendar minCal = Calendar.getInstance();
        minCal.add(Calendar.DAY_OF_MONTH, 1);
        String minExpireDate = DateFormatUtil.formatDateToString(minCal.getTime(), "yyyy-MM-dd");
        model.addAttribute("minExpireDate", minExpireDate);

        model.addAttribute("updateJobDetail", updateJobDetail);
        return "update-job-offer";
    }

    @PostMapping("/job-details/update")
    public String updateJobDetailForm(ModelMap model,
            @Valid @ModelAttribute("updateJobDetail") JobDetail updateJobDetail, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "update-job-offer";
        }

        Date expireDate = updateJobDetail.getExpireDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(expireDate);
        updateJobDetail.setExpireDate(cal.getTime());

        if(updateJobDetail.getPreferences().isEmpty()) {
            updateJobDetail.setPreferences(null);
        }
        jobDetailService.updateJobDetail(updateJobDetail);

        redirectAttributes.addFlashAttribute("jobDetailUpdateSuccess", "Job offer is updated successfully.");
        return "redirect:/mng/job-details/" + updateJobDetail.getId();
    }

    @PostMapping("/job-details/change-job-status")
    @ResponseBody
    public ResponseEntity<?> changeJobStatus(@RequestParam("id") Long id) {
        Calendar now = Calendar.getInstance();

        JobDetail jobDetail = jobDetailService.getById(id);
        jobDetail.unescapeHTML();
        boolean isJobOpen = jobDetail.getExpireDate().getTime() > now.getTime().getTime();

        if (!isJobOpen) {
            // Job is closed, it will be opened
            jobDetail.setOpenDate(now.getTime());

            now.add(Calendar.DAY_OF_MONTH, Constants.JOB_OFFER_DURATION_DAYS);
            now.set(Calendar.HOUR_OF_DAY, 23);
            now.set(Calendar.MINUTE, 59);
            now.set(Calendar.SECOND, 59);
            now.set(Calendar.MILLISECOND, 0);
            jobDetail.setExpireDate(now.getTime());
        } else {
            // Job is open, it will be closed
            now.add(Calendar.SECOND, -1);
            jobDetail.setExpireDate(now.getTime());
        }

        jobDetailService.updateJobDetail(jobDetail);

        return ResponseEntity.ok(isJobOpen ? "Closed" : "Open");
    }

    @GetMapping("/job-details/check/{id}")
    @ResponseBody
    public ResponseEntity<?> checkIfExists(@PathVariable("id") Long id) {
        boolean hasExisted = jobDetailService.existsById(id);
        return ResponseEntity.ok(hasExisted);
    }

    // Model Attributes
    @ModelAttribute("jobTitles")
    public List<JobTitle> jobTitles() {
        return jobTitleService.getActiveJobTitles();
    }

    @ModelAttribute("jobLevels")
    public List<JobLevel> jobLevels() {
        return jobLevelService.getAllJobLevels();
    }

    @ModelAttribute("workSchedules")
    public List<WorkSchedule> workSchedules() {
        return workScheduleService.findAllSchedules();
    }

    @ModelAttribute("jobTypes")
    public List<JobType> jobTypes() {
        return jobTypeService.getActiveJobTypes();
    }

    @ModelAttribute("teams")
    public List<Team> teams() {
        return teamService.getActiveTeams();
    }

    @ModelAttribute("locations")
    public List<Location> locations() {
        return locationService.getAllActiveLocations();
    }

    // @GetMapping("/job-details/{id}")
    // public String viewJobDetails(@PathVariable ("id") String id, Model model){
    // JobDetail jobDetails = jobDetailService.getById(Long.parseLong(id));
    // String description=HtmlUtils.htmlUnescape(jobDetails.getDescription());
    // String requirement=HtmlUtils.htmlUnescape(jobDetails.getRequirement());
    // String preferences=HtmlUtils.htmlUnescape(jobDetails.getPreferences());
    // String responsibilities =
    // HtmlUtils.htmlUnescape(jobDetails.getResponsibilities());
    // model.addAttribute("description", description);
    // model.addAttribute("requirement", requirement);
    // model.addAttribute("preferences", preferences);
    // model.addAttribute("responsibilities", responsibilities);
    // model.addAttribute("others", jobDetails);
    // return "jobDetail";
    // }

   @GetMapping("/jobs")
    public String jobs(ModelMap model) {
        List<String> locations = locationService.getActiveLocations();
        List<JobTitle> jobTitle = jobTitleService.getAllJobTitles();
        List<Department> departments = departmentService.getActiveDepartments();
        List<Team> teams = teamService.getActiveTeams();
        model.addAttribute("locations", locations);
        model.addAttribute("jobTitle", jobTitle);
        model.addAttribute("departments", departments);
        model.addAttribute("teams", teams);
        Date date = new Date();
        System.out.println(date);

        return "adminViewOffer";
    }

    @GetMapping("/getJobs")
    @ResponseBody
    public ResponseEntity<Page<JobDetail>> showPage(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Long position,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String team) {

        int pageSize = 6;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        Page<JobDetail> page = jobDetailService.getFilteredJobsForMng(searchQuery, location, position, status,
                department, team, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/getCandidatesCount")
    @ResponseBody
    public ResponseEntity<Integer> getCandidatesCount(
            @RequestParam Long jobId) {

        List<Candidate> list = candidateService.getAllCandidateCountByJobDetailId(jobId);
        Integer count = list.size();
        return ResponseEntity.ok(count);
    }

    @PostMapping("/your-job-open-close-endpoint")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> openCloseJobOffer(@RequestParam String id,
            @RequestParam String jobStatus) {
        System.out.println("id: " + id + " and jobStatus: " + jobStatus);
        JobDetail jobDetail = jobDetailService.getById(Long.valueOf(id));
        if (jobDetail != null) {
            if (jobStatus.equals("Close")) {
                Date currentDate = new Date();
                jobDetail.setExpireDate(currentDate);
            } else if (jobStatus.equals("Open")) {
                Date currentDate = new Date();
                jobDetail.setExpireDate(currentDate);
                jobDetail.setOpenDate(currentDate);

                // Calculate one month from the current date
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                calendar.add(Calendar.MONTH, 1);
                Date expireDate = calendar.getTime();

                jobDetail.setExpireDate(expireDate);

            }

            String success = jobDetailService.updateJobExpireDate(jobDetail);
            if (success != null) {
                // The update operation was successful, return success response
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Status changed successfully");
                response.put("expireDate", jobDetail.getExpireDate());
                response.put("id", id);
                return ResponseEntity.ok(response);
            } else {
                // The update operation failed, return error response
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Error occurred while changing status: " + success));
            }
        } else {
            // User with the given id not found, return not found response
            return ResponseEntity.notFound().build();
        }
    }
}
