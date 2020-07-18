package org.tms.tms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tms.tms.dao.Suite;

@Repository
public interface SuiteRepo extends JpaRepository<Suite, Long> {
}
