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
import gp.pyinsa.jroms.models.InterviewStage;
import gp.pyinsa.jroms.repositories.InterviewStageRepository;

@SpringBootTest
public class InterviewStageServiceTest {
    @Mock
    private InterviewStageRepository interviewStageRepository;

    @InjectMocks
    private InterviewStageServiceImpl interviewStageService;

    @Test
    void getActiveInterviewStages_WhenCalled_ShouldReturnActiveInterviewStagesList() {
        // Setup
        InterviewStage is1 = new InterviewStage((short) 1, "Java Developer", Status.ACTIVE);
        InterviewStage is2 = new InterviewStage((short) 2, "PHP Developer", Status.ACTIVE);
        List<InterviewStage> interviewStages = List.of(is1, is2);

        // Mock
        when(interviewStageRepository.findByStatus(Status.ACTIVE)).thenReturn(interviewStages);

        // Assert
        List<InterviewStage> activeInterviewStages = interviewStageService.getActiveInterviewStages();
        assertEquals(interviewStages.size(), activeInterviewStages.size());
        verify(interviewStageRepository, times(1)).findByStatus(Status.ACTIVE);
    }

    @Test
    void addNewInterviewStage_WhenInterviewStageIsValid_ShouldSaveInterviewStage() {
        // Setup
        InterviewStage interviewStage = new InterviewStage((short) 1, "Java Developer", Status.ACTIVE);

        // Mock
        when(interviewStageRepository.existsByNameAndStatus(interviewStage.getName(), Status.ACTIVE)).thenReturn(false);

        // Assert
        interviewStageService.addNewInterviewStage(interviewStage);
        verify(interviewStageRepository, times(1)).existsByNameAndStatus(interviewStage.getName(), Status.ACTIVE);
        verify(interviewStageRepository, times(1)).saveAndFlush(interviewStage);
    }

    @Test
    void addNewInterviewStage_WhenInterviewStageIsDuplicated_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        InterviewStage interviewStage = new InterviewStage((short) 1, "Java Developer", Status.ACTIVE);

        // Mock
        when(interviewStageRepository.existsByNameAndStatus(interviewStage.getName(), Status.ACTIVE)).thenReturn(true);

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> interviewStageService.addNewInterviewStage(interviewStage));
        verify(interviewStageRepository, times(1)).existsByNameAndStatus(interviewStage.getName(), Status.ACTIVE);
        verify(interviewStageRepository, times(0)).saveAndFlush(interviewStage);
    }

    @Test
    void updateInterviewStage_WhenInterviewStageIsValid_ShouldUpdateInterviewStage() {
        // Setup
        InterviewStage interviewStage = new InterviewStage((short) 1, "Java Developer Updated", Status.ACTIVE);
        InterviewStage oldInterviewStage = new InterviewStage((short) 1, "Java Developer", Status.ACTIVE);

        // Mock
        when(interviewStageRepository.existsByNameAndStatus(interviewStage.getName(), Status.ACTIVE)).thenReturn(false);
        when(interviewStageRepository.findById(interviewStage.getId())).thenReturn(Optional.of(oldInterviewStage));

        // Assert
        interviewStageService.updateInterviewStage(interviewStage);
        verify(interviewStageRepository, times(1)).existsByNameAndStatus(interviewStage.getName(), Status.ACTIVE);
        verify(interviewStageRepository, times(1)).findById(interviewStage.getId());
        verify(interviewStageRepository, times(1)).saveAndFlush(oldInterviewStage);
        assertEquals(interviewStage, oldInterviewStage);
    }

    @Test
    void updateInterviewStage_WhenInterviewStageIsDuplicated_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        InterviewStage interviewStage = new InterviewStage((short) 1, "Java Developer", Status.ACTIVE);

        // Mock
        when(interviewStageRepository.existsByNameAndStatus(interviewStage.getName(), Status.ACTIVE)).thenReturn(true);

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> interviewStageService.updateInterviewStage(interviewStage));
        verify(interviewStageRepository, times(1)).existsByNameAndStatus(interviewStage.getName(), Status.ACTIVE);
        verify(interviewStageRepository, times(0)).findById(interviewStage.getId());
        verify(interviewStageRepository, times(0)).saveAndFlush(interviewStage);
    }

    @Test
    void updateInterviewStage_WhenIdIsInvalid_ShouldThrowResourceNotFoundException() {
        // Setup
        InterviewStage interviewStage = new InterviewStage((short) 1, "Java Developer", Status.ACTIVE);

        // Mock
        when(interviewStageRepository.existsByNameAndStatus(interviewStage.getName(), Status.ACTIVE)).thenReturn(false);
        when(interviewStageRepository.findById(interviewStage.getId())).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> interviewStageService.updateInterviewStage(interviewStage));
        verify(interviewStageRepository, times(1)).existsByNameAndStatus(interviewStage.getName(), Status.ACTIVE);
        verify(interviewStageRepository, times(1)).findById(interviewStage.getId());
        verify(interviewStageRepository, times(0)).saveAndFlush(interviewStage);
    }

    @Test
    void deleteInterviewStageById_WhenIdIsValid_ShouldChangeStatusToDeleted() {
        // Setup
        InterviewStage interviewStage = new InterviewStage((short) 1, "Java Developer", Status.ACTIVE);
        short interviewStageId = 1;

        // Mock
        when(interviewStageRepository.findById(interviewStageId)).thenReturn(Optional.of(interviewStage));

        // Assert
        interviewStageService.deleteInterviewStageById(interviewStageId);
        verify(interviewStageRepository, times(1)).findById(interviewStageId);
        verify(interviewStageRepository, times(1)).saveAndFlush(interviewStage);
        assertEquals(Status.DELETED, interviewStage.getStatus());
    }

    @Test
    void deleteInterviewStageById_WhenIdIsInvalid_ShouldThrowResourceNotFoundException() {
        // Setup
        InterviewStage interviewStage = new InterviewStage((short) 1, "Java Developer", Status.ACTIVE);
        short interviewStageId = 1;

        // Mock
        when(interviewStageRepository.findById(interviewStageId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> interviewStageService.deleteInterviewStageById(interviewStageId));
        verify(interviewStageRepository, times(1)).findById(interviewStageId);
        verify(interviewStageRepository, times(0)).saveAndFlush(interviewStage);
        assertEquals(Status.ACTIVE, interviewStage.getStatus());
    }

    @Test
    void getById_WhenCalled_ShouldReturnInterviewStage() {
        // Setup
        InterviewStage interviewStage = new InterviewStage((short) 1, "Java Developer", Status.ACTIVE);
        short interviewStageId = 1;

        // Mock
        when(interviewStageRepository.findById(interviewStageId)).thenReturn(Optional.of(interviewStage));

        // Assert
        InterviewStage foundInterviewStage = interviewStageService.getInterviewStageById(interviewStageId);
        assertEquals(interviewStage, foundInterviewStage);
        verify(interviewStageRepository, times(1)).findById(interviewStageId);
    }
}
