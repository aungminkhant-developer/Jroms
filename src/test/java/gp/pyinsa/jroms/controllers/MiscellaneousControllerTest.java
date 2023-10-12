package gp.pyinsa.jroms.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
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
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.models.AppUserDetails;
import gp.pyinsa.jroms.models.InterviewStage;
import gp.pyinsa.jroms.models.JobLevel;
import gp.pyinsa.jroms.models.JobTitle;
import gp.pyinsa.jroms.models.JobType;
import gp.pyinsa.jroms.models.Role;
import gp.pyinsa.jroms.models.User;
import gp.pyinsa.jroms.services.InterviewStageService;
import gp.pyinsa.jroms.services.JobLevelService;
import gp.pyinsa.jroms.services.JobTitleService;
import gp.pyinsa.jroms.services.JobTypeService;

@SpringBootTest
@AutoConfigureMockMvc
public class MiscellaneousControllerTest {

    @MockBean
    private JobTitleService jobTitleService;

    @MockBean
    private JobLevelService jobLevelService;

    @MockBean
    private JobTypeService jobTypeService;

    @MockBean
    private InterviewStageService interviewStageService;

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
    void miscellaneousPage_WhenCalled_ShouldReturnPage() throws Exception {
        this.mockMvc.perform(get(baseUrl + "/miscellaneous").with(user(userDetails)))
                .andExpect(status().is(200))
                .andExpect(model().attributeExists("newJobTitle"))
                .andExpect(model().attributeExists("updateJobTitle"))
                .andExpect(model().attributeExists("jobTitles"))
                .andExpect(model().attributeExists("newJobLevel"))
                .andExpect(model().attributeExists("updateJobLevel"))
                .andExpect(model().attributeExists("jobLevels"))
                .andExpect(model().attributeExists("newJobType"))
                .andExpect(model().attributeExists("updateJobType"))
                .andExpect(model().attributeExists("jobTypes"))
                .andExpect(model().attributeExists("newInterviewStage"))
                .andExpect(model().attributeExists("updateInterviewStage"))
                .andExpect(model().attributeExists("interviewStages"))
                .andExpect(view().name("miscellaneous"));
    }

