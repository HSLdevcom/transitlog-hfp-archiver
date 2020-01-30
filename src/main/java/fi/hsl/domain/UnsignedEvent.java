package fi.hsl.domain;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class UnsignedEvent extends Event {

    public UnsignedEvent() {
    }
}
