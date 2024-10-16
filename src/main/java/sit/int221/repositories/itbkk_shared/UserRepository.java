package sit.int221.repositories.itbkk_shared;


import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.entities.itbkk_shared.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String userName);

    Boolean existsByUsername(String userName);

    Boolean existsByEmail(String email);

    User findByEmail(String email);


//    User findByOid(String oid);

}
