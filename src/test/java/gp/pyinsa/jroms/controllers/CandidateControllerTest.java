package gp.pyinsa.jroms.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import gp.pyinsa.jroms.dtos.CandidateRegisterDto;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.CurriculumVitae;
import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.JobLevel;
import gp.pyinsa.jroms.models.JobTitle;
import gp.pyinsa.jroms.models.JobType;
import gp.pyinsa.jroms.models.Location;
import gp.pyinsa.jroms.models.Team;
import gp.pyinsa.jroms.models.WorkSchedule;
import gp.pyinsa.jroms.repositories.CandidateRepository;
import gp.pyinsa.jroms.services.CVService;
import gp.pyinsa.jroms.services.CandidateService;
import gp.pyinsa.jroms.services.DepartmentService;
import gp.pyinsa.jroms.services.JobDetailService;
import gp.pyinsa.jroms.services.JobLevelService;
import gp.pyinsa.jroms.services.JobTitleService;
import gp.pyinsa.jroms.services.TeamService;

@SpringBootTest
@AutoConfigureMockMvc
public class CandidateControllerTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private CandidateController candidateController;

    @Mock
    private JobDetailService jobDetailService;

    @MockBean
    private JobTitleService jobTitleService;

    @MockBean
    private JobLevelService jobLevelService;

    @MockBean
    private CVService cvService;

    @MockBean
    private CandidateService candidateService;

    @MockBean
    private DepartmentService departmentService;

    @MockBean
    private TeamService teamService;

      @Autowired
    private ObjectMapper objectMapper;


    @Test
