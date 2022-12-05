package edu.msa.api.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class JwtUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(final String firebaseUid) throws UsernameNotFoundException {
        return new User(firebaseUid, "", new ArrayList<>());
    }
}
