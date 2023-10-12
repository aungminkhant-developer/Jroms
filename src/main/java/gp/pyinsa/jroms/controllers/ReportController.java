package gp.pyinsa.jroms.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import gp.pyinsa.jroms.constants.CandidateReportBy;
import gp.pyinsa.jroms.constants.InterviewProcessSummaryGroup;
import gp.pyinsa.jroms.constants.Interview_Result;
import gp.pyinsa.jroms.dtos.reports.CandidateSummaryDto;
import gp.pyinsa.jroms.dtos.reports.CandidateSummaryReportDto;
import gp.pyinsa.jroms.dtos.reports.InterviewProcessSummaryDto;
import gp.pyinsa.jroms.dtos.reports.InterviewProcessSummaryReportByDepartmentDto;
import gp.pyinsa.jroms.dtos.reports.InterviewProcessSummaryReportByJobOfferDto;
import gp.pyinsa.jroms.dtos.reports.InterviewProcessSummaryReportByJobPositionDto;
import gp.pyinsa.jroms.dtos.reports.InterviewProcessSummaryReportByTeamDto;
import gp.pyinsa.jroms.models.Candidate;
import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.models.Interview;
import gp.pyinsa.jroms.models.JobDetail;
import gp.pyinsa.jroms.models.JobLevel;
import gp.pyinsa.jroms.models.JobTitle;
import gp.pyinsa.jroms.models.Team;
import gp.pyinsa.jroms.repositories.CandidateRepository;
import gp.pyinsa.jroms.repositories.JobDetailRepository;
import gp.pyinsa.jroms.services.CandidateService;
import gp.pyinsa.jroms.services.DepartmentService;
import gp.pyinsa.jroms.services.JobLevelService;
import gp.pyinsa.jroms.services.JobTitleService;
import gp.pyinsa.jroms.services.ReportService;
import gp.pyinsa.jroms.services.TeamService;
import gp.pyinsa.jroms.utils.DateFormatUtil;
import gp.pyinsa.jroms.utils.ReportUtils;

@Controller
@RequestMapping("/mng")
public class ReportController {

    @Autowired
    private JobTitleService jobTitleService;

    @Autowired
    private JobLevelService jobLevelService;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private JobDetailRepository jobDetailRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private TeamService teamService;

    @GetMapping("/reports")
    public String reportingPage(ModelMap model) {
        model.addAttribute("interviewSummary", new InterviewProcessSummaryDto());
        model.addAttribute("candidateSummary", new CandidateSummaryDto());
        return "report";
    }

    @GetMapping("/reports/candidate-summary")
    public void reportCandidateSummary(@Valid @ModelAttribute("candidateSummary") CandidateSummaryDto candidateSummary,
            BindingResult result, @RequestParam("type") String type,
            HttpServletResponse response) {
        if (result.hasErrors()) {
            return;
        }

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("reportDate", DateFormatUtil.formatDateToString(new Date(), "MM/dd/yyyy"));

        List<CandidateSummaryReportDto> dtos = new ArrayList<>();

        if(candidateSummary.getReportBy().equals(CandidateReportBy.DATE_RANGE)) {
            if(candidateSummary.getFromDate() == null || candidateSummary.getToDate() == null) {
                return;
            }

            String fromDateStr = DateFormatUtil.formatDateToStringExcelFormat(candidateSummary.getFromDate());
            String toDateStr = DateFormatUtil.formatDateToStringExcelFormat(candidateSummary.getToDate());
            parameters.put("reportBy", "[" + fromDateStr + " - " + toDateStr + "]");

            dtos = candidateSummaryReportByDateRange(candidateSummary);
        } else {
            if(candidateSummary.getJobOfferId() == null) {
                return;
            }

            Candidate candidate = candidateService.getCandidateById(candidateSummary.getJobOfferId());
            StringBuilder sb = new StringBuilder();
            JobLevel jobLevel = candidate.getJobDetail().getJobLevel();
            if(jobLevel != null) {
                sb.append(jobLevel.getName() + " ");
            }
            sb.append(candidate.getJobDetail().getJobTitle().getName());
            sb.append(", id=" + candidateSummary.getJobOfferId());
            
            parameters.put("reportBy", "[" + sb.toString() + "]");
            dtos = candidateSummaryReportByJobOffer(candidateSummary);
        }

        ReportUtils.reportCandidateHistory(response, parameters, dtos, type);
    }

