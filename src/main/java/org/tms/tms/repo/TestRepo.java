package org.tms.tms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tms.tms.dao.Test;

@Repository
public interface TestRepo extends JpaRepository<Test, Long> {

}
