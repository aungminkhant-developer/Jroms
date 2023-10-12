package gp.pyinsa.jroms.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gp.pyinsa.jroms.dtos.WorkScheduleDto;
import gp.pyinsa.jroms.models.InterviewSchedule;
import gp.pyinsa.jroms.models.WorkSchedule;
import gp.pyinsa.jroms.repositories.WorkScheduleRepository;
import gp.pyinsa.jroms.services.WorkScheduleService;

@Controller
@RequestMapping("/mng")
@PreAuthorize("hasRole('ADMIN') or hasRole('HR_SENIOR') or hasRole('DEPARTMENT_HEAD') or hasRole('TEAM_LEADER')")
public class WorkScheduleController {
	private final WorkScheduleService workScheduleService;
	private WorkScheduleRepository workScheduleRepository;
	List<String> daysOfWeek = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
			"Sunday");

	@Autowired
	public WorkScheduleController(WorkScheduleService workScheduleService) {
		this.workScheduleService = workScheduleService;
	}

	@GetMapping("/work-schedules")
	public String showWorkScheduleForm(Model model) {
		model.addAttribute("workScheduleDto", new WorkScheduleDto());
		List<WorkSchedule> workSchedules = workScheduleService.findAllSchedules();
		model.addAttribute("daysOfWeek", daysOfWeek);
		model.addAttribute("workSchedules", workSchedules);
		return "workschedule";
	}

	@PostMapping("/work-schedules")
	public String createWorkSchedule(ModelMap model, @ModelAttribute("workScheduleDto") WorkScheduleDto workScheduleDto,
			RedirectAttributes redirectAttributes) {
		WorkSchedule workSchedule = new WorkSchedule();
		String startTime = workScheduleDto.getTime1();
		String[] timeParts = startTime.split(":");
		int startTimeHour = Integer.parseInt(timeParts[0]);
		int startTimeMin = Integer.parseInt(timeParts[1]);
		String endTime = workScheduleDto.getTime2();
		String[] timeParts2 = endTime.split(":");
		int endTimeHour = Integer.parseInt(timeParts2[0]);
		int endTimeMin = Integer.parseInt(timeParts2[1]);
		String formattedStartTimeMin = String.format("%02d", startTimeMin);
		String formattedEndTimeMin = String.format("%02d", endTimeMin);

		String hourPostFix = "";
		if (startTimeHour > 12) {
			startTimeHour = startTimeHour - 12;
			hourPostFix = "PM";

		} else if (startTimeHour == 12) {
			hourPostFix = "PM";
		} else {
			hourPostFix = "AM";
		}

		String endPostFix = "PM";
		if (endTimeHour > 12) {
			endTimeHour = endTimeHour - 12;

		} else if (endTimeHour == 12) {
		} else {
			endPostFix = "AM";
		}

		String formattedStartTimeHour = String.format("%02d", startTimeHour);
		String formattedEndTimeHour = String.format("%02d", endTimeHour);

		startTime = formattedStartTimeHour + ":" + formattedStartTimeMin + hourPostFix;
		endTime = formattedEndTimeHour + ":" + formattedEndTimeMin + endPostFix;

		workSchedule.setHour(startTime + " - " + endTime);
		workSchedule.setDay(workScheduleDto.getStartDay() + " - " + workScheduleDto.getEndDay());
		workScheduleService.createSchedule(workSchedule);
		redirectAttributes.addFlashAttribute("workScheduleCreateSuccess", "WorkSchedule is created successfully.");
		return "redirect:/mng/work-schedules";
	}

	@PostMapping("/delete-work-schedule")
	public String deleteWorkSchedule(@RequestParam short id, RedirectAttributes redirectAttributes) {
		workScheduleService.deleteWorkSchedule(id);
		redirectAttributes.addFlashAttribute("workScheduleDeleteSuccess", "WorkSchedule is deleted successfully.");
		return "redirect:/mng/work-schedules";
	}

	@PostMapping("/update")
	public String updateWrkSchedule(@ModelAttribute WorkScheduleDto workScheduleDto,
			RedirectAttributes redirectAttributes) {
		WorkSchedule workSchedule = new WorkSchedule();
		String startTime = workScheduleDto.getTime1();
		String[] timeParts = startTime.split(":");
		int startTimeHour = Integer.parseInt(timeParts[0]);
		int startTimeMin = Integer.parseInt(timeParts[1]);
		String endTime = workScheduleDto.getTime2();
		String[] timeParts2 = endTime.split(":");
		int endTimeHour = Integer.parseInt(timeParts2[0]);
		int endTimeMin = Integer.parseInt(timeParts2[1]);
		String formattedStartTimeMin = String.format("%02d", startTimeMin);
		String formattedEndTimeMin = String.format("%02d", endTimeMin);

		String hourPostFix = "";
		if (startTimeHour > 12) {
			startTimeHour = startTimeHour - 12;
			hourPostFix = "PM";

		} else if (startTimeHour == 12) {
			hourPostFix = "PM";
		} else {
			hourPostFix = "AM";
		}

		String endPostFix = "PM";
		if (endTimeHour > 12) {
			endTimeHour = endTimeHour - 12;

		} else if (endTimeHour == 12) {
		} else {
			endPostFix = "AM";
		}

		String formattedStartTimeHour = String.format("%02d", startTimeHour);
		String formattedEndTimeHour = String.format("%02d", endTimeHour);

		startTime = formattedStartTimeHour + ":" + formattedStartTimeMin + hourPostFix;
		endTime = formattedEndTimeHour + ":" + formattedEndTimeMin + endPostFix;

		workSchedule.setHour(startTime + " - " + endTime);
		workSchedule.setDay(workScheduleDto.getStartDay() + " - " + workScheduleDto.getEndDay());
		workSchedule.setId(workScheduleDto.getId());
		workScheduleService.updateWorkSchedule(workSchedule);
		redirectAttributes.addFlashAttribute("workScheduleUpdateSuccess", "WorkSchedule is updated successfully.");
		return "redirect:/mng/work-schedules";
	}

	@GetMapping("/update/{id}")
	@ResponseBody
	public ResponseEntity<WorkScheduleDto> getWorkSchedule(@PathVariable Short id) {

		// Work Schedule DTO: WorkScheduleDto(id=null, Time1=10:30, Time2=18:30,
		// startDay=Tuesday, endDay=Friday)

		WorkSchedule workSchedule = workScheduleService.getById(id);
		String[] dayArr = workSchedule.getDay().split("-");
		String startDay = dayArr[0].strip();
		String endDay = dayArr[1].strip();

		String[] hourArr = workSchedule.getHour().split("-");
		String startTime = hourArr[0].strip();
		startTime = startTime.substring(0, startTime.length() - 2);
		String endTime = hourArr[1].strip();
		endTime = endTime.substring(0, endTime.length() - 2);

		String[] timeParts = startTime.split(":");
		int startTimeHour = Integer.parseInt(timeParts[0]);
		String startTimeMin = timeParts[1];
		String[] timeParts2 = endTime.split(":");
		int endTimeHour = Integer.parseInt(timeParts2[0]);
		String endTimeMin = timeParts2[1];
		String Time1;
		String Time2;
		if (startTimeHour < 12) {
			startTimeHour = startTimeHour + 12;
			Time1 = startTimeHour + ":" + startTimeMin;
		}

		else {
			Time1 = startTimeHour + ":" + startTimeMin;
		}

		if (endTimeHour < 12) {
			endTimeHour = endTimeHour + 12;
			Time2 = endTimeHour + ":" + endTimeMin;
		} else {
			Time2 = endTimeHour + ":" + endTimeMin;
		}

		WorkScheduleDto dto = new WorkScheduleDto(id, Time1, Time2, startDay, endDay);

		return ResponseEntity.ok(dto);
	}

}
