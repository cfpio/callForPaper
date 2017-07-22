package io.cfp.model.queries;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RoleQuery {

    private int userId;
    private String eventId;
}
