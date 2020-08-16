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

    public Users newUser(SignUpField signUpField) {
        Users user = new Users();
        user.setUserEmail(signUpField.getEmail());
        user.setFullName(signUpField.getFullName());
        user.setPassword(passwordEncoder.encode(signUpField.getPass()));
        user.setRoles(new String[]{"USER"});
        return userRepository.save(user);
    }

    public Users getUserByEmail(String email){
        Users users = userRepository.findByUserEmail(email);
        return users;
    }

}
