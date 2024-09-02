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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
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
    AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid JwtRequestUser jwtRequestUser) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(jwtRequestUser.getUserName(), jwtRequestUser.getPassword());

            // authenticate the user
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            jwtUserDetailsService.validateInputs(jwtRequestUser.getUserName(), jwtRequestUser.getPassword());
            User user = jwtUserDetailsService.findByUserName(userDetails.getUsername());
            AccessTokenDTORes accessTokenDTOres = new AccessTokenDTORes();
            accessTokenDTOres.setAccess_token(jwtTokenUtil.generateToken(userDetails, user));
            return ResponseEntity.ok(accessTokenDTOres);

        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password is incorrect!");
        }


    }

    @GetMapping("/validate-token")
    public ResponseEntity<Object> validateToken(@RequestHeader("Authorization") String requestTokenHeader) {
        Claims claims = null;
        String jwtToken = null;

        if (requestTokenHeader != null) {
            if (requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
                try {
                    claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
                } catch (IllegalArgumentException ex) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No token");
                } catch (ExpiredJwtException ex) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT Token has expired");
                } catch (MalformedJwtException ex) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Malformed JWT token");
                } catch (SignatureException ex) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT signature not valid");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT Token does not begin with Bearer String");
            }
        }
        return ResponseEntity.ok(claims);
    }
}