    @GetMapping("/reports/interview-process-summary")
    public void report(@Valid @ModelAttribute("interviewSummary") InterviewProcessSummaryDto interviewSummary,
            BindingResult result, @RequestParam("type") String type,
            HttpServletResponse response) {
        if (result.hasErrors()) {
            return;
        }

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("reportDate", DateFormatUtil.formatDateToString(new Date(), "MM/dd/yyyy"));

        List<?> dtos = new ArrayList<>();

        if (interviewSummary.getGroupBy().equals(InterviewProcessSummaryGroup.JOB_POSITION)) {
            dtos = interviewProcessSummaryByJobPosition(interviewSummary.getFromDate(), interviewSummary.getToDate());
        } else if(interviewSummary.getGroupBy().equals(InterviewProcessSummaryGroup.JOB_OFFER)) {
            dtos = interviewProcessSummaryByJobOffer(interviewSummary.getFromDate(), interviewSummary.getToDate());
        } else if(interviewSummary.getGroupBy().equals(InterviewProcessSummaryGroup.DEPARTMENT)) {
            dtos = interviewProcessSummaryByDepartment(interviewSummary.getFromDate(), interviewSummary.getToDate());
        } else {
            dtos = interviewProcessSummaryByTeam(interviewSummary.getFromDate(), interviewSummary.getToDate());
        }

        ReportUtils.reportInterviewProcessSummary(response, parameters, dtos, type, interviewSummary.getGroupBy());
    }

    // Helper methods for candidate summary report
    private List<CandidateSummaryReportDto> candidateSummaryReportByDateRange(CandidateSummaryDto candidateSummary) {
        List<CandidateSummaryReportDto> dtos = new ArrayList<>();

        List<Candidate> candidates = reportService.findCandidatesByCreatedDateBetween(candidateSummary.getFromDate(),
                candidateSummary.getToDate());
        
        for (Candidate candidate : candidates) {
            List<Interview> candidateInterviews = reportService.findInterviewsByCandidate(candidate);

            // the candidate did not reach the interview stage
            if (candidateInterviews.size() <= 0) {
                CandidateSummaryReportDto dto = new CandidateSummaryReportDto();
                dto.setName(candidate.getName());
                dto.setApplyDate(DateFormatUtil.formatDateToStringExcelFormat(candidate.getCreatedDate()));
                dto.setCvStatus(candidate.getCurriculumVitae().getStatus().name());
                dto.setInterviewStage("-");
                dto.setInterviewDate("-");
                dto.setInterviewStatus(candidate.getFinalResult().name());
                dto.setDob(DateFormatUtil.formatDateToStringExcelFormat(candidate.getDob()));
                dto.setSex(candidate.getSex());
                dto.setPhoneNumber(candidate.getPhone_number());
                dto.setEmail(candidate.getEmail());
                dto.setEducation(candidate.getEducation());
                dto.setTechnicalSkills(candidate.getTechnical_skills());
                dto.setLanguageSkills(candidate.getLanguage_skills());
                dto.setAppliedPosition(candidate.getAppliedPosition());
                dto.setLevel(candidate.getLevel());
                dto.setMainTechnology(candidate.getMain_skill());
                dto.setRelatedExperienceTime(candidate.getExperience());
                dto.setExpectedSalary(candidate.getExpected_salary().toString());

                dtos.add(dto);
            } else {
                for (Interview interview : candidateInterviews) {
                    CandidateSummaryReportDto dto = new CandidateSummaryReportDto();
                    dto.setName(candidate.getName());
                    dto.setApplyDate(DateFormatUtil.formatDateToStringExcelFormat(candidate.getCreatedDate()));
                    dto.setCvStatus("-");
                    dto.setInterviewStage(interview.getInterviewStage().getName());
                    dto.setInterviewDate(DateFormatUtil
                            .formatDateToStringExcelFormat(interview.getInterviewSchedule().getInterviewDate()));
                    dto.setInterviewStatus(interview.getResult().name());
                    dto.setDob(DateFormatUtil.formatDateToStringExcelFormat(candidate.getDob()));
                    dto.setSex(candidate.getSex());
                    dto.setPhoneNumber(candidate.getPhone_number());
                    dto.setEmail(candidate.getEmail());
                    dto.setEducation(candidate.getEducation());
                    dto.setTechnicalSkills(candidate.getTechnical_skills());
                    dto.setLanguageSkills(candidate.getLanguage_skills());
                    dto.setAppliedPosition(candidate.getAppliedPosition());
                    dto.setLevel(candidate.getLevel());
                    dto.setMainTechnology(candidate.getMain_skill());
                    dto.setRelatedExperienceTime(candidate.getExperience());
                    dto.setExpectedSalary(candidate.getExpected_salary().toString());

                    dtos.add(dto);
                }
            }

        }

        return dtos;
    }

