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
    private List<String> sort = new ArrayList<>();
    private String order;

    public UserQuery addState(Proposal.State... states) {
        this.states.addAll(Arrays.asList(states));
        return this;
    }

    public UserQuery addSort(String... sort) {
        this.sort.addAll(Arrays.asList(sort));
        return this;
    }

}
