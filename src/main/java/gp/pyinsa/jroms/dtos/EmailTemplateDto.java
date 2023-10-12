package gp.pyinsa.jroms.dtos;

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
public class EmailTemplateDto {

    private short id;
    private String subject;
    private String bodyText;
    private String emailTypeName;
    
}
