package gp.pyinsa.jroms.controllers;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gp.pyinsa.jroms.constants.CVStatus;
import gp.pyinsa.jroms.constants.EmailRole;
import gp.pyinsa.jroms.constants.Interview_Result;
import gp.pyinsa.jroms.constants.Interview_Status;
import gp.pyinsa.jroms.constants.Mail_Status;
import gp.pyinsa.jroms.constants.NotificationType;
import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.dtos.InterviewEditDto;
import gp.pyinsa.jroms.dtos.NotificationDto;
import gp.pyinsa.jroms.exceptions.InterviewByCandidateNotFoundException;
import gp.pyinsa.jroms.exceptions.ResourceNotFoundException;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.models.EmailTemplate;
import gp.pyinsa.jroms.models.EmailType;
import gp.pyinsa.jroms.models.Interview;
import gp.pyinsa.jroms.models.InterviewSchedule;
import gp.pyinsa.jroms.models.InterviewStage;
import gp.pyinsa.jroms.models.InterviewType;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.NotificationMessage;
import gp.pyinsa.jroms.models.Team;
import gp.pyinsa.jroms.models.User;
import gp.pyinsa.jroms.services.CandidateService;
import gp.pyinsa.jroms.services.DepartmentService;
import gp.pyinsa.jroms.services.EmailTypeService;
import gp.pyinsa.jroms.services.InterviewScheduleService;
import gp.pyinsa.jroms.services.InterviewService;
import gp.pyinsa.jroms.services.InterviewStageService;
import gp.pyinsa.jroms.services.IntrerviewTypeService;
import gp.pyinsa.jroms.services.JobDetailService;
import gp.pyinsa.jroms.services.MailService;
import gp.pyinsa.jroms.services.NotificationMessageService;
import gp.pyinsa.jroms.services.TeamService;
import gp.pyinsa.jroms.services.UserService;
import gp.pyinsa.jroms.utils.DateFormatUtil;

@Controller
@RequestMapping("/mng/interview")
public class InterviewController {

    @Autowired
    private JobDetailService jobDetailsService;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private InterviewStageService interviewStageService;

    @Autowired
    private IntrerviewTypeService intrerviewTypeService;

    @Autowired
    private InterviewScheduleService interviewScheduleService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailTypeService emailTypeService;

    @Autowired
    private MailService mailService;

    @Autowired
    private NotificationMessageService notificationMessageService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private TeamService teamService;

    @GetMapping("/allInterviews")
    public String getInterview(Model model) {
        List<Interview> interviewList = interviewService.getAllInterviews();
        List<Department> departments = departmentService.getActiveDepartments();
        List<Team> teams = teamService.getActiveTeams();
        if (interviewList.isEmpty()) {
            model.addAttribute("EmptyInterviewList", true);
        }
        model.addAttribute("interviewList", interviewList);
        model.addAttribute("department", departments);
        model.addAttribute("team", teams);
        return "all-interviews-fixed";
    }

    // to get candidate interview by job Detail
    @GetMapping("/candidateInterview")
    public String getCanidateInterview(@RequestParam long candidateId, Model model) {
        getCandidateById(candidateId, model);
        getInterviewByCandidate(candidateId, model);
        List<InterviewStage> stage=interviewStageService.getActiveInterviewStages();
        if(!stage.isEmpty()){
            sendSelectedStage(interviewStageService.getActiveInterviewStages().get(0).getId(), model);
        }
        return "candidateInterview";
    }

