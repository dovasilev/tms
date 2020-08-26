package org.tms.tms.security.service;

import org.springframework.stereotype.Service;
import org.tms.tms.dto.UsersDto;
import org.tms.tms.security.dao.ResetToken;
import org.tms.tms.security.dao.Users;
import org.tms.tms.security.repo.ResetTokenRepository;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class ResetTokenService {

    private ResetTokenRepository resetTokenRepository;
    private UserService userService;


    public ResetTokenService(ResetTokenRepository resetTokenRepository, UserService userService) {
        this.resetTokenRepository = resetTokenRepository;
        this.userService = userService;
    }

    @Transactional
    public synchronized String newToken(String userName) {
        Users users = userService.getUserByEmail(userName);
        if (users != null) {
            ResetToken resetToken = new ResetToken();
            resetToken.setObjectId(users);
            resetToken.setToken(UUID.randomUUID().toString());
            resetTokenRepository.save(resetToken);
            return resetToken.getToken();
        } else return "";
    }

    public synchronized Users getUserByToken(String token) {
        Users users = resetTokenRepository.findByToken(token);
        return users;
    }

    @Transactional
    public synchronized void delTokenByUser(Users users) {
        resetTokenRepository.delByObjectId(users);
    }

    @Transactional
    public synchronized Users resetPassword(String email, String newPass){
        Users users = userService.getUserByEmail(email);
        users.setPassword(newPass);
        UsersDto usersDto = new UsersDto();
        usersDto.setEmail(users.getUserEmail());
        usersDto.setPass(users.getPassword());
        usersDto.setFullName(users.getFullName());
        usersDto.setRoles(users.getRoles());
        Users updateUsers = userService.update(email,usersDto);
        resetTokenRepository.delByObjectId(updateUsers);
        return users;
    }
}
