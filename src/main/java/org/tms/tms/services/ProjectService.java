package org.tms.tms.services;

import org.springframework.stereotype.Service;
import org.tms.tms.dao.Project;
import org.tms.tms.dto.ProjectDto;
import org.tms.tms.repo.ProjectRepo;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProjectService {

    private ProjectRepo projectRepo;

    public ProjectService(ProjectRepo projectRepo) {
        this.projectRepo = projectRepo;
    }

    public List<Project> getAllProjects(){
        return projectRepo.findAll();
    }

    public Project getProjectById(Long id) throws NoSuchElementException {
        return projectRepo.findById(id).get();
    }


    @Transactional
    public synchronized Project insertProject(ProjectDto projectDto){
        Project project = new Project();
        project.setTitle(projectDto.getTitle());
        project.setDescription(projectDto.getDescription());
        return projectRepo.save(project);
    }

    @Transactional
    public synchronized Project updateProject(Long id, ProjectDto projectDto){
        Project project = getProjectById(id);
        project.setTitle(projectDto.getTitle());
        project.setDescription(projectDto.getDescription());
        return projectRepo.save(project);
    }

    @Transactional
    public synchronized void deleteProject(Long id){
        Project project = getProjectById(id);
        projectRepo.delete(project);
    }

}
