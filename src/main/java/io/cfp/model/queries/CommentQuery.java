package io.cfp.model.queries;

import lombok.Data;
import lombok.experimental.Accessors;

@Data @Accessors(chain = true)
public class CommentQuery {

    private String eventId;
    private int proposalId;
    private boolean internal = false;
}
