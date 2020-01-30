package fi.hsl.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@EqualsAndHashCode
@JsonSubTypes({
        @JsonSubTypes.Type(value = VehiclePosition.class, name = "vehicleposition"),
        @JsonSubTypes.Type(value = LightPriorityEvent.class, name = "lightpriorityevent"),
        @JsonSubTypes.Type(value = OtherEvent.class, name = "otherevent"),
        @JsonSubTypes.Type(value = StopEvent.class, name = "stopevent"),
        @JsonSubTypes.Type(value = UnsignedEvent.class, name = "unsignedevent")
})
@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Event {
    private Timestamp tst;
    private String unique_vehicle_id;
    private String event_type;
    private String journey_type;
    @Id
    private UUID uuid;
    @Version
    private Long version;
    private Timestamp received_at;
    private String topic_prefix;
    private String topic_version;
    private Boolean is_ongoing;
    private String mode;
    private Integer owner_operator_id;
    private Integer vehicle_number;
    private String route_id;
    private Integer direction_id;
    private String headsign;
    private Time journey_start_time;
    private String next_stop_id;
    private Integer geohash_level;
    private Double topic_latitude;
    private Double topic_longitude;
    private Double lat;
    @Column(name = "long")
    private Double longitude;
    private String desi;
    private Integer dir;
    private Integer oper;
    private Integer veh;
    private BigInteger tsi;
    private Double spd;
    private Integer hdg;
    private Double acc;
    private Integer dl;
    private Double odo;
    private Boolean drst;
    private Date oday;
    private Integer jrn;
    private Integer line;
    private Time start;
    @Column(name = "loc")
    private String location_quality_method;
    private Integer stop;
    private String route;
    private Integer occu;
    private Integer seq;
    private Integer dr_type;

    Event() {
    }
}
