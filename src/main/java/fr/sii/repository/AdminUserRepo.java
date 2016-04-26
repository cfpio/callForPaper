package fr.sii.repository;

import fr.sii.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminUserRepo extends JpaRepository<AdminUser, Integer> {

    AdminUser findByEventIdAndEmail(String eventId, String email);

    List<AdminUser> findByEventId(String eventId);
}
