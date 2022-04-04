package org.tms.tms.web.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
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
import org.tms.tms.web.components.SuiteComponent;
import org.tms.tms.web.components.SuiteDiv;
import org.vaadin.flow.helper.HasUrlParameterMapping;
import org.vaadin.flow.helper.UrlParameter;
import org.vaadin.flow.helper.UrlParameterMapping;

import java.util.ArrayList;
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
    private final SuiteController suiteController;
    private final TestController testController;
    private List<Suite> allSuites;
    private final VerticalLayout body = new VerticalLayout();
    private final HorizontalLayout header = new HorizontalLayout();

    public ProjectView(SuiteController suiteController, TestController testController) {
        this.suiteController = suiteController;
        this.testController = testController;
        setWidthFull();
        add(header);
        add(body);
    }

    @UrlParameter(name = "projectId")
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
        init();
    }

    private void init() {
        header.removeAll();
        List<SuiteChild> allHierarchySuites = suiteController.getSuiteHierarchy(projectId);
        allSuites = new ArrayList<>(suiteController.getAllSuitesByProject(projectId));
        Button createSuiteButton = createSuiteButton();
        Button createTestButton = createTestButton();
        if (allHierarchySuites.isEmpty()) {
            header.add(createSuiteButton);
        } else {
            header.add(createSuiteButton, createTestButton);
        }
        body.removeAll();
        body.setSpacing(false);
        body.setMargin(false);
        body.setPadding(false);
        allHierarchySuites.forEach(suiteChild -> body.add(new SuiteComponent(suiteChild, allSuites, suiteController, testController)));
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
                    init();
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
                    this::init);
        });
        return createTest;
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        ReloadPage.reloadPage(localeChangeEvent, this.getClass());
    }

}
