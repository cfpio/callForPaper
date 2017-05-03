package io.cfp.mapper;

import io.cfp.model.Proposal;
import io.cfp.model.queries.ProposalQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@MybatisTest
public class ProposalMapperTest {

    private static final int USER_ID = 10;
    private static final int PROPOSAL_ID = 20;
    private static final int FORMAT_ID = 30;
    private static final int ROOM_ID = 50;
    private static final int TRACK_ID = 40;

    @Autowired
    private ProposalMapper proposalMapper;

    @Test
    public void should_find_all_proposals() {
        List<Proposal> allProposals = proposalMapper.findAll(new ProposalQuery());
        assertThat(allProposals).isNotEmpty();
    }

    @Test
    public void should_count_all_proposals() {
        Integer numberOfProposals = proposalMapper.count(new ProposalQuery());
        assertThat(numberOfProposals).isNotZero();
    }

    @Test
    public void should_count_all_proposals_by_eventId() {
        ProposalQuery proposalQuery = new ProposalQuery();
        proposalQuery.setEventId("EVENT_ID");
        Integer numberOfProposals = proposalMapper.count(proposalQuery);
        assertThat(numberOfProposals).isEqualTo(1);
    }

    @Test
    public void should_count_all_proposals_by_userId() {
        ProposalQuery proposalQuery = new ProposalQuery();
        proposalQuery.setUserId(USER_ID);
        Integer numberOfProposals = proposalMapper.count(proposalQuery);
        assertThat(numberOfProposals).isEqualTo(1);
    }

    @Test
    public void should_count_all_proposals_by_state() {
        ProposalQuery proposalQuery = new ProposalQuery();
        proposalQuery.setState(Proposal.State.ACCEPTED.name());
        Integer numberOfProposals = proposalMapper.count(proposalQuery);
        assertThat(numberOfProposals).isEqualTo(1);
    }

    @Test
    public void should_find_a_proposal_by_id() {
        Proposal foundProposal = proposalMapper.findById(PROPOSAL_ID);
        assertThat(foundProposal).isNotNull();
        assertThat(foundProposal.getId()).isEqualTo(20);
        assertThat(foundProposal.getAdded()).isEqualTo("2042-12-29");
        assertThat(foundProposal.getDate()).isEqualTo("2042-12-31");
        assertThat(foundProposal.getDescription()).isEqualTo("PROPOSAL_DESCRIPTION");
        assertThat(foundProposal.getEvent()).isNotNull();
        assertThat(foundProposal.getEvent().getId()).isEqualTo("EVENT_ID");
        assertThat(foundProposal.getEvent().isOpen()).isTrue();
        assertThat(foundProposal.getFormat()).isNotNull();
        assertThat(foundProposal.getFormat().getId()).isEqualTo(FORMAT_ID);
        assertThat(foundProposal.getFormat().getName()).isEqualTo("FORMAT_NAME");
        assertThat(foundProposal.getHeure()).isEqualTo("12H00");
        assertThat(foundProposal.getLanguage()).isEqualTo("PROPOSAL_LANGUAGE");
        assertThat(foundProposal.getDifficulty()).isEqualTo(1);
        assertThat(foundProposal.getComments()).isNull(); // TODO
        assertThat(foundProposal.getName()).isEqualTo("PROPOSAL_NAME");
        assertThat(foundProposal.getReferences()).isEqualTo("PROPOSAL_REFS");
        assertThat(foundProposal.getRoom()).isNotNull();
        assertThat(foundProposal.getRoom().getId()).isEqualTo(ROOM_ID);
        assertThat(foundProposal.getRoom().getName()).isEqualTo("ROOM_NAME");
        assertThat(foundProposal.getCospeakers()).isNull(); //TODO
        assertThat(foundProposal.getRates()).isNull(); //TODO
        assertThat(foundProposal.getSlides()).isEqualTo("PROPOSAL_SLIDES");
        assertThat(foundProposal.getState()).isEqualTo(Proposal.State.ACCEPTED);
        assertThat(foundProposal.getTrack()).isNotNull();
        assertThat(foundProposal.getTrack().getId()).isEqualTo(TRACK_ID);
        assertThat(foundProposal.getTrack().getLibelle()).isEqualTo("TRACK_LIBELLE");
        assertThat(foundProposal.getUser()).isNotNull();
        assertThat(foundProposal.getUser().getId()).isEqualTo(USER_ID);
        assertThat(foundProposal.getUser().getEmail()).isEqualTo("EMAIL");
        assertThat(foundProposal.getVideo()).isEqualTo("PROPOSAL_VIDEO");
    }

    @Test
    public void should_create_a_proposal() {
        Proposal proposal = new Proposal() ;
        proposal.setState(Proposal.State.ACCEPTED);
        proposal.setName("PROPOSAL_NAME");
        int createdLines = proposalMapper.insert(proposal);

        assertThat(createdLines).isEqualTo(1);
        assertThat(proposal.getId()).isGreaterThan(0);
    }

    @Test
    public void should_update_a_proposal() {
        Proposal proposal = new Proposal() ;
        proposal.setId(PROPOSAL_ID);
        proposal.setName("UPDATED_NAME");
        proposal.setState(Proposal.State.CONFIRMED);
        int updatedLines = proposalMapper.update(proposal);

        assertThat(updatedLines).isEqualTo(1);

        Proposal updatedProposal = proposalMapper.findById(PROPOSAL_ID);
        assertThat(updatedProposal).isNotNull();
        assertThat(updatedProposal.getName()).isEqualTo("UPDATED_NAME");
        assertThat(updatedProposal.getState()).isEqualTo(Proposal.State.CONFIRMED);
    }

    @Test
    public void should_delete_a_proposal() {
        Proposal proposal = new Proposal() ;
        proposal.setId(PROPOSAL_ID);
        int deletedLines = proposalMapper.delete(proposal);

        assertThat(deletedLines).isEqualTo(1);
    }


}
