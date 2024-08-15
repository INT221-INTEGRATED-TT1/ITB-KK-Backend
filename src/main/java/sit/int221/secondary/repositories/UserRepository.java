package sit.int221.secondary.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.secondary.entities.User;

public interface UserRepository extends JpaRepository<User, String> {
}
