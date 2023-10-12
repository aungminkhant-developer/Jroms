package gp.pyinsa.jroms.controllers;

import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import gp.pyinsa.jroms.constants.Interview_Result;
import gp.pyinsa.jroms.constants.Interview_Status;
import gp.pyinsa.jroms.constants.Mail_Status;
import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.models.AppUserDetails;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.models.Interview;
import gp.pyinsa.jroms.models.InterviewSchedule;
import gp.pyinsa.jroms.models.InterviewStage;
import gp.pyinsa.jroms.models.InterviewType;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.Location;
import gp.pyinsa.jroms.models.Role;
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
import gp.pyinsa.jroms.services.TeamService;
import gp.pyinsa.jroms.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
public class InterviewControllerTest {

    @MockBean
    private JobDetailService jobDetailService;

    @MockBean
    private CandidateService candidateService;

    @MockBean
    private InterviewService interviewService;

    @MockBean
    private InterviewStageService interviewStageService;

    @MockBean
    private IntrerviewTypeService intrerviewTypeService;

    @MockBean
    private InterviewScheduleService interviewScheduleService;

    @MockBean
    private UserService userService;

    @MockBean
    private DepartmentService departmentService;

    @MockBean
    private TeamService teamService;

    @MockBean
    private EmailTypeService emailTypeService;

    @MockBean
    private MailService mailService;

    private static String baserUrl;

    private static AppUserDetails userDetails;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void setUp(){
        baserUrl="/mng/interview";
        Role role = new Role((short) 1, "ROLE_ADMIN");
        User user = new User("U001", "John", "john", "john@gmail.com", "john", true, Status.ACTIVE, role, new Date(),
                new Date());
        userDetails = new AppUserDetails(user);
    }

    private static Stream<Arguments> interviewList(){
        List<User> interviewList=List.of(new User(),new User());
        Interview interview1=new Interview((long)1, Interview_Status.ONGOING, Interview_Result.PASSED, Mail_Status.NOT_SEND, new Candidate(), new InterviewStage(), new InterviewSchedule(),new JobDetail(), interviewList);
        Interview interview2=new Interview((long)2, Interview_Status.FINISHED, Interview_Result.PENDING, Mail_Status.SEND, new Candidate(), new InterviewStage(), new InterviewSchedule(), new JobDetail(), interviewList);
        
        return Stream.of(
            Arguments.of(interview1),
            Arguments.of(interview2)
        );
    }

    @Test
    void getAllInterview_WhenGetInterviews_ShouldGoPage() throws Exception{
        List<User> interviewList=List.of(new User(),new User());
        Interview interview1=new Interview((long)1, Interview_Status.ONGOING, Interview_Result.PASSED, Mail_Status.NOT_SEND, new Candidate(), new InterviewStage(), new InterviewSchedule(),new JobDetail(), interviewList);
        Interview interview2=new Interview((long)2, Interview_Status.FINISHED, Interview_Result.PENDING, Mail_Status.SEND, new Candidate(), new InterviewStage(), new InterviewSchedule(), new JobDetail(), interviewList);
        Department department1=new Department("D001", "Department 1", new User(), Status.ACTIVE);
        Department department2=new Department("D002", "Department 2", new User(), Status.ACTIVE);
        Team team1=new Team("T001", "Team 1", new User(), new Department(), Status.ACTIVE);
        Team team2=new Team("T002", "Team 2", new User(), new Department(), Status.ACTIVE);
        InterviewType interviewType=new InterviewType((short)1,"Online","Hlaing",new Location());
        InterviewType interviewType2=new InterviewType((short)2,"In Person","Yangon",new Location());
        List<Interview> interviewsList=List.of(interview1,interview2);
        List<Department> departmentsList=List.of(department1,department2);
        List<Team> teamList=List.of(team1,team2);
        List<InterviewType> interviewTypes=List.of(interviewType,interviewType2);

        when(interviewService.getAllInterviews()).thenReturn(interviewsList);
        when(departmentService.getActiveDepartments()).thenReturn(departmentsList);
        when(teamService.getActiveTeams()).thenReturn(teamList);
        when(intrerviewTypeService.getAllInterviewTypes()).thenReturn(interviewTypes);

        this.mockMvc.perform(get(baserUrl+"/allInterviews")
                    .with(user(userDetails))
                    .with(csrf()))
                    .andExpect(status().is(200))
                    .andExpect(model().attributeExists("interviewList"))
                    .andExpect(model().attributeExists("department"))
                    .andExpect(model().attributeExists("team"))
                    .andExpect(view().name("all-interviews-fixed"));
    }

   
    
}
