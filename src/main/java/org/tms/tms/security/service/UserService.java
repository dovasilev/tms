package org.tms.tms.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tms.tms.dto.UsersDto;
import org.tms.tms.security.dao.Users;
import org.tms.tms.security.repo.ResetTokenRepository;
import org.tms.tms.security.repo.UserRepository;
import org.tms.tms.web.view.ResetPasswordView;

import javax.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public synchronized Users newUser(UsersDto usersDto) {
        Users user = new Users();
        user.setUserEmail(usersDto.getEmail());
        user.setFullName(usersDto.getFullName());
        user.setPassword(passwordEncoder.encode(usersDto.getPass()));
        user.setRoles(new String[]{"USER"});
        return userRepository.save(user);
    }

    @Transactional
    public synchronized Users getUserByEmail(String email){
        Users users = userRepository.findByUserEmail(email);
        return users;
    }

    @Transactional
    public synchronized Users update(String email, UsersDto usersDto){
        Users users = getUserByEmail(email);
        users.setFullName(usersDto.getFullName());
        users.setPassword(passwordEncoder.encode(usersDto.getPass()));
        users.setRoles(usersDto.getRoles());
        return userRepository.save(users);
    }

}
