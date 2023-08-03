package org.tms.tms.web.view;

import com.vaadin.componentfactory.explorer.ExplorerTreeGrid;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.tms.tms.api.SuiteController;
import org.tms.tms.api.TestController;
import org.tms.tms.dao.Suite;
import org.tms.tms.dao.Test;
import org.tms.tms.dto.SuiteDto;
import org.tms.tms.web.ReloadPage;
import org.tms.tms.web.components.CreateTestComponent;
import org.tms.tms.web.components.SuiteChildData;
import org.tms.tms.web.components.SuiteDiv;
import org.tms.tms.web.components.ViewAndEditTestComponent;
import org.tms.tms.web.model.ParentWebModel;
import org.tms.tms.web.model.SuiteWebModel;
import org.tms.tms.web.model.TestWebModel;
import org.vaadin.flow.helper.HasUrlParameterMapping;
import org.vaadin.flow.helper.UrlParameter;
import org.vaadin.flow.helper.UrlParameterMapping;

import java.util.*;

@Route(value = "project", layout = MainPage.class)
@UrlParameterMapping(":projectId")
@PageTitle("Project")
@CssImport("./styles/style.css")
@CssImport("./styles/shared-styles.css")
public class ProjectView extends VerticalLayout implements HasUrlParameterMapping, LocaleChangeObserver {

    private final Button createTestButton;
    private final HorizontalLayout removeAction;
    private final HorizontalLayout projectAction;
    private Button removeButton;
    private Long projectId;
    private final SuiteController suiteController;
    private final TestController testController;
    private List<Suite> allSuites;
    private final VerticalLayout body = new VerticalLayout();
    private List<ParentWebModel> allHierarchy;

