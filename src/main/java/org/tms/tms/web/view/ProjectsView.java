package org.tms.tms.web.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.tms.tms.api.ProjectController;
import org.tms.tms.api.SuiteController;
import org.tms.tms.api.TestController;
import org.tms.tms.dao.Project;
import org.tms.tms.dto.ProjectDto;
import org.vaadin.crudui.crud.impl.GridCrud;

@CssImport("./styles/shared-styles.css")
@Route(value = "Projects", layout = MainPage.class)
@PageTitle("Projects")
public class ProjectsView extends VerticalLayout {

    private ProjectController projectController;
    private SuiteController suiteController;
    private TestController testController;
    private Tabs menu;
    GridCrud<Project> projectGrid;

    public ProjectsView(ProjectController projectController, SuiteController suiteController, TestController testController) {
        this.projectController = projectController;
        this.suiteController = suiteController;
        this.testController = testController;
        setSizeFull();
        add(createProjectButton());
        grid();
    }

    private void grid(){
        projectGrid = new GridCrud<>(Project.class);
        projectGrid.getGrid().getStyle().set("border","none");
        projectGrid.setSizeFull();
        projectGrid.getGrid().setColumns("id","title", "description");
        projectGrid.getGrid().getColumnByKey("id").setWidth("75px").setFlexGrow(0);
        projectGrid.getGrid().addComponentColumn(project -> {
            return createEditButton(project);
        });
        projectGrid.getGrid().addComponentColumn(project -> {
            return createRemoveButton(project);
        });
        projectGrid.getGrid().setColumnReorderingAllowed(false);
        projectGrid.getCrudFormFactory().setUseBeanValidation(true);
        projectGrid.getAddButton().setVisible(false);
        projectGrid.getDeleteButton().setVisible(false);
        projectGrid.getFindAllButton().setVisible(false);
        projectGrid.getUpdateButton().setVisible(false);
        projectGrid.setFindAllOperation(() -> {
            return projectController.getAllProjects();
        });
        projectGrid.getGrid().addItemClickListener(new ComponentEventListener<ItemClickEvent<Project>>() {
            @Override
            public void onComponentEvent(ItemClickEvent<Project> projectItemClickEvent) {
               UI.getCurrent().navigate(ProjectView.class,
                        projectItemClickEvent.getItem().getId().toString());
            }
        });
        add(projectGrid);
    }

    private Button createRemoveButton(Project project) {
        @SuppressWarnings("unchecked")
        Button button = new Button("Удалить", clickEvent -> {
            ConfirmDialog confirmDialog = new ConfirmDialog(
                    "Вы уверены что хотите удалить Проект: "+project.getTitle()+" ?",
                    "",
                    "Удалить",
                    ()->{
                        projectController.deleteProject(project.getId());
                        projectGrid.refreshGrid();
                    });
            confirmDialog.open();
        });
        button.setIcon(VaadinIcon.TRASH.create());
        button.setId("del");
        return button;
    }

    private Button createEditButton(Project project) {
        @SuppressWarnings("unchecked")
        Button button = new Button("Изменить", clickEvent -> {
            ProjectDto projectDto = new ProjectDto();
            Binder<ProjectDto> binder = new Binder<>();
            VerticalLayout content = new VerticalLayout();
            TextField title = new TextField("Title");
            title.setValue(project.getTitle());
            binder.forField(title).asRequired().bind(ProjectDto::getTitle, ProjectDto::setTitle);
            TextField description = new TextField("Description");
            description.setValue(project.getDescription());
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
                    if (binder.writeBeanIfValid(projectDto)) {
                        projectController.updateProject(project.getId(),projectDto);
                        window.close();
                        projectGrid.refreshGrid();
                    }
                }
            });
            window.open();
        });
        button.setIcon(VaadinIcon.EDIT.create());
        return button;
    }

    private Button createProjectButton(){
        Button createProject = new Button();
        createProject.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        createProject.setText("Создать проект");
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
                    if (binder.writeBeanIfValid(projectDto)) {
                        projectController.addProject(projectDto);
                        window.close();
                        projectGrid.refreshGrid();
                    }
                }
            });
            window.open();
        });
        return createProject;
    }




}
