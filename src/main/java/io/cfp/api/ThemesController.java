package io.cfp.api;

import io.cfp.entity.Role;
import io.cfp.mapper.ThemeMapper;
import io.cfp.model.Stat;
import io.cfp.model.Theme;
import io.cfp.multitenant.TenantId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@RestController
@RequestMapping(value = { "/v1/themes", "/api/themes" }, produces = APPLICATION_JSON_UTF8_VALUE)
public class ThemesController {


    @Autowired
    private ThemeMapper themes;

    @RequestMapping(method = GET)
    public Collection<Theme> all(@TenantId String eventId) {
        return themes.findByEvent(eventId);
    }

    @RequestMapping(method = POST)
    @Transactional
    @Secured(Role.OWNER)
    public Theme create(@RequestBody Theme theme, @TenantId String eventId) {
        themes.insert(theme.setEvent(eventId));
        return theme;
    }

    @RequestMapping(value = "/{id}", method = PUT)
    @Transactional
    @Secured(Role.OWNER)
    public void update(@PathVariable int id, @RequestBody Theme theme, @TenantId String eventId) {
        themes.updateForEvent(theme.setId(id), eventId);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @Transactional
    @Secured(Role.OWNER)
    public void delete(@PathVariable int id, @TenantId String eventId) {
        themes.deleteForEvent(id, eventId);
    }

    @GetMapping(value = "/stats")
    @Secured(Role.ADMIN)
    public Map<String, Long> getStats(@TenantId String eventId) {
        return themes.countProposalsByTheme(eventId)
            .stream()
            .collect(Collectors.toMap(Stat::getName, Stat::getCount));
    }

}
