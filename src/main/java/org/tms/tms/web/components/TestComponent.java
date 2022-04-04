package org.tms.tms.web.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.tms.tms.api.SuiteController;
import org.tms.tms.api.TestController;
import org.tms.tms.dao.Test;
import org.tms.tms.web.view.ConfirmDialog;

public class TestComponent extends VerticalLayout {

    private final Test test;
    private final SuiteController suiteController;
    private final TestController testController;

    public TestComponent(Test test, SuiteController suiteController, TestController testController) {
        this.test = test;
        this.suiteController = suiteController;
        this.testController = testController;
        HorizontalLayout testDiv = new HorizontalLayout();
        H4 titleTest = new H4(test.getTitle());
        titleTest.getStyle().set("margin-top", "auto");
        testDiv.addAndExpand(titleTest, actionsTest());
        add(testDiv);
    }

    private HorizontalLayout actionsTest() {
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
                            UI.getCurrent().getPage().reload();
                        });
                confirmDialog.open();
            }
        });
        actions.add(editTestButton(), del);
        return actions;
    }

    private Button editTestButton() {
        Button editTest = new Button();
        editTest.setIcon(VaadinIcon.EDIT.create());
        editTest.setText(getTranslation("edit"));
        editTest.addClickListener(buttonClickEvent -> {
            EditTestComponent editTestComponent = new EditTestComponent(test, suiteController, testController, () -> {
                UI.getCurrent().getPage().reload();
            });
        });
        return editTest;
    }

}
