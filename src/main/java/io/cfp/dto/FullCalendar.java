package io.cfp.dto;

import io.cfp.entity.Room;
import io.cfp.entity.Talk;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Data @NoArgsConstructor
public class FullCalendar {

    private List<Resource> resources;

    private List<Event> events;


    public FullCalendar(List<Talk> talks, List<Room> rooms) {

        resources = rooms.stream()
            .map(Resource::new)
            .collect(Collectors.toList());

        events = talks.stream()
            .filter(t -> t.getDate() != null && t.getRoom() != null)
            .map(Event::new)
            .collect(Collectors.toList());
    }

    @Data @NoArgsConstructor
    public static class Resource {
        private String id;
        private String title;

        public Resource(Room r) {
            this.id = String.valueOf(r.getId());
            this.title = r.getName();
        }
    }

    @Data @NoArgsConstructor
    public static class Event {
        private String id;
        private String resourceId;
        private String start;
        private String end;
        private String title;

        public Event(Talk talk) {
            this.id = String.valueOf(talk.getId());
            this.title = talk.getName();
            this.resourceId = String.valueOf(talk.getRoom().getId());
            this.start = DateTimeFormatter.ISO_INSTANT.format(talk.getDate().toInstant());
            this.end = DateTimeFormatter.ISO_INSTANT.format(talk.getDate().toInstant().plus(talk.getDuree(), ChronoUnit.MINUTES));
        }
    }

}
