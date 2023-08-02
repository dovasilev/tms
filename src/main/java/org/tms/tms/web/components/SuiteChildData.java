package org.tms.tms.web.components;

import lombok.Getter;
import org.tms.tms.web.model.ParentWebModel;
import org.tms.tms.web.model.SuiteWebModel;

import java.util.ArrayList;
import java.util.List;

public class SuiteChildData {

    @Getter
    private final List<ParentWebModel> rootChild;

    public SuiteChildData(List<ParentWebModel> rootChild) {
        this.rootChild = new ArrayList<>();
        rootChild.forEach(parentWebModel -> {
            if (parentWebModel instanceof SuiteWebModel) {
                this.rootChild.add(parentWebModel);
            }
        });
    }

    public List<ParentWebModel> getChildren(ParentWebModel parent) {
        if (parent != null) {
            return parent.getChildren();
        }
        else {
            return new ArrayList<>();
        }
    }
}