    private List<CandidateSummaryReportDto> candidateSummaryReportByJobOffer(CandidateSummaryDto candidateSummary) {
        List<CandidateSummaryReportDto> dtos = new ArrayList<>();

        List<Candidate> candidates = reportService.findCandidatesByJobOffer(candidateSummary.getJobOfferId());
        
        for (Candidate candidate : candidates) {
            List<Interview> candidateInterviews = reportService.findInterviewsByCandidate(candidate);

            // the candidate did not reach the interview stage
            if (candidateInterviews.size() <= 0) {
                CandidateSummaryReportDto dto = new CandidateSummaryReportDto();
                dto.setName(candidate.getName());
                dto.setApplyDate(DateFormatUtil.formatDateToStringExcelFormat(candidate.getCreatedDate()));
                dto.setCvStatus(candidate.getCurriculumVitae().getStatus().name());
                dto.setInterviewStage("-");
                dto.setInterviewDate("-");
                dto.setInterviewStatus(candidate.getFinalResult().name());
                dto.setDob(DateFormatUtil.formatDateToStringExcelFormat(candidate.getDob()));
                dto.setSex(candidate.getSex());
                dto.setPhoneNumber(candidate.getPhone_number());
                dto.setEmail(candidate.getEmail());
                dto.setEducation(candidate.getEducation());
                dto.setTechnicalSkills(candidate.getTechnical_skills());
                dto.setLanguageSkills(candidate.getLanguage_skills());
                dto.setAppliedPosition(candidate.getAppliedPosition());
                dto.setLevel(candidate.getLevel());
                dto.setMainTechnology(candidate.getMain_skill());
                dto.setRelatedExperienceTime(candidate.getExperience());
                dto.setExpectedSalary(candidate.getExpected_salary().toString());

                dtos.add(dto);
            } else {
                // Add candidate CV stage
                CandidateSummaryReportDto dto = new CandidateSummaryReportDto();
                dto.setName(candidate.getName());
                dto.setApplyDate(DateFormatUtil.formatDateToStringExcelFormat(candidate.getCreatedDate()));
                dto.setCvStatus(candidate.getCurriculumVitae().getStatus().name());
                dto.setInterviewStage("-");
                dto.setInterviewDate("-");
                dto.setInterviewStatus(candidate.getFinalResult().name());
                dto.setDob(DateFormatUtil.formatDateToStringExcelFormat(candidate.getDob()));
                dto.setSex(candidate.getSex());
                dto.setPhoneNumber(candidate.getPhone_number());
                dto.setEmail(candidate.getEmail());
                dto.setEducation(candidate.getEducation());
                dto.setTechnicalSkills(candidate.getTechnical_skills());
                dto.setLanguageSkills(candidate.getLanguage_skills());
                dto.setAppliedPosition(candidate.getAppliedPosition());
                dto.setLevel(candidate.getLevel());
                dto.setMainTechnology(candidate.getMain_skill());
                dto.setRelatedExperienceTime(candidate.getExperience());
                dto.setExpectedSalary(candidate.getExpected_salary().toString());

                dtos.add(dto);

                for (Interview interview : candidateInterviews) {
                    dto = new CandidateSummaryReportDto();
                    dto.setName(candidate.getName());
                    dto.setApplyDate(DateFormatUtil.formatDateToStringExcelFormat(candidate.getCreatedDate()));
                    dto.setCvStatus("-");
                    dto.setInterviewStage(interview.getInterviewStage().getName());
                    dto.setInterviewDate(DateFormatUtil
                            .formatDateToStringExcelFormat(interview.getInterviewSchedule().getInterviewDate()));
                    dto.setInterviewStatus(interview.getResult().name());
                    dto.setDob(DateFormatUtil.formatDateToStringExcelFormat(candidate.getDob()));
                    dto.setSex(candidate.getSex());
                    dto.setPhoneNumber(candidate.getPhone_number());
                    dto.setEmail(candidate.getEmail());
                    dto.setEducation(candidate.getEducation());
                    dto.setTechnicalSkills(candidate.getTechnical_skills());
                    dto.setLanguageSkills(candidate.getLanguage_skills());
                    dto.setAppliedPosition(candidate.getAppliedPosition());
                    dto.setLevel(candidate.getLevel());
                    dto.setMainTechnology(candidate.getMain_skill());
                    dto.setRelatedExperienceTime(candidate.getExperience());
                    dto.setExpectedSalary(candidate.getExpected_salary().toString());

                    dtos.add(dto);
                }
            }

        }

        return dtos;
    }