    // to create interview
    @PostMapping("/candidateInterview/createInterview")
    public String createInterview(
            @ModelAttribute("schedule") InterviewSchedule schedule,
            @RequestParam Short interviewTypeId, long candidateId, String location,
            @RequestParam(value = "departHeadId", required = false) String departHeadId, Short stageId,
            @RequestParam(value = "teamLeaderId", required = false) String teamLeaderId, RedirectAttributes redirect,
            Model model) {
        Candidate candidate = candidateService.getCandidateById(candidateId);
        List<Interview> interviewsList = interviewService.getAllInterviewByCandidateId(candidateId);
        getInterviewByCandidate(candidateId, model);
       
        //to check interview stage
        if (!interviewsList.isEmpty()) {
            sendTime(schedule, model);
            getCandidateById(candidateId, model);
            findInterviewerForSelectBox(departHeadId, teamLeaderId, model);
            sendSelectedStage(stageId, model);
            model.addAttribute("btnValue", "Create");
            InterviewStage stage = interviewStageService.getInterviewStageById(stageId);
            if (interviewService.existsByCandidateAndInterviewStage(candidate, stage)) {
                model.addAttribute("notSendEmailExist", "This interview stage have already been !!");
                return "candidateInterview";
            }
        }
        // set interviewer list to interview
        String interviewerId = null;
        if (departHeadId == null) {
            interviewerId = teamLeaderId;
        } else
            interviewerId = departHeadId;
        List<User> interviewerList = new ArrayList<>();
        if (departHeadId != null && teamLeaderId != null) {
            User departHead = userService.getById(departHeadId);
            User teamLeader = userService.getById(teamLeaderId);
            interviewerList.add(departHead);
            interviewerList.add(teamLeader);
        } else {
            User interviewer = userService.getById(interviewerId);
            interviewerList.add(interviewer);
        } //

        InterviewStage stage = interviewStageService.getInterviewStageById(stageId);
        InterviewType interviewType = intrerviewTypeService.getInterviewTypeById(interviewTypeId);
        schedule.setInterviewLocation(location);
        schedule.setInterviewType(interviewType);
        interviewScheduleService.saveInterviewSchedule(schedule);// save interview schedule
        Interview interview = new Interview(null, Interview_Status.ONGOING, Interview_Result.PENDING,
                Mail_Status.NOT_SEND,
                candidate, stage, schedule, candidate.getJobDetail(), interviewerList);
        interviewService.saveInterview(interview);
        redirect.addAttribute("candidateId", candidateId);

        // Send notification
        /* Send interview notification message */
        // save notification message in the database
        JobDetail jobDetail = candidate.getJobDetail();
        String jobLevel = jobDetail.getJobLevel() != null ? jobDetail.getJobLevel().getName() + " " : "";
        NotificationMessage noti = new NotificationMessage();
        noti.setJobDetail(jobDetail);
        noti.setNotificationType(NotificationType.INTERVIEW);
        noti.setMessage("You have a meeting with " + candidate.getName() + " for the position: " + jobLevel
                + jobDetail.getJobTitle().getName() + " on "
                + DateFormatUtil.formatDateToString(schedule.getInterviewDate(), "yyyy-MM-dd"));
        // noti.setInterviewers(interview.getInterviewerList());

        List<User> notiInterviewers = new ArrayList<>(interview.getInterviewerList());
        noti.setInterviewers(notiInterviewers);
        NotificationMessage savedNotification = notificationMessageService.saveNotification(noti);
        System.out.println("Notification: " + savedNotification);

        // create notification dto to send
        NotificationDto notiDto = new NotificationDto(savedNotification.getId(), savedNotification.getMessage(),
                jobDetail.getId(), jobLevel + jobDetail.getJobTitle().getName(),
                DateFormatUtil.formatDateToString(savedNotification.getCreatedDate()), savedNotification
                        .getInterviewers().stream().map(intv -> intv.getUsername()).collect(Collectors.toList()));

        // Convert dto to JSON
        ObjectMapper mapper = new ObjectMapper();
        String notificationJson = "";
        try {
            notificationJson = mapper.writeValueAsString(notiDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        redirect.addFlashAttribute("notification", notificationJson);
        //to send create success box with candidate name
        String message=candidate.getName()+"'s interview schedule has created successfully.";
        redirect.addFlashAttribute("interviewCreateSuccess", message);
        return "redirect:/mng/interview/candidateInterview";
    }

    // to update interview schedule
    @PostMapping("/updateInterview")
    public String getupdateInterview(@ModelAttribute("schedule") InterviewSchedule schedule,
            @RequestParam Long interviewId, @RequestParam Short interviewTypeId,
            long candidateId, String location,
            @RequestParam(value = "departHeadId", required = false) String departHeadId, Short stageId,
            @RequestParam(value = "jobDetailId", required = false) Long jobDetailId,
            @RequestParam(value = "teamLeaderId", required = false) String teamLeaderId,
            @RequestParam(value = "allInterviews", required = false) String allInterviews,
            RedirectAttributes redirect, Model model) {
        InterviewStage stage = interviewStageService.getInterviewStageById(stageId);
        Candidate candidate = candidateService.getCandidateById(candidateId);
        Interview interview2 = interviewService.getInterviewById(interviewId);
        if (interview2.getInterviewStage().getName() != stage.getName()) {
            if (interviewService.existsByCandidateAndInterviewStage(candidate, stage)) {
                sendTime(schedule, model);
                getCandidateById(candidateId, model);
                findInterviewerForSelectBox(departHeadId,teamLeaderId, model);
                sendSelectedStage(stageId, model);
                getInterviewByCandidate(candidateId, model);
                model.addAttribute("interviewId", interviewId);
                model.addAttribute("btnValue", "Update");
                model.addAttribute("notSendEmailExist", "This interview stage have already been !!");
                if (jobDetailId != null) {
                    getJobDetailAndJobPositionName(jobDetailId, model);
                    return "jobDetail_Interview";
                }
                if (allInterviews != null) {
                    return "all_interviews";
                } else {
                    return "candidateInterview";
                }
            }
        }
        // set interviewer list to interview
        String interviewerId = null;
        if (departHeadId == null) {
            interviewerId = teamLeaderId;
        } else
            interviewerId = departHeadId;
        List<User> interviewerList = new ArrayList<>();
        if (departHeadId != null && teamLeaderId != null) {
            User departHead = userService.getById(departHeadId);
            User teamLeader = userService.getById(teamLeaderId);
            interviewerList.add(departHead);
            interviewerList.add(teamLeader);
        } else {
            User interviewer = userService.getById(interviewerId);
            interviewerList.add(interviewer);
        } //

        //
        InterviewSchedule schedule2 = interview2.getInterviewSchedule();
        InterviewType interviewType2 = intrerviewTypeService.getInterviewTypeById(interviewTypeId);
        InterviewStage interviewStage2 = interviewStageService.getInterviewStageById(stageId);
        schedule2.setInterviewDate(schedule.getInterviewDate());
        schedule2.setStartTime(schedule.getStartTime());
        schedule2.setEndTime(schedule.getEndTime());
        if (interviewType2.getMeetingUrl() == null) {
            schedule2.setInterviewLocation(location);
        } else
            schedule2.setInterviewLocation(location);
        schedule2.setInterviewType(interviewType2);

        interview2.setInterviewerList(interviewerList);
        interview2.setInterviewSchedule(schedule2);
        interview2.setInterviewStage(interviewStage2);
        interviewService.saveInterview(interview2);
        //to send udpate success box with candidate name
        String message=candidate.getName()+"'s interview schedule has updated successfully.";
        if (jobDetailId != null) {
            redirect.addFlashAttribute("interviewUpdateSuccess", message);
            return "redirect:/mng/interview/jobDetailInterview/"+jobDetailId;
        }
        if (allInterviews != null) {
            redirect.addFlashAttribute("interviewUpdateSuccess", message);
            return "redirect:/mng/interview/allInterviews";
        } else {
            redirect.addAttribute("candidateId", candidateId);
            redirect.addFlashAttribute("interviewUpdateSuccess", message);
            return "redirect:/mng/interview/candidateInterview";
        }
    }

    // for showing jobDetail interview list
    @GetMapping("/jobDetailInterview/{jobDetailId}")
    public String interviewsByJobDetailId(@PathVariable Long jobDetailId, Model model) {
        getInterviewByJobDetailId(jobDetailId, model);
        JobDetail jobDetail = jobDetailsService.getById(jobDetailId);
        String jobPosition = "";
        if (jobDetail.getJobLevel() != null) {
            jobPosition += jobDetail.getJobLevel().getName() + " ";
        }
        jobPosition += jobDetail.getJobTitle().getName();
        model.addAttribute("jobDetail", jobDetail);
        model.addAttribute("jobPosition", jobPosition);
        model.addAttribute("jobDetailId", jobDetailId);
        return "jobDetail_Interview";
    }

    // to send mail interview
    @PostMapping("/sendMail")
    public String sendInterviewMail(@ModelAttribute("emailTemplate") EmailTemplate emailTemplate,
            @RequestParam String adminEmail, String candidateEmail, long candidateId, Model model,
            @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments,
            @RequestParam int redirectPage,
            long interviewId, String nullAttachment, RedirectAttributes redirect) throws MessagingException {
        Candidate candidate = candidateService.getCandidateById(candidateId);
        // for check redirect page
        String page = null;
        if (redirectPage == 0) {
            redirect.addAttribute("jobDetailId", candidate.getJobDetail().getId());
            page = "redirect:/mng/interview/jobDetailInterview";
        }
        if (redirectPage == 1) {
            page = "redirect:/mng/interview/allInterviews";
        }
        if (redirectPage == 2) {
            redirect.addAttribute("candidateId", candidateId);
            page = "redirect:/mng/interview/candidateInterview";
        }
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
        List<Interview> interviewList = interviewService.getAllInterviewByCandidateId(candidateId);
        for (Interview interview : interviewList) {
            Interview updateInterview = interviewService.getInterviewById(interview.getId());
            updateInterview.setMailStatus(Mail_Status.NEXT_MAIL_SEND);
            updateInterview.setStatus(Interview_Status.FINISHED);
            interviewService.saveInterview(updateInterview);
        }
        Interview updInterview = interviewService.getInterviewById(interviewId);
        updInterview.setMailStatus(Mail_Status.SEND);
        updInterview.setStatus(Interview_Status.ONGOING);
        updInterview.setResult(Interview_Result.PENDING);
        interviewService.saveInterview(updInterview);
        //add candidate final result when interview mail send
        candidate.setFinalResult(Interview_Result.PENDING);
        //add CV status CONSIDERING when send interview mail
        candidate.getCurriculumVitae().setStatus(CVStatus.CONSIDERING);
        candidateService.save(candidate);
        redirect.addFlashAttribute("mailSuccess", "Mail is successfully sent");
        return page;
    }

    // to add interview result by fetch method
    @PostMapping("/addInterviewResult")
    public ResponseEntity<String> addInterviewResult(@RequestBody Interview interview) {
        Interview interview1 = interviewService.getInterviewById(interview.getId());
        interview1.setResult(interview.getResult());
        interview1.setStatus(interview.getStatus());
        interview1.getCandidate().setFinalResult(interview.getResult());
        interviewService.saveInterview(interview1);
        return ResponseEntity.ok("success");
    }

    // to get interview type when change select box by fetch method
    @PostMapping("/getInterviewType")
    public ResponseEntity<InterviewType> getInterviewType(@RequestBody short typeId) {
        InterviewType interviewType = intrerviewTypeService.getInterviewTypeById(typeId);
        return ResponseEntity.ok(interviewType);
    }

    // send interview by id for edit using fetch
    @PostMapping("/editInterview")
    public ResponseEntity<InterviewEditDto> sendInterviewId(@RequestBody Long interviewId) {
        Interview interview = interviewService.getInterviewById(interviewId);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String starDate = dateFormat.format(interview.getInterviewSchedule().getInterviewDate());
        String startTime = getStartTime(interview.getInterviewSchedule());
        String endTime = getEndTime(interview.getInterviewSchedule());
        String typeValue = interview.getInterviewSchedule().getInterviewLocation();
        List<String> rollNameList = new ArrayList<>();
        List<String> interviewerNameList = new ArrayList<>();
        List<String> idList = new ArrayList<>();
        for (User user : interview.getInterviewerList()) {
            rollNameList.add(user.getRole().getName());
            interviewerNameList.add(user.getName());
        }
        idList.add(interview.getJobDetail().getTeam().getDepartment().getDepartmentHead().getId());
        idList.add(interview.getJobDetail().getTeam().getTeamLeader().getId());
        String jobLevelName = null;
        String jobDetailName = interview.getJobDetail().getJobTitle().getName();
        String candidateName = interview.getCandidate().getName();
        if (interview.getJobDetail().getJobLevel() != null) {
            jobLevelName = interview.getJobDetail().getJobLevel().getName();
        }
        InterviewEditDto editDto = new InterviewEditDto(interviewId, starDate, startTime, endTime,
                interview.getInterviewSchedule().getStartTime(), interview.getInterviewSchedule().getEndTime(),
                interview.getInterviewSchedule().getInterviewType().getName(), typeValue,
                rollNameList, interview.getInterviewStage().getName(), interviewerNameList,
                idList, interview.getResult(),
                interview.getJobDetail().getTeam().getDepartment().getDepartmentHead().getName(),
                interview.getJobDetail().getTeam().getTeamLeader().getName(), candidateName, jobDetailName,
                jobLevelName);
        return ResponseEntity.ok(editDto);
    }

    // to send candidate count for interview result confirmation box by ajax
    @GetMapping("/candidateCount/{id}")
    @ResponseBody
    public ResponseEntity<Integer> getCandidateCount(@PathVariable Long id) {
        List<Interview> interviewList = interviewService.getAllInterviewByCandidateId(id);
        return ResponseEntity.ok(interviewList.size());
    }

    // check interview result when create interview by ajax
    @GetMapping("/checkInterviewResult/{id}")
    @ResponseBody
    public ResponseEntity<Boolean> checkInterviewResult(@PathVariable Long id) {
        Candidate candidate = candidateService.getCandidateById(id);
        boolean check = false;
        if (interviewService.existsByCandidateAndResult(candidate, Interview_Result.PASSED)
                || interviewService.existsByCandidateAndResult(candidate, Interview_Result.PENDING) ||
                interviewService.existsByCandidateAndResult(candidate, Interview_Result.FAILED) ||
                interviewService.existsByCandidateAndResult(candidate, Interview_Result.CANCEL)) {
            check = true;
        }
        return ResponseEntity.ok(check);
    }

    // check interview result when send mail by ajax
    @GetMapping("/checkIneterviewResultByMailStatus/{id}")
    @ResponseBody
    public ResponseEntity<Boolean> checkResultByMailStatus(@PathVariable Long id) {
        List<Interview> interviewsList = interviewService.getAllInterviewByCandidateId(id);
        boolean check = false;
        for (Interview interview : interviewsList) {
            if (interview.getMailStatus() == Mail_Status.SEND
                    && interview.getResult() != Interview_Result.NEXT_INTERVIEW) {
                check = true;
            }
        }
        return ResponseEntity.ok(check);
    }

    // check mail status when create interview by ajax
    @GetMapping("/checkMailStatus/{id}")
    @ResponseBody
    public ResponseEntity<Boolean> checkMailStatusByCandidate(@PathVariable Long id) {
        boolean check = false;
        Candidate candidate = candidateService.getCandidateById(id);
        if (interviewService.existsByCandidateAndMailStatus(candidate, Mail_Status.NOT_SEND)) {
            check = true;
        }
        return ResponseEntity.ok(check);
    }

    // create method to get all candidates
    public void getAllCandidateByJobDetail(long id, Model model) {
        List<Candidate> candidates = candidateService.getAllCandidateByJobDetailId(id);
        model.addAttribute("candidateList", candidates);
    }

    // create method to get interviews by candidate
    public void getInterviewByCandidate(long candidateId, Model model) {
        List<Interview> interviewList = interviewService.getAllInterviewByCandidateId(candidateId);
        try {
            if (interviewList.isEmpty()) {
                model.addAttribute("EmptyInterviewList", true);
                throw new InterviewByCandidateNotFoundException(
                        "Interviews have not found in candidate Id " + candidateId);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        model.addAttribute("candidateInterviewCount", interviewList.size());
        model.addAttribute("interviewList", interviewList);
    }

    // create method to get candidate by id
    public void getCandidateById(long id, Model model) {
        Candidate candidate = candidateService.getCandidateById(id);
        model.addAttribute("candidate", candidate);
    }

    // create method to get interviews by jobDetail
    public void getInterviewByJobDetailId(long jobDetailId, Model model) {
        List<Interview> interviewList = interviewService.getAllInterviewByJobDetailId(jobDetailId);

        try {
            if (interviewList.isEmpty()) {
                model.addAttribute("EmptyInterviewList", true);
                throw new InterviewByCandidateNotFoundException(
                        "Interviews have not found in jobdetail Id " + jobDetailId);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        model.addAttribute("interviewList", interviewList);
    }

    // for send selected interview stage id
    public void sendSelectedStage(Short stageId, Model model) {
        InterviewStage stage = interviewStageService.getInterviewStageById(stageId);
        model.addAttribute("selectStage", stage);
    }

    // to check where select interviewer box
    public void findInterviewerForSelectBox(String departId, String teamLeaderId, Model model) {
        String id = null;
        if (departId == null) {
            id = teamLeaderId;
        } else
            id = departId;
        User interviewer = userService.getById(id);
        if (departId != null && teamLeaderId != null) {
            model.addAttribute("interviewerDepart", interviewer);
            model.addAttribute("interviewerTeam", interviewer);
        }
        if (interviewer.getRole().getName().contains("DEPART")) {
            model.addAttribute("interviewerDepart", interviewer);
        } else
            model.addAttribute("interviewerTeam", interviewer);
    }

    // to use time format value for return
    public void sendTime(InterviewSchedule schedule, Model model) {
        String time1 = getStartTime(schedule);
        String time2 = getEndTime(schedule);
        model.addAttribute("time1", time1);
        model.addAttribute("time2", time2);

    }

    // to get formatted interview startTime
    public String getStartTime(InterviewSchedule schedule) {
        String time;
        int getTime = Integer.parseInt(schedule.getStartTime().substring(0, 2));
        if (schedule.getStartTime().contains("AM") && getTime == 12) {
            return time = "00" + schedule.getStartTime().substring(2, 5);
        }
        if (schedule.getStartTime().contains("PM") && getTime != 12) {
            return time = (getTime + 12) +
                    schedule.getStartTime().substring(2, 5);
        } else {
            return time = schedule.getStartTime().substring(0, 5);
        }
    }

    // to get formatted interview endTime
    public String getEndTime(InterviewSchedule schedule) {
        String time;
        int getTime = Integer.parseInt(schedule.getEndTime().substring(0, 2));
        if (schedule.getEndTime().contains("AM") && getTime == 12) {
            return time = "00" + schedule.getEndTime().substring(2, 5);
        }
        if (schedule.getEndTime().contains("PM") && getTime != 12) {
            return time = (getTime + 12) +
                    schedule.getEndTime().substring(2, 5);
        } else {
            return time = schedule.getEndTime().substring(0, 05);
        }
    }

    // to get job detail and job position name
    public void getJobDetailAndJobPositionName(Long jobDetailId, Model model) {
        JobDetail jobDetail = jobDetailsService.getById(jobDetailId);
        String jobPosition = "";
        jobPosition += jobDetail.getJobTitle().getName();
        if (jobDetail.getJobLevel() != null) {
            jobPosition += "("+jobDetail.getJobLevel().getName() + ")";
        }
        model.addAttribute("jobPosition", jobPosition);
        model.addAttribute("jobDetail", jobDetail);

    }

    @ModelAttribute("jobList")
    public List<JobDetail> getAllJobDetail() {
        return jobDetailsService.findAllJobDetails();
    }

    // create method to get all interview stage
    @ModelAttribute("stageList")
    public List<InterviewStage> getAllInterviewStage() {
        return interviewStageService.getActiveInterviewStages();
    }

    @ModelAttribute("schedule")
    public InterviewSchedule getNewInterviewSchedule() {
        return new InterviewSchedule();
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

    @ModelAttribute("emailTypeListByInterviewInvite")
    public List<EmailType> getAllEmailTypesByInterviewInvite(){
        return emailTypeService.getAllEmailTypeByActiveAndEmailRole(Status.ACTIVE, EmailRole.INTERVIEW_INVITATION);
    }

}
