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
import sit.int221.entities.primary.Board;
import sit.int221.exceptions.AuthException;
import sit.int221.exceptions.ItemNotFoundException;
import sit.int221.repositories.primary.BoardRepository;

@Component
public class AuthorizationService {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    String jwtToken;
    Claims claims;
    //    @Autowired
//    Tasks3Service tasks3Service;
    @Autowired
    BoardRepository boardRepository;

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
            } catch (MalformedJwtException e) {
                throw new AuthException("Malformed JWT token");
            } catch (SignatureException e) {
                throw new AuthException("JWT signature not valid");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "JWT Token does not begin with Bearer String");
        }
        return claims;
    }

    public void checkIdThatBelongsToUser(Claims claims, String boardId) {
        String oid = (String) claims.get("oid");
        Board board = getBoardId(boardId);
        if (!board.getOwnerId().equals(oid)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Owner id " + oid +
                    " doest not belong to " + board.getOwnerId());
        }
    }

    public Board getBoardId(String boardId){
        return boardRepository.findById(boardId).orElseThrow(() ->
                new ItemNotFoundException("Board id " + boardId + " not found"));
    }

}
