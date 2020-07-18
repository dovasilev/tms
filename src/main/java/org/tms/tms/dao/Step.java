package org.tms.tms.dao;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class Step {

    private Integer number;
    private String action;
    private String expectedResult;
    private String actualResult;

}
