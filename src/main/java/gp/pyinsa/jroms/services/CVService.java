package gp.pyinsa.jroms.services;

import java.util.List;

import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.CurriculumVitae;

public interface CVService {
    public List<Long> getFilteredCandidateIds(List<Candidate> filteredData);
    String save(CurriculumVitae cv);
    CurriculumVitae getCurriculumVitaeById(long id);
    List<CurriculumVitae> getAllCV();
}
