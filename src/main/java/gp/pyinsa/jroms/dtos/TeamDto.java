package gp.pyinsa.jroms.dtos;

import java.util.List;

import gp.pyinsa.jroms.models.JobDetail;
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
public class TeamDto {
    
    private String id;
    private String name;
    private List<JobDetail> jobDetails;
}
