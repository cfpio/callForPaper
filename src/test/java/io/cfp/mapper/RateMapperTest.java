package io.cfp.mapper;

import io.cfp.model.Rate;
import io.cfp.model.Stat;
import io.cfp.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@MybatisTest
public class RateMapperTest {

    private static final int RATE_ID = 71;
    private static final String EVENT_ID = "EVENT_ID";
    private static final int USER_ID = 10;

    @Autowired
    private RateMapper rateMapper;

    @Test
    public void should_get_rates_group_by_email() {
        List<Stat> ratesByEmail = rateMapper.getRateByEmailUsers(EVENT_ID);
        assertThat(ratesByEmail).isNotEmpty();
    }

    @Test
    public void should_delete_a_rate() {
        int deletedLines = rateMapper.deleteForEvent(RATE_ID, EVENT_ID);

        assertThat(deletedLines).isEqualTo(1);
    }

    @Test
    public void should_update_the_rate() {
        Rate rate = new Rate();
        rate.setId(RATE_ID);
        rate.setUser(new User().setId(USER_ID));
        rate.setEventId(EVENT_ID);
        rate.setAdded(new Date());

        int updatedLines = rateMapper.update(rate);

        assertThat(updatedLines).isEqualTo(1);
    }


}
