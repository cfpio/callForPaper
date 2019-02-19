package io.cfp.model.queries;

import io.cfp.model.Proposal;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data @Accessors(chain = true)
public class UserQuery {

    private String email;
    private List<Proposal.State> states = new ArrayList<>();
    private String eventId;
    private String sort;
    private String order;

    public UserQuery addStates(Proposal.State... states) {
        this.states.addAll(Arrays.asList(states));
        return this;
    }

}
