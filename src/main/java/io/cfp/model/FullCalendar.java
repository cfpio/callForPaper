package io.cfp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Data @NoArgsConstructor
public class FullCalendar {

    private List<Resource> resources;

    private List<Event> events;


    public FullCalendar(List<Proposal> talks, List<Room> rooms, List<Format> formats, List<Theme> themes) {

        resources = rooms.stream()
            .map(Resource::new)
            .collect(Collectors.toList());

        events = talks.stream()
            .filter(t -> t.getSchedule() != null && t.getRoomId() != null)
            .map(p -> {

                Format format = formats.stream()
                    .filter(f -> f.getId() == p.getFormat())
                    .findFirst()
                    .orElse(new Format());

                Theme theme = themes.stream()
                    .filter(th -> th.getId() == p.getTrackId())
                    .findFirst()
                    .orElse(new Theme());

                Event event = new Event(p, format, theme);
                return event;
            })
            .collect(Collectors.toList());
    }

    @Data @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Resource {
        private String id;
        private String title;

        public Resource(Room r) {
            this.id = String.valueOf(r.getId());
            this.title = r.getName();
        }
    }

    @Data @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Event {
        private String id;
        private String resourceId;
        private String start;
        private int duration;
        private String end;
        private String color;
        private String format;
        private String icon;
        private String title;
        private String slides;
        private String videos;

        public Event(Proposal proposal, Format format, Theme theme) {
            this.id = String.valueOf(proposal.getId());
            this.title = proposal.getName();
            this.resourceId = String.valueOf(proposal.getRoomId());
            if (proposal.getSchedule() != null) {
                this.start = DateTimeFormatter.ISO_INSTANT.format(proposal.getSchedule().toInstant());
                this.end = DateTimeFormatter.ISO_INSTANT.format(proposal.getSchedule().toInstant().plus(format.getDuration(), ChronoUnit.MINUTES));
            }
            this.color = theme.getColor();
            this.format = format.getName();
            this.icon = format.getIcon();
            this.duration = format.getDuration();
            this.slides = proposal.getSlides();
            this.videos = proposal.getVideo();
        }
    }

}
