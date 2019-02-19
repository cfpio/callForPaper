package io.cfp.model.queries;

import io.cfp.model.Proposal;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Accessors(chain = true)
public class ProposalQuery {

    private String eventId;
    private Integer userId;
    private List<Proposal.State> states = new ArrayList<>();
    private String track;
    private String room;
    private String format;
    private List<String> sort = new ArrayList<>();
    private String order;

    public ProposalQuery addStates(Proposal.State... states) {
        this.states.addAll(Arrays.asList(states));
        return this;
    }

    public ProposalQuery addSort(String... sort) {
        this.sort.addAll(Arrays.asList(sort));
        return this;
    }
}
