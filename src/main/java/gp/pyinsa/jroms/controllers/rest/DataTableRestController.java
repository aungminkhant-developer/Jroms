package gp.pyinsa.jroms.controllers.rest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gp.pyinsa.jroms.constants.Interview_Result;
import gp.pyinsa.jroms.constants.Mail_Status;
import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.models.Interview;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.Location;
import gp.pyinsa.jroms.models.Team;
import gp.pyinsa.jroms.repositories.CandidateRepository;
import gp.pyinsa.jroms.repositories.DepartmentRepository;
import gp.pyinsa.jroms.repositories.InterviewRepository;
import gp.pyinsa.jroms.repositories.JobDetailRepository;
import gp.pyinsa.jroms.repositories.datatable.CandidateDTRepository;
import gp.pyinsa.jroms.repositories.datatable.DepartmentDTRepository;
import gp.pyinsa.jroms.repositories.datatable.InterviewDTRepository;
import gp.pyinsa.jroms.repositories.datatable.InterviewsDTRepository;
import gp.pyinsa.jroms.repositories.datatable.LocationDTRepository;
import gp.pyinsa.jroms.repositories.datatable.TeamDTRepository;
import gp.pyinsa.jroms.repositories.datatable.UserDTRepository;

@RestController
@RequestMapping("/mng/rest")
public class DataTableRestController {
    @Autowired
    private LocationDTRepository locationRepository;

    @Autowired
    private DepartmentDTRepository departmentDTRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TeamDTRepository teamDTRepository;

    @Autowired
    private JobDetailRepository jobDetailsRepository;

    @Autowired
    private InterviewDTRepository interviewDTRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CandidateDTRepository candidateDTRepository;

    @Autowired
    private UserDTRepository userDTRepository;

