package org.tms.tms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tms.tms.dao.Type;

@Repository
public interface TypeRepo extends JpaRepository<Type, Long> {
}
