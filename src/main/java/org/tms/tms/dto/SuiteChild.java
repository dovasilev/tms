package org.tms.tms.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.tms.tms.dao.Suite;
import org.tms.tms.dao.Test;
import org.tms.tms.services.TestService;

import java.util.Collection;
import java.util.List;

@Data
public class SuiteChild {

    public SuiteChild(Suite suite, List<SuiteChild> childSuites, List<Suite> allChildren, Collection<Test> tests) {
        this.suite = suite;
        this.childSuites = childSuites;
        this.allChildren = allChildren;
        this.tests = tests;
    }

    Suite suite;
    List<SuiteChild> childSuites;
    List<Suite> allChildren;
    Collection<Test> tests;
}
