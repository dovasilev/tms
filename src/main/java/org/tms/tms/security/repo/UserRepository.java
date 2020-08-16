package org.tms.tms.security.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tms.tms.security.dao.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

    Users findByUsername(String username);

}
