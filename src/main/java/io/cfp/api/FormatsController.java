package io.cfp.api;

import io.cfp.entity.Role;
import io.cfp.mapper.FormatMapper;
import io.cfp.mapper.ThemeMapper;
import io.cfp.model.Format;
import io.cfp.model.Theme;
import io.cfp.multitenant.TenantId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@RestController
@RequestMapping(value = { "/v1/formats", "/api/formats" }, produces = APPLICATION_JSON_UTF8_VALUE)
public class FormatsController {


    @Autowired
    private FormatMapper formats;

    @RequestMapping(method = GET)
    public Collection<Format> all(@TenantId String eventId) {
        return formats.findByEvent(eventId);
    }

    @RequestMapping(method = POST)
    @Transactional
    @Secured(Role.OWNER)
    public Format create(@RequestBody Format format, @TenantId String eventId) {
        formats.insert(format.setEvent(eventId));
        return format;
    }

    @RequestMapping(value = "/{id}", method = PUT)
    @Transactional
    @Secured(Role.OWNER)
    public void update(@PathVariable int id, @RequestBody Format format, @TenantId String eventId) {
        formats.updateForEvent(format.setId(id), eventId);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @Transactional
    @Secured(Role.OWNER)
    public void delete(@PathVariable int id, @TenantId String eventId) {
        formats.deleteForEvent(id, eventId);
    }

}