public void testCandidateView() throws Exception {
    List<JobTitle> jobTitles = new ArrayList<>();

    List<JobLevel> jobLevels = new ArrayList<>();

    List<CurriculumVitae> cvs = new ArrayList<>();

    List<Candidate> candidates = new ArrayList<>();

    List<Department> departments = new ArrayList<>();

    List<Team> teams = new ArrayList<>();

    when(jobTitleService.getActiveJobTitles()).thenReturn(jobTitles);
    when(jobLevelService.getActiveJobLevels()).thenReturn(jobLevels);
    when(cvService.getAllCV()).thenReturn(cvs);
    when(candidateService.getAllCandidate()).thenReturn(candidates);
    when(departmentService.getActiveDepartments()).thenReturn(departments);
    when(teamService.getActiveTeams()).thenReturn(teams);

    // Perform the request and assert the response
    mockMvc.perform(get("/mng/candidates"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("jobTitle", jobTitles))
        .andExpect(model().attribute("jobLevel", jobLevels))
        .andExpect(model().attribute("CV", cvs))
        .andExpect(model().attribute("jobOffer", candidates))
        .andExpect(model().attribute("candidates", candidates.size()))
        .andExpect(model().attribute("department", departments))
        .andExpect(model().attribute("team", teams))
        .andExpect(view().name("candidates"));
}

@Test
    public void testCandidateDataView() throws Exception {
        DataTablesInput input = new DataTablesInput();

        DataTablesOutput<Candidate> mockOutput = new DataTablesOutput<>();
        Candidate mockCandidate = new Candidate();
        mockOutput.setData(Collections.singletonList(mockCandidate));

        when(candidateService.getAllCandidates(any())).thenReturn(mockOutput);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/mng/rest/candidates/data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
                //.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].yourProperty").value(mockCandidate.getYourProperty())); // Adjust this for your actual properties
    }


    @Test
    public void testCandidateData() throws Exception {
        DataTablesInput input = new DataTablesInput();

        DataTablesOutput<Candidate> mockOutput = new DataTablesOutput<>();
        Candidate mockCandidate = new Candidate();
        mockOutput.setData(Collections.singletonList(mockCandidate));

        when(candidateService.getAllCandidates(any())).thenReturn(mockOutput);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/mng/rest/candidates/chart-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
                //.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].yourProperty").value(mockCandidate.getYourProperty())); // Adjust this for your actual properties
    }

     @Test
    public void testCandidateApplicationValidId() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Prepare test data
        String id = "1";
        Model model = mock(Model.class);
        JobDetail jobDetail = new JobDetail();
        when(jobDetailService.getById(1L)).thenReturn(jobDetail);

        // Call the controller method
        String viewName = candidateController.candidateApplication(id, model);

        // Verify interactions and assertions
        verify(jobDetailService, times(1)).getById(1L);
        verify(model, times(1)).addAttribute("job", id);
        verify(model, times(1)).addAttribute("newCandidate", any());
        assertEquals("candidate", viewName);
    }

    @Test
    public void testCandidateApplicationInvalidId() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Prepare test data
        String invalidId = "invalid";
        Model model = mock(Model.class);
        when(jobDetailService.getById(anyLong())).thenThrow(new Exception("Invalid ID"));

        // Call the controller method
        String viewName = candidateController.candidateApplication(invalidId, model);

        // Verify interactions and assertions
        verify(model, times(1)).addAttribute("error", "Cheater, no Cheating!");
        verify(model, times(1)).addAttribute("job", invalidId);
        assertEquals("home", viewName);
    }

     @Test
    public void testCandidateRegisterValidData() throws Exception {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Prepare test data
        CandidateRegisterDto newCandidateDto = new CandidateRegisterDto();
        // Set valid data in newCandidateDto

        Model model = mock(Model.class);
        BindingResult result = mock(BindingResult.class);

        MockMultipartFile file = new MockMultipartFile("cv", "cv.pdf", "application/pdf", "cvdata".getBytes());

       // Mock CVService behavior to return a string message
String successMessage = "CV saved successfully"; // Replace with your desired success message
when(cvService.save(any(CurriculumVitae.class))).thenReturn(successMessage);

        // Mock JobDetailService behavior
        JobDetail jobDetail = new JobDetail();
        when(jobDetailService.getById(anyLong())).thenReturn(jobDetail);

        // Call the controller method
        String viewName = candidateController.candidateRegister(null, null, newCandidateDto, result, null, file, model, null);

        // Assertions
        assertEquals("redirect:/", viewName);
        verify(candidateService, times(1)).save(any(Candidate.class));
    }

    @Test
    public void testCandidateRegisterInvalidData() throws Exception {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Prepare test data with invalid data that triggers result.hasErrors()
        CandidateRegisterDto newCandidateDto = new CandidateRegisterDto();
        // Set invalid data in newCandidateDto

        Model model = mock(Model.class);
        BindingResult result = mock(BindingResult.class);

        MockMultipartFile file = new MockMultipartFile("cv", "cv.pdf", "application/pdf", "cvdata".getBytes());

        // Call the controller method
        String viewName = candidateController.candidateRegister(null, null, newCandidateDto, result, null, file, model, null);

        // Assertions
        assertEquals("candidate", viewName);
        verify(candidateService, never()).save(any(Candidate.class));
    }

     @Test
    public void testViewJobDetailsValidId() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Prepare test data
        String id = "1";
        Model model = mock(Model.class);

        // Mock JobDetailService behavior
        JobDetail jobDetail = new JobDetail();
        jobDetail.setId(1L); // Set the expected ID
        jobDetail.setDescription("Sample description");
        jobDetail.setRequirement("Sample requirement");
        jobDetail.setPreferences("Sample preferences");
        jobDetail.setResponsibilities("Sample responsibilities");
        jobDetail.setExpireDate(new Date()); // Set an unexpired date
        when(jobDetailService.getById(1L)).thenReturn(jobDetail);

        // Mock other service behaviors if needed (jobTitleService, workScheduleService, etc.)

        // Call the controller method
        String viewName = candidateController.viewJobDetails(id, model);

        // Assertions
        assertEquals("jobDetail", viewName);
        verify(model, times(1)).addAttribute("description", "Sample description");
        verify(model, times(1)).addAttribute("requirement", "Sample requirement");
        verify(model, times(1)).addAttribute("preferences", "Sample preferences");
        verify(model, times(1)).addAttribute("jobLevel", anyString());
        verify(model, times(1)).addAttribute("responsibilities", "Sample responsibilities");
        verify(model, times(1)).addAttribute("jobTitle", any(JobTitle.class));
        verify(model, times(1)).addAttribute("schedule", any(WorkSchedule.class));
        verify(model, times(1)).addAttribute("location", any(Location.class));
        verify(model, times(1)).addAttribute("jobType", any(JobType.class));
        verify(model, times(1)).addAttribute("isJobClosed", false);
    }

    @Test
    public void testViewJobDetailsInvalidId() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Prepare test data with an invalid ID
        String invalidId = "invalid";
        Model model = mock(Model.class);

        // Mock JobDetailService behavior to throw an exception
        when(jobDetailService.getById(anyLong())).thenThrow(new Exception("Invalid ID"));

        // Call the controller method
        String viewName = candidateController.viewJobDetails(invalidId, model);

        // Assertions
        assertEquals("home", viewName);
        verify(model, times(1)).addAttribute("error", "There is no Job Detail with this id, G!");
    }

     @Test
    public void testGetCandidateDetailsValidId() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Prepare test data
        Long id = 1L;

        // Mock CandidateRepository behavior
        Candidate candidate = new Candidate();
        when(candidateRepository.findById(id)).thenReturn(Optional.of(candidate));

        // Call the controller method
        ResponseEntity<Candidate> response = candidateController.getCandidateDetails(id);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(candidate, response.getBody());
    }

    @Test
    public void testGetCandidateDetailsInvalidId() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Prepare test data with an invalid ID
        Long invalidId = 999L;

        // Mock CandidateRepository behavior to return an empty Optional
        when(candidateRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Call the controller method
        ResponseEntity<Candidate> response = candidateController.getCandidateDetails(invalidId);

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}


