package gp.pyinsa.jroms.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import gp.pyinsa.jroms.constants.NotificationType;
import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.models.AppUserDetails;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.JobTitle;
import gp.pyinsa.jroms.models.NotificationMessage;
import gp.pyinsa.jroms.models.Role;
import gp.pyinsa.jroms.models.User;
import gp.pyinsa.jroms.services.NotificationMessageService;

@SpringBootTest
@AutoConfigureMockMvc
public class NotificationControllerTest {
    @MockBean
    private NotificationMessageService notificationService;

    @Autowired
    private MockMvc mockMvc;

    private static String baseUrl;

    private static AppUserDetails userDetails;

    @BeforeAll
    static void setup() {
        baseUrl = "/mng";
        Role role = new Role((short) 1, "ROLE_ADMIN");
        User user = new User("U001", "John", "john", "john@gmail.com", "john", true, Status.ACTIVE, role, new Date(),
                new Date());
        userDetails = new AppUserDetails(user);
    }

    @Test
    void getApplicantNotifications_WhenCalled_ShouldReturnApplicantNotifications() throws Exception {
        NotificationMessage msg = new NotificationMessage();
        msg.setId(1L);
        msg.setCandidateId(2L);
        msg.setCreatedDate(new Date());
        msg.setMessage("This is a message.");
        JobDetail jobDetail = new JobDetail();
        jobDetail.setJobTitle(new JobTitle((short) 1, "Java Developer", Status.ACTIVE));
        msg.setJobDetail(jobDetail);
        msg.setNotificationType(NotificationType.CANDIDATE_APPLICATION);

        when(notificationService.getTop50CandidateNotifications()).thenReturn(List.of(msg));

        this.mockMvc.perform(get(baseUrl + "/notifications/applicants").with(user(userDetails)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].message", Matchers.is(msg.getMessage())));
    }

    @Test
    void getApplicantNotificationGroup_WhenCalled_ShouldReturnNotificationsByJobDetail() throws Exception {
        NotificationMessage msg = new NotificationMessage();
        msg.setId(1L);
        msg.setCandidateId(2L);
        msg.setCreatedDate(new Date());
        msg.setMessage("This is a message.");
        JobDetail jobDetail = new JobDetail();
        jobDetail.setCreatedDate(new Date());
        jobDetail.setJobTitle(new JobTitle((short) 1, "Java Developer", Status.ACTIVE));
        msg.setJobDetail(jobDetail);
        msg.setNotificationType(NotificationType.CANDIDATE_APPLICATION);

        when(notificationService.getTop50ActiveJobs()).thenReturn(List.of(jobDetail));
        when(notificationService.getCandidatesInJobDetailByDateDesc(jobDetail)).thenReturn(List.of());

        this.mockMvc.perform(get(baseUrl + "/notifications/applicants/group").with(user(userDetails)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].jobPosition", Matchers.is(jobDetail.getJobTitle().getName())));

    }

    @Test
    void getInterviewNotifications_WhenCalled_ShouldReturnInterviewNotifications() throws Exception {
        NotificationMessage msg = new NotificationMessage();
        msg.setId(1L);
        msg.setCandidateId(2L);
        msg.setCreatedDate(new Date());
        msg.setMessage("This is a message.");
        msg.setInterviewers(List.of());
        JobDetail jobDetail = new JobDetail();
        jobDetail.setCreatedDate(new Date());
        jobDetail.setJobTitle(new JobTitle((short) 1, "Java Developer", Status.ACTIVE));
        msg.setJobDetail(jobDetail);
        msg.setNotificationType(NotificationType.CANDIDATE_APPLICATION);

        String username = "john";

        when(notificationService.getTop50InterviewNotificationsForInterviewer(username)).thenReturn(List.of(msg));

        this.mockMvc
                .perform(get(baseUrl + "/notifications/interviews").with(user(userDetails)).param("username", username))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].message", Matchers.is(msg.getMessage())));

    }

}
