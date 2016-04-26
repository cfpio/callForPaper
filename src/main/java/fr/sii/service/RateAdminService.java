package fr.sii.service;

import fr.sii.dto.RateAdmin;
import fr.sii.entity.AdminUser;
import fr.sii.entity.Event;
import fr.sii.entity.Rate;
import fr.sii.repository.RateRepo;
import fr.sii.repository.TalkRepo;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Service for managing rates by admin
 */
@Service
@Transactional
public class RateAdminService {

    @Autowired
    private RateRepo rateRepo;

    @Autowired
    private TalkRepo talkRepo;

    @Autowired
    private MapperFacade mapper;

    /**
     * Retrieve all rates
     * @return All rates
     */
    public List<RateAdmin> getAll() {
        List<Rate> rates = rateRepo.findByEventId(Event.current());
        return mapper.mapAsList(rates, RateAdmin.class);
    }

    /**
     * Retrieve all rate for all talks for a user
     * @param userId Id of the user
     * @return Rates
     */
    public List<RateAdmin> findForUser(int userId) {
        List<Rate> rates = rateRepo.findByEventIdAndTalkUserId(Event.current(), userId);
        return mapper.mapAsList(rates, RateAdmin.class);
    }

    /**
     * Retrieve all rate for a talk
     * @param talkId Id of the talk
     * @return Rates
     */
    public List<RateAdmin> findForTalk(int talkId) {
        List<Rate> rates = rateRepo.findByEventIdAndTalkId(Event.current(), talkId);
        return mapper.mapAsList(rates, RateAdmin.class);
    }

    /**
     * Retrieve rate for a talk and an admin
     * @param talkId Id of the talk
     * @param adminId Id of the admin to get
     * @return Rate or null if not talk rated by this admin
     */
    public RateAdmin findForTalkAndAdmin(int talkId, int adminId) {
        Rate rates = rateRepo.findByEventIdAndTalkIdAndAdminUserId(Event.current(), talkId, adminId);
        return mapper.map(rates, RateAdmin.class);
    }

    /**
     * Retrieve a rate
     * @param rateId Id of the rate to retrieve
     * @return Rate or null if not found
     */
    public RateAdmin get(int rateId) {
        Rate rate = rateRepo.findByIdAndEventId(rateId, Event.current());
        return mapper.map(rate, RateAdmin.class);
    }

    /**
     * Add a new rate
     * @param rate Rate to add
     * @param admin Admin adding the rate
     * @param talkId Talk attached to the rate
     * @return Added rate
     */
    public RateAdmin add(RateAdmin rate, AdminUser admin, int talkId) {
        Rate newRate = mapper.map(rate, Rate.class);
        newRate.setAdded(new Date());
        newRate.setAdminUser(admin);
        newRate.setTalk(talkRepo.getOne(talkId));     // FIXME do we need to check the talk belong to current event ?
        rateRepo.save(newRate);
        rateRepo.flush(); //to get rate id
        return mapper.map(newRate, RateAdmin.class);
    }

    /**
     * Edit a rate
     * @param rate rate to edit
     * @return Edited rate
     */
    public RateAdmin edit(RateAdmin rate) {
        Rate editRate = rateRepo.findByIdAndEventId(rate.getId(), Event.current());
        mapper.map(rate, editRate);
        editRate.setAdded(new Date());
        rateRepo.flush();
        return mapper.map(editRate, RateAdmin.class);
    }

    /**
     * Delete a rate
     * @param rateId Id of the rate to delete
     * @return Deleted rate
     */
    public RateAdmin delete(int rateId) {
        Rate rate = rateRepo.findByIdAndEventId(rateId, Event.current());
        RateAdmin deleted = mapper.map(rate, RateAdmin.class);
        rateRepo.delete(rate);
        return deleted;
    }

    /**
     * Delete all rates
     */
    public void deleteAll() {
        rateRepo.deleteByEventId(Event.current());
    }
}
