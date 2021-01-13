package org.tms.tms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProjectDto {

    private Long id;
    private String title;
    private String description;

}
