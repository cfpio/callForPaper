package io.cfp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.cfp.entity.Event;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Room {

    private int id;
    private String name;
    private String event;
}
