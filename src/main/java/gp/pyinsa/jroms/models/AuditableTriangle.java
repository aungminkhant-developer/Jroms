package gp.pyinsa.jroms.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditableTriangle{
	@CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
	
	 @LastModifiedDate
	 @Temporal(TemporalType.TIMESTAMP)
	 @Column(name = "last_updated")
	 private Date lastUpdated;
	    
	 @LastModifiedBy
	 @ManyToOne
	 @JoinColumn(name = "last_updated_by")
	 private User lastUpdatedBy;
}
