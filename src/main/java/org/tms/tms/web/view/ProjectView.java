package org.tms.tms.web.view;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.tms.tms.api.ProjectController;
import org.tms.tms.api.SuiteController;
import org.tms.tms.api.TestController;
import org.tms.tms.dao.Suite;
import org.tms.tms.dao.Test;
import org.vaadin.flow.helper.HasUrlParameterMapping;
import org.vaadin.flow.helper.UrlParameter;
import org.vaadin.flow.helper.UrlParameterMapping;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "Project",layout = MainPage.class)
@UrlParameterMapping(":projectId")
@PageTitle("Project")
@CssImport("./styles/style.css")
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


    private void init(){
        removeAll();
        setWidthFull();
        Collection<Suite> suites = suiteController.getAllSuitesByProject(projectId);
        Collection<Suite> parent = suites.stream().filter(x->x.getParentId()==null).collect(Collectors.toList());
        List<Component> details = new LinkedList<>();
        parent.forEach(x -> {
            Details detail = new Details();
            detail.getElement().getStyle().set("with","100%");
            detail.setSummary(setSummary(x));
            detail.setContent(childs(suites,x));
            detail.setOpened(false);
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidthFull();
            horizontalLayout.add(detail);
            details.add(horizontalLayout);
        });
        if (!details.isEmpty())
            details.forEach(x->add(x));
    }

    private Component childs(Collection<Suite> allSuites, Suite parentSuite){
        Collection<Suite> suites = allSuites.stream()
                .filter(x-> x.getParentId()!=null && x.getParentId().equals(parentSuite))
                .collect(Collectors.toList());
        List<Component> details = new LinkedList<>();
        ListBox listBox = new ListBox();
        Collection<Test> tests = testController.getTestInSuite(parentSuite.getId());
        tests.forEach(test->{
            HorizontalLayout testDiv = new HorizontalLayout();
            testDiv.add(new H4(test.getTitle()));
            listBox.add(testDiv);
        });
        if (!tests.isEmpty()) {
            details.add(listBox);
        }
        if (suites.isEmpty()){
            details.add(new Text("Нет дочерних сьютов"));
        }
        suites.forEach(x -> {
            Details detail = new Details();
            detail.getElement().getStyle().set("with","100%");
            detail.setSummary(setSummary(x));
            detail.setContent(childs(allSuites,x));
            detail.getElement().getStyle().set("border-left","1px solid #e5e5e5");
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidthFull();
            horizontalLayout.add(detail);
            details.add(horizontalLayout);
        });
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        if (!details.isEmpty())
            details.forEach(x->verticalLayout.add(x));
        return verticalLayout;
    }

    private Component setSummary(Suite suite){
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        H3 h3 = new H3(suite.getTitle());
        horizontalLayout.add(h3,actionsSuite(suite));
        return horizontalLayout;
    }

    private HorizontalLayout actionsSuite(Suite suite){
        HorizontalLayout actions = new HorizontalLayout();
        Button del = new Button("Удалить");
        del.setIcon(VaadinIcon.TRASH.create());
        del.getElement().setAttribute("theme", "error tertiary");
        del.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                suiteController.deleteSuite(suite.getId());
                init();
            }
        });
        del.setId("open");
        actions.add(del);
        return actions;
    }

}
