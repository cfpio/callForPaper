package fr.sii.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Comment on a talk
 */
@Entity
@Table(name = "comments")
public class Comment {

    private int id;
    private String comment;
    private Date added;
    private boolean internal;

    private Talk talk;
    private User user;
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

    @NotNull
    @Type(type="text")
    public String getComment() {
        return comment;
    }

    @NotNull
    public Date getAdded() {
        return added;
    }

    public boolean isInternal() {
        return internal;
    }

    @ManyToOne
    @JoinColumn(name = "talk")
    public Talk getTalk() {
        return talk;
    }

    @ManyToOne
    @JoinColumn(name = "user")
    public User getUser() {
        return user;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setAdded(Date added) {
        this.added = added;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public void setTalk(Talk talk) {
        this.talk = talk;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