    // Helper methods for interivew process summary report

    private List<InterviewProcessSummaryReportByJobPositionDto> interviewProcessSummaryByJobPosition(Date fromDate,
            Date toDate) {
        // Get job level and titles
        List<JobTitle> jobTitles = jobTitleService.getAllJobTitles();
        List<JobLevel> jobLevels = jobLevelService.getAllJobLevels();

        // Null value of job level is possible
        jobLevels.add(null);

        List<InterviewProcessSummaryReportByJobPositionDto> dtos = new ArrayList<>();

        for (JobTitle jobTitle : jobTitles) {
            for (JobLevel jobLevel : jobLevels) {
                // If there is no job offer with given jobtitle and joblevel, it shouldn't be
                // reported
                if (!jobDetailRepository.existsByJobTitleAndJobLevel(jobTitle, jobLevel)) {
                    continue;
                }

                InterviewProcessSummaryReportByJobPositionDto dto = new InterviewProcessSummaryReportByJobPositionDto();

                List<Candidate> candidates = candidateRepository
                        .findByJobDetailJobTitleAndJobDetailJobLevelAndCreatedDateBetween(jobTitle,
                                jobLevel, fromDate, toDate);

                String vacantPosition = "";
                if (jobLevel != null) {
                    vacantPosition += jobLevel.getName() + " ";
                }
                vacantPosition += jobTitle.getName();
                dto.setVacantPosition(vacantPosition);

                int totalReceivedCandidates = candidates.size();
                int interviewCandidates = 0;
                int passedCandidates = 0;
                int pendingCandidates = 0;
                int cancelCandidates = 0;
                int notInterviewCandidates = 0;
                int offerMailLetter = 0;
                int acceptedCandidates = 0;

                for (Candidate candidate : candidates) {
                    Interview_Result finalResult = candidate.getFinalResult();
                    if (finalResult.equals(Interview_Result.NOT_REACHED)) {
                        notInterviewCandidates++;
                    } else if (finalResult.equals(Interview_Result.PASSED)) {
                        passedCandidates++;
                    } else if (finalResult.equals(Interview_Result.PENDING)) {
                        pendingCandidates++;
                    } else if (finalResult.equals(Interview_Result.CANCEL)) {
                        cancelCandidates++;
                    }

                    if (candidate.getJobOfferMailSent()) {
                        offerMailLetter++;
                    }

                    if(candidate.isJobAccepted()) {
                        acceptedCandidates++;
                    }
                }
                interviewCandidates = totalReceivedCandidates - notInterviewCandidates;

                dto.setFromDate(DateFormatUtil.formatDateToString(fromDate, "MM/dd/yyyy"));
                dto.setToDate(DateFormatUtil.formatDateToString(toDate, "MM/dd/yyyy"));

                dto.setTotalReceivedCandidates(totalReceivedCandidates);
                dto.setInterviewCandidates(interviewCandidates);
                dto.setNotInterviewCandidates(notInterviewCandidates);
                dto.setPassedCandidates(passedCandidates);
                dto.setPendingCandidates(pendingCandidates);
                dto.setCancelCandidates(cancelCandidates);
                dto.setOfferMailLetter(offerMailLetter);
                dto.setAcceptedCandidates(acceptedCandidates);

                dtos.add(dto);

            }
        }

        return dtos;
    }

