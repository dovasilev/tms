package org.tms.tms.dto;

import lombok.Data;

@Data
public class SuiteDto {

    private String title;
    private String description;
    private Long projectId;
    private Long parentId;

}
