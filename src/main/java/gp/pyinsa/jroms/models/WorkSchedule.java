package gp.pyinsa.jroms.models;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.dtos.WorkScheduleDto;
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
@Entity
@Table(name = "work_schedules")
public class WorkSchedule extends Auditable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;
	
	@Column(name = "work_hours")
    private String hour;
	@Column(name = "work_days")
    private String day;

    private Status status = Status.ACTIVE;

    public String workingTime() {
        return hour + " [" + day + "]";
    }

    
    
}
