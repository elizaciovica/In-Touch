package edu.msa.api.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    public JwtRequestFilter(final JwtUserDetailsService jwtUserDetailsService) {
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            final String token = authorizationHeader.substring("Bearer ".length());

            try {
                setAuthenticationPrincipalInTheSecurityContextHolder(token);
                filterChain.doFilter(request, response);
            } catch (FirebaseAuthException exception) {
                response.setHeader("error", exception.getMessage());
                response.sendError(FORBIDDEN.value());
            }
        } else {
            response.setHeader("error", "No JWT token provided.");
            response.sendError(BAD_REQUEST.value());

            filterChain.doFilter(request, response);
        }
    }

    private void setAuthenticationPrincipalInTheSecurityContextHolder(final String token) throws FirebaseAuthException {
        final FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token);
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(firebaseToken.getUid());
        final UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
