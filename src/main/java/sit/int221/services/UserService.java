package sit.int221.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int221.secondary.entities.User;
import sit.int221.secondary.repositories.UserRepository;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public List<User> getAllCustomer() {
        return repository.findAll();
    }
}
