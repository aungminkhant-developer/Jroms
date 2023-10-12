package gp.pyinsa.jroms.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;

import gp.pyinsa.jroms.constants.Interview_Result;
import gp.pyinsa.jroms.dtos.ContactForm;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.JobTitle;
import gp.pyinsa.jroms.services.CandidateService;
import gp.pyinsa.jroms.services.JobDetailService;
import gp.pyinsa.jroms.services.JobTitleService;
import gp.pyinsa.jroms.services.LocationService;
import gp.pyinsa.jroms.services.TeamService;

@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTest {

    @Mock
    private ModelMap model;

    @MockBean
    JobDetailService jobDetailService;

    @MockBean
    private LocationService locationService;

    @MockBean
    JobTitleService jobTitleService;

    @MockBean
    CandidateService candidateService;

    @MockBean
    private TeamService teamService;

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private HomeController homeController;

    @Autowired
    private MockMvc mockMvc;

    private static String baseUrl;

    @BeforeAll
    static void setup() {

        baseUrl = "/";
    }

    @Test
    void contactUsView_WhenCalled_ShouldLoadViewPage() throws Exception {
        this.mockMvc.perform(get(baseUrl + "contactUs"))
                .andExpect(status().is(200))
                .andExpect(model().attributeExists("contactForm"))
                .andExpect(view().name("contect-us"));
    }

    @Test
    public void testSendEmail_Success() {
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        String result = homeController.sendEmail("TestUser", "test@example.com", "Hello");

        assertNull(result); // No errors expected
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    public void testSendEmail_ConnectionError() {
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        doThrow(new MailSendException("Connection refused")).when(javaMailSender).send(any(MimeMessage.class));

        String result = homeController.sendEmail("TestUser", "test@example.com", "Hello");

        assertEquals("Failed to send email. Please try again", result);
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    public void testSendEmail_TimeoutError() {
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        doThrow(new MailSendException("Connection timed out", new RuntimeException(new java.net.SocketTimeoutException())))
            .when(javaMailSender).send(any(MimeMessage.class));

        String result = homeController.sendEmail("TestUser", "test@example.com", "Hello");

        assertEquals("Failed to send email. Please try again", result);
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    public void testSendEmail_FailedToSend() {
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        doThrow(new MailSendException("Failed to send")).when(javaMailSender).send(any(MimeMessage.class));

        String result = homeController.sendEmail("TestUser", "test@example.com", "Hello");

        assertEquals("Failed to send email. Please try again", result);
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    // @Test
    // public void testGetCandidateCount_ValidInput() {
    // // Mock candidateService methods as before

    // ResponseEntity<List<Long>> response =
    // homeController.getCandidateCount("2023");

    // List<Long> expectedCounts = new ArrayList<>();
    // for (int i = 1; i <= 12; i++) {
    // expectedCounts.add(5L); // Mocked count for createdDateBetween
    // expectedCounts.add(3L); // Mocked count for PASSED
    // expectedCounts.add(2L); // Mocked count for FAILED
    // }

    // assertEquals(expectedCounts, response.getBody());
    // }

    @Test
    public void testGetCandidateCount_InvalidInput() {
        ResponseEntity<List<Long>> response = homeController.getCandidateCount("invalid-json");

        assertNull(response.getBody());
    }

    // @Test
    // public void testGetCountJobOffer() {
    // // Mock jobDetailService.getJobOfferCountByCreatedDateBetween(...)
    // when(jobDetailService.getJobOfferCountByCreatedDateBetween(anyString(),
    // anyString())).thenReturn(7L);

    // List<Long> expectedCounts = new ArrayList<>();
    // for (int i = 1; i <= 12; i++) {
    // expectedCounts.add(7L); // Mocked job offer count
    // }

    // List<Long> response = homeController.getCountJobOffer("2023");

    // assertEquals(expectedCounts, response);
    // }

    @Test
    void testJobs() throws Exception {
        List<String> locations = Arrays.asList("location1", "location2");

        JobTitle jobTitle1 = new JobTitle();
        jobTitle1.setName("title1");
        JobTitle jobTitle2 = new JobTitle();
        jobTitle2.setName("title2");
        List<JobTitle> jobTitles = Arrays.asList(jobTitle1, jobTitle2);

        when(locationService.getActiveLocations()).thenReturn(locations);
        when(jobTitleService.getAllJobTitles()).thenReturn(jobTitles);

        // MockMvc mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();

        this.mockMvc.perform(get("/jobs"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("locations", locations))
                .andExpect(model().attribute("jobTitle", jobTitles));
    }

    @Test
    void testAbout() throws Exception {
        this.mockMvc.perform(get(baseUrl + "about"))
                .andExpect(status().is(200))
                .andExpect(view().name("about"));
    }

    @Test
    public void testShowPage() throws Exception {
        // Create test data
        List<JobDetail> jobDetails = new ArrayList<>();
        jobDetails.add(new JobDetail());
        jobDetails.add(new JobDetail());
        Page<JobDetail> page = new PageImpl<>(jobDetails);

        // Define the expected parameters
        int pageNo = 1;
        String searchQuery = "Java";
        String location = "London";
        Long position = 1L;
        String status = "Open";

        // Mock the jobDetailService behavior
        when(jobDetailService.getFilteredJobs(searchQuery, location, position, status,
                PageRequest.of(pageNo - 1, 6))).thenReturn(page);

        // Perform the GET request and verify the response
        mockMvc.perform(get("/getJobs")
                .param("pageNo", String.valueOf(pageNo))
                .param("searchQuery", searchQuery)
                .param("location", location)
                .param("position", String.valueOf(position))
                .param("status", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(jobDetails.size()));

        // Verify that the jobDetailService method was called with the expected
        // parameters
        verify(jobDetailService, times(1)).getFilteredJobs(searchQuery, location, position, status,
                PageRequest.of(pageNo - 1, 6));
    }

    @Test
    public void testHandleForm() throws Exception {
        ContactForm contactForm = new ContactForm();
        contactForm.setUsername("John Doe");
        contactForm.setEmail("johndoe@example.com");
        contactForm.setMessage("Hello, this is a test message.");

        mockMvc.perform(post("/contactUs").with(csrf())
                .flashAttr("contactForm", contactForm))
                .andExpect(status().isOk())
                .andExpect(view().name("contect-us"))
                .andExpect(model().attributeExists("mailSuccess"))
                .andExpect(
                        model().attribute("mailSuccess", "Your email has been successfully sent"))
                .andExpect(model().attribute("contactForm", new ContactForm()));
    }

}
