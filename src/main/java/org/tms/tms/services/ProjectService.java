package org.tms.tms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tms.tms.dto.ProjectDto;
import org.tms.tms.mappers.ProjectMapper;
import org.tms.tms.repo.ProjectRepo;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProjectService {

    private ProjectRepo projectRepo;

    @Autowired
    private ProjectMapper projectMapper;

    public ProjectService(ProjectRepo projectRepo) {
        this.projectRepo = projectRepo;
    }

    public List<ProjectDto> getAllProjects() {
        return projectMapper.projectToProjectDto(projectRepo.findAll());
    }

    public ProjectDto getProjectById(Long id) throws NoSuchElementException {
        return projectMapper.projectToProjectDto(projectRepo.findById(id).get());
    }


    @Transactional
    public synchronized ProjectDto insertProject(ProjectDto projectDto) {
        return projectMapper.projectToProjectDto(
                projectRepo.save(projectMapper.projectDtoToProject(projectDto)));
    }

    @Transactional
    public synchronized ProjectDto updateProject(Long id, ProjectDto projectDto) {
        ProjectDto project = getProjectById(id);
        project.setTitle(projectDto.getTitle());
        project.setDescription(projectDto.getDescription());
        return projectMapper.projectToProjectDto(
                projectRepo.save(projectMapper.projectDtoToProject(project))
        );
    }

    @Transactional
    public synchronized void deleteProject(Long id) {
        ProjectDto project = getProjectById(id);
        projectRepo.delete(projectMapper.projectDtoToProject(project));
    }

}
