package org.tms.tms.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tms.tms.dto.SignUpField;
import org.tms.tms.security.dao.Users;
import org.tms.tms.security.repo.UserRepository;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Users newUser(SignUpField signUpField){
        Users user = new Users();
        user.setUsername(signUpField.getLogin());
        user.setFullName(signUpField.getFullName());
        user.setPassword(passwordEncoder.encode(signUpField.getPass()));
        user.setEmail(signUpField.getEmail());
        user.setRoles(new String[]{"USER"});
        return userRepository.save(user);
    }

    public Users getUserByUsername(String userName){
        Users users = userRepository.findByUsername(userName);
        return users;
    }
}
