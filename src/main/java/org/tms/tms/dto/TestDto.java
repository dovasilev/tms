package org.tms.tms.dto;

import lombok.Data;
import org.tms.tms.dao.Step;
import org.tms.tms.dao.Steps;

import java.util.List;

@Data
public class TestDto {

    private String title;
    private String status;
    private String description;
    private Long suiteId;
    private Long projectId;
    private Boolean automated;
    private Steps steps;

}
