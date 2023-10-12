package gp.pyinsa.jroms.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;

import gp.pyinsa.jroms.constants.InterviewProcessSummaryGroup;
import gp.pyinsa.jroms.dtos.reports.CandidateSummaryReportDto;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

public class ReportUtils {

    // .jrxml files
    private static final String INTERVIEW_PROCESS_SUMMARY_BY_JOB_POSITION = "Interview_Process_Summary_Report_By_Job_Position.jrxml";
    private static final String INTERVIEW_PROCESS_SUMMARY_BY_JOB_OFFER = "Interview_Process_Summary_Report_By_Job_Offer.jrxml";
    private static final String INTERVIEW_PROCESS_SUMMARY_BY_DEPARTMENT = "Interview_Process_Summary_Report_By_Department.jrxml";
    private static final String INTERVIEW_PROCESS_SUMMARY_BY_TEAM = "Interview_Process_Summary_Report_By_Team.jrxml";
    private static final String CADNDIATE_SUMMARY = "Candidate_Summary_Report.jrxml";

    /**
     * This method will export report file of chosen type of the list of interview
     * summary data in a particular
     * group. The file will be downloaded on the client side.
     * 
     * @param response   HttpServletResponse to send file back to the client
     * @param parameters Parameters Map for the jasper report .jrxml
     * @param dtos       Collection with the fields for the jasper report
     */
    public static void reportInterviewProcessSummary(HttpServletResponse response, Map<String, Object> parameters,
            Collection<?> dtos, String type, InterviewProcessSummaryGroup groupBy) {
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dtos);

        String fileName = INTERVIEW_PROCESS_SUMMARY_BY_JOB_POSITION;
        if (groupBy.equals(InterviewProcessSummaryGroup.JOB_OFFER)) {
            fileName = INTERVIEW_PROCESS_SUMMARY_BY_JOB_OFFER;
        } else if (groupBy.equals(InterviewProcessSummaryGroup.DEPARTMENT)) {
            fileName = INTERVIEW_PROCESS_SUMMARY_BY_DEPARTMENT;
        } else if (groupBy.equals(InterviewProcessSummaryGroup.TEAM)) {
            fileName = INTERVIEW_PROCESS_SUMMARY_BY_TEAM;
        }

        try {
            JasperReport report = JasperCompileManager
                    .compileReport(ResourceUtils.getFile("classpath:" + fileName).getAbsolutePath());

            if (type.equals("pdf")) {
                JasperPrint print = JasperFillManager.fillReport(report, parameters, dataSource);
                response.setContentType(MediaType.APPLICATION_PDF_VALUE);
                response.setHeader("Content-Disposition", "attachment; filename=interview_process_summary.pdf");

                JRPdfExporter exporter = new JRPdfExporter();
                exporter.setExporterInput(new SimpleExporterInput(print));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
                exporter.exportReport();
            } else {
                parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);
                JasperPrint print = JasperFillManager.fillReport(report, parameters, dataSource);

                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                response.setHeader("Content-Disposition", "attachment; filename=interview_process_summary.xlsx");

                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(print));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
                SimpleXlsxReportConfiguration xlsxConfig = new SimpleXlsxReportConfiguration();
                xlsxConfig.setWhitePageBackground(false);
                xlsxConfig.setDetectCellType(true);
                xlsxConfig.setAutoFitRow(true);
                xlsxConfig.setRemoveEmptySpaceBetweenRows(true);
                exporter.setConfiguration(xlsxConfig);
                exporter.exportReport();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JRException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void reportCandidateHistory(HttpServletResponse response, Map<String, Object> parameters,
            List<CandidateSummaryReportDto> dtos, String type) {
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dtos);

        try {
            JasperReport report = JasperCompileManager
                    .compileReport(ResourceUtils.getFile("classpath:" + CADNDIATE_SUMMARY).getAbsolutePath());

            if (type.equals("pdf")) {
                JasperPrint print = JasperFillManager.fillReport(report, parameters, dataSource);
                response.setContentType(MediaType.APPLICATION_PDF_VALUE);
                response.setHeader("Content-Disposition", "attachment; filename=candidates_process_history.pdf");

                JRPdfExporter exporter = new JRPdfExporter();
                exporter.setExporterInput(new SimpleExporterInput(print));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
                exporter.exportReport();
            } else {
                parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);
                JasperPrint print = JasperFillManager.fillReport(report, parameters, dataSource);

                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                response.setHeader("Content-Disposition", "attachment; filename=candidate_summary.xlsx");

                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(print));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
                SimpleXlsxReportConfiguration xlsxConfig = new SimpleXlsxReportConfiguration();
                xlsxConfig.setWhitePageBackground(false);
                xlsxConfig.setDetectCellType(true);
                xlsxConfig.setAutoFitRow(true);
                xlsxConfig.setRemoveEmptySpaceBetweenRows(true);
                exporter.setConfiguration(xlsxConfig);
                exporter.exportReport();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JRException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
