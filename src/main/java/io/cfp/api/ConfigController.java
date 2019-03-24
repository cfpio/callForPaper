package io.cfp.api;

import io.cfp.entity.Role;
import io.cfp.multitenant.TenantId;
import io.cfp.service.admin.config.ApplicationConfigService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping(value = { "/v1/config", "/api/config" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ConfigController {

    private static final Logger LOGGER = getLogger(ConfigController.class);

    @Autowired
    private ApplicationConfigService applicationConfigService;

    /**
     * Disable or enable submission of new talks
     * @param key enable submission if true, else disable
     * @return key
     */
    @RequestMapping(value="/enableSubmissions", method= RequestMethod.POST)
    @Secured(Role.ADMIN)
    public io.cfp.domain.common.Key postEnableSubmissions(@Valid @RequestBody io.cfp.domain.common.Key key,
                                                          @TenantId String eventId) {

        if (key.getKey().equals("true")) {
            LOGGER.info("Open submissions of {}", eventId);
            applicationConfigService.openCfp(eventId);
        }
        if (key.getKey().equals("false")) {
            LOGGER.info("Close submissions of {}", eventId);
            applicationConfigService.closeCfp(eventId);
        }

        return key;
    }
}
