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
import gp.pyinsa.jroms.models.Location;
import gp.pyinsa.jroms.models.Role;
import gp.pyinsa.jroms.models.User;
import gp.pyinsa.jroms.services.LocationService;

@SpringBootTest
@AutoConfigureMockMvc
public class LocationControllerTest {
    @MockBean
    private LocationService locationService;

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
    void locationsPage_WhenCalled_ShouldLoadLocationsPage() throws Exception {
        this.mockMvc.perform(get(baseUrl + "/locations").with(user(userDetails)))
                .andExpect(status().is(200))
                .andExpect(model().attributeExists("newLocation"))
                .andExpect(model().attributeExists("updateLocation"))
                .andExpect(view().name("locations"));
    }

    @Test
    void addNewLocation_WhenLocationDataIsValid_ShouldSaveAndRedirect() throws Exception {
        Location newLocation = new Location(null, "Building 1", "Township 1", "Division 1", Status.ACTIVE);

        this.mockMvc
                .perform(post(baseUrl + "/locations/add").with(user(userDetails)).with(csrf()).flashAttr("newLocation",
                        newLocation))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/locations"));
    }

    private static Stream<Arguments> faultyLocations() {
        Location l1 = new Location(null, null, "Township 1", "Division 1", Status.ACTIVE);
        Location l2 = new Location(null, "Building 1", null, "Division 1", Status.ACTIVE);
        Location l3 = new Location(null, "Building 1", "Township 1", "", Status.ACTIVE);

        return Stream.of(
                Arguments.of(l1),
                Arguments.of(l2),
                Arguments.of(l3));
    }

    @ParameterizedTest
    @MethodSource("faultyLocations")
    void addNewLocation_WhenLocationDataHasErrors_ShouldGoBackToViewPage(Location location) throws Exception {

        this.mockMvc
                .perform(post(baseUrl + "/locations/add").with(user(userDetails)).with(csrf()).flashAttr("newLocation",
                        location))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("locations"));
    }

    @Test
    void addNewLocation_WhenServerError_ShouldGoBackToViewPage() throws Exception {
        Location newLocation = new Location(null, "Building 1", "Township 1", "Division 1", Status.ACTIVE);
        Mockito.doThrow(new ResourceAlreadyExistsException("")).when(locationService).addNewLocation(newLocation);;

        this.mockMvc
                .perform(post(baseUrl + "/locations/add").with(user(userDetails)).with(csrf()).flashAttr("newLocation",
                        newLocation))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("locations"));
    }

    @Test
    void updateLocation_WhenLocationDataIsValid_ShouldUpdateAndRedirect() throws Exception {
        Location updateLocation = new Location(null, "Building 1", "Township 1", "Division 1", Status.ACTIVE);

        this.mockMvc
                .perform(post(baseUrl + "/locations/update").with(user(userDetails)).with(csrf()).flashAttr("updateLocation",
                        updateLocation))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/locations"));
    }

    @ParameterizedTest
    @MethodSource("faultyLocations")
    void updateLocation_WhenLocationDataHasErrors_ShouldGoBackToViewPage(Location location) throws Exception {

        this.mockMvc
                .perform(post(baseUrl + "/locations/add").with(user(userDetails)).with(csrf()).flashAttr("updateLocation",
                        location))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("locations"));
    }

    @Test
    void updateLocation_WhenServerError_ShouldGoBackToViewPage() throws Exception {
        Location updateLocation = new Location(null, "Building 1", "Township 1", "Division 1", Status.ACTIVE);
        Mockito.doThrow(new ResourceAlreadyExistsException("")).when(locationService).updateLocation(updateLocation);;

        this.mockMvc
                .perform(post(baseUrl + "/locations/add").with(user(userDetails)).with(csrf()).flashAttr("updateLocation",
                        updateLocation))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("locations"));
    }

    @Test
    void deleteLocation_WhenCalled_ShouldDeleteAndRedirect() throws Exception {
        String locationId = "1";

        this.mockMvc
                .perform(post(baseUrl + "/locations/delete").with(user(userDetails)).with(csrf()).param("id", locationId))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/locations"));
    }

}
