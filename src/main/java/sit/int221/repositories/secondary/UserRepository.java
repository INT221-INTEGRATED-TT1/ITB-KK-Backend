package sit.int221.repositories.secondary;


import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.entities.secondary.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String userName);

    Boolean existsByUsername(String userName);

    Boolean existsByEmail(String email);


//    User findByOid(String oid);

}
