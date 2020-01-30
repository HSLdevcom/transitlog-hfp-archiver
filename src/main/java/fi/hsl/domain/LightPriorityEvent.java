package fi.hsl.domain;


import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class LightPriorityEvent extends Event {
    private Integer tlp_requestid;
    private String tlp_requesttype;
    private String tlp_prioritylevel;
    private String tlp_reason;
    private Integer tlp_att_seq;
    private String tlp_decision;
    private Integer sid;
    private Integer signal_groupid;
    private Integer tlp_signalgroupnbr;
    private Integer tlp_line_configid;
    private Integer tlp_point_configid;
    private Integer tlp_frequency;
    private String tlp_protocol;

    public LightPriorityEvent() {
    }

}
