package org.tms.tms.web.model;

import java.util.List;

public interface ParentWebModel {

    String getName();

    List<ParentWebModel> getChildren();

}
