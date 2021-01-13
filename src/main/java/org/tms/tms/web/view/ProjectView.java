package org.tms.tms.web.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.Data;
import org.tms.tms.api.ProjectController;
import org.tms.tms.api.SuiteController;
import org.tms.tms.api.TestController;
import org.tms.tms.dao.Project;
import org.tms.tms.dao.Suite;
import org.tms.tms.dao.Test;
import org.tms.tms.dto.SuiteChild;
import org.tms.tms.dto.SuiteDto;
import org.tms.tms.web.ReloadPage;
import org.tms.tms.web.components.CreateTestComponent;
import org.tms.tms.web.components.EditTestComponent;
import org.vaadin.flow.helper.HasUrlParameterMapping;
import org.vaadin.flow.helper.UrlParameter;
import org.vaadin.flow.helper.UrlParameterMapping;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "project", layout = MainPage.class)
@UrlParameterMapping(":projectId")
@PageTitle("Project")
@CssImport("./styles/style.css")
@CssImport("./styles/shared-styles.css")
public class ProjectView extends VerticalLayout implements HasUrlParameterMapping, LocaleChangeObserver {

    private Long projectId;
    private ProjectController projectController;
    private SuiteController suiteController;
    private TestController testController;
    private VerticalLayout verticalLayout;
    private List<SuiteChild> allHierarchySuites;
    private List<Suite> allSuites;

    public ProjectView(ProjectController projectController, SuiteController suiteController, TestController testController) {
        this.projectController = projectController;
        this.suiteController = suiteController;
        this.testController = testController;
    }

