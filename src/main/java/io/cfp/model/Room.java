package io.cfp.model;

import io.cfp.entity.Event;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

public class Room {

    private int id;
    private String name;
    private Event event;


    public int getId() {
        return id;
    }

    @ManyToOne
    public Event getEvent() {
        return event;
    }

    @NotNull(message = "Room name is required")
    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEvent(io.cfp.entity.Event event) {
        this.event = event;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Room withName(String name) {
        this.name = name;
        return this;
    }

    public Room withEvent(Event event) {
        this.event = event;
        return this;
    }
}