    @Autowired
    private InterviewsDTRepository interviewsDTRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @PostMapping("/locations")
    public DataTablesOutput<Location> listPOST(@Valid @RequestBody DataTablesInput input) {
        Specification<Location> spec = new Specification<Location>() {

            @Override
            public Predicate toPredicate(Root<Location> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("status"), Status.ACTIVE);
            }

        };
        return locationRepository.findAll(input, spec);
    }

    @PostMapping("/departments")
    public DataTablesOutput<Department> getActiveDepartments(@Valid @RequestBody DataTablesInput input) {
        Specification<Department> spec = new Specification<Department>() {

            @Override
            public Predicate toPredicate(Root<Department> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("status"), Status.ACTIVE);
            }

        };
        return departmentDTRepository.findAll(input, spec);
    }

    @PostMapping("/teams")
    public DataTablesOutput<Team> getActiveTeamsUnderTeam(@Valid @RequestBody DataTablesInput input,
            @RequestParam("dept-id") String deptId) {
        Optional<Department> deptOpt = departmentRepository.findById(deptId);
        if (deptOpt.isEmpty()) {
            return new DataTablesOutput<>();
        }

        Specification<Team> spec = new Specification<Team>() {

            @Override
            public Predicate toPredicate(Root<Team> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.equal(root.get("status"), Status.ACTIVE));
                predicates.add(criteriaBuilder.equal(root.get("department"), deptOpt.get()));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }

        };
        return teamDTRepository.findAll(input, spec);
    }

    @PostMapping("/interviews")
    public DataTablesOutput<Interview> getinterviews(@Valid @RequestBody DataTablesInput input) {
        return interviewDTRepository.findAll(input);
    }

    @PostMapping("/jobDetailInterview")
    public DataTablesOutput<Interview> getInterviewsUnderJobdetail(@Valid @RequestBody DataTablesInput input,@RequestParam("jobDetailId")Long jobDetailId){
        Optional<JobDetail> jobOpt = jobDetailsRepository.findById(jobDetailId);
       

        Specification<Interview> spec=new Specification<Interview>() {
            @Override
            public Predicate toPredicate(Root<Interview> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
                List<Predicate> predicates= new ArrayList<>();
                predicates.add(criteriaBuilder.equal(root.get("jobDetail"), jobOpt.get()));
                 return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
            
        };
       
        return interviewDTRepository.findAll(input,spec);
    }

    @PostMapping("/jobDetailInterview/finalStage")
    public DataTablesOutput<Interview> getInterviewsFinalStageUnderJobDetail(@Valid @RequestBody DataTablesInput input,@RequestParam("jobDetailId")Long jobDetailId){
        Optional<JobDetail> jOptional=jobDetailsRepository.findById(jobDetailId);

        Specification<Interview> spec=new Specification<Interview>() {

            @Override
            public Predicate toPredicate(Root<Interview> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
               List<Predicate> predicates= new ArrayList<>();
                predicates.add(criteriaBuilder.equal(root.get("jobDetail"), jOptional.get()));
                predicates.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("mailStatus"), Mail_Status.SEND),
                                                    criteriaBuilder.and(criteriaBuilder.equal(root.get("mailStatus"), Mail_Status.NEXT_MAIL_SEND), 
                                                                        criteriaBuilder.equal(root.get("result"), Interview_Result.PASSED))));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }    
        };
        return interviewDTRepository.findAll(input,spec);
    }

    @PostMapping("/allInterviews/finalStage")
    public DataTablesOutput<Interview> getAllInterviewsFinalStage(@Valid @RequestBody DataTablesInput input){
        Specification<Interview> spec=new Specification<Interview>() {    
            @Override
            public Predicate toPredicate(Root<Interview> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates=new ArrayList<>();
                predicates.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("mailStatus"), Mail_Status.SEND),
                                                    criteriaBuilder.and(criteriaBuilder.equal(root.get("mailStatus"), Mail_Status.NEXT_MAIL_SEND), 
                                                                        criteriaBuilder.equal(root.get("result"), Interview_Result.PASSED))));

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return interviewDTRepository.findAll(input,spec); 
    }

    @PostMapping("/candidate")
    public DataTablesOutput<Candidate> getCandidatesUnderJobdetail(@Valid @RequestBody DataTablesInput input,@RequestParam("jobdetail-id")Long jobDetailId){
        // Get the date range value from view 
        // *** the index should be replaced with the index of date column you want to filter ***
        String dateRange = input.getColumns().get(2).getSearch().getValue();
        Optional<JobDetail> jobOpt = jobDetailsRepository.findById(jobDetailId);

        // Filter with job detail
        Specification<Candidate> spec = new Specification<Candidate>() {

            @Override
            public Predicate toPredicate(Root<Candidate> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.equal(root.get("jobDetail"), jobOpt.get()));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }

        };

        if (!dateRange.contains(";")) {
            DataTablesOutput<Candidate> output = candidateDTRepository.findAll(input, spec);
            return output;
        } else {
            String[] dates = dateRange.split(";");
            try {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate startDate = LocalDate.parse(dates[0], dtf);
                LocalDate endDate = LocalDate.parse(dates[1], dtf);

                // Filter with date between
                Specification<Candidate> dateSpec = new Specification<Candidate>() {

                    @Override
                    public Predicate toPredicate(Root<Candidate> root, CriteriaQuery<?> query,
                            CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates= new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("jobDetail"), jobOpt.get()));

                        Expression<LocalDate> expr = root.get("createdDate").as(LocalDate.class);
                        predicates.add(criteriaBuilder.between(expr, startDate, endDate));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                    }

                };

                return candidateDTRepository.findAll(input, dateSpec);
            } catch (DateTimeParseException e) {
                // Date is failed to parse, return all rows
                return candidateDTRepository.findAll(input, spec);
            }

        }
    }

    // @PostMapping("/interview")
    // public DataTablesOutput<Interview> gets(@Valid @RequestBody DataTablesInput input){

    //     return interviewDTRepository.findAll(input);
    // }


    @PostMapping("/candidateInterview")
    public DataTablesOutput<Interview> getCandidateInterview(@Valid @RequestBody DataTablesInput input,
            @RequestParam("candidateId") long candidateId) {
        Optional<Candidate> candidate = candidateRepository.findById(candidateId);
        if (candidate.isEmpty()) {
            return new DataTablesOutput<>();
        }
        Specification<Interview> spec = new Specification<Interview>() {

            @Override
            public Predicate toPredicate(Root<Interview> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.equal(root.get("candidate"), candidate.get()));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }

        };
        return interviewDTRepository.findAll(input, spec);
    }

    // @PostMapping("/test")
    // public DataTablesOutput<User> getTestData(@Valid @RequestBody DataTablesInput input) {
    //     // Get the date range value from view 
    //     // *** the index should be replaced with the index of date column you want to filter ***
    //     String dateRange = input.getColumns().get(3).getSearch().getValue();

    //     if (dateRange == null || dateRange.isEmpty()) {
    //         return userDTRepository.findAll(input);
    //     } else {
    //         String[] dates = dateRange.split(";");
    //         try {
    //             DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //             LocalDate startDate = LocalDate.parse(dates[0], dtf);
    //             LocalDate endDate = LocalDate.parse(dates[1], dtf);

    //             // Filter with date between
    //             Specification<User> spec = new Specification<User>() {

    //                 @Override
    //                 public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query,
    //                         CriteriaBuilder criteriaBuilder) {
    //                     Expression<LocalDate> expr = root.get("createdDate").as(LocalDate.class);
    //                     return criteriaBuilder.between(expr, startDate, endDate);
    //                 }

    //             };

    //             return userDTRepository.findAll(input, spec);
    //         } catch (DateTimeParseException e) {
    //             // Date is failed to parse, return all rows
    //             return userDTRepository.findAll(input);
    //         }

    //     }

    // }
    @PostMapping("/test")
    public DataTablesOutput<Interview> getTestData(@Valid @RequestBody DataTablesInput input) {
        // Get the date range value from view 
        // *** the index should be replaced with the index of date column you want to filter ***
        String dateRange = input.getColumns().get(3).getSearch().getValue();

        if (dateRange == null || dateRange.isEmpty()) {
            return interviewDTRepository.findAll(input);
        } else {
            String[] dates = dateRange.split(";");
            try {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate startDate = LocalDate.parse(dates[0], dtf);
                LocalDate endDate = LocalDate.parse(dates[1], dtf);

                // Filter with date between
                Specification<Interview> spec = new Specification<Interview>() {

                    @Override
                    public Predicate toPredicate(Root<Interview> root, CriteriaQuery<?> query,
                            CriteriaBuilder criteriaBuilder) {
                                Join<Object, Object> interviewSchedule = root.join("interviewSchedule");
                        Expression<LocalDate> expr = interviewSchedule.get("interviewDate").as(LocalDate.class);
                        return criteriaBuilder.between(expr, startDate, endDate);
                    }

                };

                return interviewDTRepository.findAll(input, spec);
            } catch (DateTimeParseException e) {
                // Date is failed to parse, return all rows
                return interviewDTRepository.findAll(input);
            }

        }

    }

}
