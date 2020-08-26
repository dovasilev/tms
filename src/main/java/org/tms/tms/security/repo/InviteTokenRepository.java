package org.tms.tms.security.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tms.tms.security.dao.InviteToken;

public interface InviteTokenRepository extends JpaRepository<InviteToken, Long> {

    @Query("select inviteToken from InviteToken inviteToken where inviteToken.email=:email")
    InviteToken findByEmail(String email);

    @Query("select inviteToken from InviteToken inviteToken where inviteToken.token=:token")
    InviteToken findByToken(String token);

}