    // By Job Offer
    private List<InterviewProcessSummaryReportByJobOfferDto> interviewProcessSummaryByJobOffer(Date fromDate,
            Date toDate) {
        List<InterviewProcessSummaryReportByJobOfferDto> dtos = new ArrayList<>();

        List<JobDetail> jobDetails = jobDetailRepository.findByCreatedDateBetween(fromDate, toDate);

        for (JobDetail jobDetail : jobDetails) {
            List<Candidate> candidates = candidateRepository.findByJobDetail(jobDetail);

            InterviewProcessSummaryReportByJobOfferDto dto = new InterviewProcessSummaryReportByJobOfferDto();

            String jobOffer = "";
            if (jobDetail.getJobLevel() != null) {
                jobOffer += jobDetail.getJobLevel().getName() + " ";
            }
            jobOffer += jobDetail.getJobTitle().getName();
            dto.setJobOfferId(jobDetail.getId());
            dto.setJobOffer(jobOffer);
            dto.setOpenDate(DateFormatUtil.formatDateToString(jobDetail.getOpenDate(), "MM/dd/yyyy"));
            dto.setExpireDate(DateFormatUtil.formatDateToString(jobDetail.getExpireDate(), "MM/dd/yyyy"));

            dto.setTeamId(jobDetail.getTeam().getId());
            dto.setDepartmentId(jobDetail.getTeam().getDepartment().getId());

            int totalReceivedCandidates = candidates.size();
            int interviewCandidates = 0;
            int passedCandidates = 0;
            int pendingCandidates = 0;
            int cancelCandidates = 0;
            int notInterviewCandidates = 0;
            int offerMailLetter = 0;
            int acceptedCandidates = 0;

            for (Candidate candidate : candidates) {
                Interview_Result finalResult = candidate.getFinalResult();
                if (finalResult.equals(Interview_Result.NOT_REACHED)) {
                    notInterviewCandidates++;
                } else if (finalResult.equals(Interview_Result.PASSED)) {
                    passedCandidates++;
                } else if (finalResult.equals(Interview_Result.PENDING)) {
                    pendingCandidates++;
                } else if (finalResult.equals(Interview_Result.CANCEL)) {
                    cancelCandidates++;
                }

                if (candidate.getJobOfferMailSent()) {
                    offerMailLetter++;
                }

                if(candidate.isJobAccepted()) {
                    acceptedCandidates++;
                }
            }
            interviewCandidates = totalReceivedCandidates - notInterviewCandidates;

            dto.setFromDate(DateFormatUtil.formatDateToString(fromDate, "MM/dd/yyyy"));
            dto.setToDate(DateFormatUtil.formatDateToString(toDate, "MM/dd/yyyy"));

            dto.setTotalReceivedCandidates(totalReceivedCandidates);
            dto.setInterviewCandidates(interviewCandidates);
            dto.setNotInterviewCandidates(notInterviewCandidates);
            dto.setPassedCandidates(passedCandidates);
            dto.setPendingCandidates(pendingCandidates);
            dto.setCancelCandidates(cancelCandidates);
            dto.setOfferMailLetter(offerMailLetter);
            dto.setAcceptedCandidates(acceptedCandidates);

            dtos.add(dto);
        }

        return dtos;
    }

