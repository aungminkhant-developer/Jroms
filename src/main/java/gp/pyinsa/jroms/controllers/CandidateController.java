package gp.pyinsa.jroms.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gp.pyinsa.jroms.constants.EmailRole;
import gp.pyinsa.jroms.constants.NotificationType;
import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.dtos.CandidateRegisterDto;
import gp.pyinsa.jroms.dtos.NotificationDto;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.CurriculumVitae;
import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.models.EmailTemplate;
import gp.pyinsa.jroms.models.EmailType;
import gp.pyinsa.jroms.models.InterviewType;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.JobLevel;
import gp.pyinsa.jroms.models.JobTitle;
import gp.pyinsa.jroms.models.JobType;
import gp.pyinsa.jroms.models.Location;
import gp.pyinsa.jroms.models.NotificationMessage;
import gp.pyinsa.jroms.models.Team;
import gp.pyinsa.jroms.models.WorkSchedule;
import gp.pyinsa.jroms.repositories.CVRepository;
import gp.pyinsa.jroms.repositories.CandidateRepository;
import gp.pyinsa.jroms.services.CVService;
import gp.pyinsa.jroms.services.CandidateService;
import gp.pyinsa.jroms.services.DepartmentService;
import gp.pyinsa.jroms.services.EmailTypeService;
import gp.pyinsa.jroms.services.InterviewService;
import gp.pyinsa.jroms.services.IntrerviewTypeService;
import gp.pyinsa.jroms.services.JobDetailService;
import gp.pyinsa.jroms.services.JobLevelService;
import gp.pyinsa.jroms.services.JobTitleService;
import gp.pyinsa.jroms.services.JobTypeService;
import gp.pyinsa.jroms.services.LocationService;
import gp.pyinsa.jroms.services.NotificationMessageService;
import gp.pyinsa.jroms.services.TeamService;
import gp.pyinsa.jroms.services.WorkScheduleService;
import gp.pyinsa.jroms.utils.DateFormatUtil;
import gp.pyinsa.jroms.utils.FileUploadUtil;

@Controller
@RequestMapping("/")
public class CandidateController {

    @Autowired
    private LocationService locationService;
    
    @Autowired
    private EmailTypeService emailTypeService;

    @Autowired
    private IntrerviewTypeService intrerviewTypeService;

    @Autowired
    private WorkScheduleService workScheduleService;

    @Autowired
    private JobDetailService jobDetailService;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CVRepository cvRepository;

    @Autowired
    private JobLevelService jobLevelService;

    @Autowired
    private JobTitleService jobTitleService;

    @Autowired
    private CVService cvService;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private NotificationMessageService notificationMessageService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private JobTypeService jobTypeService;
    @GetMapping("/mng/candidates")
    public String candidateView(ModelMap model) {
        List<JobTitle> jobTitle = jobTitleService.getActiveJobTitles();
        List<JobLevel> jobLevel = jobLevelService.getActiveJobLevels();
        List<CurriculumVitae> curriculumVitae = cvService.getAllCV();
        List<Candidate> jobOffer = candidateService.getAllCandidate();
        List<Department> departments = departmentService.getActiveDepartments();
        List<Team> teams = teamService.getActiveTeams();


        Integer candidates = jobOffer.size();
        model.addAttribute("jobTitle", jobTitle);
        model.addAttribute("jobLevel", jobLevel);
        model.addAttribute("CV", curriculumVitae);
        model.addAttribute("jobOffer", jobOffer);
        model.addAttribute("candidates", candidates);
        model.addAttribute("department", departments);
        model.addAttribute("team", teams);

        return "candidates";
    }

    @PostMapping("/mng/rest/candidates/data")
    @ResponseBody
    public DataTablesOutput<Candidate> candidateDataView(@Valid @RequestBody DataTablesInput input) {
        DataTablesOutput<Candidate> output = candidateService.getAllCandidates(input);

        return output;
    }

    @PostMapping("/mng/rest/candidates/chart-data")
    @ResponseBody
    public DataTablesOutput<Candidate> candidateData(@Valid @RequestBody DataTablesInput input) {
        DataTablesOutput<Candidate> output = candidateService.getAllCandidates(input);
        
        return output;
    }

    @PostMapping("/job-details/candidateRegister")
    public String candidateApplication(@RequestParam("id") String id, Model model) {
        try {
            JobDetail jobDetails = jobDetailService.getById(Long.parseLong(id));
        } catch (Exception e) {
            model.addAttribute("error", "Cheater, no Cheating!");
            return "home";
        }
        CandidateRegisterDto newCandidate = new CandidateRegisterDto();
        newCandidate.setSex("M");
        model.addAttribute("job", id);
        model.addAttribute("newCandidate", newCandidate);
        return "candidate";
    }

