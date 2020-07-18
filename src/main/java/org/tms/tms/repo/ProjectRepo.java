package org.tms.tms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tms.tms.dao.Project;

@Repository
public interface ProjectRepo extends JpaRepository<Project,Long> {
}
