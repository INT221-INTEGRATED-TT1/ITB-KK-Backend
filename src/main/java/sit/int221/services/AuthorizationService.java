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
import sit.int221.dtos.response.CollaboratorDTORes;
import sit.int221.entities.primary.Board;
import sit.int221.exceptions.AuthException;
import sit.int221.exceptions.ItemNotFoundException;
import sit.int221.repositories.primary.BoardRepository;
import sit.int221.repositories.secondary.UserRepository;

@Component
public class AuthorizationService {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    BoardRepository boardRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired

    CollaboratorService collaboratorService;

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


    public Claims validateRefreshToken(String token) {
        if (token != null) {
            jwtToken = token;
            System.out.println("This is refresh token:" + token);
            try {
                claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
                System.out.println("This is Refresh Token Subject" + claims.getSubject());
                if (userRepository.existsByUsername(claims.getSubject()) == false) {
                    throw new AuthException("user not exist !");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get Refresh Token");
                throw new AuthException("Invalid Refresh token");
            } catch (ExpiredJwtException e) {
                System.out.println("Refresh Token has expired");
                throw new AuthException("Refresh Token has expired");
            } catch (MalformedJwtException e) {
                throw new AuthException("Malformed Refresh token");
            } catch (SignatureException e) {
                throw new AuthException("Refresh Token signature not valid");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "JWT Token does not begin with Bearer String");
        }
        return claims;
    }

    public void validateClaims(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Authorization required for private boards");
        }

        String jwtToken = token.substring(7);
        try {
            jwtTokenUtil.getAllClaimsFromToken(jwtToken);
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
    }

    public void checkIdThatBelongsToUser(Claims claims, String boardId) {
        Board board = getBoardId(boardId);
        String oid = (String) claims.get("oid");
        if (!board.getOwnerId().equals(oid)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Owner id " + oid + " doest not belong to " + board.getOwnerId());
        }
    }

    public Board getBoardId(String boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board id " + boardId + " not found"));
    }

    public boolean isUserHaveWriteAccess(Claims claims , String boardId){
        String oid = (String) claims.get("oid");
        Board board = getBoardId(boardId);


        if(board.getOwnerId().equals(oid)){
            System.out.println("Owner have write access");
            return true;
        }
        else {
            CollaboratorDTORes collaborator = collaboratorService.getCollabById(boardId, oid);
            if (collaborator.getAccessRight().equals("WRITE")){
                System.out.println("Collaborator have write access");

                return true;
            }
            return false;


        }


    }
}
