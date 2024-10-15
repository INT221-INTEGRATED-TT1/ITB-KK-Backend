package sit.int221.services.task_base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int221.entities.task_base.LocalUser;
import sit.int221.entities.itbkk_shared.User;
import sit.int221.repositories.task_base.LocalUserRepository;
import sit.int221.repositories.itbkk_shared.UserRepository;

@Service
public class LocalUserService {
    @Autowired
    LocalUserRepository localUserRepository;
    @Autowired
    UserRepository userRepository;

    public void insertLocalUser(String username) {
        LocalUser localuser = new LocalUser();
        User user = userRepository.findByUsername(username);

        if (!localUserRepository.existsByUsername(username)) {
            localuser.setOid(user.getOid());
            localuser.setUsername(user.getUsername());
            localuser.setName(user.getName());
            localuser.setEmail(user.getEmail());
            localUserRepository.saveAndFlush(localuser);
        }
    }

    public boolean checkLocalUser(String username) {
        return localUserRepository.existsByUsername(username);
    }
}
