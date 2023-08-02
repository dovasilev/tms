package org.tms.tms.web.model;

import lombok.Getter;
import org.tms.tms.dao.Test;

import java.util.ArrayList;
import java.util.List;

public class TestWebModel implements ParentWebModel {

    @Getter
    private Test test;

    public TestWebModel(Test test) {
        this.test = test;
    }

    @Override
    public List<ParentWebModel> getChildren() {
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return test.getTitle();
    }
}
