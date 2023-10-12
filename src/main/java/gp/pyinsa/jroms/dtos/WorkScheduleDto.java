package gp.pyinsa.jroms.dtos;

import javax.validation.constraints.NotBlank;

import gp.pyinsa.jroms.constants.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WorkScheduleDto {
	private Short id;
	@NotBlank(message = "Time cannot be empty")
	private String Time1;
	@NotBlank(message = "Time cannot be empty")
	private String Time2;
	@NotBlank(message = "Day cannot be empty")
	private String startDay;
	@NotBlank(message = "Day cannot be empty")
	private String endDay;
    
	
	
}
