package gp.pyinsa.jroms.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

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
@Entity
@Table(name = "locations")
public class Location extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Length(max = 45, message = "{location.valid.building.length}")
    @NotBlank(message = "{location.valid.building.notblank}")
    private String building;

    @Length(max = 45, message = "{location.valid.township.length}")
    @NotBlank(message = "{location.valid.township.notblank}")
    private String township;

    @Length(max = 45, message = "{location.valid.division.length}")
    @NotBlank(message = "{location.valid.division.notblank}")
    private String division;

    @NotNull(message = "{location.valid.status.notnull}")
    private Status status = Status.ACTIVE;

    public String locationStr() {
        return building + ", " + township + ", " + division;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((building == null) ? 0 : building.hashCode());
        result = prime * result + ((township == null) ? 0 : township.hashCode());
        result = prime * result + ((division == null) ? 0 : division.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Location other = (Location) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (building == null) {
            if (other.building != null)
                return false;
        } else if (!building.equals(other.building))
            return false;
        if (township == null) {
            if (other.township != null)
                return false;
        } else if (!township.equals(other.township))
            return false;
        if (division == null) {
            if (other.division != null)
                return false;
        } else if (!division.equals(other.division))
            return false;
        return true;
    }

}
