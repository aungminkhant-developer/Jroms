package gp.pyinsa.jroms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import gp.pyinsa.jroms.exceptions.JobDetailNotFoundException;
import gp.pyinsa.jroms.exceptions.ResourceNotFoundException;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.JobLevel;
import gp.pyinsa.jroms.models.JobTitle;
import gp.pyinsa.jroms.repositories.JobDetailRepository;

@SpringBootTest
public class JobDetailServiceTest {
    @Mock
    private JobDetailRepository jobDetailRepository;

    @InjectMocks
    private JobDetailServiceImpl jobDetailService;

    @Test
    void getById_WhenIdIsValid_ShouldReturnJobDetail() {
        // Setup
        Long jobDetailId = 1L;
        JobDetail jobDetail = new JobDetail();
        jobDetail.setId(jobDetailId);

        // Mock
        when(jobDetailRepository.findById(jobDetailId)).thenReturn(Optional.of(jobDetail));

        // Assert
        JobDetail foundJobDetail = jobDetailService.getById(jobDetailId);
        assertEquals(jobDetailId, foundJobDetail.getId());
        verify(jobDetailRepository, times(1)).findById(jobDetailId);
    }

    @Test
    void getById_WhenIdIsInvalid_ShouldThrowJobDetailNotFoundException() {
        // Setup
        Long jobDetailId = 1L;
        JobDetail jobDetail = new JobDetail();
        jobDetail.setId(jobDetailId);

        // Mock
        when(jobDetailRepository.findById(jobDetailId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(JobDetailNotFoundException.class, () -> jobDetailService.getById(jobDetailId));
        verify(jobDetailRepository, times(1)).findById(jobDetailId);
    }

    @Test
    void findAllJobDetails_WhenCalled_ShouldReturnJobDetailList() {
        // Setup
        JobDetail jd1 = new JobDetail();
        JobDetail jd2 = new JobDetail();
        List<JobDetail> jobDetails = List.of(jd1, jd2);

        // Mock
        when(jobDetailRepository.findAll()).thenReturn(jobDetails);

        // Assert
        List<JobDetail> allJobDetails = jobDetailService.findAllJobDetails();
        assertEquals(jobDetails.size(), allJobDetails.size());
        verify(jobDetailRepository, times(1)).findAll();
    }

    @Test
    void addNewJobDetail_WhenCalled_ShouldSave() {
        // Setup
        String description = "<strong>description</strong>";
        JobDetail jobDetail = new JobDetail();
        jobDetail.setId(1L);
        jobDetail.setDescription(description);
        jobDetail.setResponsibilities("<i>responsibilities</i>");
        jobDetail.setRequirement("<p>requirement</p>");

        // Mock

        // Assert
        jobDetailService.addNewJobDetail(jobDetail);

        // current date
        Calendar now = Calendar.getInstance();
        int day = now.get(Calendar.DAY_OF_MONTH);
        int month = now.get(Calendar.MONTH);
        int year = now.get(Calendar.YEAR);

        // open date of jobdetail before adding
        Date openDate = jobDetail.getOpenDate();
        now.setTime(openDate);

        assertEquals(day, now.get(Calendar.DAY_OF_MONTH));
        assertEquals(month, now.get(Calendar.MONTH));
        assertEquals(year, now.get(Calendar.YEAR));
        assertNotEquals(description, jobDetail.getDescription());
        verify(jobDetailRepository, times(1)).saveAndFlush(jobDetail);
    }

    @Test
    void updateJobDetail_WhenDataIsValid_ShouldUpdate() {
        // Setup
        String description = "Updated description";
        JobDetail jobDetail = new JobDetail();
        jobDetail.setId(1L);
        jobDetail.setDescription(description);
        jobDetail.setResponsibilities("responsibilities");
        jobDetail.setRequirement("<p>requirement</p>");

        JobDetail oldJobDetail = new JobDetail();
        oldJobDetail.setId(1L);
        oldJobDetail.setDescription("Old description");
        oldJobDetail.setResponsibilities("old responsibilities");
        oldJobDetail.setRequirement("<p>old requirement</p>");

        // Mock
        when(jobDetailRepository.findById(jobDetail.getId())).thenReturn(Optional.of(oldJobDetail));

        // Assert
        jobDetailService.updateJobDetail(jobDetail);

        assertEquals(description, oldJobDetail.getDescription());
        verify(jobDetailRepository, times(1)).saveAndFlush(oldJobDetail);
        verify(jobDetailRepository, times(1)).findById(jobDetail.getId());
    }

    @Test
    void updateJobDetail_WhenOldJobDetailNotFound_ShouldThrowResourceNotFoundException() {
        // Setup
        String description = "Updated description";
        JobDetail jobDetail = new JobDetail();
        jobDetail.setId(1L);
        jobDetail.setDescription(description);
        jobDetail.setResponsibilities("responsibilities");
        jobDetail.setRequirement("<p>requirement</p>");

        JobDetail oldJobDetail = new JobDetail();
        oldJobDetail.setId(1L);
        oldJobDetail.setDescription("Old description");
        oldJobDetail.setResponsibilities("old responsibilities");
        oldJobDetail.setRequirement("<p>old requirement</p>");

        // Mock
        when(jobDetailRepository.findById(jobDetail.getId())).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> jobDetailService.updateJobDetail(jobDetail));

        verify(jobDetailRepository, times(1)).findById(jobDetail.getId());
        verify(jobDetailRepository, times(0)).saveAndFlush(oldJobDetail);
    }

    @Test
    void getJobDetailByJobTitlesAndJobLevels_WhenCalled_ShouldReturnList() {
        // Setup
        JobTitle jobTitle = new JobTitle();
        JobLevel jobLevel = new JobLevel();
        List<JobTitle> jobTitles = List.of(jobTitle);
        List<JobLevel> jobLevels = List.of(jobLevel);

        JobDetail jobDetail = new JobDetail();
        List<JobDetail> jobDetails = List.of(jobDetail);

        // Mock
        when(jobDetailRepository.findByJobTitleInAndJobLevelIn(jobTitles, jobLevels)).thenReturn(jobDetails);

        // Assert
        List<JobDetail> foundJobDetails = jobDetailService.getJobDetailByJobTitlesAndJobLevels(jobTitles, jobLevels);
        assertEquals(jobDetails.size(), foundJobDetails.size());
        verify(jobDetailRepository, times(1)).findByJobTitleInAndJobLevelIn(jobTitles, jobLevels);

    }
}