    // By Department
    private List<InterviewProcessSummaryReportByDepartmentDto> interviewProcessSummaryByDepartment(Date fromDate,
            Date toDate) {
        List<InterviewProcessSummaryReportByDepartmentDto> dtos = new ArrayList<>();
        List<Department> departments = departmentService.getAllDepartments();

        for (Department dept : departments) {
            List<Candidate> candidates = reportService.findCandidatesByDepartmentAndDateBetween(dept, fromDate, toDate);
            InterviewProcessSummaryReportByDepartmentDto dto = new InterviewProcessSummaryReportByDepartmentDto();

            dto.setDepartmentId(dept.getId());
            dto.setDepartmentName(dept.getName());

            int totalReceivedCandidates = candidates.size();
            int interviewCandidates = 0;
            int passedCandidates = 0;
            int pendingCandidates = 0;
            int cancelCandidates = 0;
            int notInterviewCandidates = 0;
            int offerMailLetter = 0;
            int acceptedCandidates = 0;

            for (Candidate candidate : candidates) {
                Interview_Result finalResult = candidate.getFinalResult();
                if (finalResult.equals(Interview_Result.NOT_REACHED)) {
                    notInterviewCandidates++;
                } else if (finalResult.equals(Interview_Result.PASSED)) {
                    passedCandidates++;
                } else if (finalResult.equals(Interview_Result.PENDING)) {
                    pendingCandidates++;
                } else if (finalResult.equals(Interview_Result.CANCEL)) {
                    cancelCandidates++;
                }

                if (candidate.getJobOfferMailSent()) {
                    offerMailLetter++;
                }

                if(candidate.isJobAccepted()) {
                    acceptedCandidates++;
                }
            }
            interviewCandidates = totalReceivedCandidates - notInterviewCandidates;

            dto.setFromDate(DateFormatUtil.formatDateToString(fromDate, "MM/dd/yyyy"));
            dto.setToDate(DateFormatUtil.formatDateToString(toDate, "MM/dd/yyyy"));

            dto.setTotalReceivedCandidates(totalReceivedCandidates);
            dto.setInterviewCandidates(interviewCandidates);
            dto.setNotInterviewCandidates(notInterviewCandidates);
            dto.setPassedCandidates(passedCandidates);
            dto.setPendingCandidates(pendingCandidates);
            dto.setCancelCandidates(cancelCandidates);
            dto.setOfferMailLetter(offerMailLetter);
            dto.setAcceptedCandidates(acceptedCandidates);

            dtos.add(dto);
        }

        return dtos;
    }

    // By Team
    private List<InterviewProcessSummaryReportByTeamDto> interviewProcessSummaryByTeam(Date fromDate,
            Date toDate) {
        List<InterviewProcessSummaryReportByTeamDto> dtos = new ArrayList<>();
        List<Team> teams = teamService.getAllTeams();

        for (Team team : teams) {
            List<Candidate> candidates = reportService.findCandidatesByTeamAndDateBetween(team, fromDate, toDate);
            InterviewProcessSummaryReportByTeamDto dto = new InterviewProcessSummaryReportByTeamDto();

            dto.setTeamId(team.getId());
            dto.setTeamName(team.getName());

            int totalReceivedCandidates = candidates.size();
            int interviewCandidates = 0;
            int passedCandidates = 0;
            int pendingCandidates = 0;
            int cancelCandidates = 0;
            int notInterviewCandidates = 0;
            int offerMailLetter = 0;
            int acceptedCandidates = 0;

            for (Candidate candidate : candidates) {
                Interview_Result finalResult = candidate.getFinalResult();
                if (finalResult.equals(Interview_Result.NOT_REACHED)) {
                    notInterviewCandidates++;
                } else if (finalResult.equals(Interview_Result.PASSED)) {
                    passedCandidates++;
                } else if (finalResult.equals(Interview_Result.PENDING)) {
                    pendingCandidates++;
                } else if (finalResult.equals(Interview_Result.CANCEL)) {
                    cancelCandidates++;
                }

                if (candidate.getJobOfferMailSent()) {
                    offerMailLetter++;
                }

                if(candidate.isJobAccepted()) {
                    acceptedCandidates++;
                }
            }
            interviewCandidates = totalReceivedCandidates - notInterviewCandidates;

            dto.setFromDate(DateFormatUtil.formatDateToString(fromDate, "MM/dd/yyyy"));
            dto.setToDate(DateFormatUtil.formatDateToString(toDate, "MM/dd/yyyy"));

            dto.setTotalReceivedCandidates(totalReceivedCandidates);
            dto.setInterviewCandidates(interviewCandidates);
            dto.setNotInterviewCandidates(notInterviewCandidates);
            dto.setPassedCandidates(passedCandidates);
            dto.setPendingCandidates(pendingCandidates);
            dto.setCancelCandidates(cancelCandidates);
            dto.setOfferMailLetter(offerMailLetter);
            dto.setAcceptedCandidates(acceptedCandidates);

            dtos.add(dto);
        }

        return dtos;
    }

}
