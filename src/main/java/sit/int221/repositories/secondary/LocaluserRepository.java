package sit.int221.repositories.secondary;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.entities.secondary.Localuser;
import sit.int221.entities.secondary.User;

public interface LocaluserRepository extends JpaRepository<Localuser, String> {


    Localuser findByUsername(String userName);

//    Localuser findByOid(String oid);
}