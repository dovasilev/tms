package org.tms.tms.dao;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Step {

    private Integer number;
    private String action;
    private String expectedResult;

}
