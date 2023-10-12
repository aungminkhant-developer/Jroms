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
import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.models.Role;
import gp.pyinsa.jroms.models.Team;
import gp.pyinsa.jroms.models.User;
import gp.pyinsa.jroms.services.TeamService;
import gp.pyinsa.jroms.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
public class TeamControllerTest {

    @MockBean
    private TeamService teamService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private static String baseUrl;

    private static AppUserDetails userDetails;

    private static String deptId = "DPT01";

    @BeforeAll
    static void setup() {
        baseUrl = "/mng/departments/" + deptId;
        Role role = new Role((short) 1, "ROLE_ADMIN");
        User user = new User("U001", "John", "john", "john@gmail.com", "john", true, Status.ACTIVE, role, new Date(),
                new Date());
        userDetails = new AppUserDetails(user);
    }

    @Test
    void teamView_WhenCalled_ShouldLoadViewPage() throws Exception {
        this.mockMvc.perform(get(baseUrl + "/teams").with(user(userDetails)).param("dept-id", deptId))
                .andExpect(status().is(200))
                .andExpect(model().attributeExists("teams"))
                .andExpect(model().attributeExists("newTeam"))
                .andExpect(model().attributeExists("teamLeaders"))
                .andExpect(model().attributeExists("updateTeam"))
                .andExpect(view().name("teams"));
    }

    @Test
    void addNewTeam_WhenDataIsValid_ShouldSaveAndRedirect() throws Exception {
        Team newTeam = new Team("TEAM01", "Team One", new User(), new Department(), Status.ACTIVE);

        this.mockMvc
                .perform(post(baseUrl + "/teams/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newTeam",
                        newTeam).param("dept-id", deptId))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl(baseUrl + "/teams"));
    }

    private static Stream<Arguments> faultyTeams() {
        Team team1 = new Team("", "Team One", new User(), new Department(), Status.ACTIVE);
        Team team2 = new Team(null, "Team One", new User(), new Department(), Status.ACTIVE);
        Team team3 = new Team("DPT01", "", new User(), new Department(), Status.ACTIVE);
        Team team4 = new Team("DPT01", null, new User(), new Department(), Status.ACTIVE);

        return Stream.of(
                Arguments.of(team1),
                Arguments.of(team2),
                Arguments.of(team3),
                Arguments.of(team4));
    }

    @ParameterizedTest
    @MethodSource("faultyTeams")
    void addNewTeam_WhenDataHasErrors_ShouldGoBackToViewPage(Team newTeam) throws Exception {

        this.mockMvc
                .perform(post(baseUrl + "/teams/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newTeam",
                        newTeam))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("teams"));
    }

    @Test
    void addNewTeam_WhenServerError_ShouldGoBackToViewPage() throws Exception {
        Team newTeam = new Team("DPT01", "Team One", new User(), new Department(), Status.ACTIVE);
        Mockito.doThrow(new ResourceAlreadyExistsException("")).when(teamService).addNewTeam(deptId, newTeam);

        this.mockMvc
                .perform(post(baseUrl + "/teams/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newTeam",
                        newTeam))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("teams"));
    }

    @Test
    void updateTeam_WhenDataIsValid_ShouldUpdateAndRedirect() throws Exception {
        Team updateTeam = new Team("DPT01", "Team One", new User(), new Department(), Status.ACTIVE);

        this.mockMvc
                .perform(post(baseUrl + "/teams/update").with(user(userDetails)).with(csrf()).flashAttr(
                        "updateTeam",
                        updateTeam))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl(baseUrl + "/teams"));
    }

    @ParameterizedTest
    @MethodSource("faultyTeams")
    void updateTeam_WhenDataHasErrors_ShouldGoBackToViewPage(Team updateTeam) throws Exception {

        this.mockMvc
                .perform(post(baseUrl + "/teams/update").with(user(userDetails)).with(csrf()).flashAttr(
                        "updateTeam",
                        updateTeam))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("teams"));
    }

    @Test
    void updateTeam_WhenServerError_ShouldGoBackToViewPage() throws Exception {
        Team updateTeam = new Team("DPT01", "Team One", new User(), new Department(), Status.ACTIVE);
        Mockito.doThrow(new ResourceAlreadyExistsException("")).when(teamService).updateTeam(deptId, updateTeam);

        this.mockMvc
                .perform(post(baseUrl + "/teams/update").with(user(userDetails)).with(csrf()).flashAttr(
                        "updateTeam",
                        updateTeam))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("teams"));
    }

    @Test
    void deleteTeam_WhenCalled_ShouldDeleteAndRedirect() throws Exception {
        String teamId = "DPT01";

        this.mockMvc
                .perform(post(baseUrl + "/teams/delete").with(user(userDetails)).with(csrf()).param("id", teamId))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl(baseUrl + "/teams"));
    }

    
}
