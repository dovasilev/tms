package org.tms.tms.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.tms.tms.dto.ProjectDto;
import org.tms.tms.services.ProjectService;
import java.util.Collection;

@RestController
@RequestMapping("/api")
@Tag(name = "Project", description = "the Project in TMS API")
public class ProjectController {

    private ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(summary = "Get all Projects", description = "Return all Projects", tags = {"Project"})
    @GetMapping(value = "/projects")
    public Collection<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }


    @Operation(summary = "Get Project by Id", description = "Return Project", tags = {"Project"})
    @GetMapping(value = "/project/{projectId}")
    public ProjectDto getProject(@PathVariable Long projectId) {
        return projectService.getProjectById(projectId);
    }

    @Operation(summary = "Add Project", description = "Return created Project", tags = {"Project"})
    @PostMapping(value = "/project", consumes = "application/json")
    public ProjectDto addProject(@Validated @RequestBody ProjectDto project) {
        ProjectDto result = projectService.insertProject(project);
        return result;
    }

    @Operation(summary = "Update Project", description = "Return updated Project", tags = {"Project"})
    @PutMapping(value = "/project/{projectId}", consumes = "application/json")
    public ProjectDto updateProject(@PathVariable Long projectId,
                                    @Validated @RequestBody ProjectDto project) {
        ProjectDto result = projectService.updateProject(projectId, project);
        return result;
    }

    @Operation(summary = "Delete Project", description = "", tags = {"Project"})
    @DeleteMapping(value = "/project/{projectId}")
    public void deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
    }
}