    public ProjectView(SuiteController suiteController, TestController testController) {
        this.suiteController = suiteController;
        this.testController = testController;
        setWidthFull();
        setHeightFull();
        Button createSuiteButton = createSuiteButton();
        createTestButton = createTestButton();
        removeAction = new HorizontalLayout();
        projectAction = new HorizontalLayout();
        projectAction.setAlignItems(Alignment.CENTER);
        Button backButton = new Button();
        Icon icon = VaadinIcon.ARROW_BACKWARD.create();
        icon.getStyle().set("padding", "0");
        icon.getStyle().set("width", "var(--lumo-icon-size-l)");
        icon.getStyle().set("height", "var(--lumo-icon-size-l)");
        icon.getStyle().set("margin-top", "1.25em");
        backButton.setIcon(icon);
        backButton.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(ProjectsView.class));
        projectAction.add(backButton);
        HorizontalLayout action = new HorizontalLayout();
        action.add(createSuiteButton, createTestButton, removeAction);
        add(projectAction, action);
        add(body);

    }

    @UrlParameter(name = "projectId")
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
        projectAction.add(new H2(getTranslation("project").concat(projectId.toString())));
        refresh();
    }

    private void refresh() {
        removeAction.removeAll();
        removeButton = new Button();
        removeButton.setVisible(false);
        removeAction.add(removeButton);
        allHierarchy = suiteController.getSuiteHierarchyNew(projectId);
        allSuites = new ArrayList<>(suiteController.getAllSuitesByProject(projectId));
        createTestButton.setVisible(!allHierarchy.isEmpty());
        body.removeAll();
        TreeGrid<ParentWebModel> grid = buildGrid();
        body.add(grid);
    }

    private TreeGrid<ParentWebModel> buildGrid() {
        ExplorerTreeGrid<ParentWebModel> grid = new ExplorerTreeGrid<>();
        SuiteChildData suiteChildData = new SuiteChildData(allHierarchy);
        grid.setItems(suiteChildData.getRootChild(), suiteChildData::getChildren);
        grid.addComponentHierarchyColumn(value -> {
            Icon icon = VaadinIcon.FOLDER_OPEN.create();
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setAlignItems(Alignment.CENTER);
            HorizontalLayout action;
            if (value instanceof TestWebModel) {
                icon = new Icon("lumo", "ordered-list");
                action = actionsTest((TestWebModel) value);
            } else {
                action = actionsSuite((SuiteWebModel) value);
            }
            horizontalLayout.addAndExpand(icon, new Label(value.getName()), action);
            return horizontalLayout;
        });
        grid.setSizeFull();
        grid.setMinHeight("800px");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.expand(suiteChildData.getRootChild());
        grid.asMultiSelect().addSelectionListener(selectionEvent -> {
            Set<ParentWebModel> allSelectedItems = selectionEvent.getAllSelectedItems();
            if (!allSelectedItems.isEmpty()) {
                removeButton.setText(String.format("Удалить %s выбранных элементов", allSelectedItems.size()));
            }
            removeButton.setVisible(!allSelectedItems.isEmpty());
        });
        removeButton.addClickListener(buttonClickEvent -> {
            if (removeButton.isVisible()) {
                Set<ParentWebModel> selectedItems = grid.asMultiSelect().getSelectedItems();
                ConfirmDialog confirmDialog = new ConfirmDialog(
                        String.format("%s %s %s",
                                getTranslation("notificationRemove"),
                                selectedItems.size(),
                                "элементов?"),
                        "",
                        getTranslation("remove"),
                        () -> {
                            deleteItems(selectedItems);
                            refresh();
                        });
                confirmDialog.open();
            }
        });
        return grid;
    }

    private void deleteItems(Set<ParentWebModel> selectedItems) {
        selectedItems.forEach(parentWebModel -> {
            if (parentWebModel instanceof TestWebModel) {
                Test test = ((TestWebModel) parentWebModel).getTest();
                if (!testController.getTestById(test.getId()).getStatusCode().isError()) {
                    testController.deleteTest(test.getId());
                }
            } else {
                Suite suite = ((SuiteWebModel) parentWebModel).getSuite();
                if (!suiteController.getSuiteById(suite.getId()).getStatusCode().isError()) {
                    suiteController.deleteSuite(((SuiteWebModel) parentWebModel).getSuite().getId());
                }
            }
        });
        refresh();
    }

    private HorizontalLayout actionsSuite(SuiteWebModel suite) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(editSuiteButton(suite), deleteSuiteButton(suite));
        return actions;
    }

    private Button deleteSuiteButton(SuiteWebModel suite) {
        Button del = new Button(getTranslation("remove"));
        del.setIcon(VaadinIcon.TRASH.create());
        del.setId("del");
        del.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            ConfirmDialog confirmDialog = new ConfirmDialog(
                    getTranslation("notificationRemove")
                            + getTranslation("suite")
                            + suite.getSuite().getTitle() + " ?",
                    "",
                    getTranslation("remove"),
                    () -> {
                        suiteController.deleteSuite(suite.getSuite().getId());
                        refresh();
                    });
            confirmDialog.open();
        });
        return del;
    }

    private Button editSuiteButton(SuiteWebModel suite) {
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
            suiteDto.setProjectId(suite.getSuite().getProjectId().getId());
            title.setValue(suite.getSuite().getTitle());
            description.setValue(suite.getSuite().getDescription());
            Select<SuiteDiv> select = new Select<>();
            select.setLabel("Parent suite");
            Collection<SuiteDiv> suiteList = new LinkedList<>();
            allSuites.forEach(x -> {
                suiteList.add(new SuiteDiv(x));
            });
            suiteList.removeIf(suiteDiv -> suiteDiv.getId().equals(suite.getSuite().getId()));
            suite.getChildrenSuites().forEach(x -> {
                suiteList.removeIf(suiteDiv -> suiteDiv.getId().equals(x.getSuite().getId()));
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
            cancel.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent1 -> window.close());
            save.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent12 -> {
                binder.validate();
                if (binder.writeBeanIfValid(suiteDto)) {
                    if (select.getValue() != null) {
                        suiteDto.setParentId(select.getValue().getId());
                    }
                    suiteController.updateSuite(suite.getSuite().getId(), suiteDto);
                    window.close();
                    refresh();
                }
            });
            window.open();
        });
        return createSuite;
    }

    private HorizontalLayout actionsTest(TestWebModel test) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(viewTestButton(test), editTestButton(test), deleteTestButton(test));
        return actions;
    }

    private Button deleteTestButton(TestWebModel test) {
        Button del = new Button(getTranslation("remove"));
        del.setIcon(VaadinIcon.TRASH.create());
        del.setId("del");
        del.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            ConfirmDialog confirmDialog = new ConfirmDialog(
                    getTranslation("notificationRemove")
                            + getTranslation("test")
                            + test.getName() + " ?",
                    "",
                    getTranslation("remove"),
                    () -> {
                        testController.deleteTest(test.getTest().getId());
                        refresh();
                    });
            confirmDialog.open();
        });
        return del;
    }

    private Button editTestButton(TestWebModel test) {
        Button editTest = new Button();
        editTest.setIcon(VaadinIcon.EDIT.create());
        editTest.setText(getTranslation("edit"));
        editTest.addClickListener(buttonClickEvent ->
                new ViewAndEditTestComponent(test.getTest(), suiteController, testController, this::refresh, true));
        return editTest;
    }

    private Button viewTestButton(TestWebModel test) {
        Button editTest = new Button();
        editTest.setIcon(VaadinIcon.VIEWPORT.create());
        editTest.setText(getTranslation("view"));
        editTest.addClickListener(buttonClickEvent ->
                new ViewAndEditTestComponent(test.getTest(), suiteController, testController, this::refresh, false));
        return editTest;
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
            cancel.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent1 -> window.close());
            save.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent12 -> {
                binder.validate();
                if (binder.writeBeanIfValid(suiteDto)) {
                    if (select.getValue() != null) {
                        suiteDto.setParentId(select.getValue().getId());
                    }
                    suiteController.addSuite(suiteDto);
                    window.close();
                    refresh();
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
                    this::refresh);
        });
        return createTest;
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        ReloadPage.reloadPage(localeChangeEvent, this.getClass());
    }
}
