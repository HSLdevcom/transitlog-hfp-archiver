package fi.hsl.domain;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class VehiclePosition extends Event {
    public VehiclePosition() {
    }
}