    @UrlParameter(name = "projectId")
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
        setWidthFull();
        init();
    }

    private void init() {
        removeAll();
        verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        allHierarchySuites = suiteController.getSuiteHierarchy(projectId);
        allSuites = suiteController.getAllSuitesByProject(projectId).stream().collect(Collectors.toList());
        HorizontalLayout creater = new HorizontalLayout();
        if (allHierarchySuites.isEmpty()) {
            creater.add(createSuiteButton());
        } else {
            creater.add(createSuiteButton(), createTestButton());
        }
        add(creater, verticalLayout);
        List<Component> details = new LinkedList<>();
        allHierarchySuites.forEach(x -> {
            Details detail = new Details();
            detail.getElement().getStyle().set("with", "100%");
            detail.setSummary(setSummary(x));
            detail.setContent(childs(x.getChildSuites(), x));
            detail.setOpened(false);
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidthFull();
            horizontalLayout.add(detail);
            details.add(horizontalLayout);
        });
        if (!details.isEmpty())
            details.forEach(x -> verticalLayout.add(x));
    }

    private Component childs(Collection<SuiteChild> allSuites, SuiteChild parentSuite) {
        List<Component> details = new LinkedList<>();
        Collection<Test> tests = parentSuite.getTests();
        VerticalLayout testsLayout = new VerticalLayout();
        testsLayout.setSpacing(false);
        testsLayout.setMargin(false);
        tests.forEach(test -> {
            HorizontalLayout testDiv = new HorizontalLayout();
            H4 titleTest = new H4(test.getTitle());
            titleTest.getStyle().set("margin-top", "auto");
            testDiv.addAndExpand(titleTest, actionsTest(test));
            testsLayout.add(testDiv);
        });
        details.add(testsLayout);
        allSuites.forEach(x -> {
            Details detail = new Details();
            detail.getElement().getStyle().set("with", "100%");
            detail.setSummary(setSummary(x));
            detail.setContent(childs(x.getChildSuites(), x));
            detail.getElement().getStyle().set("border-left", "1px solid #e5e5e5");
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidthFull();
            horizontalLayout.add(detail);
            details.add(horizontalLayout);
        });
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        if (!details.isEmpty())
            details.forEach(x -> verticalLayout.add(x));
        return verticalLayout;
    }

    private Component setSummary(SuiteChild suite) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setPadding(true);
        H3 h3 = new H3(suite.getSuite().getTitle());
        horizontalLayout.addAndExpand(h3, actionsSuite(suite));
        return horizontalLayout;
    }

    private Button createSuiteButton() {
        Button createSuite = new Button();
        createSuite.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        createSuite.setText(getTranslation("createSuite"));
        createSuite.addClickListener(buttonClickEvent -> {
            SuiteDto suiteDto = new SuiteDto();
            Binder<SuiteDto> binder = new Binder<>();
            VerticalLayout content = new VerticalLayout();
            TextField title = new TextField("Title");
            binder.forField(title).asRequired().bind(SuiteDto::getTitle, SuiteDto::setTitle);
            TextField description = new TextField("Description");
            binder.forField(description).bind(SuiteDto::getDescription, SuiteDto::setDescription);
            suiteDto.setProjectId(projectId);
            Select<SuiteDiv> select = new Select<>();
            select.setLabel("Parent suite");
            List<SuiteDiv> suiteList = new LinkedList<>();
            suiteController.getAllSuitesByProject(projectId)
                    .forEach(x -> {
                        suiteList.add(new SuiteDiv(x));
                    });
            select.setItemLabelGenerator(SuiteDiv::getAllTitle);
            select.setItems(suiteList);
            FormLayout gridLayout = new FormLayout();
            gridLayout.add(title, description, select);
            Button cancel = new Button(getTranslation("cancel"));
            Button save = new Button(getTranslation("save"));
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
                    if (binder.writeBeanIfValid(suiteDto)) {
                        if (select.getValue() != null) {
                            suiteDto.setParentId(select.getValue().getId());
                        }
                        suiteController.addSuite(suiteDto);
                        window.close();
                        init();
                    }
                }
            });
            window.open();
        });
        return createSuite;
    }

    private Button createTestButton() {
        Button createTest = new Button();
        createTest.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        createTest.setText(getTranslation("createTest"));
        createTest.addClickListener(buttonClickEvent -> {
            CreateTestComponent createTestComponent = new CreateTestComponent(projectId, suiteController, testController,
                    () -> {
                        init();
                    });
        });
        return createTest;
    }


    private HorizontalLayout actionsSuite(SuiteChild suite) {
        HorizontalLayout actions = new HorizontalLayout();
        Button del = new Button(getTranslation("remove"));
        del.setIcon(VaadinIcon.TRASH.create());
        del.setId("del");
        del.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                ConfirmDialog confirmDialog = new ConfirmDialog(
                        getTranslation("notificationRemove")
                                + getTranslation("suite")
                                + suite.getSuite().getTitle() + " ?",
                        "",
                        getTranslation("remove"),
                        () -> {
                            suiteController.deleteSuite(suite.getSuite().getId());
                            init();
                        });
                confirmDialog.open();
            }
        });
        actions.add(editSuiteButton(suite), del);
        return actions;
    }

    private Button editSuiteButton(SuiteChild suite) {
        Button createSuite = new Button();
        createSuite.setIcon(VaadinIcon.EDIT.create());
        createSuite.setText(getTranslation("edit"));
        createSuite.addClickListener(buttonClickEvent -> {
            SuiteDto suiteDto = new SuiteDto();
            Binder<SuiteDto> binder = new Binder<>();
            VerticalLayout content = new VerticalLayout();
            TextField title = new TextField("Title");
            binder.forField(title).asRequired().bind(SuiteDto::getTitle, SuiteDto::setTitle);
            TextField description = new TextField("Description");
            binder.forField(description).bind(SuiteDto::getDescription, SuiteDto::setDescription);
            suiteDto.setProjectId(projectId);
            title.setValue(suite.getSuite().getTitle());
            description.setValue(suite.getSuite().getDescription());
            Select<SuiteDiv> select = new Select<>();
            select.setLabel("Parent suite");
            Collection<SuiteDiv> suiteList = new LinkedList<>();
            allSuites.forEach(x -> {
                suiteList.add(new SuiteDiv(x));
            });
            suiteList.removeIf(suiteDiv -> suiteDiv.getId().equals(suite.getSuite().getId()));
            suite.getAllChildren().forEach(x -> {
                suiteList.removeIf(suiteDiv -> suiteDiv.getId().equals(x.getId()));
            });
            select.setItemLabelGenerator(SuiteDiv::getAllTitle);
            select.setItems(suiteList);
            if (suite.getSuite().getParentId() != null) {
                select.setValue(suiteList.stream().filter(x -> x.getId().equals(suite.getSuite().getParentId().getId())).findFirst().get());
            }
            FormLayout gridLayout = new FormLayout();
            gridLayout.add(title, description, select);
            Button cancel = new Button(getTranslation("cancel"));
            Button save = new Button(getTranslation("save"));
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
                    if (binder.writeBeanIfValid(suiteDto)) {
                        if (select.getValue() != null) {
                            suiteDto.setParentId(select.getValue().getId());
                        }
                        suiteController.updateSuite(suite.getSuite().getId(), suiteDto);
                        init();
                        window.close();
                    }
                }
            });
            window.open();
        });
        return createSuite;
    }


    private HorizontalLayout actionsTest(Test test) {
        HorizontalLayout actions = new HorizontalLayout();
        Button del = new Button(getTranslation("remove"));
        del.setIcon(VaadinIcon.TRASH.create());
        del.setId("del");
        del.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                ConfirmDialog confirmDialog = new ConfirmDialog(
                        getTranslation("notificationRemove")
                                + getTranslation("test")
                                + test.getTitle() + " ?",
                        "",
                        getTranslation("remove"),
                        () -> {
                            testController.deleteTest(test.getId());
                            init();
                        });
                confirmDialog.open();
            }
        });
        actions.add(editTestButton(test), del);
        return actions;
    }

    private Button editTestButton(Test test) {
        Button editTest = new Button();
        editTest.setIcon(VaadinIcon.EDIT.create());
        editTest.setText(getTranslation("edit"));
        editTest.addClickListener(buttonClickEvent -> {
            EditTestComponent editTestComponent = new EditTestComponent(test, suiteController, testController, () -> {
                init();
            });
        });
        return editTest;
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        ReloadPage.reloadPage(localeChangeEvent, this.getClass());
    }


    @Data
    public static class SuiteDiv {

        private String allTitle;
        private Suite suite;

        public SuiteDiv(Suite suite) {
            this.suite = suite;
            id = suite.getId();
            title = suite.getTitle();
            description = suite.getDescription();
            projectId = suite.getProjectId();
            parentId = suite.getParentId();
            allTitle = "";
            if (parentId != null) {
                setTitles(parentId);
                char uniChar = '\u21b3';
                allTitle += uniChar + " " + title;

            } else {
                allTitle = title;
            }

        }

        private Long id;

        private String title;

        private String description;

        private Project projectId;

        private Suite parentId;

        private void setTitles(Suite parentSuite) {
            if (parentSuite != null) {
                this.allTitle += " | ";
                setTitles(parentSuite.getParentId());
            }
        }

    }

}
