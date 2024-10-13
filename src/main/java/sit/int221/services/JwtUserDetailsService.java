package sit.int221.services;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.entities.primary.Board;
import sit.int221.entities.secondary.AuthUser;
import sit.int221.entities.secondary.User;
import sit.int221.repositories.primary.BoardRepository;
import sit.int221.repositories.secondary.UserRepository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    BoardRepository boardRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(userName);
//        String oid = user.getOid();
//        System.out.println(user);

//        Board board = authorizationService.getBoardId(boardId);


        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password is incorrect");
        }

//        Boolean board = boardRepository.findByOwnerId(oid);
//        if(!board){
//            throw  new ResponseStatusException(HttpStatus.FORBIDDEN, "fdf");
//        }

        List<GrantedAuthority> authorities = new LinkedList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_OWNER"));
        return new AuthUser(userName, user.getPassword(), authorities);
    }

    public UserDetails setCollaboratorWriteAccess(String userName) {
        User user = userRepository.findByUsername(userName);
//        String oid = user.getOid();
//        System.out.println(user);

//        Board board = authorizationService.getBoardId(boardId);


        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password is incorrect");
        }

//        Boolean board = boardRepository.findByOwnerId(oid);
//        if(!board){
//            throw  new ResponseStatusException(HttpStatus.FORBIDDEN, "fdf");
//        }

        List<GrantedAuthority> authorities = new LinkedList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_COLLABORATOR_WRITE"));
        return new AuthUser(userName, user.getPassword(), authorities);
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
