package fr.sii.service.admin.user;

import java.util.List;

import fr.sii.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.sii.entity.AdminUser;
import fr.sii.entity.User;
import fr.sii.repository.AdminUserRepo;
import fr.sii.repository.UserRepo;

@Service
@Transactional
public class AdminUserService {

    @Autowired
    private AdminUserRepo adminUserRepo;

    @Autowired
    private UserRepo userRepo;

    /** Current loggued admin, scoped request object */
    @Autowired
    private AdminUser currentAdmin;

    public List<AdminUser> findAll()
    {
        return adminUserRepo.findByEventId(Event.current());
    }

    /**
     * Retrieve an admin if the user e-mail match an admin e-mail
     * @param userId Id of the connected user
     * @return AdminUser if existing, null otherwise
     */
    public AdminUser findFromUserId(int userId) {
        User user = userRepo.findOne(userId);
        if (user == null) return null;
        return adminUserRepo.findByEventIdAndEmail(Event.current(), user.getEmail());
    }

    /**
     * Set connected admin for the current request
     * @param admin Admin to set
     */
    public void setCurrentAdmin(AdminUser admin) {
        //warning, do not replace the autowired object because it's a proxy around an object created by spring in the request
        currentAdmin.setId(admin.getId());
        currentAdmin.setEmail(admin.getEmail());
        currentAdmin.setName(admin.getName());
    }

    public AdminUser getCurrentUser() {
        if (currentAdmin.getEmail() == null) return null;
        return currentAdmin;
    }

}
