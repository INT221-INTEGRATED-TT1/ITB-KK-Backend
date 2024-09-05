package sit.int221.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.entities.secondary.AuthUser;
import sit.int221.entities.secondary.User;
import sit.int221.repositories.secondary.UserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(userName);
        System.out.println(user);

//        System.out.println("Service");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password is incorrect");
        }
//        List<GrantedAuthority> roles = new ArrayList<>();
//        GrantedAuthority grantedAuthority = new GrantedAuthority() {
//            public String getAuthority() {
//                return user.getRole();
//            }
//        };
//        roles.add(grantedAuthority);
        UserDetails userDetails = new AuthUser(userName, user.getPassword());
        return userDetails;
    }

    public User findByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    public void validateInputs(String userName, String password) {
        if (userName == null || password == null || userName.isBlank() && password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username or password is incorrect");
        }
        if (userName.length() > 50 || password.length() > 14) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username or password is incorrect");
        }
    }


}
