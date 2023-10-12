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
import gp.pyinsa.jroms.models.JobType;
import gp.pyinsa.jroms.repositories.JobTypeRepository;

@SpringBootTest
public class JobTypeServiceTest {
    @Mock
    private JobTypeRepository jobTypeRepository;

    @InjectMocks
    private JobTypeServiceImpl jobTypeService;

    @Test
    void getActiveJobTypes_WhenCalled_ShouldReturnActiveJobTypesList() {
        // Setup
        JobType jt1 = new JobType((short) 1, "Full Time", Status.ACTIVE);
        JobType jt2 = new JobType((short) 2, "Part Time", Status.ACTIVE);
        List<JobType> jobTypes = List.of(jt1, jt2);

        // Mock
        when(jobTypeRepository.findByStatus(Status.ACTIVE)).thenReturn(jobTypes);

        // Assert
        List<JobType> activeJobTypes = jobTypeService.getActiveJobTypes();
        assertEquals(jobTypes.size(), activeJobTypes.size());
        verify(jobTypeRepository, times(1)).findByStatus(Status.ACTIVE);
    }

    @Test
    void addNewJobType_WhenJobTypeIsValid_ShouldSaveJobType() {
        // Setup
        JobType jobType = new JobType((short) 1, "Full Time", Status.ACTIVE);

        // Mock
        when(jobTypeRepository.existsByNameAndStatus(jobType.getName(), Status.ACTIVE)).thenReturn(false);

        // Assert
        jobTypeService.addNewJobType(jobType);
        verify(jobTypeRepository, times(1)).existsByNameAndStatus(jobType.getName(), Status.ACTIVE);
        verify(jobTypeRepository, times(1)).saveAndFlush(jobType);
    }

    @Test
    void addNewJobType_WhenJobTypeIsDuplicated_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        JobType jobType = new JobType((short) 1, "Full Time", Status.ACTIVE);

        // Mock
        when(jobTypeRepository.existsByNameAndStatus(jobType.getName(), Status.ACTIVE)).thenReturn(true);

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> jobTypeService.addNewJobType(jobType));
        verify(jobTypeRepository, times(1)).existsByNameAndStatus(jobType.getName(), Status.ACTIVE);
        verify(jobTypeRepository, times(0)).saveAndFlush(jobType);
    }

    @Test
    void updateJobType_WhenJobTypeIsValid_ShouldUpdateJobType() {
        // Setup
        JobType jobType = new JobType((short) 1, "Full Time Updated", Status.ACTIVE);
        JobType oldJobType = new JobType((short) 1, "Full Time", Status.ACTIVE);

        // Mock
        when(jobTypeRepository.existsByNameAndStatus(jobType.getName(), Status.ACTIVE)).thenReturn(false);
        when(jobTypeRepository.findById(jobType.getId())).thenReturn(Optional.of(oldJobType));

        // Assert
        jobTypeService.updateJobType(jobType);
        verify(jobTypeRepository, times(1)).existsByNameAndStatus(jobType.getName(), Status.ACTIVE);
        verify(jobTypeRepository, times(1)).findById(jobType.getId());
        verify(jobTypeRepository, times(1)).saveAndFlush(oldJobType);
        assertEquals(jobType, oldJobType);
    }

    @Test
    void updateJobType_WhenJobTypeIsDuplicated_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        JobType jobType = new JobType((short) 1, "Full Time", Status.ACTIVE);

        // Mock
        when(jobTypeRepository.existsByNameAndStatus(jobType.getName(), Status.ACTIVE)).thenReturn(true);

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> jobTypeService.updateJobType(jobType));
        verify(jobTypeRepository, times(1)).existsByNameAndStatus(jobType.getName(), Status.ACTIVE);
        verify(jobTypeRepository, times(0)).findById(jobType.getId());
        verify(jobTypeRepository, times(0)).saveAndFlush(jobType);
    }

    @Test
    void updateJobType_WhenIdIsInvalid_ShouldThrowResourceNotFoundException() {
        // Setup
        JobType jobType = new JobType((short) 1, "Full Time", Status.ACTIVE);

        // Mock
        when(jobTypeRepository.existsByNameAndStatus(jobType.getName(), Status.ACTIVE)).thenReturn(false);
        when(jobTypeRepository.findById(jobType.getId())).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> jobTypeService.updateJobType(jobType));
        verify(jobTypeRepository, times(1)).existsByNameAndStatus(jobType.getName(), Status.ACTIVE);
        verify(jobTypeRepository, times(1)).findById(jobType.getId());
        verify(jobTypeRepository, times(0)).saveAndFlush(jobType);
    }

    @Test
    void deleteJobTypeById_WhenIdIsValid_ShouldChangeStatusToDeleted() {
        // Setup
        JobType jobType = new JobType((short) 1, "Full Time", Status.ACTIVE);
        short jobTypeId = 1;

        // Mock
        when(jobTypeRepository.findById(jobTypeId)).thenReturn(Optional.of(jobType));

        // Assert
        jobTypeService.deleteJobTypeById(jobTypeId);
        verify(jobTypeRepository, times(1)).findById(jobTypeId);
        verify(jobTypeRepository, times(1)).saveAndFlush(jobType);
        assertEquals(Status.DELETED, jobType.getStatus());
    }

    @Test
    void deleteJobTypeById_WhenIdIsInvalid_ShouldThrowResourceNotFoundException() {
        // Setup
        JobType jobType = new JobType((short) 1, "Full Time", Status.ACTIVE);
        short jobTypeId = 1;

        // Mock
        when(jobTypeRepository.findById(jobTypeId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> jobTypeService.deleteJobTypeById(jobTypeId));
        verify(jobTypeRepository, times(1)).findById(jobTypeId);
        verify(jobTypeRepository, times(0)).saveAndFlush(jobType);
        assertEquals(Status.ACTIVE, jobType.getStatus());
    }

}
