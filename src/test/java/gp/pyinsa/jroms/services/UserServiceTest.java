package gp.pyinsa.jroms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.models.Role;
import gp.pyinsa.jroms.models.User;
import gp.pyinsa.jroms.repositories.UserRepository;
import gp.pyinsa.jroms.repositories.datatable.UserDTRepository;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDTRepository userDTRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testGetById_UserFound() {
        String userId = "1";
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetById_UserNotFound() {
        String userId = "1";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.getById(userId);
        });

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testSearchByIdOrName_IdEmpty_NameNotEmpty() {
        // Arrange
        String id = "";
        String name = "John";
        List<User> expectedUsers = Arrays.asList(
                createUser("1", "John Doe", "johndoe", "johndoe@example.com", "password", true),
                createUser("2", "John Smith", "johnsmith", "johnsmith@example.com", "password", true));
        when(userRepository.findByNameContaining(name)).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.searchByIdOrName(id, name);

        // Assert
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    void testSearchByIdOrName_IdNotEmpty_NameEmpty() {
        // Arrange
        String id = "1";
        String name = "";
        List<User> expectedUsers = Arrays.asList(
                createUser("1", "John Doe", "johndoe", "johndoe@example.com", "password", true),
                createUser("1a", "Jane Smith", "janesmith", "janesmith@example.com", "password", true));
        when(userRepository.findByIdContaining(id)).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.searchByIdOrName(id, name);

        // Assert
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    void testSearchByIdOrName_IdNotEmpty_NameNotEmpty() {
        // Arrange
        String id = "1";
        String name = "John";
        List<User> expectedUsers = Arrays.asList(
                createUser("1", "John Doe", "johndoe", "johndoe@example.com", "password", true),
                createUser("1a", "John Smith", "johnsmith", "johnsmith@example.com", "password", true));
        when(userRepository.findByIdContainingAndNameContaining(id, name)).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.searchByIdOrName(id, name);

        // Assert
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    void testSearchByIdOrName_IdEmpty_NameEmpty() {
        // Arrange
        String id = "";
        String name = "";
        List<User> expectedUsers = Arrays.asList(
                createUser("1", "John Doe", "johndoe", "johndoe@example.com", "password", true),
                createUser("2", "Jane Smith", "janesmith", "janesmith@example.com", "password", true));
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.searchByIdOrName(id, name);

        // Assert
        assertEquals(expectedUsers, actualUsers);
    }

    private User createUser(String id, String name, String username, String email, String password, boolean enabled) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setEnabled(enabled);
        user.setRole(new Role());
        user.setCreatedDate(new Date());
        user.setLastUpdated(new Date());
        return user;
    }

    @Test
    void testSave_NewUser_SuccessfullySaved() {
        // Arrange
        User user = createUser("U001", "John Doe", "john.doe@example.com",
                "password");
        String encodedPassword = "encodedPassword";
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        String result = userService.save(user);

        // Assert
        assertNull(result);
        assertEquals(encodedPassword, user.getPassword());
        assertEquals(Status.ACTIVE, user.getStatus());
        assertNotNull(user.getRole());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSave_UsernameAlreadyExists_ExceptionThrown() {
        // Arrange
        User existingUser = createUser("U001", "Existing User",
                "existing.user@example.com", "password");
        User user = createUser("U002", "John Doe", "john.doe@example.com",
                "password");
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        // Act
        String result = userService.save(user);

        // Assert
        assertEquals("Username already exists.", result);
        verify(userRepository, never()).save(user);
    }

    @Test
    void testSave_EmailAlreadyExists_ExceptionThrown() {
        // Arrange
        User existingUser = createUser("U001", "Existing User",
                "existing.user@example.com", "password");
        User user = createUser("U002", "John Doe", "john.doe@example.com",
                "password");
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        // Act
        String result = userService.save(user);

        // Assert
        assertEquals("Email already exists.", result);
        verify(userRepository, never()).save(user);
    }

    private User createUser(String id, String name, String email, String password) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setEnabled(true);
        user.setStatus(Status.ACTIVE);
        user.setRole(new Role()); // set the appropriate Role instance

        return user;
    }

    @Test
    void testDeleteById() {
        // Create a mock user and set its status to ACTIVE
        User user = new User();
        user.setStatus(Status.ACTIVE);

        when(userRepository.findById("userId")).thenReturn(Optional.of(user));

        // Call the deleteById method
        userService.deleteById("userId");

        // Verify that the user's status was set to DELETED and save was called
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteLatestUser() {
        // Create a mock user and set its createdDate
        User user = new User();
        user.setCreatedDate(new Date());

        when(userRepository.findTopByOrderByCreatedDateDesc()).thenReturn(user);

        // Call the deleteLatestUser method
        userService.deleteLatestUser();

        // Verify that the delete method was called with the user
        verify(userRepository, times(1)).delete(user);
    }

    // @Test
    // void testGetDepartmentHeads() {
    // // Create a list of mock users
    // List<User> departmentHeads = new ArrayList<>();

    // when(userRepository.findByRoleName("ROLE_DEPARTMENT_HEAD")).thenReturn(departmentHeads);

    // // Call the getDepartmentHeads method
    // List<User> result = userService.getDepartmentHeads();

    // // Verify that the result matches the mocked list of departmentHeads
    // assertEquals(departmentHeads, result);
    // }

    // @Test
    // void testGetTeamLeaders() {
    // // Create a list of mock users
    // List<User> teamLeaders = new ArrayList<>();

    // when(userRepository.findByRoleName("ROLE_TEAM_LEADER")).thenReturn(teamLeaders);

    // // Call the getTeamLeaders method
    // List<User> result = userService.getTeamLeaders();

    // // Verify that the result matches the mocked list of teamLeaders
    // assertEquals(teamLeaders, result);
    // }

}
