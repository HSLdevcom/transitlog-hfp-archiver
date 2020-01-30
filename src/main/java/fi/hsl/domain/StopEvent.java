package fi.hsl.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@Entity
public class StopEvent extends Event {
    public StopEvent() {
    }
}
