package gp.pyinsa.jroms.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Date;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.JobDetailNotFoundException;
import gp.pyinsa.jroms.models.AppUserDetails;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.JobLevel;
import gp.pyinsa.jroms.models.JobTitle;
import gp.pyinsa.jroms.models.JobType;
import gp.pyinsa.jroms.models.Location;
import gp.pyinsa.jroms.models.Role;
import gp.pyinsa.jroms.models.Team;
import gp.pyinsa.jroms.models.User;
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

@SpringBootTest
@AutoConfigureMockMvc
public class JobDetailControllerTest {

    @MockBean
    private JobDetailService jobDetailService;

    @MockBean
    private JobTitleService jobTitleService;

    @MockBean
    private JobLevelService jobLevelService;

    @MockBean
    private WorkScheduleService workScheduleService;

    @MockBean
    private LocationService locationService;

    @MockBean
    private JobTypeService jobTypeService;

    @MockBean
    private TeamService teamService;

    @MockBean
    private CandidateService candidateService;

    @MockBean
    private DepartmentService departmentService; 

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
    void addJobDetailsForm_WhenCalled_ShouldReturnAddFormPage() throws Exception {
        this.mockMvc.perform(get(baseUrl + "/job-details/add").with(user(userDetails)))
                .andExpect(status().is(200))
                .andExpect(model().attributeExists("minExpireDate"))
                .andExpect(model().attributeExists("newJobDetail"))
                .andExpect(view().name("add-job-offer"));
    }

    @Test
    void addJobDetail_WhenDataIsValid_ShouldSaveAndRedirect() throws Exception {
        JobDetail newJobDetail = new JobDetail(1L, new JobTitle(), new JobLevel(), (short) 1, "400000 MMK", new WorkSchedule(), new Location(), new JobType(), new Team(), "Description", "Responsibilities", "Requirement", "Preferences", new Date(), new Date());

        this.mockMvc
                .perform(post(baseUrl + "/job-details/add").with(user(userDetails)).with(csrf()).flashAttr("newJobDetail",
                        newJobDetail))
                .andExpect(status().is(302))
                .andExpect(flash().attributeExists("jobDetailAddSuccess"))
                .andExpect(redirectedUrl("/mng/job-details/add"));

        verify(jobDetailService, times(1)).addNewJobDetail(any(JobDetail.class));
    }

    private static Stream<Arguments> faultyJobDetails() {
        JobDetail jd1 = new JobDetail(1L, new JobTitle(), new JobLevel(), (short) 0, "400000 MMK", new WorkSchedule(), new Location(), new JobType(), new Team(), "Description", "Responsibilities", "Requirement", "Preferences", new Date(), new Date());
        JobDetail jd2 = new JobDetail(1L, new JobTitle(), new JobLevel(), (short) 1, "", new WorkSchedule(), new Location(), new JobType(), new Team(), "Description", "Responsibilities", "Requirement", "Preferences", new Date(), new Date());
        JobDetail jd3 = new JobDetail(1L, new JobTitle(), new JobLevel(), (short) 1, null, new WorkSchedule(), new Location(), new JobType(), new Team(), "Description", "Responsibilities", "Requirement", "Preferences", new Date(), new Date());
        JobDetail jd4 = new JobDetail(1L, new JobTitle(), new JobLevel(), (short) 1, "400000 MMK", new WorkSchedule(), new Location(), new JobType(), new Team(), "", "Responsibilities", "Requirement", "Preferences", new Date(), new Date());
        JobDetail jd5 = new JobDetail(1L, new JobTitle(), new JobLevel(), (short) 1, "400000 MMK", new WorkSchedule(), new Location(), new JobType(), new Team(), "Description", "", "Requirement", "Preferences", new Date(), new Date());
        JobDetail jd6 = new JobDetail(1L, new JobTitle(), new JobLevel(), (short) 1, "400000 MMK", new WorkSchedule(), new Location(), new JobType(), new Team(), "Description", "Responsibilities", "", "Preferences", new Date(), new Date());

        return Stream.of(
                Arguments.of(jd1),
                Arguments.of(jd2),
                Arguments.of(jd3),
                Arguments.of(jd4),
                Arguments.of(jd5),
                Arguments.of(jd6));
    }

    @ParameterizedTest
    @MethodSource("faultyJobDetails")
    void addJobDetail_WhenDataHasErrors_ShouldGoBackToViewPage(JobDetail jobDetail) throws Exception {

        this.mockMvc
                .perform(post(baseUrl + "/job-details/add").with(user(userDetails)).with(csrf()).flashAttr("newJobDetail",
                        jobDetail))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("add-job-offer"));
    }

    @Test
    void updateJobDetailForm_WhenIdIsValid_ShouldReturnUpdateFormPage() throws Exception {
        long jobDetailId = 1L;
        JobDetail jobDetail = new JobDetail(1L, new JobTitle(), new JobLevel(), (short) 1, "400000 MMK", new WorkSchedule(), new Location(), new JobType(), new Team(), "Description", "Responsibilities", "Requirement", "Preferences", new Date(), new Date());

        when(jobDetailService.getById(jobDetailId)).thenReturn(jobDetail);

        this.mockMvc.perform(get(baseUrl + "/job-details/" + jobDetailId).with(user(userDetails)))
                .andExpect(status().is(200))
                .andExpect(model().attributeExists("minExpireDate", "isJobClosed", "updateJobDetail"))
                .andExpect(view().name("update-job-offer"));
    }

    @Test
    void updateJobDetailForm_WhenIdIsInvalid_ShouldThrowException() throws Exception {
        long jobDetailId = 1L;

        Mockito.doThrow(new JobDetailNotFoundException("")).when(jobDetailService).getById(jobDetailId);

        this.mockMvc.perform(get(baseUrl + "/job-details/" + jobDetailId).with(user(userDetails)))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/job-details/add"));
    }

    @Test
    void updateJobDetailForm_WhenDataIsValid_ShouldUpdateAndRedirect() throws Exception {
        JobDetail updateJobDetail = new JobDetail(1L, new JobTitle(), new JobLevel(), (short) 1, "400000 MMK", new WorkSchedule(), new Location(), new JobType(), new Team(), "Description", "Responsibilities", "Requirement", "Preferences", new Date(), new Date());

        this.mockMvc
                .perform(post(baseUrl + "/job-details/update").with(user(userDetails)).with(csrf()).flashAttr("updateJobDetail",
                updateJobDetail))
                .andExpect(status().is(302))
                .andExpect(flash().attributeExists("jobDetailUpdateSuccess"))
                .andExpect(redirectedUrl("/mng/job-details/" + updateJobDetail.getId()));

        verify(jobDetailService, times(1)).updateJobDetail(any(JobDetail.class));
    }
    
}
