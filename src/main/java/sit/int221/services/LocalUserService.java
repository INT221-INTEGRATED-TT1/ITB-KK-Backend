package sit.int221.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int221.entities.secondary.Localuser;
import sit.int221.repositories.secondary.LocaluserRepository;


@Service
public class LocalUserService {

    @Autowired
    private LocaluserRepository localuserRepository;


    public Localuser findNewUser(String username) {
        Localuser localuser = new Localuser();
        try {
            localuser = localuserRepository.findByUsername(username);
            System.out.println("Not New user");
        } catch (Exception ex) {
            System.out.println("New user are coming");
        }
        return localuser;

    }
}
