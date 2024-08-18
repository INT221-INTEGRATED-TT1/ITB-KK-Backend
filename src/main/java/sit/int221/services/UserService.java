package sit.int221.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int221.entities.secondary.User;
import sit.int221.repositories.secondary.UserRepository;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public List<User> getAllCustomer() {
        return repository.findAll();
    }
}
