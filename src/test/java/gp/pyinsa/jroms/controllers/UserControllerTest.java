package gp.pyinsa.jroms.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.mapping.Order;
import org.springframework.data.jpa.datatables.mapping.Search;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.dtos.UserDto;
import gp.pyinsa.jroms.dtos.UserUpdateDto;
import gp.pyinsa.jroms.models.AppUserDetails;
import gp.pyinsa.jroms.models.Role;
import gp.pyinsa.jroms.models.User;
import gp.pyinsa.jroms.services.RoleService;
import gp.pyinsa.jroms.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Mock
    private ModelMap model;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private MockMvc mockMvc;

    private static String baseUrl;

    @InjectMocks
    private UserController userController;

    private static AppUserDetails userDetails;

    @BeforeAll
    static void setup() {
        baseUrl = "/mng/users";
        Role role = new Role((short) 1, "ROLE_ADMIN");
        User user = new User("U001", "John", "john", "john@gmail.com", "john", true, Status.ACTIVE, role, new Date(),
                new Date());
        userDetails = new AppUserDetails(user);
    }

    @Test
    void testAddUserForm() throws Exception {
        Role role = new Role((short) 1, "ROLE_ADMIN");
        List<Role> roles = Arrays.asList(role);

        when(roleService.getAllRoles()).thenReturn(roles);

        mockMvc.perform(get(baseUrl + "/add").with(user(userDetails)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("add_user"))
                .andExpect(model().attributeExists("newUser"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().attribute("roles", roles));
    }

    @Test
    void testAddNewUser_Success() throws Exception {
        Role role = new Role((short) 1, "ROLE_ADMIN");

        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test User");
        newUserDto.setUsername("testuser");
        newUserDto.setEmail("testuser@example.com");
        newUserDto.setPassword("password");
        newUserDto.setEnabled(true);
        newUserDto.setRoleId((short) 1);

        MimeMessage mimeMessage = mock(MimeMessage.class); // Create a mock MimeMessage

        when(roleService.getRoleById(anyShort())).thenReturn(role);
        when(userService.save(any(User.class))).thenReturn(null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage); // Set up javaMailSender.createMimeMessage()
                                                                          // to return the mock MimeMessage
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        mockMvc.perform(post(baseUrl + "/add").with(user(userDetails)).with(csrf())
                .flashAttr("newUser", newUserDto)) // Use the newUserDto object you created and set up
                .andExpect(status().isOk())
                .andExpect(view().name("add_user"))
                .andExpect(model().attributeExists("newUser"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().attributeExists("success"));
    }

    @Test
    void testAddNewUser_Failed() throws Exception {
        Role role = new Role((short) 1, "ROLE_ADMIN");

        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test User");
        newUserDto.setUsername("testuser");
        newUserDto.setEmail("testuser@example.com");
        newUserDto.setPassword("password");
        newUserDto.setEnabled(true);
        newUserDto.setRoleId((short) 1);

        when(roleService.getRoleById(anyShort())).thenReturn(role);
        when(userService.save(any(User.class))).thenReturn("Error message");
        // Simulate failed user addition by
        // returning an error message

        mockMvc.perform(post(baseUrl + "/add").with(user(userDetails)).with(csrf())
                .flashAttr("newUser", newUserDto))
                .andExpect(status().isOk())
                .andExpect(view().name("add_user"))
                .andExpect(model().attributeExists("newUser"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void testViewUserTable() throws Exception {
        Role role = new Role((short) 1, "ROLE_ADMIN");
        List<Role> roles = Arrays.asList(role);

        when(roleService.getAllRoles()).thenReturn(roles);

        mockMvc.perform(get(baseUrl + "/viewUser").with(user(userDetails)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("view_user"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().attribute("roles", roles));
    }

    @Test
    public void testEditUserForm() throws Exception {
        User oldUser = new User();
        oldUser.setId("1");
        oldUser.setName("John Doe");
        oldUser.setUsername("johndoe");
        oldUser.setEmail("johndoe@example.com");
        oldUser.setPassword("password");
        oldUser.setEnabled(true);
        // oldUser.setStatus(Status.ACTIVE);
        Role role = new Role((short) 2, "Admin");
        oldUser.setRole(role);
        // oldUser.setCreatedDate(new Date());
        // oldUser.setLastUpdated(new Date());

        when(userService.getById("1")).thenReturn(oldUser);

        List<Role> roles = Arrays.asList(
                new Role((short) 1, "User"),
                new Role((short) 2, "Admin"),
                new Role((short) 3, "SuperAdmin"));
        when(roleService.getAllRoles()).thenReturn(roles);

        mockMvc.perform(get(baseUrl + "/edit/{id}", "1").with(user(userDetails)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-user"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().attribute("user", hasProperty("id", equalTo("1"))))
                .andExpect(model().attribute("user", hasProperty("name", equalTo("John Doe"))))
                .andExpect(model().attribute("user", hasProperty("username",
                        equalTo("johndoe"))))
                .andExpect(model().attribute("user", hasProperty("email",
                        equalTo("johndoe@example.com"))))
                .andExpect(model().attribute("user", hasProperty("password",
                        equalTo("password"))))
                .andExpect(model().attribute("user", hasProperty("enabled", equalTo(true))))
                // .andExpect(model().attribute("user", hasProperty("status",
                // equalTo(Status.ACTIVE))))
                // .andExpect(model().attribute("user", hasProperty("role", equalTo(role))))
                .andExpect(model().attribute("roles", hasSize(3)));
    }

    // @Test
    // public void testEditUser() throws Exception {
    // UserUpdateDto userUpdateDto = new UserUpdateDto();
    // userUpdateDto.setId("1");
    // userUpdateDto.setName("John");
    // userUpdateDto.setUsername("john123");
    // userUpdateDto.setEmail("john@example.com");
    // userUpdateDto.setPassword("password");
    // userUpdateDto.setEnabled(true);
    // userUpdateDto.setRoleId((short) 1);

    // User userBeforeUpdate = new User();
    // userBeforeUpdate.setId("1");
    // userBeforeUpdate.setName("John");
    // userBeforeUpdate.setUsername("john123");
    // userBeforeUpdate.setEmail("john@example.com");
    // userBeforeUpdate.setPassword("oldpassword");
    // userBeforeUpdate.setEnabled(true);
    // Role oldRole = new Role();
    // oldRole.setId((short) 1);
    // userBeforeUpdate.setRole(oldRole);

    // Role role = new Role();
    // role.setId((short) 1);
    // role.setName("ROLE_ADMIN");
    // userBeforeUpdate.setRole(role);

    // given(userService.getById(userUpdateDto.getId())).willReturn(userBeforeUpdate);
    // given(roleService.getRoleById((short)
    // userUpdateDto.getRoleId())).willReturn(role);
    // given(userService.update(any(User.class))).willReturn(null);

    // mockMvc.perform(post(baseUrl + "/edit").with(user(userDetails)).with(csrf())
    // .flashAttr("user", userUpdateDto))
    // .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
    // .andExpect(MockMvcResultMatchers.redirectedUrl("/mng/users/viewUser"));

    // verify(userService).getById(userUpdateDto.getId());
    // verify(roleService).getRoleById((short) userUpdateDto.getRoleId());
    // verify(userService).update(any(User.class));
    // }

    @Test
    public void testChangeUserPermission() throws Exception {
        String userId = "1";
        boolean currentStatus = true;

        User user = new User();
        user.setId(userId);
        user.setEnabled(currentStatus);

        when(userService.getById(userId)).thenReturn(user);

        mockMvc.perform(post(baseUrl + "/userPermissionChange").with(user(userDetails)).with(csrf())
                .param("id", userId)
                .param("status", String.valueOf(currentStatus)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.newStatus").value(!currentStatus))
                .andExpect(jsonPath("$.message").value("Status changed successfully"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        String userId = "1";

        User user = new User();
        user.setId(userId);

        when(userService.getById(userId)).thenReturn(user);
        doNothing().when(userService).deleteById(userId);

        mockMvc.perform(get(baseUrl + "/delete/{id}", userId).with(user(userDetails)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("deleteSuccess"))
                .andExpect(model().attribute("deleteSuccess", "User is deleted successfully"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(view().name("view_user"));

        verify(userService, times(1)).deleteById(userId);
    }

    @Test
    public void testDeleteUser_UserNotFound() throws Exception {
        String userId = "1";

        when(userService.getById(userId)).thenReturn(null);

        mockMvc.perform(get(baseUrl + "/delete/{id}", userId).with(user(userDetails)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("deleteFailed"))
                .andExpect(model().attribute("deleteFailed", "Failed to delete user"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(view().name("view_user"));

        verify(userService, never()).deleteById(userId);
    }

}
