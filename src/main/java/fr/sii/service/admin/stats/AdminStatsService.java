package fr.sii.service.admin.stats;

import fr.sii.domain.admin.meter.AdminMeter;
import fr.sii.entity.Event;
import fr.sii.repository.TalkRepo;
import fr.sii.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;

import static fr.sii.entity.Talk.State.*;

@Service
@Transactional
public class AdminStatsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TalkRepo talkRepo;

    public AdminMeter getAdminMeter() {
        AdminMeter meter = new AdminMeter();
        meter.setSpeakers((int) userRepo.count());
        meter.setTalks((int) talkRepo.countByEventIdAndStateIn(Event.current(), Arrays.asList(CONFIRMED, ACCEPTED, REFUSED)));
        meter.setDrafts((int) talkRepo.countByEventIdAndStateIn(Event.current(), Collections.singletonList(DRAFT)));
        return meter;
    }
}
