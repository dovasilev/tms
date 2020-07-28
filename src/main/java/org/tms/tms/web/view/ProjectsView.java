package org.tms.tms.web.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.springframework.objenesis.SpringObjenesis;
import org.tms.tms.api.ProjectController;
import org.tms.tms.dao.Project;
import org.tms.tms.dto.ProjectDto;

import java.util.Arrays;
import java.util.Collection;

@Route(value = "projects", layout = MainPage.class)
@PageTitle("Projects")
public class ProjectsView extends VerticalLayout {

    private ProjectController projectController;
    private Tabs menu;

    public ProjectsView(ProjectController projectController) {
        this.projectController = projectController;
        menu = new Tabs();
        menu.setOrientation(Tabs.Orientation.VERTICAL);
        menu.add(createTabs());
        menu.setWidthFull();
        setSizeFull();
        Button createProject = new Button();
        createProject.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        createProject.setText("Создать проект");
        add(createProject);
        add(new VerticalLayout(menu));
        createProject.addClickListener(buttonClickEvent -> {
            ProjectDto projectDto = new ProjectDto();
            Binder<ProjectDto> binder = new Binder<>();
            VerticalLayout content = new VerticalLayout();
            TextField title = new TextField("Title");
            binder.forField(title).asRequired().bind(ProjectDto::getTitle, ProjectDto::setTitle);
            TextField description = new TextField("Description");
            binder.forField(description).bind(ProjectDto::getDescription, ProjectDto::setDescription);
            FormLayout gridLayout = new FormLayout();
            gridLayout.add(title, description);
            Button cancel = new Button("Отмена");
            Button save = new Button("Сохранить");
            save.setIcon(VaadinIcon.PENCIL.create());
            content.add(gridLayout);
            HorizontalLayout hor = new HorizontalLayout();
            hor.add(cancel, save);
            content.add(hor);
            content.setAlignSelf(FlexComponent.Alignment.END, hor);
            Dialog window = new Dialog();
            window.add(content);
            window.setModal(true);
            cancel.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
                @Override
                public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                    window.close();
                }
            });
            save.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
                @Override
                public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                    binder.validate();
                    binder.writeBeanIfValid(projectDto);
                    projectController.addProject(projectDto);
                    window.close();
                    menu.removeAll();
                    menu.add(createTabs());
                }
            });
            window.open();
        });
    }

    public Tab[] createTabs() {
        Collection<Project> projects = projectController.getAllProjects();
        RouterLink[] links =
                projects.stream()
                        .map(x -> new RouterLink(x.getTitle(), ProjectView.class, x.getId().toString()))
                        .toArray(size -> new RouterLink[size]);
        return Arrays.stream(links).map(ProjectsView::createTab).toArray(Tab[]::new);

    }

    private static Tab createTab(Component content) {
        final Tab tab = new Tab();
        tab.add(content);
        return tab;
    }


}
