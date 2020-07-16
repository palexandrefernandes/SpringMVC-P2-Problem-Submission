package com.hydreath.webprojeto2;

import com.hydreath.webprojeto2.models.User;
import com.hydreath.webprojeto2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(s);
        user.orElseThrow(() -> new UsernameNotFoundException("NotFound"));
        return user.map(MyUserDetails::new).get();
    }
}
