package sit.int221.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sit.int221.entities.secondary.AuthUser;
import sit.int221.entities.secondary.User;
import sit.int221.repositories.secondary.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new UsernameNotFoundException(userName + " doest not exist!!");
        }
        List<GrantedAuthority> roles = new ArrayList<>();
        GrantedAuthority grantedAuthority = new GrantedAuthority() {
            public String getAuthority() {
                return user.getRole();
            }
        };
        roles.add(grantedAuthority);
        UserDetails userDetails = new AuthUser(userName, user.getPassword(), roles);
        return userDetails;
    }


}
