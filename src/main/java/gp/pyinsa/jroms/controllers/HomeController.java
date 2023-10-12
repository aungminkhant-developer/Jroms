package gp.pyinsa.jroms.controllers;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.JobTitle;
import gp.pyinsa.jroms.services.JobDetailService;
import gp.pyinsa.jroms.services.JobTitleService;
import gp.pyinsa.jroms.services.LocationService;
import gp.pyinsa.jroms.services.MailService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gp.pyinsa.jroms.constants.Interview_Result;
import gp.pyinsa.jroms.constants.JobStatus;
import gp.pyinsa.jroms.dtos.ContactForm;
import gp.pyinsa.jroms.dtos.TeamDto;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.Team;
import gp.pyinsa.jroms.models.VerificationToken;
import gp.pyinsa.jroms.services.CandidateService;
import gp.pyinsa.jroms.services.TeamService;
import gp.pyinsa.jroms.services.VerificationTokenService;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    JobDetailService jobDetailService;

    @Autowired
    LocationService locationService;

    @Autowired
    JobTitleService jobTitleService;

    @Autowired
    CandidateService candidateService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Autowired
    private MailService mailService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/mng/dashboard")
    public String homePage(Model model) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String currentYear = dateFormat.format(new Date());
        int currentYearInt = Integer.valueOf(currentYear);
        List<Integer> yearList = new ArrayList<>();
        for (int i = 0; i <= currentYearInt - 2023; i++) {
            yearList.add(2023 + i);
        }
        List<Long> candidateCountList = getCountCandidate(currentYear);
        List<Long> jobOfferCountList = getCountJobOffer(currentYear);
        List<JobDetail> jobDetailsList = jobDetailService.findAllJobDetails();
        List<Team> teamList = teamService.getActiveTeams();
        List<TeamDto> teamDtoList = new ArrayList<>();
        List<Candidate> candidatesList = candidateService.getAllCandidate();
        List<Candidate> candidatesListByPassed = candidateService
                .getCandidatesByInterviewResultAndOfferMailSent(Interview_Result.PASSED, false);
        List<Candidate> candidatesListByFaild = candidateService
                .getCandidatesByInterviewResult(Interview_Result.FAILED);
        List<Candidate> candidatesListByPending = candidateService
                .getCandidatesByInterviewResult(Interview_Result.PENDING);
        List<Candidate> candidatesListByCancel = candidateService
                .getCandidatesByInterviewResult(Interview_Result.CANCEL);
        List<Candidate> candidatesListByNextInterview = candidateService
                .getCandidatesByInterviewResult(Interview_Result.NEXT_INTERVIEW);
        List<Candidate> candidatesListByNotReachInterview = candidateService
                .getCandidatesByInterviewResult(Interview_Result.NOT_REACHED);
        List<Candidate> candidatesListByJobOffered = candidateService
                .getCandidatesByInterviewResultAndOfferMailSent(Interview_Result.PASSED, true);
        long jobAcceptedList = candidateService.getCountByJobAccepted(true);
        for (Team team : teamList) {
            List<JobDetail> jobDetails = jobDetailService.getJobDetailsByTeamId(team.getId());
            TeamDto teamDto = new TeamDto(team.getId(), team.getName(), jobDetails);
            teamDtoList.add(teamDto);
        }
        int totalPost = getTotalPost();
        model.addAttribute("candidateCountList", candidateCountList);
        model.addAttribute("jobOfferCountList", jobOfferCountList);
        model.addAttribute("yearList", yearList);
        model.addAttribute("currentYear", currentYearInt);
        model.addAttribute("jobDetailList", jobDetailsList);
        model.addAttribute("teamList", teamDtoList);
        model.addAttribute("allCandidateCount", candidatesList.size());
        model.addAttribute("passedList", candidatesListByPassed.size());
        model.addAttribute("failedList", candidatesListByFaild.size());
        model.addAttribute("pendingList", candidatesListByPending.size());
        model.addAttribute("cancelList", candidatesListByCancel.size());
        model.addAttribute("nextInterviewList", candidatesListByNextInterview.size());
        model.addAttribute("notReachList", candidatesListByNotReachInterview.size());
        model.addAttribute("jobOfferedList", candidatesListByJobOffered.size());
        model.addAttribute("totalPost", totalPost);
        model.addAttribute("jobAcceptedList", jobAcceptedList);
        return "dashboard";
    }

    @GetMapping("/contactUs")
    public String contactUs(ModelMap model) {
        model.addAttribute("contactForm", new ContactForm());
        return "contect-us";
    }

    public String sendEmail(String username, String email, String message) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlMsg = "<h3>Message from " + username + " (" + email + ")</h3><p>" + message + "</p>";

            helper.setText(htmlMsg, true); // Use this flag to set the text to HTML
            helper.setTo("acecompany771@gmail.com");
            helper.setSubject("New message from " + username);
            helper.setFrom(email);

            try {
                javaMailSender.send(mimeMessage);
                return null; // No errors
            } catch (MailException ex) {
                if (ex instanceof MailSendException) {
                    if (ex.getCause() != null && ex.getCause() instanceof ConnectException) {
                        return "Connection error: Unable to connect to the mail server";
                    } else if (ex.getCause() != null && ex.getCause() instanceof SocketTimeoutException) {
                        return "Connection timeout: Mail server connection timed out";
                    }
                }
                return "Failed to send email. Please try again";
            }
        } catch (MessagingException e) {
            return "Failed to send email. Please try again"; // Handle exception
        }
    }

    @PostMapping("/contactUs")
    public String handleForm(
            @Valid ContactForm contactForm,
            BindingResult bindingResult, ModelMap model) throws MessagingException {

        if (bindingResult.hasErrors()) {
            return "contect-us";
        }

        String mailError = sendEmail(contactForm.getUsername(), contactForm.getEmail(), contactForm.getMessage());
        if (mailError != null) {
            model.addAttribute("mailException", mailError);
            return "contect-us";
        }
        model.addAttribute("mailSuccess", "Your email has been successfully sent");
        return "contect-us";
    }

    // to get candidate count in each month by fetch
    @PostMapping("/getCandidateCountByMonth")
    public ResponseEntity<List<Long>> getCandidateCount(@RequestBody String year) {
        List<Long> couList = null;
        try {
            JsonNode jsonNode = objectMapper.readTree(year);
            String year1 = jsonNode.asText();
            couList = getCountCandidate(year1);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(couList);
    }

    // to get job offer count in each month by fetch
    @PostMapping("/getJobOfferCountByMonth")
    public ResponseEntity<List<Long>> getJobOfferCount(@RequestBody String year) {
        List<Long> couList = null;
        try {
            JsonNode jsonNode = objectMapper.readTree(year);
            String year1 = jsonNode.asText();
            couList = getCountJobOffer(year1);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(couList);
    }

    // send candidate count under job detail by interview result by fetch
    @PostMapping("/candidateByInterviewResult")
    public ResponseEntity<List<Integer>> getCandidateByInterviewResult(@RequestBody Long id) {
        List<Integer> resultCount = new ArrayList<>();
        List<Candidate> candidatesListByPassed = candidateService
                .getCandidatesByJobDetail_IdAndInterviewResultAndOfferMailSent(id,
                        Interview_Result.PASSED, false);
        List<Candidate> candidatesListByFailed = candidateService.getCandidatesByJobDetail_IdAndInterviewResult(id,
                Interview_Result.FAILED);
        List<Candidate> candidatesListByPending = candidateService.getCandidatesByJobDetail_IdAndInterviewResult(id,
                Interview_Result.PENDING);
        List<Candidate> candidatesListByCancel = candidateService.getCandidatesByJobDetail_IdAndInterviewResult(id,
                Interview_Result.CANCEL);
        List<Candidate> candidatesListByNextInterview = candidateService
                .getCandidatesByJobDetail_IdAndInterviewResult(id, Interview_Result.NEXT_INTERVIEW);
        List<Candidate> candidatesListByJobAccepted = candidateService
                .getCandidatesByJobDetail_IdAndInterviewResultAndOfferMailSent(id, Interview_Result.PASSED, true);
        List<Candidate> candidatesListByNotReach = candidateService
                .getCandidatesByJobDetail_IdAndInterviewResult(id, Interview_Result.NOT_REACHED);
        resultCount.add(candidatesListByPassed.size());
        resultCount.add(candidatesListByFailed.size());
        resultCount.add(candidatesListByPending.size());
        resultCount.add(candidatesListByCancel.size());
        resultCount.add(candidatesListByNextInterview.size());
        resultCount.add(candidatesListByJobAccepted.size());
        resultCount.add(candidatesListByNotReach.size());
        return ResponseEntity.ok(resultCount);
    }

    // send jobOfferStatistics data under job detail
    @PostMapping("/jobOfferStatisticsUnderJobDetail")
    public ResponseEntity<List<Long>> sendJobOfferStatisticsDataUnderJobDetail(@RequestBody Long id) {
        List<Long> dataCount = new ArrayList<>();
        JobDetail jobDetail = jobDetailService.getById(id);
        long totalPost = jobDetail.getPosts();
        long jobAcceptedCount = candidateService.getCountByJobDetail_IdAndJobAccepted(jobDetail, true);
        List<Candidate> candidatesListByJobOffered = candidateService
                .getCandidatesByJobDetail_IdAndInterviewResultAndOfferMailSent(id, Interview_Result.PASSED, true);
        List<Candidate> candidatesListByPassed = candidateService.getCandidatesByJobDetail_IdAndInterviewResultAndOfferMailSent(id, Interview_Result.PASSED, false);
        long jobOfferedCount = candidatesListByJobOffered.size();
        long passedCount = candidatesListByPassed.size();
        dataCount.add(totalPost);
        dataCount.add(jobAcceptedCount);
        dataCount.add(jobOfferedCount);
        dataCount.add(passedCount);
        return ResponseEntity.ok(dataCount);
    }

    @GetMapping("/accept")
    public String accept(@RequestParam String token, Model model){
        model.addAttribute("token", token);
        return "jobAcceptWait";
    }

    // to verify link for job accept
    @GetMapping("/verifyJobAccept")
    public String verifyJobAccept(@RequestParam String token, Model model) {
        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
        if (verificationToken == null) {
            sendModelAttributeToJobAccept("Sorry...", "Invalid token.", model);
            return "jobAccept";
        }
        String verificationResult = verificationTokenService.validateToken(token);
        String emailTemplate = getJobAcceptMailTemplate(verificationToken.getCandidate().getName(),
                getJobDetailAndJobPositionName(verificationToken.getCandidate().getJobDetail().getId()),
                verificationToken.getCandidate().getJobDetail().getTeam().getName(),
                verificationToken.getCandidate().getJobDetail().getTeam().getDepartment().getName());
        if (verificationResult.equalsIgnoreCase("success")) {
            try {
                mailService.jobAcceptMail(emailTemplate, verificationToken.getCandidate().getEmail(),
                        verificationToken.getCandidate().getJobDetail().getTeam().getTeamLeader().getEmail(),
                        verificationToken.getCandidate().getJobDetail().getTeam().getDepartment().getDepartmentHead()
                                .getEmail());
            } catch (SocketTimeoutException | ConnectException e) {
                sendModelAttributeToJobAccept("Sorry...", "Please try again.", model);
                return "jobAccept";
            } catch (MessagingException | IOException e) {
                sendModelAttributeToJobAccept("Sorry...", "Please try again.", model);
                return "jobAccept";
            } catch (Exception e) {
                sendModelAttributeToJobAccept("Sorry...", "Please try again.", model);
                return "jobAccept";
            }
            Candidate candidate = candidateService.getCandidateById(verificationToken.getCandidate().getId());
            candidate.setJobAccepted(true);
            candidateService.save(candidate);
            sendModelAttributeToJobAccept("Congratulation...",
                    "You have got a job.We will contact you as soon as possible.", model);
        } else {
            sendModelAttributeToJobAccept("Sorry...", verificationResult, model);
        }
        return "jobAccept";
    }

    // send candidate count under team by fetch
    @PostMapping("/candidateCountUnderTeam")
    public ResponseEntity<List<Integer>> sendCandidateCountUnderTeam(@RequestBody String id) {
        List<Integer> resultCount = null;
        try {
            JsonNode jsonNode = objectMapper.readTree(id);
            String teamId = jsonNode.asText();
            List<JobDetail> jobDetails = jobDetailService.getJobDetailsByTeamId(teamId);
            resultCount = getCandidateCountUnderTeam(jobDetails);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(resultCount);
    }

    // send jobOfferStatistics chart data under team by fetch
    @PostMapping("/jobOfferStatisticsUnderTeam")
    public ResponseEntity<List<Integer>> sendJobOfferStatisticsData(@RequestBody String id) {
        List<Integer> dataCount = null;
        try {
            JsonNode jsonNode = objectMapper.readTree(id);
            String teamId = jsonNode.asText();
            List<JobDetail> jobDetails = jobDetailService.getJobDetailsByTeamId(teamId);
            dataCount = getJobOfferStatisticsDataUnderTeam(jobDetails);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(dataCount);
    }

    @GetMapping("/add-user")
    public String addUserForm(Model model) {
        Object job_positions = "";
        Object job_levels = "";
        model.addAttribute(job_positions);
        return "add_user";
    }

    @GetMapping("/interviews")
    public String interviews() {
        return "interviews";
    }

    @GetMapping("/view_user")
    public String viewUserForm(Model model) {
        Object job_positions = "";
        Object job_levels = "";
        model.addAttribute(job_positions);
        return "view_user";
    }

    @GetMapping("/candidates")
    public String candidates(Model model) {
        Object job_positions = "";
        Object job_levels = "";
        model.addAttribute(job_positions);
        return "candidates";
    }

    // to get count list candidate
    public List<Long> getCountCandidate(String year) {
        List<Long> countList = new ArrayList<>();
        // loop for each month and get count by month
        for (int i = 1; i < 13; i++) {
            long count = candidateService.getCountByCreatedDateBetween(year, String.valueOf(i));
            countList.add(count);
        }
        for (int i = 1; i < 13; i++) {
            long count = candidateService.getCountByJobAcceptedAndCreatedDate(true, year, String.valueOf(i));
            countList.add(count);
        }
        for (int i = 1; i < 13; i++) {
            long count = candidateService.getCountByFinalResultAndCreatedDate(Interview_Result.FAILED, year,
                    String.valueOf(i));
            countList.add(count);
        }
        return countList;
    }

    public List<Long> getCountJobOffer(String year) {
        List<Long> countList = new ArrayList<>();
        // loop for each month and get count by month
        for (int i = 1; i < 13; i++) {
            long count = jobDetailService.getJobOfferCountByCreatedDateBetween(year, String.valueOf(i));
            countList.add(count);
        }
        return countList;
    }

    @GetMapping("/")
    public String jobs(ModelMap model) {
        List<String> locations = locationService.getActiveLocations();
        List<JobTitle> jobTitle = jobTitleService.getAllJobTitles();
        model.addAttribute("locations", locations);
        model.addAttribute("jobTitle", jobTitle);
        return "home";
    }

    @GetMapping("/getJobs")
    @ResponseBody
    public ResponseEntity<Page<JobDetail>> showPage(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Long position,
            @RequestParam(required = false) String status) {

        int pageSize = 6;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        Page<JobDetail> page = jobDetailService.getFilteredJobs(searchQuery, location, position, status, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    // get candidate count method by interview result under team
    public List<Integer> getCandidateCountUnderTeam(List<JobDetail> jobDetails) {
        int passedSize = 0;
        int failedSize = 0;
        int pendingSize = 0;
        int cancelSize = 0;
        int nextSize = 0;
        int jobAccept = 0;
        int notReach = 0;
        List<Integer> resultCount = new ArrayList<>();
        for (JobDetail jobDetail : jobDetails) {
            List<Candidate> candidatesListByPassed = candidateService
                    .getCandidatesByJobDetail_IdAndInterviewResultAndOfferMailSent(jobDetail.getId(),
                            Interview_Result.PASSED, false);
            List<Candidate> candidatesListByFailed = candidateService
                    .getCandidatesByJobDetail_IdAndInterviewResult(jobDetail.getId(), Interview_Result.FAILED);
            List<Candidate> candidatesListByPending = candidateService
                    .getCandidatesByJobDetail_IdAndInterviewResult(jobDetail.getId(), Interview_Result.PENDING);
            List<Candidate> candidatesListByCancel = candidateService
                    .getCandidatesByJobDetail_IdAndInterviewResult(jobDetail.getId(), Interview_Result.CANCEL);
            List<Candidate> candidatesListByNextInterview = candidateService
                    .getCandidatesByJobDetail_IdAndInterviewResult(jobDetail.getId(), Interview_Result.NEXT_INTERVIEW);
            List<Candidate> candidatesListByJobAccepted = candidateService
                    .getCandidatesByJobDetail_IdAndInterviewResultAndOfferMailSent(jobDetail.getId(),
                            Interview_Result.PASSED, true);
            List<Candidate> candidatesListByNotReach = candidateService
                    .getCandidatesByJobDetail_IdAndInterviewResult(jobDetail.getId(), Interview_Result.NOT_REACHED);
            passedSize += candidatesListByPassed.size();
            failedSize += candidatesListByFailed.size();
            pendingSize += candidatesListByPending.size();
            cancelSize += candidatesListByCancel.size();
            nextSize += candidatesListByNextInterview.size();
            jobAccept += candidatesListByJobAccepted.size();
            notReach += candidatesListByNotReach.size();
        }
        resultCount.add(passedSize);
        resultCount.add(failedSize);
        resultCount.add(pendingSize);
        resultCount.add(cancelSize);
        resultCount.add(nextSize);
        resultCount.add(jobAccept);
        resultCount.add(notReach);
        return resultCount;
    }

    public String getJobAcceptMailTemplate(String candidateName, String jobPosition, String team, String department) {
        String mailTemplate = "<p>Candidate's Name&nbsp; &nbsp; &nbsp; - " + candidateName + "</p>\r\n" +
                "<p>Accepted Job&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; - " + jobPosition + "</p>\r\n" +
                "<p>Team&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;- "
                + team + "</p>\r\n" +
                "<p>Department&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;- " + department
                + "</p>\r\n" +
                "<p>&nbsp;</p>";
        return mailTemplate;
    }

    // to get job position name
    public String getJobDetailAndJobPositionName(Long jobDetailId) {
        JobDetail jobDetail = jobDetailService.getById(jobDetailId);
        String jobPosition = "";
        jobPosition += jobDetail.getJobTitle().getName();
        if (jobDetail.getJobLevel() != null) {
            jobPosition += "(" + jobDetail.getJobLevel().getName() + ")";
        }
        return jobPosition;
    }

    public void sendModelAttributeToJobAccept(String title, String body, Model model) {
        model.addAttribute("title", title);
        model.addAttribute("verifyJobAccept", body);
    }

    // get total post for all job details
    public Integer getTotalPost() {
        int totalPost = 0;
        List<JobDetail> jobDetails = jobDetailService.findAllJobDetails();
        for (JobDetail jobDetail : jobDetails) {
            totalPost += jobDetail.getPosts();
        }
        return totalPost;
    }

    // get jobOfferStatistics data under team
    private List<Integer> getJobOfferStatisticsDataUnderTeam(List<JobDetail> jobDetails) {
        int totalPost = 0;
        int jobAcceptedList = 0;
        int passedList = 0;
        int jobOfferedList = 0;
        List<Integer> dataCount = new ArrayList<>();
        for (JobDetail jobDetail : jobDetails) {
            totalPost += jobDetail.getPosts();
            jobAcceptedList += candidateService.getCountByJobDetail_IdAndJobAccepted(jobDetail, true);
            List<Candidate> candidatesListByPassed = candidateService
                    .getCandidatesByJobDetail_IdAndInterviewResultAndOfferMailSent(jobDetail.getId(),
                            Interview_Result.PASSED, false);
            List<Candidate> candidatesListByJobOffered = candidateService
                    .getCandidatesByJobDetail_IdAndInterviewResultAndOfferMailSent(jobDetail.getId(),
                            Interview_Result.PASSED, true);
            passedList += candidatesListByPassed.size();
            jobOfferedList += candidatesListByJobOffered.size();
        }
        dataCount.add(totalPost);
        dataCount.add(jobAcceptedList);
        dataCount.add(jobOfferedList);
        dataCount.add(passedList);
        return dataCount;
    }

}
