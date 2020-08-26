package org.tms.tms.security.service;

import org.springframework.stereotype.Service;
import org.tms.tms.security.dao.InviteToken;
import org.tms.tms.security.dao.Users;
import org.tms.tms.security.repo.InviteTokenRepository;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class InviteTokenService {

    private InviteTokenRepository inviteTokenRepository;
    private UserService userService;

    public InviteTokenService(InviteTokenRepository inviteTokenRepository, UserService userService) {
        this.inviteTokenRepository = inviteTokenRepository;
        this.userService = userService;
    }


    @Transactional
    public synchronized void newInvite(String email) {
        Users users = userService.getUserByEmail(email);
        if (users == null) {
            InviteToken inviteToken = new InviteToken();
            inviteToken.setEmail(email);
            inviteToken.setToken(UUID.randomUUID().toString());
            inviteTokenRepository.save(inviteToken);
        } else new Exception("User with this email already exists");
    }

    public synchronized String getEmailByToken(String token) {
        InviteToken inviteToken = getToken(token);
        if (inviteToken!=null)
            return inviteToken.getEmail();
        else return null;
    }

    private synchronized InviteToken getToken(String token) {
        InviteToken inviteToken = inviteTokenRepository.findByToken(token);
        return inviteToken;
    }


    @Transactional
    public synchronized void delInviteByEmail(String email) {
        InviteToken inviteToken = inviteTokenRepository.findByEmail(email);
        inviteTokenRepository.delete(inviteToken);
    }


}
