package org.tms.tms.security.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tms.tms.security.dao.ResetToken;

public interface ResetTokenRepository extends JpaRepository<ResetToken,Long> {
}
