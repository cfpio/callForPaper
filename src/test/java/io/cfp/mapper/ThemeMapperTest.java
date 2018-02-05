package io.cfp.mapper;

import io.cfp.model.Stat;
import io.cfp.model.Theme;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@MybatisTest
public class ThemeMapperTest {

    private static final int THEME_ID = 40;
    private static final int THEME_ID_TO_DELETE = 41;
    private static final String EVENT_ID = "EVENT_ID";
    private static final String ALL_STATE = "*";

    @Autowired
    private ThemeMapper themeMapper;

    @Test
    public void should_delete_a_theme() {
        int deletedLines = themeMapper.deleteForEvent(THEME_ID_TO_DELETE, EVENT_ID);

        assertThat(deletedLines).isEqualTo(1);
    }

    @Test
    public void should_update_the_theme() {
        Theme theme = new Theme();
        theme.setId(THEME_ID);
        theme.setEvent(EVENT_ID);
        theme.setLibelle("UPDATED_LIBELLE");

        int updatedLines = themeMapper.updateForEvent(theme, EVENT_ID);

        assertThat(updatedLines).isEqualTo(1);
    }

    @Test
    public void should_count_the_proposals_by_theme_of_an_event() {
        List<Stat> stats = themeMapper.countProposalsByThemeAndState(EVENT_ID, null);

        assertThat(stats).hasSize(1);
    }

    @Test
    public void should_count_the_ACCEPTED_proposals_by_theme_of_an_event_() {
        List<Stat> stats = themeMapper.countProposalsByThemeAndState(EVENT_ID, "ACCEPTED");

        assertThat(stats).hasSize(1);
    }


}
