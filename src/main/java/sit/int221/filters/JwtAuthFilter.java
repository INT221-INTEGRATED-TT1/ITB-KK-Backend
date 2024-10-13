package sit.int221.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.components.JwtTokenUtil;
import sit.int221.exceptions.AuthException;
import sit.int221.repositories.primary.BoardRepository;
import sit.int221.services.AuthorizationService;
import sit.int221.services.BoardService;
import sit.int221.services.JwtUserDetailsService;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private BoardService boardService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        String requestURI = request.getRequestURI();
        if (requestURI.equals("/login") || requestURI.equals("/token")) {
            chain.doFilter(request, response);
            return;
        }

        String method = request.getMethod();
        boolean isPublicGetEndpoint = (method.equalsIgnoreCase("GET")) &&
                (requestURI.matches("/v3/boards/[A-Za-z0-9]+") ||
                        requestURI.matches("/v3/boards/[A-Za-z0-9]+/statuses(/\\d+)?") ||
                        requestURI.matches("/v3/boards/[A-Za-z0-9]+/tasks(/\\d+)?") || requestURI.matches("/v3/boards/[A-Za-z0-9]+/collabs(/([A-Za-z0-9]+))?")) ;


        // handle Http Method all method but not GET Method
        boolean isPublicEndPointOperation = (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT") || method.equalsIgnoreCase("PATCH") || method.equalsIgnoreCase("DELETE") ) &&
                (requestURI.matches("/v3/boards/[A-Za-z0-9]+") ||
                        requestURI.matches("/v3/boards/[A-Za-z0-9]+/statuses(/\\d+)?") ||
                        requestURI.matches("/v3/boards/[A-Za-z0-9]+/tasks(/\\d+)?") || requestURI.matches("/v3/boards/[A-Za-z0-9]+/collabs(/([A-Za-z0-9]+))?")) ;
        System.out.println("Debug" + "Block 1");
        System.out.println(isPublicEndPointOperation);
        System.out.println(isPublicGetEndpoint);

//      //   If the endpoint is public GET, allow access without token
        if (isPublicGetEndpoint) {
            chain.doFilter(request, response);
            return;
        }

        if(isPublicEndPointOperation){
            System.out.println("Debug " + "Block 2");
            // Validate Access Token
            if(StringUtils.hasText(request.getHeader("Authorization")) == false){
                logger.warn("This is Block 1");
                System.out.println(request.getHeader("Authorization"));
                response.setStatus(HttpStatus.UNAUTHORIZED.value()); // Set the status to 401 Unauthorized
                response.getWriter().write("Authorization header is missing or empty"); // Optional: write a message to the response
                return; // Stop further processing, do not call chain.doFilter()
            }
            try {
                authorizationService.validateToken(request.getHeader("Authorization"));
            }
            catch (AuthException e){
                System.out.println("This is Block 2");
                response.setStatus(HttpStatus.UNAUTHORIZED.value()); // Set the status to 401 Unauthorized
                response.getWriter().write(e.getMessage());
                return;
            }
            // Check is board exist


            String[] uriParts = requestURI.split("/");
            System.out.println("Board ID in filter: " + uriParts[3]);
            if ("boards".equals(uriParts[2])) {
                // Extract the boardId
                String boardId = uriParts[3];
                if (boardService.boardExist(boardId) == false){
                    response.setStatus(HttpStatus.NOT_FOUND.value()); // Set the status to 404 Not Found
                    response.getWriter().write("Board does not exist");
                    return;
                }
                // Check is Board Owner

                try {
                    authorizationService.checkIdThatBelongsToUser(authorizationService.validateToken(request.getHeader("Authorization")),boardId);
                    System.out.println("Checked owner access board");
                }
                // If token expire


                // if not owner of Board
                catch (ResponseStatusException e){
                     try {
                         authorizationService.isUserHaveWriteAccess(authorizationService.validateToken(request.getHeader("Authorization")),boardId);
                         chain.doFilter(request, response);
                     }
                     catch (ResponseStatusException error){
                         response.setStatus(HttpStatus.FORBIDDEN.value());
                         response.getWriter().write(error.getMessage());
                         return;
                     }


//
                    // User Have write Access
                    boolean isUserHaveWriteAccess = authorizationService.isUserHaveWriteAccess(authorizationService.validateToken(request.getHeader("Authorization")),boardId);

                      if(isUserHaveWriteAccess){
                          if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                              UserDetails userDetails = this.jwtUserDetailsService.setCollaboratorWriteAccess(username);
                              if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                                  System.out.println("Validate Token");
                                  UsernamePasswordAuthenticationToken authToken = new
                                          UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                  authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                  SecurityContextHolder.getContext().setAuthentication(authToken);
                              }
                          }
                          chain.doFilter(request, response);
                          return;

                      }
                      else {
                          response.setStatus(HttpStatus.FORBIDDEN.value());
                          response.getWriter().write("You are not have write access");
                      }

                    return;
                }
                System.out.println("Doing Filter chain");
                if (requestTokenHeader != null) {
                    if (requestTokenHeader.startsWith("Bearer ")) {
                        jwtToken = requestTokenHeader.substring(7);
                        try {
                            username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid JWT token");
                        } catch (ExpiredJwtException e) {
                            System.out.println("JWT Token has expired");
                        } catch (MalformedJwtException e) {
                            System.out.println("Malformed JWT token");
                        } catch (SignatureException e) {
                            System.out.println("JWT signature not valid");
                        }
                    } else {
                        System.out.println("JWT Token does not begin with Bearer String");
                    }
                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
                    if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                        System.out.println("Validate Token");
                        UsernamePasswordAuthenticationToken authToken = new
                                UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
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
                return;
            }
        }

        if (requestTokenHeader != null) {
            if (requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
                try {
                    username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid JWT token");
                } catch (ExpiredJwtException e) {
                    System.out.println("JWT Token has expired");
                } catch (MalformedJwtException e) {
                    System.out.println("Malformed JWT token");
                } catch (SignatureException e) {
                    System.out.println("JWT signature not valid");
                }
            } else {
                System.out.println("JWT Token does not begin with Bearer String");
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                System.out.println("Validate Token");
                UsernamePasswordAuthenticationToken authToken = new
                        UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(request, response);
    }
}