package gp.pyinsa.jroms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.CurriculumVitae;
import gp.pyinsa.jroms.repositories.CandidateRepository;

@SpringBootTest
public class CandidateServiceTest {

    private CandidateRepository candidateRepository;

    private CandidateService candidateService;

    @BeforeEach
    public void setUp() {
        candidateRepository = mock(CandidateRepository.class);
    }

    @Test
    public void testSaveCandidate_Success() {
        Candidate candidate = new Candidate(); // Create a candidate instance
        when(candidateRepository.saveAndFlush(candidate)).thenReturn(candidate);

        String result = candidateService.save(candidate);

        assertNull(result); // Since the method returns null on success
        verify(candidateRepository, times(1)).saveAndFlush(candidate);
    }

    @Test
    public void testSaveCandidate_Failure() {
        Candidate candidate = new Candidate(); // Create a candidate instance
        when(candidateRepository.saveAndFlush(candidate)).thenThrow(new RuntimeException("Mocked error"));

        String result = candidateService.save(candidate);

        assertEquals("Unknown error", result);
        verify(candidateRepository, times(1)).saveAndFlush(candidate);
    }

    @Test
    void getAllCandidates_WhenCall_ShouldReturnAllCandidates() {
        // Setup
        Candidate c1 = new Candidate();
        Candidate c2 = new Candidate();
        List<Candidate> candidates = List.of(c1, c2);

        // Mock
        when(candidateRepository.findAll()).thenReturn(candidates);

        // Assert
        List<Candidate> allCandidates = candidateService.getAllCandidate();
        assertEquals(allCandidates.size(), candidates.size(),
                "Returned location size does not match with existing location size.");
        verify(candidateRepository, times(1)).findAll();
    }

    @Test
    public void testGetCandidateById_Exists() {
        long candidateId = 1L;
        Candidate expectedCandidate = new Candidate(); // Create an instance of the expected candidate
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(expectedCandidate));

        Candidate result = candidateService.getCandidateById(candidateId);

        assertEquals(expectedCandidate, result);
        verify(candidateRepository, times(1)).findById(candidateId);
    }

    @Test
    public void testGetCandidateById_NotFound() {
        long candidateId = 2L;
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            candidateService.getCandidateById(candidateId);
        });
        verify(candidateRepository, times(1)).findById(candidateId);
    }

     @Test
    public void testGetFilteredCurriculumVitaeById() {
        List<Long> candidateIds = List.of(1L, 2L);
        Candidate candidate1 = new Candidate();
        Candidate candidate2 = new Candidate();
        CurriculumVitae cv1 = new CurriculumVitae();
        CurriculumVitae cv2 = new CurriculumVitae();
        
        candidate1.setCurriculumVitae(cv1);
        candidate2.setCurriculumVitae(cv2);

        when(candidateRepository.findAllById(candidateIds)).thenReturn(List.of(candidate1, candidate2));

        List<CurriculumVitae> result = candidateService.getFilteredCurriculumVitaeById(candidateIds);

        assertEquals(2, result.size());
        assertTrue(result.contains(cv1));
        assertTrue(result.contains(cv2));
        verify(candidateRepository, times(1)).findAllById(candidateIds);
    }

}