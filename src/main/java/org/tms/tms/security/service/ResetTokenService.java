package org.tms.tms.security.service;

import org.springframework.stereotype.Service;
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
    public synchronized String newToken (String userName)
    {

        Users users = userService.getUserByUsername(userName);
        ResetToken resetToken = new ResetToken();
        resetToken.setObjectId(users);
        resetToken.setToken(UUID.randomUUID().toString());

        resetTokenRepository.save(resetToken);

        return resetToken.getToken();
    }
}
