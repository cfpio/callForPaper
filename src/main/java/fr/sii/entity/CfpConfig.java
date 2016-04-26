package fr.sii.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by lhuet on 21/11/15.
 */
@Entity
@Table(name = "cfpconfig", uniqueConstraints = @UniqueConstraint(columnNames = {"key"}) )
public class CfpConfig {
    private int id;
    private String key;
    private String value;
    private Event event;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    @ManyToOne
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
