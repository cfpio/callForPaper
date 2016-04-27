package fr.sii.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Entity
@Table(name = "events")
public class Event {

    public static String current() {
        return "default"; // TODO
    }

    @Id
    private String id;

}
