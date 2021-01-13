package org.tms.tms.mappers;

import org.mapstruct.Mapper;
import org.tms.tms.dao.Project;
import org.tms.tms.dto.ProjectDto;

import java.util.List;

@Mapper
public interface ProjectMapper {

    ProjectDto projectToProjectDto(Project project);

    Project projectDtoToProject(ProjectDto projectDto);

    List<ProjectDto> projectToProjectDto(List<Project> projects);

}
