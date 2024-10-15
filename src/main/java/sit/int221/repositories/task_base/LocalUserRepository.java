package sit.int221.repositories.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.entities.primary.LocalUser;

public interface LocalUserRepository extends JpaRepository<LocalUser, String> {
    boolean existsByUsername(String username);
    LocalUser findByEmail(String email);
}
