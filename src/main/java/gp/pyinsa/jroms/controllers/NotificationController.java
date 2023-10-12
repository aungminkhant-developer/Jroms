package gp.pyinsa.jroms.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gp.pyinsa.jroms.dtos.JobDetailApplicantDto;
import gp.pyinsa.jroms.dtos.NotificationDto;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.NotificationMessage;
import gp.pyinsa.jroms.services.NotificationMessageService;
import gp.pyinsa.jroms.utils.DateFormatUtil;

@Controller
@RequestMapping("/mng")
public class NotificationController {

    @Autowired
    private NotificationMessageService notificationService;

    @GetMapping("/notifications/applicants")
    @ResponseBody
    public ResponseEntity<List<NotificationDto>> getApplicantNotifications() {

        List<NotificationMessage> notifications = notificationService.getTop50CandidateNotifications();
        List<NotificationDto> dtos = notifications.stream().map(noti -> {
            NotificationDto dto = new NotificationDto();
            dto.setId(noti.getId());
            dto.setJobDetailId(noti.getJobDetail().getId());
            String jobLevel = noti.getJobDetail().getJobLevel() != null
                    ? noti.getJobDetail().getJobLevel().getName() + " "
                    : "";
            dto.setJobPosition(jobLevel + noti.getJobDetail().getJobTitle().getName());
            dto.setMessage(noti.getMessage());
            dto.setCreatedDate(DateFormatUtil.formatDateToString(noti.getCreatedDate()));
            dto.setCandidateId(noti.getCandidateId());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/notifications/applicants/group")
    @ResponseBody
    public ResponseEntity<List<JobDetailApplicantDto>> getApplicantNotificationGroup() {
        List<JobDetail> jobDetails = notificationService.getTop50ActiveJobs();
        List<JobDetailApplicantDto> dtoList = new ArrayList<>();
        jobDetails.forEach(jobDetail -> {
            JobDetailApplicantDto dto = new JobDetailApplicantDto();
            List<Candidate> candidates = notificationService.getCandidatesInJobDetailByDateDesc(jobDetail);
            dto.setApplicantCount((long) candidates.size());
            dto.setJobDetailId(jobDetail.getId());
            dto.setJobPosition(null);
            String jobLevel = jobDetail.getJobLevel() != null ? jobDetail.getJobLevel().getName() + " " : "";
            dto.setJobPosition(jobLevel + jobDetail.getJobTitle().getName());

            if(candidates.size() > 0) {
                dto.setLastAppliedDate(DateFormatUtil.formatDateToString(candidates.get(0).getCreatedDate()));
            } else {
                dto.setLastAppliedDate(DateFormatUtil.formatDateToString(jobDetail.getCreatedDate()));
            }
            dtoList.add(dto);
        });

        Collections.sort(dtoList, new Comparator<JobDetailApplicantDto>() {

            @Override
            public int compare(JobDetailApplicantDto o1, JobDetailApplicantDto o2) {
                Date date1 = DateFormatUtil.parseStringToDatePrefixed(o1.getLastAppliedDate());
                Date date2 = DateFormatUtil.parseStringToDatePrefixed(o2.getLastAppliedDate());

                return date1.getTime() > date2.getTime() ? -1 : 1;
            }
            
        });

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/notifications/interviews")
    @ResponseBody
    public ResponseEntity<List<NotificationDto>> getInterviewNotifications(@RequestParam("username") String username) {

        List<NotificationMessage> notifications = notificationService.getTop50InterviewNotificationsForInterviewer(username);

        List<NotificationDto> dtos = notifications.stream().map(noti -> {
            NotificationDto dto = new NotificationDto();
            dto.setId(noti.getId());
            dto.setJobDetailId(noti.getJobDetail().getId());
            String jobLevel = noti.getJobDetail().getJobLevel() != null
                    ? noti.getJobDetail().getJobLevel().getName() + " "
                    : "";
            dto.setJobPosition(jobLevel + noti.getJobDetail().getJobTitle().getName());
            dto.setMessage(noti.getMessage());
            dto.setCreatedDate(DateFormatUtil.formatDateToString(noti.getCreatedDate()));
            dto.setUsernames(noti.getInterviewers().stream().map(intv -> intv.getUsername()).collect(Collectors.toList()));
            dto.setCandidateId(noti.getCandidateId());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
