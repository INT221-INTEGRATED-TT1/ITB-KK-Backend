package sit.int221.controllers.secondary;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.components.JwtTokenUtil;
import sit.int221.dtos.request.JwtRequestUser;
import sit.int221.services.JwtUserDetailsService;

@RestController
@RequestMapping("/authentications")
public class AuthenticationController {
    @Autowired
    JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid JwtRequestUser jwtRequestUser) {

        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(jwtRequestUser.getUserName());
        if (!passwordEncoder.matches(jwtRequestUser.getPassword(), userDetails.getPassword()) || userDetails.getUsername() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password is incorrect");
        }

        String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(token);
    }
}
