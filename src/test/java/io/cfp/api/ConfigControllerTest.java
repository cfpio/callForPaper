package io.cfp.api;

import io.cfp.mapper.UserMapper;
import io.cfp.model.Role;
import io.cfp.model.User;
import io.cfp.service.admin.config.ApplicationConfigService;
import io.cfp.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ConfigController.class)
public class ConfigControllerTest {

    @MockBean
    private ApplicationConfigService applicationConfigService;

    @MockBean
    private UserMapper userMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_open_cfp() throws Exception {

        User user = new User();
        user.setId(20);
        user.setEmail("EMAIL");
        user.addRole(Role.ADMIN);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        String openCFP = Utils.getContent("/json/config/open_cfp.json");

        mockMvc.perform(post("/api/config/enableSubmissions")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer " + token)
            .content(openCFP)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.key").value("true"))
        ;

         verify(applicationConfigService).openCfp(anyString());
    }

    @Test
    public void should_close_cfp() throws Exception {

        User user = new User();
        user.setId(20);
        user.setEmail("EMAIL");
        user.addRole(Role.ADMIN);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        String closeCFP = Utils.getContent("/json/config/close_cfp.json");

        mockMvc.perform(post("/api/config/enableSubmissions")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer " + token)
            .content(closeCFP)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.key").value("false"))
        ;

        verify(applicationConfigService).closeCfp(anyString());
    }
}
