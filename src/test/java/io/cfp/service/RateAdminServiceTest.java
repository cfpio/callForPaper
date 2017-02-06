package io.cfp.service;

import io.cfp.dto.RateAdmin;
import io.cfp.entity.Rate;
import io.cfp.entity.Talk;
import io.cfp.entity.User;
import io.cfp.repository.EventRepository;
import io.cfp.repository.RateRepo;
import io.cfp.repository.TalkRepo;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author mpetitdant
 *         Date: 07/02/17
 */

@RunWith(MockitoJUnitRunner.class)
public class RateAdminServiceTest {

    private static final int TALK_ID = 123;
    private static final int RATE_ID = 456;

    @Mock
    private RateRepo rateRepo;

    @Mock
    private TalkRepo talkRepo;

    private MapperFacade mapper = new DefaultMapperFactory.Builder().build().getMapperFacade();

    @Mock
    private EventRepository events;

    private RateAdminService rateAdminService;

    @Before
    public void setup() {
        rateAdminService = new RateAdminService(rateRepo, talkRepo, mapper, events);
    }

    @Test
    public void getAll() throws Exception {
        Date now = new Date();

        // Given
        List<Rate> rates = new ArrayList<>();
        rates.add(createRate(RATE_ID, now, 12, TALK_ID, false, true,
            "mail@mail.com", "firstname", "lastname"));

        when(rateRepo.findByEventId(anyString())).thenReturn(rates);

        // When
        List<RateAdmin> result = rateAdminService.getAll();

        // Then
        assertThat(result).hasSize(1);
        RateAdmin rate = result.get(0);

        assertThat(rate.getAdded()).isEqualTo(now);
        assertThat(rate.getId()).isEqualTo(RATE_ID);
        assertThat(rate.getRate()).isEqualTo(12);
        assertThat(rate.getTalkId()).isEqualTo(TALK_ID);
        assertThat(rate.getUser().getEmail()).isEqualTo("mail@mail.com");
        assertThat(rate.getUser().getName()).isEqualTo("firstname lastname");
        assertThat(rate.isHate()).isEqualTo(true);
        assertThat(rate.isLove()).isEqualTo(false);
    }

    private Rate createRate(int id, Date added, int rate, int talkId, boolean love, boolean hate, String email, String firstname, String lastname) {
        Rate result = new Rate();
        result.setId(id);
        result.setAdded(added);
        result.setRate(rate);
        result.setLove(love);
        result.setHate(hate);

        Talk talk = new Talk();
        talk.setId(talkId);
        result.setTalk(talk);

        User user = new User();
        user.setEmail(email);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        result.setAdminUser(user);

        return result;
    }

}
