package gp.pyinsa.jroms;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.models.AppUserDetails;
import gp.pyinsa.jroms.models.Role;
import gp.pyinsa.jroms.models.User;
import gp.pyinsa.jroms.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityAuthorizationTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private static User admin;
    private static AppUserDetails adminUserDetails;
    private static AppUserDetails seniorHRUserDetails;
    private static AppUserDetails juniorHRUserDetails;
    private static AppUserDetails departmentHeadUserDetails;
    private static AppUserDetails teamLeaderUserDetails;

    @BeforeAll
    static void setup() {
        Role adminRole = new Role((short) 1, "ROLE_ADMIN");
        Role seniorHRRole = new Role((short) 2, "ROLE_HR_SENIOR");
        Role juniorHRRole = new Role((short) 3, "ROLE_HR_JUNIOR");
        Role departmentHeadRole = new Role((short) 4, "ROLE_DEPARTMENT_HEAD");
        Role teamLeaderRole = new Role((short) 5, "ROLE_TEAM_LEADER");
        admin = new User("U001", "John", "john", "john@gmail.com", "john", true, Status.ACTIVE, adminRole,
                new Date(),
                new Date());
        User seniorHR = new User("U002", "Mary", "mary", "mary@gmail.com", "mary", true, Status.ACTIVE, seniorHRRole,
                new Date(),
                new Date());
        User juniorHR = new User("U003", "David", "david", "david@gmail.com", "david", true, Status.ACTIVE,
                juniorHRRole, new Date(),
                new Date());
        User departmentHead = new User("U004", "David", "david", "david@gmail.com", "david", true, Status.ACTIVE,
                departmentHeadRole, new Date(),
                new Date());
        User teamLeader = new User("U005", "David", "david", "david@gmail.com", "david", true, Status.ACTIVE,
                teamLeaderRole, new Date(),
                new Date());
        adminUserDetails = new AppUserDetails(admin);
        seniorHRUserDetails = new AppUserDetails(seniorHR);
        juniorHRUserDetails = new AppUserDetails(juniorHR);
        departmentHeadUserDetails = new AppUserDetails(departmentHead);
        teamLeaderUserDetails = new AppUserDetails(teamLeader);
    }

    private static Stream<Arguments> adminRoutes() {
        return Stream.of(
                Arguments.of("/mng/users/viewUser"),
                Arguments.of("/mng/users/add"),
                Arguments.of("/mng/users/edit/U001"));
    }

    private static Stream<Arguments> setupRoutes() {
        return Stream.of(
                Arguments.of("/mng/miscellaneous"),
                Arguments.of("/mng/locations"),
                Arguments.of("/mng/departments"),
                Arguments.of("/mng/work-schedules"));
    }

    @ParameterizedTest
    @MethodSource("adminRoutes")
    void adminRoutes_WhenAccessedWithAdminRole_ShouldBeOk(String route) throws Exception {
        String userId = "U001";
        when(userService.getById(userId)).thenReturn(admin);

        this.mockMvc.perform(get(route).with(user(adminUserDetails)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("adminRoutes")
    void adminRoutes_WhenAccessedWithOtherRole_ShouldForbid(String route) throws Exception {
        String userId = "U001";
        when(userService.getById(userId)).thenReturn(admin);

        List<AppUserDetails> userDetails = List.of(
                seniorHRUserDetails,
                juniorHRUserDetails,
                departmentHeadUserDetails,
                teamLeaderUserDetails);

        for (AppUserDetails userDetail : userDetails) {
            this.mockMvc.perform(get(route).with(user(userDetail)))
                    .andExpect(status().isForbidden());
        }

    }

    @ParameterizedTest
    @MethodSource("setupRoutes")
    void setupRoutes_WhenAccessedByJuniorHR_ShouldForbid(String route) throws Exception {
        this.mockMvc.perform(get(route).with(user(juniorHRUserDetails)))
                .andExpect(status().isForbidden());
    }

}
