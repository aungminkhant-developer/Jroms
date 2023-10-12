package gp.pyinsa.jroms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.exceptions.ResourceNotFoundException;
import gp.pyinsa.jroms.models.JobLevel;
import gp.pyinsa.jroms.repositories.JobLevelRepository;

@SpringBootTest
public class JobLevelServiceTest {

    @Mock
    private JobLevelRepository jobLevelRepository;

    @InjectMocks
    private JobLevelServiceImpl jobLevelService;

    @Test
    void getActiveJobLevels_WhenCalled_ShouldReturnActiveJobLevelList() {
        // Setup
        JobLevel jl1 = new JobLevel((short) 1, "Senior", Status.ACTIVE);
        JobLevel jl2 = new JobLevel((short) 2, "Junior", Status.ACTIVE);
        List<JobLevel> jobLevels = List.of(jl1, jl2);

        // Mock
        when(jobLevelRepository.findByStatus(Status.ACTIVE)).thenReturn(jobLevels);

        // Assert
        List<JobLevel> activeJobTitles = jobLevelService.getActiveJobLevels();
        assertEquals(jobLevels.size(), activeJobTitles.size());
        verify(jobLevelRepository, times(1)).findByStatus(Status.ACTIVE);
    }

    @Test
    void addNewJobLevel_WhenJobLevelIsValid_ShouldSaveJobLevel() {
        // Setup
        JobLevel jobLevel = new JobLevel((short) 1, "Senior", Status.ACTIVE);

        // Mock
        when(jobLevelRepository.existsByNameAndStatus(jobLevel.getName(), Status.ACTIVE)).thenReturn(false);

        // Assert
        jobLevelService.addNewJobLevel(jobLevel);
        verify(jobLevelRepository, times(1)).existsByNameAndStatus(jobLevel.getName(), Status.ACTIVE);
        verify(jobLevelRepository, times(1)).saveAndFlush(jobLevel);
    }

    @Test
    void addNewJobLevel_WhenJobLevelIsDuplicated_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        JobLevel jobLevel = new JobLevel((short) 1, "Senior", Status.ACTIVE);

        // Mock
        when(jobLevelRepository.existsByNameAndStatus(jobLevel.getName(), Status.ACTIVE)).thenReturn(true);

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> jobLevelService.addNewJobLevel(jobLevel));
        verify(jobLevelRepository, times(1)).existsByNameAndStatus(jobLevel.getName(), Status.ACTIVE);
        verify(jobLevelRepository, times(0)).saveAndFlush(jobLevel);
    }

    @Test
    void updateJobLevel_WhenJobLevelIsValid_ShouldUpdateJobLevel() {
        // Setup
        JobLevel jobLevel = new JobLevel((short) 1, "Senior Updated", Status.ACTIVE);
        JobLevel oldJobTitle = new JobLevel((short) 1, "Senior", Status.ACTIVE);

        // Mock
        when(jobLevelRepository.existsByNameAndStatus(jobLevel.getName(), Status.ACTIVE)).thenReturn(false);
        when(jobLevelRepository.findById(jobLevel.getId())).thenReturn(Optional.of(oldJobTitle));

        // Assert
        jobLevelService.updateJobLevel(jobLevel);
        verify(jobLevelRepository, times(1)).existsByNameAndStatus(jobLevel.getName(), Status.ACTIVE);
        verify(jobLevelRepository, times(1)).findById(jobLevel.getId());
        verify(jobLevelRepository, times(1)).saveAndFlush(oldJobTitle);
        assertEquals(jobLevel, oldJobTitle);
    }

    @Test
    void updateJobLevel_WhenJobLevelIsDuplicated_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        JobLevel jobLevel = new JobLevel((short) 1, "Senior", Status.ACTIVE);

        // Mock
        when(jobLevelRepository.existsByNameAndStatus(jobLevel.getName(), Status.ACTIVE)).thenReturn(true);

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> jobLevelService.updateJobLevel(jobLevel));
        verify(jobLevelRepository, times(1)).existsByNameAndStatus(jobLevel.getName(), Status.ACTIVE);
        verify(jobLevelRepository, times(0)).findById(jobLevel.getId());
        verify(jobLevelRepository, times(0)).saveAndFlush(jobLevel);
    }

    @Test
    void updateJobLevel_WhenIdIsInvalid_ShouldThrowResourceNotFoundException() {
        // Setup
        JobLevel jobLevel = new JobLevel((short) 1, "Senior", Status.ACTIVE);

        // Mock
        when(jobLevelRepository.existsByNameAndStatus(jobLevel.getName(), Status.ACTIVE)).thenReturn(false);
        when(jobLevelRepository.findById(jobLevel.getId())).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> jobLevelService.updateJobLevel(jobLevel));
        verify(jobLevelRepository, times(1)).existsByNameAndStatus(jobLevel.getName(), Status.ACTIVE);
        verify(jobLevelRepository, times(1)).findById(jobLevel.getId());
        verify(jobLevelRepository, times(0)).saveAndFlush(jobLevel);
    }

    @Test
    void deleteJobLevelById_WhenIdIsValid_ShouldChangeStatusToDeleted() {
        // Setup
        JobLevel jobLevel = new JobLevel((short) 1, "Senior", Status.ACTIVE);
        short jobTitleId = 1;

        // Mock
        when(jobLevelRepository.findById(jobTitleId)).thenReturn(Optional.of(jobLevel));

        // Assert
        jobLevelService.deleteJobLevelById(jobTitleId);
        verify(jobLevelRepository, times(1)).findById(jobTitleId);
        verify(jobLevelRepository, times(1)).saveAndFlush(jobLevel);
        assertEquals(Status.DELETED, jobLevel.getStatus());
    }

    @Test
    void deleteJobLevelById_WhenIdIsInvalid_ShouldThrowResourceNotFoundException() {
        // Setup
        JobLevel jobLevel = new JobLevel((short) 1, "Senior", Status.ACTIVE);
        short jobTitleId = 1;

        // Mock
        when(jobLevelRepository.findById(jobTitleId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> jobLevelService.deleteJobLevelById(jobTitleId));
        verify(jobLevelRepository, times(1)).findById(jobTitleId);
        verify(jobLevelRepository, times(0)).saveAndFlush(jobLevel);
        assertEquals(Status.ACTIVE, jobLevel.getStatus());
    }

    @Test
    void getAllJobLevels_WhenCalled_ShouldReturnAllJobLevelList() {
        // Setup
        JobLevel jl1 = new JobLevel((short) 1, "Senior", Status.ACTIVE);
        JobLevel jl2 = new JobLevel((short) 2, "Junior", Status.DELETED);
        List<JobLevel> jobLevels = List.of(jl1, jl2);

        // Mock
        when(jobLevelRepository.findAll()).thenReturn(jobLevels);

        // Assert
        List<JobLevel> allJobTitles = jobLevelService.getAllJobLevels();
        verify(jobLevelRepository, times(1)).findAll();
        assertEquals(jobLevels.size(), allJobTitles.size());
    }
    
}
