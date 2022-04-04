package org.tms.tms.web.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
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
import org.tms.tms.api.SuiteController;
import org.tms.tms.api.TestController;
import org.tms.tms.dao.Suite;
import org.tms.tms.dto.SuiteChild;
import org.tms.tms.dto.SuiteDto;
import org.tms.tms.web.view.ConfirmDialog;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SuiteComponent extends VerticalLayout {

    private final SuiteChild suite;
    private final SuiteController suiteController;
    private final List<Suite> allSuites;

    public SuiteComponent(SuiteChild suite, List<Suite> allSuites, SuiteController suiteController, TestController testController) {
        this.suite = suite;
        this.suiteController = suiteController;
        this.allSuites = allSuites;
        Details detail = new Details();
        detail.getElement().getStyle().set("with", "100%");
        detail.setSummary(setSummary());
        VerticalLayout verticalLayout = new VerticalLayout();
        suite.getTests().forEach(test -> verticalLayout.add(new TestComponent(test, suiteController, testController)));
        suite.getChildSuites().forEach(suiteChild ->
                verticalLayout.add(
                        new SuiteComponent(
                                suiteChild,
                                allSuites,
                                suiteController,
                                testController)));
        detail.setContent(verticalLayout);
        detail.setOpened(false);
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.add(detail);
        add(horizontalLayout);
    }

    private Component setSummary() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setPadding(true);
        H3 h3 = new H3(suite.getSuite().getTitle());
        horizontalLayout.addAndExpand(h3, actionsSuite());
        return horizontalLayout;
    }

    private HorizontalLayout actionsSuite() {
        HorizontalLayout actions = new HorizontalLayout();
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
                        UI.getCurrent().getPage().reload();
                    });
            confirmDialog.open();
        });
        actions.add(editSuiteButton(), del);
        return actions;
    }

    private Button editSuiteButton() {
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
            cancel.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent1 -> window.close());
            save.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent12 -> {
                binder.validate();
                if (binder.writeBeanIfValid(suiteDto)) {
                    if (select.getValue() != null) {
                        suiteDto.setParentId(select.getValue().getId());
                    }
                    suiteController.updateSuite(suite.getSuite().getId(), suiteDto);
                    window.close();
                    UI.getCurrent().getPage().reload();
                }
            });
            window.open();
        });
        return createSuite;
    }
}
