package sit.int221.filters;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import sit.int221.components.JwtTokenUtil;
import sit.int221.services.JwtUserDetailsService;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {


//    HandlerExceptionResolver exceptionResolver;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

//    public JwtAuthFilter(HandlerExceptionResolver exceptionResolver) {
//        this.exceptionResolver = exceptionResolver;
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
//        throws ServletException, IOException
        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;
//        try {
        if (requestTokenHeader != null) {
            if (requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);

                jwtToken = requestTokenHeader.substring(7);
                try {
                    username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                } catch (IllegalArgumentException e) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
                } catch (ExpiredJwtException e) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
                } catch (MalformedJwtException e) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
                } catch (SignatureException e) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
                }
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JW T Token does not begin with Bearer String");
            }
        }


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new
                        UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(request, response);
//        }
//        catch (Exception ex) {
//            exceptionResolver.resolveException(request, response, null, ex);
//        }
    }
}