    @PostMapping("/registerCandidate")
    public String candidateRegister(RedirectAttributes redirectAttributes, ModelMap model,
            @ModelAttribute("newCandidate") @Valid CandidateRegisterDto newCandidateDto, BindingResult result,
            HttpServletRequest request, @RequestParam("cv") MultipartFile file, Model modal, @RequestParam Long id)
            throws ParseException {

        // return error messages
        if (result.hasErrors()) {
            return "candidate";
        } else {

            System.out.println("Data arrived");

            Candidate candidate = null;
            JobDetail jobDetail = null;

            try {

                CurriculumVitae vitae = new CurriculumVitae();

                // CV Upload
                // check if cv file is empty
                if (!file.isEmpty()) {
                    System.out.println("file arrived");

                    // get cv file name
                    String cv = Instant.now().toEpochMilli() + "-" + StringUtils.cleanPath(file.getOriginalFilename());

                    // save cv
                    vitae.setCvurl(cv);
                    try {
                        cvService.save(vitae);
                    } catch (Exception e) {
                        model.addAttribute("error", "Candidate Creation Error Please Check if All Fields are Valid");
                        return "candidate";
                    }

                    // save file to file path
                    try {
                        String uploadDir = "product-photos/";
                        FileUploadUtil.saveFile(uploadDir, cv, file);
                    } catch (Exception e) {
                        System.out.println("file creation error");
                        return "candidate";
                    }

                    System.out.println("file saved in file path");

                    // Candidate Summary

                    // change String dob to Date dob
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String date = newCandidateDto.getDob();
                    Date parsed = format.parse(date);

                    // change String salary to Big Decimal salary

                    BigDecimal bigDecimal = BigDecimal.valueOf(Double.valueOf(newCandidateDto.getExpected_salary()));

                    // build candidate object
                    candidate = new Candidate();

                    // set candidate dob/salary with the Date dob/BigDecimal salary
                    candidate.setDob(parsed);
                    candidate.setExpected_salary(bigDecimal);
                    candidate.setAddress(newCandidateDto.getAddress());
                    candidate.setName(newCandidateDto.getName());
                    candidate.setEducation(newCandidateDto.getEducation());
                    candidate.setEmail(newCandidateDto.getEmail());
                    candidate.setExperience(newCandidateDto.getExperience());
                    candidate.setTechnical_skills(newCandidateDto.getTechnical_skills());
                    candidate.setLanguage_skills(newCandidateDto.getLanguage_skills());
                    candidate.setMain_skill(newCandidateDto.getMain_skill());
                    candidate.setPhone_number(newCandidateDto.getPhone_number());
                    candidate.setSex(newCandidateDto.getSex());
                    candidate.setAppliedPosition(newCandidateDto.getAppliedPosition());
                    candidate.setLevel(newCandidateDto.getLevel());
                    candidate.setCurriculumVitae(cvRepository.findByCvurl(cv));

                    // parse String id to long
                    // long job = Long.parseLong(id);
                    try {
                        // get job details
                        jobDetail = jobDetailService.getById(id);
                        // set job details
                        candidate.setJobDetail(jobDetail);

                    } catch (Exception e) {
                        // TODO: handle exception
                        model.addAttribute("error", "Can't find the job details my g");
                        return "candidate";
                    }

                    // create candidate
                    String msg = candidateService.save(candidate);
                    System.out.println(msg);
                    // check if there are errors
                    if (msg != null) {
                        // failed to create; back to candidate
                        model.addAttribute("error", msg);
                        return "candidate";
                    }

                    System.out.println("candidate created");

                }
            } catch (Exception e) {
                // failed to create; back to candidate
                e.printStackTrace();
                model.addAttribute("error", "unknown error occurred");
                return "candidate";
            }
            // redirect home with success
            redirectAttributes.addFlashAttribute("success", "Candidate is successfully registered.");

            /* Send candidate application notification message */
            // save notification message in the database
            long jobDetailId = id;
            jobDetail.setId(jobDetailId);
            String jobLevel = jobDetail.getJobLevel() != null ? jobDetail.getJobLevel().getName() + " " : "";

            NotificationMessage noti = new NotificationMessage();
            noti.setJobDetail(jobDetail);
            noti.setNotificationType(NotificationType.CANDIDATE_APPLICATION);
            noti.setMessage(candidate.getName() + " applied for the position: " + jobLevel
                    + jobDetail.getJobTitle().getName() + ".");
            NotificationMessage savedNotification = notificationMessageService.saveNotification(noti);

            // create notification dto to send
            NotificationDto notiDto = new NotificationDto(savedNotification.getId(), savedNotification.getMessage(),
                    jobDetailId, jobLevel + jobDetail.getJobTitle().getName(),
                    DateFormatUtil.formatDateToString(savedNotification.getCreatedDate()), null);

            // Convert dto to JSON
            ObjectMapper mapper = new ObjectMapper();
            String notificationJson = "";
            try {
                notificationJson = mapper.writeValueAsString(notiDto);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            redirectAttributes.addFlashAttribute("notification", notificationJson);
            redirectAttributes.addFlashAttribute("error", "successfully registered");
            return "redirect:/";
        }

    }

    @GetMapping("/job-details/{id}")
    public String viewJobDetails(@PathVariable("id") String id, Model model) {
        try {
            JobDetail jobDetails = jobDetailService.getById(Long.parseLong(id));
            jobDetails.unescapeHTML();
            // String description = HtmlUtils.htmlUnescape(jobDetails.getDescription());
            // String requirement = HtmlUtils.htmlUnescape(jobDetails.getRequirement());

            // try {
            //     String preferences = HtmlUtils.htmlUnescape(jobDetails.getPreferences());
            //     model.addAttribute("preferences", preferences);
            // } catch (Exception e) {
            //     System.out.println("No Preference");
            //     // TODO: handle exception
            // }

            JobLevel jobLevelObj = jobDetails.getJobLevel();
            String jobLevel = "";
            if(jobLevelObj != null) {
                jobLevel = jobLevelObj.getName();
            }
            // String responsibilities = HtmlUtils.htmlUnescape(jobDetails.getResponsibilities());
            JobTitle jobTitle = jobTitleService.getById(jobDetails.getJobTitle().getId());
            WorkSchedule workSchedule = workScheduleService.getById(jobDetails.getWorkSchedule().getId());
            Location location = locationService.getLocationById(jobDetails.getLocation().getId()); 
            JobType jobType = jobTypeService.getJobTypeById(jobDetails.getJobType().getId());
            model.addAttribute("description", jobDetails.getDescription());
            model.addAttribute("requirement", jobDetails.getRequirement());
            model.addAttribute("jobLevel", jobLevel);
            model.addAttribute("responsibilities", jobDetails.getResponsibilities());
            model.addAttribute("others", jobDetails);
            model.addAttribute("jobTitle", jobTitle);
            model.addAttribute("schedule", workSchedule);
            model.addAttribute("location", location);
            model.addAttribute("jobType", jobType);

            // isJobClosed
            Date now = new Date();
            boolean isJobClosed = true;
            if (jobDetails.getExpireDate().getTime() > now.getTime()) {
                isJobClosed = false;
            }
            model.addAttribute("isJobClosed", isJobClosed);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            model.addAttribute("error", "There is no Job Detail with this id, G!");
            return "home";
        }

        return "jobDetail";
    }

    @GetMapping("/candidate/{id}")
    @ResponseBody
    public ResponseEntity<Candidate> getCandidateDetails(@PathVariable Long id) {
        Candidate candidate = candidateRepository.findById(id).orElse(null);
        return ResponseEntity.ok(candidate);
    }

    @GetMapping("/mng/candidate/download-cv/{id}")
    public String downloadCV(@PathVariable String id, HttpServletResponse response, ModelMap model,
            @RequestParam(required = false) String errorpage) {

        List<JobTitle> jobTitle = jobTitleService.getActiveJobTitles();
        List<JobLevel> jobLevel = jobLevelService.getActiveJobLevels();

        List<CurriculumVitae> curriculumVitae = cvService.getAllCV();
        List<Candidate> jobOffer = candidateService.getAllCandidate();

        System.out.println(jobLevel);

        Integer candidates = jobOffer.size();
        model.addAttribute("jobTitle", jobTitle);
        model.addAttribute("jobLevel", jobLevel);
        model.addAttribute("CV", curriculumVitae);
        model.addAttribute("jobOffer", jobOffer);
        model.addAttribute("candidates", candidates);

        // get cv from database
        try {
            System.out.println();
            System.out.println(id);
            CurriculumVitae cv = cvService.getCurriculumVitaeById(Long.parseLong(id));
            System.out.println(cv.getCvurl());
            try {
                String fileName = cv.getCvurl();
                String uploadDir = "product-photos";

                // Create the full path to the file by combining the directory and filename
                Path filePath = Paths.get(uploadDir, fileName);
                System.out.println(filePath);

                // Convert the Path object to a File object
                File file = filePath.toFile();

                System.out.println(file);
                System.out.println();

                // Check if the file exists
                if (file.exists()) {
                    // Set the response content type to indicate it's an attachment
                    response.setContentType("application/octet-stream");

                    // Set the "Content-Disposition" header to suggest the filename for download
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

                    try (InputStream inputStream = new FileInputStream(file);
                            OutputStream outputStream = response.getOutputStream()) {

                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        // Read from the input stream and write to the response output stream
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        model.addAttribute("error", "Error Occurred While Downloading");
                        return errorpage;
                    }
                } else {
                    model.addAttribute("error", "Can't find file");
                    return errorpage;
                }

            } catch (Exception e) {
                model.addAttribute("error", "Error Occurred, CV may be null");
                return errorpage;
            }
        } catch (Exception e) {
            model.addAttribute("error", "CV not found.");
            return errorpage;
        }

        model.addAttribute("error", "Download Successful");
        return errorpage;
    }

    @GetMapping("mng/candidate/download-all")
    public void downloadAllFiles(HttpServletResponse response) {
        // Retrieve a list of all CurriculumVitae objects from the database
        List<CurriculumVitae> cvList = cvService.getAllCV();

        // Prepare the response to download multiple files as a zip archive
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"all_cvs.zip\"");

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
            for (CurriculumVitae cv : cvList) {
                String fileName = cv.getCvurl();
                String uploadDir = "product-photos"; // Adjust this to your upload directory
                Path filePath = Paths.get(uploadDir, fileName);

                File file = filePath.toFile();

                if (file.exists()) {
                    // Create a new entry in the zip file
                    zipOutputStream.putNextEntry(new ZipEntry(fileName));

                    try (InputStream inputStream = new FileInputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        // Write the file content to the zip output stream
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            zipOutputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    zipOutputStream.closeEntry();
                }
            }
        } catch (IOException e) {
            // Handle the exception
        }
        // return "redirect:/mng/candidates";
    }

