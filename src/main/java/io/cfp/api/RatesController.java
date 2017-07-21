package io.cfp.api;

import io.cfp.entity.Role;
import io.cfp.mapper.RateMapper;
import io.cfp.model.Rate;
import io.cfp.model.Stat;
import io.cfp.model.queries.RateQuery;
import io.cfp.multitenant.TenantId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = { "/v1/rates", "/api/rates" }, produces = APPLICATION_JSON_UTF8_VALUE)
public class RatesController {


    @Autowired
    private RateMapper rates;

    /**
     * Get all ratings
     */
    @GetMapping
    @Secured(Role.ADMIN)
    public List<Rate> getRates(@TenantId String eventId) {
        RateQuery rateQuery = new RateQuery().setEventId(eventId);
        return rates.findAll(rateQuery);
    }

    /**
     * Delete all ratings
     */
    @DeleteMapping
    @Secured(Role.ADMIN)
    public void deleteRates(@TenantId String eventId) {
        rates.deleteAllForEvent(eventId);
    }

    /**
     * Delete specific rating
     */
    @DeleteMapping("/{rateId}")
    @Secured(Role.ADMIN)
    public void deleteRate(@PathVariable int rateId, @TenantId String eventId) {
        rates.deleteForEvent(rateId, eventId);
    }


    /**
     * Get Rates stats
     */
    @GetMapping(value = "/stats")
    @Secured(Role.ADMIN)
    public Map<String, Long> getRateStats(@TenantId String eventId) {
        return rates.getRateByEmailUsers(eventId)
            .stream()
            .collect(Collectors.toMap(Stat::getName, Stat::getCount));
    }
}
