package io.cfp.service;

import io.cfp.dto.TalkAdmin;
import io.cfp.entity.Format;
import io.cfp.entity.Rate;
import io.cfp.entity.Talk;
import io.cfp.entity.Track;
import io.cfp.repository.RateRepo;
import io.cfp.repository.TalkRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TalkAdminServiceTest {

    public static final int USER_ID = 10;
    @InjectMocks
    private TalkAdminService talkAdminService;


    @Mock
    private TalkRepo talkRepo;

    @Mock
    private RateRepo rateRepo;

    @Test
    public void should_return_all_talkAdmins() {

        io.cfp.entity.User adminUser = new io.cfp.entity.User();
        adminUser.setId(USER_ID);

        Talk talk = new Talk();
        talk.setId(20);
        Format format = new Format();
        format.setId(30);
        talk.setFormat(format);
        Track track = new Track();
        track.setId(40);
        talk.setTrack(track);
        talk.setUser(adminUser);
        talk.setCospeakers(new HashSet<>());

        List<Talk> talksAdminList = new ArrayList<>();
        talksAdminList.add(talk);
        when(talkRepo.findByEventIdAndStatesFetch("EVENT_ID", Collections.emptyList())).thenReturn(talksAdminList);

        List<Rate> rates = new ArrayList<>();

        Rate rate = new Rate();

        rate.setAdminUser(adminUser);
        rate.setTalk(talk);

        rates.add(rate);
        when(rateRepo.findAllFetchAdmin("EVENT_ID")).thenReturn(rates);

        List<TalkAdmin> returnedTalkAdminList = talkAdminService.findAll("EVENT_ID", USER_ID);
        assertThat(returnedTalkAdminList).isNotEmpty();
    }
}
