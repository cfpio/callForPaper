package io.cfp.service;

import io.cfp.dto.TalkAdmin;
import io.cfp.entity.Format;
import io.cfp.entity.Track;
import io.cfp.mapper.ProposalMapper;
import io.cfp.mapper.RateMapper;
import io.cfp.model.Proposal;
import io.cfp.model.queries.ProposalQuery;
import io.cfp.model.queries.RateQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TalkAdminServiceTest {

    public static final int USER_ID = 10;
    @InjectMocks
    private TalkAdminService talkAdminService;


    @Mock
    private ProposalMapper proposalMapper;

    @Mock
    private RateMapper rateMapper;

    @Test
    public void should_return_all_talkAdmins() {

        io.cfp.model.User adminUser = new io.cfp.model.User();
        adminUser.setId(USER_ID);

        Proposal talk = new Proposal();
        talk.setId(20);
        Format format = new Format();
        format.setId(30);
        talk.setFormat(30);
        Track track = new Track();
        track.setId(40);
        talk.setTrackId(40);
        talk.setSpeaker(adminUser);
        talk.setCospeakers(new HashSet<>());
        talk.setState(Proposal.State.ACCEPTED);

        List<Proposal> talksAdminList = new ArrayList<>();
        talksAdminList.add(talk);
        when(proposalMapper.findAll(any(ProposalQuery.class))).thenReturn(talksAdminList);

        List<io.cfp.model.Rate> rates = new ArrayList<>();

        io.cfp.model.Rate rate = new io.cfp.model.Rate();

        rate.setUser(adminUser);
        rate.setTalk(talk);

        rates.add(rate);
        when(rateMapper.findAll(any(RateQuery.class))).thenReturn(rates);

        List<TalkAdmin> returnedTalkAdminList = talkAdminService.findAll("EVENT_ID", USER_ID);
        assertThat(returnedTalkAdminList).isNotEmpty();
    }
}
