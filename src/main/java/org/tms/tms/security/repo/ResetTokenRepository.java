package org.tms.tms.security.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.tms.tms.dao.Suite;
import org.tms.tms.security.dao.ResetToken;
import org.tms.tms.security.dao.Users;

import java.util.List;

public interface ResetTokenRepository extends JpaRepository<ResetToken,Long> {

    @Query("select resetToken.objectId from ResetToken resetToken where resetToken.token = :token")
    public Users findByToken(String token);


    @Modifying
    @Query(value = "delete from ResetToken where objectId = :users")
    public void delByObjectId(Users users);

}
