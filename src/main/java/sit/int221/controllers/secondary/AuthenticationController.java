package sit.int221.controllers.secondary;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.components.JwtTokenUtil;
import sit.int221.dtos.request.JwtRequestUser;
import sit.int221.dtos.response.AccessTokenDTORes;
import sit.int221.entities.secondary.User;
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
        if (userDetails.getUsername() == null || !passwordEncoder.matches(jwtRequestUser.getPassword(), userDetails.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password is incorrect");
        }

        jwtUserDetailsService.validateInputs(jwtRequestUser.getUserName(), jwtRequestUser.getPassword());

        User user = jwtUserDetailsService.findByUserName(userDetails.getUsername());
        AccessTokenDTORes accessTokenDTOres = new AccessTokenDTORes();
        accessTokenDTOres.setAccess_token(jwtTokenUtil.generateToken(userDetails, user));
        return ResponseEntity.ok(accessTokenDTOres);
    }
}
