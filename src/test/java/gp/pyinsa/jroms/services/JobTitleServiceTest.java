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
import gp.pyinsa.jroms.models.JobTitle;
import gp.pyinsa.jroms.repositories.JobTitleRepository;

@SpringBootTest
public class JobTitleServiceTest {
    @Mock
    private JobTitleRepository jobTitleRepository;

    @InjectMocks
    private JobTitleServiceImpl jobTitleService;

    @Test
    void getActiveJobTitles_WhenCalled_ShouldReturnActiveJobTitlesList() {
        // Setup
        JobTitle jt1 = new JobTitle((short) 1, "Java Developer", Status.ACTIVE);
        JobTitle jt2 = new JobTitle((short) 2, "PHP Developer", Status.ACTIVE);
        List<JobTitle> jobTitles = List.of(jt1, jt2);

        // Mock
        when(jobTitleRepository.findByStatus(Status.ACTIVE)).thenReturn(jobTitles);

        // Assert
        List<JobTitle> activeJobTitles = jobTitleService.getActiveJobTitles();
        assertEquals(jobTitles.size(), activeJobTitles.size());
        verify(jobTitleRepository, times(1)).findByStatus(Status.ACTIVE);
    }

    @Test
    void addNewJobTitle_WhenJobTitleIsValid_ShouldSaveJobTitle() {
        // Setup
        JobTitle jobTitle = new JobTitle((short) 1, "Java Developer", Status.ACTIVE);

        // Mock
        when(jobTitleRepository.existsByNameAndStatus(jobTitle.getName(), Status.ACTIVE)).thenReturn(false);

        // Assert
        jobTitleService.addNewJobTitle(jobTitle);
        verify(jobTitleRepository, times(1)).existsByNameAndStatus(jobTitle.getName(), Status.ACTIVE);
        verify(jobTitleRepository, times(1)).saveAndFlush(jobTitle);
    }

    @Test
    void addNewJobTitle_WhenJobTitleIsDuplicated_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        JobTitle jobTitle = new JobTitle((short) 1, "Java Developer", Status.ACTIVE);

        // Mock
        when(jobTitleRepository.existsByNameAndStatus(jobTitle.getName(), Status.ACTIVE)).thenReturn(true);

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> jobTitleService.addNewJobTitle(jobTitle));
        verify(jobTitleRepository, times(1)).existsByNameAndStatus(jobTitle.getName(), Status.ACTIVE);
        verify(jobTitleRepository, times(0)).saveAndFlush(jobTitle);
    }

    @Test
    void updateJobTitle_WhenJobTitleIsValid_ShouldUpdateJobTitle() {
        // Setup
        JobTitle jobTitle = new JobTitle((short) 1, "Java Developer Updated", Status.ACTIVE);
        JobTitle oldJobTitle = new JobTitle((short) 1, "Java Developer", Status.ACTIVE);

        // Mock
        when(jobTitleRepository.existsByNameAndStatus(jobTitle.getName(), Status.ACTIVE)).thenReturn(false);
        when(jobTitleRepository.findById(jobTitle.getId())).thenReturn(Optional.of(oldJobTitle));

        // Assert
        jobTitleService.updateJobTitle(jobTitle);
        verify(jobTitleRepository, times(1)).existsByNameAndStatus(jobTitle.getName(), Status.ACTIVE);
        verify(jobTitleRepository, times(1)).findById(jobTitle.getId());
        verify(jobTitleRepository, times(1)).saveAndFlush(oldJobTitle);
        assertEquals(jobTitle, oldJobTitle);
    }

    @Test
    void updateJobTitle_WhenJobTitleIsDuplicated_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        JobTitle jobTitle = new JobTitle((short) 1, "Java Developer", Status.ACTIVE);

        // Mock
        when(jobTitleRepository.existsByNameAndStatus(jobTitle.getName(), Status.ACTIVE)).thenReturn(true);

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> jobTitleService.updateJobTitle(jobTitle));
        verify(jobTitleRepository, times(1)).existsByNameAndStatus(jobTitle.getName(), Status.ACTIVE);
        verify(jobTitleRepository, times(0)).findById(jobTitle.getId());
        verify(jobTitleRepository, times(0)).saveAndFlush(jobTitle);
    }

    @Test
    void updateJobTitle_WhenIdIsInvalid_ShouldThrowResourceNotFoundException() {
        // Setup
        JobTitle jobTitle = new JobTitle((short) 1, "Java Developer", Status.ACTIVE);

        // Mock
        when(jobTitleRepository.existsByNameAndStatus(jobTitle.getName(), Status.ACTIVE)).thenReturn(false);
        when(jobTitleRepository.findById(jobTitle.getId())).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> jobTitleService.updateJobTitle(jobTitle));
        verify(jobTitleRepository, times(1)).existsByNameAndStatus(jobTitle.getName(), Status.ACTIVE);
        verify(jobTitleRepository, times(1)).findById(jobTitle.getId());
        verify(jobTitleRepository, times(0)).saveAndFlush(jobTitle);
    }

    @Test
    void deleteJobTitleById_WhenIdIsValid_ShouldChangeStatusToDeleted() {
        // Setup
        JobTitle jobTitle = new JobTitle((short) 1, "Java Developer", Status.ACTIVE);
        short jobTitleId = 1;

        // Mock
        when(jobTitleRepository.findById(jobTitleId)).thenReturn(Optional.of(jobTitle));

        // Assert
        jobTitleService.deleteJobTitleById(jobTitleId);
        verify(jobTitleRepository, times(1)).findById(jobTitleId);
        verify(jobTitleRepository, times(1)).saveAndFlush(jobTitle);
        assertEquals(Status.DELETED, jobTitle.getStatus());
    }

    @Test
    void deleteJobTitleById_WhenIdIsInvalid_ShouldThrowResourceNotFoundException() {
        // Setup
        JobTitle jobTitle = new JobTitle((short) 1, "Java Developer", Status.ACTIVE);
        short jobTitleId = 1;

        // Mock
        when(jobTitleRepository.findById(jobTitleId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> jobTitleService.deleteJobTitleById(jobTitleId));
        verify(jobTitleRepository, times(1)).findById(jobTitleId);
        verify(jobTitleRepository, times(0)).saveAndFlush(jobTitle);
        assertEquals(Status.ACTIVE, jobTitle.getStatus());
    }

    @Test
    void getAllJobTitles_WhenCalled_ShouldReturnAllJobTitleList() {
        // Setup
        JobTitle jt1 = new JobTitle((short) 1, "Java Developer", Status.ACTIVE);
        JobTitle jt2 = new JobTitle((short) 2, "PHP Developer", Status.DELETED);
        List<JobTitle> jobTitles = List.of(jt1, jt2);

        // Mock
        when(jobTitleRepository.findAll()).thenReturn(jobTitles);

        // Assert
        List<JobTitle> allJobTitles = jobTitleService.getAllJobTitles();
        verify(jobTitleRepository, times(1)).findAll();
        assertEquals(jobTitles.size(), allJobTitles.size());
    }

    @Test
    void getById_WhenCalled_ShouldReturnJobTitle() {
        // Setup
        JobTitle jobTitle = new JobTitle((short) 1, "Java Developer", Status.ACTIVE);
        short jobTitleId = 1;

        // Mock
        when(jobTitleRepository.findById(jobTitleId)).thenReturn(Optional.of(jobTitle));

        // Assert
        JobTitle foundJobTitle = jobTitleService.getById(jobTitleId);
        assertEquals(jobTitle, foundJobTitle);
        verify(jobTitleRepository, times(1)).findById(jobTitleId);
    }
}