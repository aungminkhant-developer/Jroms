package gp.pyinsa.jroms.services;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.CurriculumVitae;
import gp.pyinsa.jroms.repositories.CVRepository;

@Service
public class CVServiceImpl implements CVService{

    @Autowired
    private CVRepository cvRepository;

    @Override
    public String save(CurriculumVitae cv) {
        // TODO Auto-generated method stub
        try {
            cvRepository.saveAndFlush(cv);
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown error";
        }
        return null;
    }

    @Override
    public List<CurriculumVitae> getAllCV() {
        // TODO Auto-generated method stub
        return cvRepository.findAll();
    }

    @Override
    public CurriculumVitae getCurriculumVitaeById(long id) {
        // TODO Auto-generated method stub
        
        return cvRepository.findById(id).get();

    }

    @Override
    public List<Long> getFilteredCandidateIds(List<Candidate> filteredData) {
        // TODO Auto-generated method stub
        List<Long> candidateIds = new ArrayList<>();
        
        for (Candidate candidate : filteredData) {
            candidateIds.add(candidate.getId());
        }
        
        return candidateIds;    
    }

    
    
}