    @PostMapping("/mng/candidate/download-filtered")
    @ResponseBody
    public String downloadFilteredCVs(@RequestBody List<Candidate> filteredData, HttpServletResponse response) {
        // get candidate id from the returned list
        List<Long> candidateIds = cvService.getFilteredCandidateIds(filteredData);
        System.out.println();
        System.out.println(candidateIds);
        System.out.println();

        // get cv id from returned candidate id
        List<CurriculumVitae> cvList = candidateService.getFilteredCurriculumVitaeById(candidateIds);
        System.out.println();
        System.out.println(cvList);
        System.out.println();

        // Prepare the response to download multiple files as a zip archive
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"filtered_cvs.zip\"");

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
            for (CurriculumVitae cv : cvList) {
                String fileName = cv.getCvurl();
                String uploadDir = "product-photos"; // Adjust this to your upload directory
                Path filePath = Paths.get(uploadDir, fileName);

                File file = filePath.toFile();

                if (file.exists()) {
                    // Create a new entry in the zip file
                    zipOutputStream.putNextEntry(new ZipEntry(fileName));

                    try (InputStream inputStream = new FileInputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        // Write the file content to the zip output stream
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            zipOutputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    zipOutputStream.closeEntry();
                }
            }
        } catch (IOException e) {
            // Handle the exception
        }
        return "redirect:/mng/candidates";

    }

    @PostMapping("/mng/candidate/download")
    @ResponseBody
    public String downloadSelectedCVs(@RequestBody List<Long> filteredData, HttpServletResponse response) {

        System.out.println(filteredData);

        // get cv list
        List<CurriculumVitae> cvList = candidateService.getFilteredCurriculumVitaeById(filteredData);
        System.out.println();
        System.out.println(cvList);
        System.out.println();

        // Prepare the response to download multiple files as a zip archive
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"selected_cvs.zip\"");

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
            for (CurriculumVitae cv : cvList) {
                String fileName = cv.getCvurl();
                String uploadDir = "product-photos"; // Adjust this to your upload directory
                Path filePath = Paths.get(uploadDir, fileName);

                File file = filePath.toFile();

                if (file.exists()) {
                    // Create a new entry in the zip file
                    zipOutputStream.putNextEntry(new ZipEntry(fileName));

                    try (InputStream inputStream = new FileInputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        // Write the file content to the zip output stream
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            zipOutputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    zipOutputStream.closeEntry();
                }
            }
        } catch (IOException e) {
            // Handle the exception
        }
        return "redirect:/mng/candidates";

    }

    @PostMapping("/mng/candidate/change-job-accepted-status/{id}")
    @ResponseBody
    public ResponseEntity<?> changeJobAcceptedStatus(@PathVariable("id") long id) {
        Optional<Candidate> candOpt = candidateRepository.findById(id);
        if(candOpt.isPresent()) {
            Candidate candidate = candOpt.get();
            boolean jobAccepted = candidate.isJobAccepted();
            candidate.setJobAccepted(!jobAccepted);
            candidateService.save(candidate);
        }
        return ResponseEntity.ok("success");
    }
    

    @ModelAttribute("typeList")
    public List<InterviewType> getInterviewTypes() {
        return intrerviewTypeService.getAllInterviewTypes();
    }

    @ModelAttribute("emailTypeList")
    public List<EmailType> getAllEmailTypes() {
        return emailTypeService.getAllEmailTypeByActive(Status.ACTIVE);
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
