package org.tms.tms.dto;

import lombok.Data;
import org.tms.tms.dao.Suite;

import java.util.List;

@Data
public class SuiteChild {

    public SuiteChild(Suite suite, List<SuiteChild> childSuites, List<Suite> allChildren) {
        this.suite = suite;
        this.childSuites = childSuites;
        this.allChildren = allChildren;
    }

    Suite suite;
    List<SuiteChild> childSuites;
    List<Suite> allChildren;
}
