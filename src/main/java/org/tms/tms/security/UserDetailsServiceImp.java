package org.tms.tms.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImp implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImp.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Users myUser= userRepository.findByUsername(userName);
        if (myUser == null) {
            throw new UsernameNotFoundException("Unknown user: "+userName);
        }
        UserDetails user = User.builder()
                .username(myUser.getUsername())
                .password(myUser.getPassword())
                .roles(myUser.getRoles())
                .build();
        return user;
    }

}
