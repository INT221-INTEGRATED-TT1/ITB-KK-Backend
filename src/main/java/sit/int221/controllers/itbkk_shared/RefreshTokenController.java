package sit.int221.controllers.itbkk_shared;

import io.jsonwebtoken.Claims;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.components.JwtTokenUtil;
import sit.int221.dtos.response.RefreshTokenDTORes;
import sit.int221.entities.itbkk_shared.User;
import sit.int221.services.itbkk_shared.AuthorizationService;
import sit.int221.services.itbkk_shared.JwtUserDetailsService;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "https://intproj23.sit.kmutt.ac.th", "http://localhost:80", "https://ip23tt1.sit.kmutt.ac.th"})
@RequestMapping("/token")
public class RefreshTokenController {
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    JwtUserDetailsService jwtUserDetailsService;
    @PostMapping ("")
    public ResponseEntity<RefreshTokenDTORes> getNewAccessToken(@RequestHeader("x-refresh-token") String refresh_token){
        System.out.println("Running Validate token");
        Claims claims = authorizationService.validateRefreshToken(refresh_token);
        User user = jwtUserDetailsService.findByUserName(claims.getSubject());
        System.out.println("This is claim" + claims.getSubject());

        String newAccessToken = jwtTokenUtil.generateAccessTokenByRefreshToken(user);
        RefreshTokenDTORes response = new RefreshTokenDTORes();
        response.setAccess_token(newAccessToken);
        return ResponseEntity.ok(response);
    }
}