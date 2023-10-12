package gp.pyinsa.jroms.dtos;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotificationDto implements Serializable {
    private Long id;
    private String message;
    private Long jobDetailId;
    private String jobPosition;
    private String createdDate;
    private List<String> usernames;
    private Long candidateId;

    public NotificationDto(Long id, String message, Long jobDetailId, String jobPosition, String createdDate,
            List<String> usernames) {
        this.id = id;
        this.message = message;
        this.jobDetailId = jobDetailId;
        this.jobPosition = jobPosition;
        this.createdDate = createdDate;
        this.usernames = usernames;
    }

}
