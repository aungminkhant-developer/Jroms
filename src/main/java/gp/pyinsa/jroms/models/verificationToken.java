package gp.pyinsa.jroms.models;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "verification_token")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    @Column(name = "expiration_time")
    private Date expirationTime;

    @OneToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    public VerificationToken(String token, Candidate candidate) {
        super();
        this.token = token;
        this.candidate=candidate;
        this.expirationTime =this.getTokenExpirationTime();
    }

    private Date getTokenExpirationTime() {
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, 1440);
        return new Date(calendar.getTime().getTime());
    }

    

}
