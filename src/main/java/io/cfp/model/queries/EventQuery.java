package io.cfp.model.queries;

import lombok.Data;
import lombok.experimental.Accessors;

@Data @Accessors(chain = true)
public class EventQuery {

    private int user;
    private boolean open;
    private boolean passed;
    private String name;
}
