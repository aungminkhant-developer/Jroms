package gp.pyinsa.jroms.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.constants.Constants;
import gp.pyinsa.jroms.constants.Interview_Result;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.CurriculumVitae;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.repositories.CandidateRepository;
import gp.pyinsa.jroms.repositories.UserRepository;
import gp.pyinsa.jroms.repositories.datatable.CandidateDTRepository;

@Service
public class CandidateServiceImpl implements CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandidateDTRepository candidateDTRepository;

    @Override
    public DataTablesOutput<Candidate> getAllCandidates(@Valid DataTablesInput input) {
        // Get the date range value from view
        // *** the index should be replaced with the index of date column you want to
        // filter ***
        String dateRange = input.getColumns().get(4).getSearch().getValue();

        if (dateRange == null || dateRange.isEmpty()) {
            return candidateDTRepository.findAll(input);
        } else {
            String[] dates = dateRange.split(";");
            try {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate startDate = LocalDate.parse(dates[0], dtf);
                LocalDate endDate = LocalDate.parse(dates[1], dtf);

                // Filter with date between
                Specification<Candidate> spec = new Specification<Candidate>() {

                    @Override
                    public Predicate toPredicate(Root<Candidate> root, CriteriaQuery<?> query,
                            CriteriaBuilder criteriaBuilder) {
                        Expression<LocalDate> expr = root.get("createdDate").as(LocalDate.class);
                        return criteriaBuilder.between(expr, startDate, endDate);
                    }

                };

                return candidateDTRepository.findAll(input, spec);
            } catch (DateTimeParseException e) {
                // Date is failed to parse, return all rows
                return candidateDTRepository.findAll(input);
            }

        }

    }

    @Override
    public Page<Candidate> getCandidates(int currentPage) {
        // TODO Auto-generated method stub
        PageRequest pageable = PageRequest.of(currentPage, Constants.USER_PAGE_SIZE);
        return candidateRepository.findAll(pageable);
    }

    @Override
    public Candidate getById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    @Override
    public List<Candidate> searchByIdOrName(String id, String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByIdOrName'");
    }

    @Override
    public String save(Candidate candidate) {
        // TODO Auto-generated method stub
        try {
            candidateRepository.saveAndFlush(candidate);
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown error";
        }
        return null;
    }

    @Override
    public String update(Candidate candidate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void deleteById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

    @Override
    public List<Candidate> getAllCandidateByJobDetailId(long id) {
        return candidateRepository.findAllByJobDetail_Id(id);
    }

    public List<Candidate> getAllCandidateCountByJobDetailId(Long jobDetailId) {
        return candidateRepository.findAllByJobDetailId(jobDetailId);
    }

    @Override
    public Candidate getCandidateById(long id) {
        return candidateRepository.findById(id).get();
    }

    @Override
    public List<Candidate> getAllCandidate() {
        return candidateRepository.findAll();
    }

    @Override
    public long getCountByCreatedDateBetween(String year, String month) {

        if (Integer.valueOf(month) < 10) {
            month = "0" + month;
        }
        String date1 = year + "-" + month + "-01"+" 00:00:00";
        String date2 = year + "-" + month + "-31"+" 23:59:59";
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date1);
            endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return candidateRepository.countByCreatedDateBetween(startDate, endDate);

    }

    @Override
    public List<Candidate> getCandidatesByInterviewResult(Interview_Result result) {
        return candidateRepository.findAllByFinalResult(result);
    }

    @Override
    public List<Candidate> getCandidatesByJobDetail_IdAndInterviewResult(Long jobDetailId, Interview_Result result) {
        return candidateRepository.findAllByJobDetail_IdAndFinalResult(jobDetailId, result);
    }

    @Override
    public long getCountByFinalResultAndCreatedDate(Interview_Result result, String year, String month) {
        if (Integer.valueOf(month) < 10) {
            month = "0" + month;
        }
        String date1 = year + "-" + month + "-01"+" 00:00:00";
        String date2 = year + "-" + month + "-31"+" 23:59:59";
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date1);
            endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return candidateRepository.countByCreatedDateBetweenAndFinalResult(startDate, endDate, result);
    }

    @Override
    public List<CurriculumVitae> getFilteredCurriculumVitaeById(List<Long> candidateIds) {
        // TODO Auto-generated method stub
        List<CurriculumVitae> cvUrl = new ArrayList<>();
        List<Candidate> candidates = candidateRepository.findAllById(candidateIds);

        // find cv id of each candidate id
        for (Candidate candidate : candidates) {
            CurriculumVitae cv = candidate.getCurriculumVitae();
            cvUrl.add(cv);
        }

        return cvUrl;
    }

    @Override
    public List<Candidate> getCandidatesByInterviewResultAndOfferMailSent(Interview_Result result, boolean status) {
        return candidateRepository.findAllByFinalResultAndJobOfferMailSent(result, status);
    }

    @Override
    public List<Candidate> getCandidatesByJobDetail_IdAndInterviewResultAndOfferMailSent(Long jobDetailId,
            Interview_Result result, boolean status) {
       return candidateRepository.findAllByJobDetail_IdAndFinalResultAndJobOfferMailSent(jobDetailId, result, status);
    }

    @Override
    public long getCountByFinalResultAndOfferMailSendAndCreatedDate(Interview_Result result, boolean status,
            String year, String month) {
         if (Integer.valueOf(month) < 10) {
            month = "0" + month;
        }
        String date1 = year + "-" + month + "-01"+" 00:00:00";
        String date2 = year + "-" + month + "-31"+" 23:59:59";
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date1);
            endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return candidateRepository.countByCreatedDateBetweenAndFinalResultAndJobOfferMailSent(startDate, endDate, result,status);
    }

    @Override
    public long getCountByJobAcceptedAndCreatedDate(boolean status, String year, String month) {
         if (Integer.valueOf(month) < 10) {
            month = "0" + month;
        }
        String date1 = year + "-" + month + "-01"+" 00:00:00";
        String date2 = year + "-" + month + "-31"+" 23:59:59";
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date1);
            endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return candidateRepository.countByCreatedDateBetweenAndJobAccepted(startDate, endDate, status);
    }

    @Override
    public long getCountByJobAccepted(boolean status) {
        return candidateRepository.countByJobAccepted(status);
    }

    @Override
    public long getCountByJobDetail_IdAndJobAccepted(JobDetail jobDetail, boolean status) {
        return candidateRepository.countByJobDetailAndJobAccepted(jobDetail, status);
    }

}
