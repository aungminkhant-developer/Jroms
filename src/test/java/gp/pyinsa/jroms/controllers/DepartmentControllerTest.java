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
import gp.pyinsa.jroms.models.User;
import gp.pyinsa.jroms.services.DepartmentService;
import gp.pyinsa.jroms.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
public class DepartmentControllerTest {
    @MockBean
    private DepartmentService departmentService;

    @MockBean
    private UserService userService;

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
    void departmentView_WhenCalled_ShouldLoadViewPage() throws Exception {
        this.mockMvc.perform(get(baseUrl + "/departments").with(user(userDetails)))
                .andExpect(status().is(200))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("newDepartment"))
                .andExpect(model().attributeExists("departmentHeads"))
                .andExpect(model().attributeExists("updateDepartment"))
                .andExpect(view().name("departments"));
    }

    @Test
    void addNewDepartment_WhenDataIsValid_ShouldSaveAndRedirect() throws Exception {
        Department newDepartment = new Department("DPT01", "Department One", new User(), Status.ACTIVE);

        this.mockMvc
                .perform(post(baseUrl + "/departments/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newDepartment",
                        newDepartment))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/departments"));
    }

    private static Stream<Arguments> faultyDepartments() {
        Department department1 = new Department("", "Department One", new User(), Status.ACTIVE);
        Department department2 = new Department(null, "Department One", new User(), Status.ACTIVE);
        Department department3 = new Department("DPT01", "", new User(), Status.ACTIVE);
        Department department4 = new Department("DPT01", null, new User(), Status.ACTIVE);

        return Stream.of(
                Arguments.of(department1),
                Arguments.of(department2),
                Arguments.of(department3),
                Arguments.of(department4));
    }

    @ParameterizedTest
    @MethodSource("faultyDepartments")
    void addNewDepartment_WhenDataHasErrors_ShouldGoBackToViewPage(Department newDepartment) throws Exception {

        this.mockMvc
                .perform(post(baseUrl + "/departments/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newDepartment",
                        newDepartment))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("departments"));
    }

    @Test
    void addNewDepartment_WhenServerError_ShouldGoBackToViewPage() throws Exception {
        Department newDepartment = new Department("DPT01", "Department One", new User(), Status.ACTIVE);
        Mockito.doThrow(new ResourceAlreadyExistsException("")).when(departmentService).addNewDepartment(newDepartment);

        this.mockMvc
                .perform(post(baseUrl + "/departments/add").with(user(userDetails)).with(csrf()).flashAttr(
                        "newDepartment",
                        newDepartment))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("departments"));
    }

    @Test
    void updateDepartment_WhenDataIsValid_ShouldUpdateAndRedirect() throws Exception {
        Department updateDepartment = new Department("DPT01", "Department One", new User(), Status.ACTIVE);

        this.mockMvc
                .perform(post(baseUrl + "/departments/update").with(user(userDetails)).with(csrf()).flashAttr(
                        "updateDepartment",
                        updateDepartment))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/departments"));
    }

    @ParameterizedTest
    @MethodSource("faultyDepartments")
    void updateDepartment_WhenDataHasErrors_ShouldGoBackToViewPage(Department newDepartment) throws Exception {

        this.mockMvc
                .perform(post(baseUrl + "/departments/update").with(user(userDetails)).with(csrf()).flashAttr(
                        "newDepartment",
                        newDepartment))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("departments"));
    }

    @Test
    void updateDepartment_WhenServerError_ShouldGoBackToViewPage() throws Exception {
        Department newDepartment = new Department("DPT01", "Department One", new User(), Status.ACTIVE);
        Mockito.doThrow(new ResourceAlreadyExistsException("")).when(departmentService).addNewDepartment(newDepartment);

        this.mockMvc
                .perform(post(baseUrl + "/departments/update").with(user(userDetails)).with(csrf()).flashAttr(
                        "newDepartment",
                        newDepartment))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("departments"));
    }

    @Test
    void deleteDepartment_WhenCalled_ShouldDeleteAndRedirect() throws Exception {
        String departmentId = "DPT01";

        this.mockMvc
                .perform(post(baseUrl + "/departments/delete").with(user(userDetails)).with(csrf()).param("id", departmentId))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/departments"));
    }
}
