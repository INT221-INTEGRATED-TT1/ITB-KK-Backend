package sit.int221.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.components.JwtTokenUtil;
import sit.int221.exceptions.AuthException;

@Component
public class AuthorizationService {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    String jwtToken;
    Claims claims;

    public Claims validateToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            jwtToken = token.substring(7);
            try {
                claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
                throw new AuthException("Invalid JWT token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
                throw new AuthException("JWT Token has expired");
            }catch (MalformedJwtException e){
                throw new AuthException("Malformed JWT token");
            } catch (SignatureException e){
                throw new AuthException("JWT signature not valid");
            }
        } else {

            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "JWT Token does not begin with Bearer String");
        }
        return claims;
    }
}
