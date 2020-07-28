package org.tms.tms.web.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.tms.tms.api.ProjectController;
import org.tms.tms.api.SuiteController;
import org.tms.tms.api.TestController;
import org.vaadin.flow.helper.HasUrlParameterMapping;
import org.vaadin.flow.helper.UrlParameter;
import org.vaadin.flow.helper.UrlParameterMapping;

@Route(value = "Project",layout = MainPage.class)
@UrlParameterMapping(":projectId")
@PageTitle("Project")
public class ProjectView extends VerticalLayout implements HasUrlParameterMapping {

    private Long projectId;
    private ProjectController projectController;
    private SuiteController suiteController;
    private TestController testController;

    public ProjectView(ProjectController projectController, SuiteController suiteController, TestController testController) {
        this.projectController = projectController;
        this.suiteController = suiteController;
        this.testController = testController;
    }

    @UrlParameter(name = "projectId")
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
        init();
    }

    public void init(){
        add(new Text(projectId.toString()));
    }


}
