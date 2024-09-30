package sit.int221.controllers.secondary;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.components.JwtTokenUtil;
import sit.int221.dtos.request.JwtRequestUser;
import sit.int221.dtos.response.AccessTokenDTORes;
import sit.int221.entities.secondary.User;
import sit.int221.exceptions.AuthException;
import sit.int221.services.AuthorizationService;
import sit.int221.services.JwtUserDetailsService;
import sit.int221.services.LocalUserService;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "https://intproj23.sit.kmutt.ac.th", "http://localhost:80", "https://ip23tt1.sit.kmutt.ac.th"})
@RequestMapping("/login")
public class AuthenticationController {
    @Autowired
    JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    LocalUserService localUserService;


    @PostMapping("")
    public ResponseEntity<Object> login(@RequestBody @Valid JwtRequestUser jwtRequestUser) {
        try {
            jwtUserDetailsService.validateInputs(jwtRequestUser.getUserName(), jwtRequestUser.getPassword());
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(jwtRequestUser.getUserName(), jwtRequestUser.getPassword());

            // authenticate the user
            Authentication authentication = authenticationManager.authenticate(authenticationToken);


            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = jwtUserDetailsService.findByUserName(userDetails.getUsername());
            AccessTokenDTORes accessTokenDTOres = new AccessTokenDTORes();
            if (!localUserService.checkLocalUser(jwtRequestUser.getUserName())) {
                localUserService.insertLocalUser(jwtRequestUser.getUserName());
                accessTokenDTOres.setAccess_token(jwtTokenUtil.generateToken(userDetails, user));
                accessTokenDTOres.setRefresh_token(jwtTokenUtil.generateRefreshToken(userDetails,user));
            }
            accessTokenDTOres.setAccess_token(jwtTokenUtil.generateToken(userDetails, user));
            accessTokenDTOres.setRefresh_token(jwtTokenUtil.generateRefreshToken(userDetails,user));
            return ResponseEntity.ok(accessTokenDTOres);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password is incorrect");
        }
    }


    @GetMapping("/validate-token")
    public ResponseEntity<Object> validateToken(@RequestHeader("Authorization") String requestTokenHeader) {

        Claims claims = authorizationService.validateToken(requestTokenHeader);
        return ResponseEntity.ok(claims);
    }
}
