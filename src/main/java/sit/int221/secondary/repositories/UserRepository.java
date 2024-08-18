package sit.int221.secondary.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.secondary.entities.UserTest;

public interface UserRepository extends JpaRepository<UserTest, String> {

    UserTest findByUsername(String userName);
}
