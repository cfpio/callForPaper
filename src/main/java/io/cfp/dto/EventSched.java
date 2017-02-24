package io.cfp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO used to export proposal into Sched format
 */
@Data @Builder(toBuilder = true) @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventSched {

    private String id;
    /** Title of the proposal */
    private String name;

    /** Complete summary */
    private String description;

    /** Speakers and co-speakers delimiter , */
    private String speakers;

    private String language;

    @JsonProperty("event_start")
    private LocalDateTime eventStart;

    @JsonProperty("event_end")
    private LocalDateTime eventEnd;

    /** Track name */
    @JsonProperty("event_type")
    private String eventType;

    /** Type (conference, lab...) */
    private String format;

    /** Room name */
    private String venue;
    /** Room id */
    private Integer venueId;

    /** Not used */
    private String active = "Y";
    @JsonProperty("event_key")
    private String eventKey = "tobedefined";
    private String goers = "tobedefined";
    private String seats = "tobedefined";
    @JsonProperty("invite_only")
    private String inviteOnly = "N";




}
