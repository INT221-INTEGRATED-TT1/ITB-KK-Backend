package sit.int221.repositories.task_base;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.entities.task_base.LocalUser;

public interface LocalUserRepository extends JpaRepository<LocalUser, String> {
    boolean existsByUsername(String username);
    LocalUser findByEmail(String email);
}