    /* Job Title Module START */
    @Test
    void addJobTitle_WhenDataIsValid_ShouldSaveAndRedirect() throws Exception {
        JobTitle newJobTitle = new JobTitle((short) 1, "Java Developer", Status.ACTIVE);

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-titles/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newJobTitle",
                        newJobTitle))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/miscellaneous"));
    }

    private static Stream<Arguments> faultyJobTitles() {
        JobTitle jobTitle1 = new JobTitle((short) 1, "", Status.ACTIVE);
        JobTitle jobTitle2 = new JobTitle((short) 1, null, Status.ACTIVE);

        return Stream.of(
                Arguments.of(jobTitle1),
                Arguments.of(jobTitle2));
    }

    @ParameterizedTest
    @MethodSource("faultyJobTitles")
    void addJobTitle_WhenDataHasErrors_ShouldGoBackToViewPage(JobTitle newJobTitle) throws Exception {

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-titles/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newJobTitle",
                        newJobTitle))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("miscellaneous"));
    }

    @Test
    void addJobTitle_WhenServerError_ShouldGoBackToViewPage() throws Exception {
        JobTitle newJobTitle = new JobTitle((short) 1, "Java Developer", Status.ACTIVE);
        Mockito.doThrow(new ResourceAlreadyExistsException("")).when(jobTitleService).addNewJobTitle(newJobTitle);

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-titles/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newJobTitle",
                        newJobTitle))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("miscellaneous"));
    }

    @Test
    void updateJobTitle_WhenDataIsValid_ShouldUpdateAndRedirect() throws Exception {
        JobTitle updateJobTitle = new JobTitle((short) 1, "Java Developer", Status.ACTIVE);

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-titles/update").with(user(userDetails)).with(csrf()).flashAttr("updateJobTitle",
                        updateJobTitle))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/miscellaneous"));
    }

    @ParameterizedTest
    @MethodSource("faultyJobTitles")
    void updateJobTitle_WhenDataHasErrors_ShouldGoBackToViewPage(JobTitle updateJobTitle) throws Exception {

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-titles/update").with(user(userDetails)).with(csrf()).flashAttr(
                        "updateJobTitle",
                        updateJobTitle))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("miscellaneous"));
    }

    @Test
    void updateJobTitle_WhenServerError_ShouldGoBackToViewPage() throws Exception {
        JobTitle updateJobTitle = new JobTitle((short) 1, "Java Developer", Status.ACTIVE);
        Mockito.doThrow(new ResourceAlreadyExistsException("")).when(jobTitleService).updateJobTitle(updateJobTitle);

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-titles/update").with(user(userDetails)).with(csrf()).flashAttr(
                        "updateJobTitle",
                        updateJobTitle))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("miscellaneous"));
    }

    @Test
    void deleteJobTitle_WhenCalled_ShouldDeleteAndRedirect() throws Exception {
        String jobTitleId = "1";

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-titles/delete").with(user(userDetails)).with(csrf()).param("id", jobTitleId))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/miscellaneous"));
    }
    /* Job Title Module END */

    /* JobLevel Module START */
    @Test
    void addJobLevel_WhenDataIsValid_ShouldSaveAndRedirect() throws Exception {
        JobLevel newJobLevel = new JobLevel((short) 1, "Senior", Status.ACTIVE);

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-levels/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newJobLevel",
                        newJobLevel))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/miscellaneous"));
    }

    private static Stream<Arguments> faultyJobLevels() {
        JobLevel jobLevel1 = new JobLevel((short) 1, "", Status.ACTIVE);
        JobLevel jobLevel2 = new JobLevel((short) 1, null, Status.ACTIVE);

        return Stream.of(
                Arguments.of(jobLevel1),
                Arguments.of(jobLevel2));
    }

    @ParameterizedTest
    @MethodSource("faultyJobLevels")
    void addJobLevel_WhenDataHasErrors_ShouldGoBackToViewPage(JobLevel newJobLevel) throws Exception {

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-levels/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newJobLevel",
                        newJobLevel))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("miscellaneous"));
    }

    @Test
    void addJobLevel_WhenServerError_ShouldGoBackToViewPage() throws Exception {
        JobLevel newJobLevel = new JobLevel((short) 1, "Senior", Status.ACTIVE);
        Mockito.doThrow(new ResourceAlreadyExistsException("")).when(jobLevelService).addNewJobLevel(newJobLevel);

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-levels/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newJobLevel",
                        newJobLevel))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("miscellaneous"));
    }

    @Test
    void updateJobLevel_WhenDataIsValid_ShouldUpdateAndRedirect() throws Exception {
        JobLevel updateJobLevel = new JobLevel((short) 1, "Senior", Status.ACTIVE);

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-levels/update").with(user(userDetails)).with(csrf()).flashAttr("updateJobLevel",
                        updateJobLevel))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/miscellaneous"));
    }

    @ParameterizedTest
    @MethodSource("faultyJobLevels")
    void updateJobLevel_WhenDataHasErrors_ShouldGoBackToViewPage(JobLevel updateJobLevel) throws Exception {

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-levels/update").with(user(userDetails)).with(csrf()).flashAttr(
                        "updateJobLevel",
                        updateJobLevel))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("miscellaneous"));
    }

    @Test
    void updateJobLevel_WhenServerError_ShouldGoBackToViewPage() throws Exception {
        JobLevel updateJobLevel = new JobLevel((short) 1, "Senior", Status.ACTIVE);
        Mockito.doThrow(new ResourceAlreadyExistsException("")).when(jobLevelService).updateJobLevel(updateJobLevel);

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-levels/update").with(user(userDetails)).with(csrf()).flashAttr(
                        "updateJobLevel",
                        updateJobLevel))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("miscellaneous"));
    }

    @Test
    void deleteJobLevel_WhenCalled_ShouldDeleteAndRedirect() throws Exception {
        String jobLevelId = "1";

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-levels/delete").with(user(userDetails)).with(csrf()).param("id", jobLevelId))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/miscellaneous"));
    }
    /* Job Title Module END */
    
    /* JobType Module START */
    @Test
    void addJobType_WhenDataIsValid_ShouldSaveAndRedirect() throws Exception {
        JobType newJobType = new JobType((short) 1, "Full Time", Status.ACTIVE);

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-types/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newJobType",
                        newJobType))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/miscellaneous"));
    }

    private static Stream<Arguments> faultyJobTypes() {
        JobType jobType1 = new JobType((short) 1, "", Status.ACTIVE);
        JobType jobType2 = new JobType((short) 1, null, Status.ACTIVE);

        return Stream.of(
                Arguments.of(jobType1),
                Arguments.of(jobType2));
    }

    @ParameterizedTest
    @MethodSource("faultyJobTypes")
    void addJobType_WhenDataHasErrors_ShouldGoBackToViewPage(JobType newJobType) throws Exception {

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-types/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newJobType",
                        newJobType))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("miscellaneous"));
    }

    @Test
    void addJobType_WhenServerError_ShouldGoBackToViewPage() throws Exception {
        JobType newJobType = new JobType((short) 1, "Full Time", Status.ACTIVE);
        Mockito.doThrow(new ResourceAlreadyExistsException("")).when(jobTypeService).addNewJobType(newJobType);

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-types/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newJobType",
                        newJobType))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("miscellaneous"));
    }

    @Test
    void updateJobType_WhenDataIsValid_ShouldUpdateAndRedirect() throws Exception {
        JobType updateJobType = new JobType((short) 1, "Full Time", Status.ACTIVE);

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-types/update").with(user(userDetails)).with(csrf()).flashAttr("updateJobType",
                        updateJobType))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/miscellaneous"));
    }

    @ParameterizedTest
    @MethodSource("faultyJobTypes")
    void updateJobType_WhenDataHasErrors_ShouldGoBackToViewPage(JobType updateJobType) throws Exception {

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-types/update").with(user(userDetails)).with(csrf()).flashAttr(
                        "updateJobType",
                        updateJobType))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("miscellaneous"));
    }

    @Test
    void updateJobType_WhenServerError_ShouldGoBackToViewPage() throws Exception {
        JobType updateJobType = new JobType((short) 1, "Full Time", Status.ACTIVE);
        Mockito.doThrow(new ResourceAlreadyExistsException("")).when(jobTypeService).updateJobType(updateJobType);

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-types/update").with(user(userDetails)).with(csrf()).flashAttr(
                        "updateJobType",
                        updateJobType))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("miscellaneous"));
    }

    @Test
    void deleteJobType_WhenCalled_ShouldDeleteAndRedirect() throws Exception {
        String jobTypeId = "1";

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/job-types/delete").with(user(userDetails)).with(csrf()).param("id", jobTypeId))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/miscellaneous"));
    }
    /* JobType Module END */

    /* InterviewStage Module START */
    @Test
    void addInterviewStage_WhenDataIsValid_ShouldSaveAndRedirect() throws Exception {
        InterviewStage newInterviewStage = new InterviewStage((short) 1, "First Stage", Status.ACTIVE);

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/interview-stages/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newInterviewStage",
                        newInterviewStage))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/miscellaneous"));
    }

    private static Stream<Arguments> faultyInterviewStages() {
        InterviewStage interviewStage1 = new InterviewStage((short) 1, "", Status.ACTIVE);
        InterviewStage interviewStage2 = new InterviewStage((short) 1, null, Status.ACTIVE);

        return Stream.of(
                Arguments.of(interviewStage1),
                Arguments.of(interviewStage2));
    }

    @ParameterizedTest
    @MethodSource("faultyInterviewStages")
    void addInterviewStage_WhenDataHasErrors_ShouldGoBackToViewPage(InterviewStage newInterviewStage) throws Exception {

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/interview-stages/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newInterviewStage",
                        newInterviewStage))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("miscellaneous"));
    }

    @Test
    void addInterviewStage_WhenServerError_ShouldGoBackToViewPage() throws Exception {
        InterviewStage newInterviewStage = new InterviewStage((short) 1, "First Stage", Status.ACTIVE);
        Mockito.doThrow(new ResourceAlreadyExistsException("")).when(interviewStageService).addNewInterviewStage(newInterviewStage);

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/interview-stages/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newInterviewStage",
                        newInterviewStage))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("miscellaneous"));
    }

    @Test
    void updateInterviewStage_WhenDataIsValid_ShouldUpdateAndRedirect() throws Exception {
        InterviewStage updateInterviewStage = new InterviewStage((short) 1, "First Stage", Status.ACTIVE);

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/interview-stages/update").with(user(userDetails)).with(csrf()).flashAttr("updateInterviewStage",
                        updateInterviewStage))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/miscellaneous"));
    }

    @ParameterizedTest
    @MethodSource("faultyInterviewStages")
    void updateInterviewStage_WhenDataHasErrors_ShouldGoBackToViewPage(InterviewStage updateInterviewStage) throws Exception {

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/interview-stages/update").with(user(userDetails)).with(csrf()).flashAttr(
                        "updateInterviewStage",
                        updateInterviewStage))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("miscellaneous"));
    }

    @Test
    void updateInterviewStage_WhenServerError_ShouldGoBackToViewPage() throws Exception {
        InterviewStage updateInterviewStage = new InterviewStage((short) 1, "First Stage", Status.ACTIVE);
        Mockito.doThrow(new ResourceAlreadyExistsException("")).when(interviewStageService).updateInterviewStage(updateInterviewStage);

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/interview-stages/update").with(user(userDetails)).with(csrf()).flashAttr(
                        "updateInterviewStage",
                        updateInterviewStage))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("miscellaneous"));
    }

    @Test
    void deleteInterviewStage_WhenCalled_ShouldDeleteAndRedirect() throws Exception {
        String interviewStageId = "1";

        this.mockMvc
                .perform(post(baseUrl + "/miscellaneous/interview-stages/delete").with(user(userDetails)).with(csrf()).param("id", interviewStageId))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/miscellaneous"));
    }
    /* InterviewStage Module END */

}
