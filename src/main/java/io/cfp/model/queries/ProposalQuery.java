package io.cfp.model.queries;

import io.cfp.domain.exception.BadRequestException;
import io.cfp.model.Proposal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProposalQuery {

    private String eventId;
    private Integer userId;
    private Proposal.State state;
    private String track;
    private String room;
    private String format;

    public ProposalQuery setState(String state) {
        switch (state.toUpperCase()) {
            case "ACCEPTED":
                this.state = Proposal.State.ACCEPTED;break;
            default:
                throw new BadRequestException("Unsupported state filter :"+state);
        }

        return this;
    }
}
