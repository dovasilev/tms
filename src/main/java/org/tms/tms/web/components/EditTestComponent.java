package org.tms.tms.web.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.tms.tms.api.SuiteController;
import org.tms.tms.api.TestController;
import org.tms.tms.dao.Step;
import org.tms.tms.dao.Steps;
import org.tms.tms.dao.Test;
import org.tms.tms.dto.TestDto;
import org.tms.tms.web.converters.ConvertSuiteDivToSuiteDto;
import org.tms.tms.web.view.ProjectView;
import org.vaadin.maxime.MarkdownArea;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@CssImport("./styles/shared-styles.css")
public class EditTestComponent extends Dialog {

    private VerticalLayout verticalStep = new VerticalLayout();
    private Binder<TestDto> binder;
    private TestDto testDto;
    int nextStep = 1;
    private HorizontalLayout action;
    Select<ProjectView.SuiteDiv> select;
    TestController testController;
    SuiteController suiteController;
    private HorizontalLayout hor;
    private Map<Step, Binder> stepBinderMap;
    Runnable actionClose;

    private Test test;

    /**
     * @param test
     * @param suiteController
     * @param testController
     * @param actionClose
     */
    public EditTestComponent(Test test, SuiteController suiteController, TestController testController, Runnable actionClose) {
        this.test = test;
        this.suiteController = suiteController;
        this.testController = testController;
        this.actionClose = actionClose;
        this.stepBinderMap = new LinkedHashMap<>();
        setSizeFull();
        setAriaLabel(getTranslation("editTest") + " " + test.getTitle());
        getElement().getStyle().set("scrolling", "auto");
        testDto = new TestDto();
        testDto.setTitle(test.getTitle());
        testDto.setDescription(test.getDescription());
        testDto.setStatus(test.getStatus());
        testDto.setSuiteId(test.getSuiteId().getId());
        testDto.setProjectId(test.getProjectId().getId());
        testDto.setAutomated(test.getAutomated());
        testDto.setSteps(test.getSteps());
        binder = new Binder<>();
        VerticalLayout content = new VerticalLayout();
        TextField title = new TextField("Title");
        title.setRequired(true);
        binder.forField(title)
                .withValidator(new StringLengthValidator(getTranslation("fill"), 1, 200))
                .bind(TestDto::getTitle, TestDto::setTitle);
        TextField description = new TextField("Description");
        binder.forField(description).bind(TestDto::getDescription, TestDto::setDescription);
        testDto.setProjectId(test.getProjectId().getId());
        select = new Select<>();
        select.setRequiredIndicatorVisible(true);
        select.setLabel("Parent Suite");
        List<ProjectView.SuiteDiv> suiteList = new LinkedList<>();
        suiteController.getAllSuitesByProject(test.getProjectId().getId())
                .forEach(x -> {
                    suiteList.add(new ProjectView.SuiteDiv(x));
                });
        select.setItemLabelGenerator(ProjectView.SuiteDiv::getAllTitle);
        select.setItems(suiteList);
        binder.forField(select).withConverter(new ConvertSuiteDivToSuiteDto()).asRequired().bind(TestDto::getSuiteId, TestDto::setSuiteId);
        Checkbox isAutomated = new Checkbox();
        isAutomated.setLabel("Automated");
        binder.forField(isAutomated).bind(TestDto::getAutomated, TestDto::setAutomated);
        TextField status = new TextField("Status");
        binder.forField(status).bind(TestDto::getStatus, TestDto::setStatus);
        action = new HorizontalLayout();
        Button createStep = new Button(getTranslation("createStep"));
        action.add(createStep);
        createStep.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                addStep();
            }
        });
        FormLayout gridLayout = new FormLayout();
        gridLayout.setWidthFull();
        gridLayout.add(select, 0);
        gridLayout.add(title, 1);
        gridLayout.add(description, 3);
        gridLayout.add(isAutomated, 4);
        gridLayout.add(status, 5);
        gridLayout.add(verticalStep, 6);
        gridLayout.add(action, 7);
        content.add(gridLayout);
        add(content);
        cancelSave();
        binder.setBean(testDto);
        select.setValue(suiteList.stream().filter(x -> x.getId().equals(test.getSuiteId().getId())).findFirst().get());
        testDto.getSteps().getSteps().forEach(step -> {
            renderStep(step);
        });
        stepBinderMap.forEach((step, binder1) -> {
            binder1.setBean(step);
        });
        open();
    }

    private void cancelSave() {
        Button cancel = new Button(getTranslation("cancel"));
        Button save = new Button(getTranslation("save"));
        save.setIcon(VaadinIcon.PENCIL.create());
        cancel.addClickListener(buttonClickEvent -> {
            close();
            actionClose.run();
        });
        save.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                List<Step> steps = new LinkedList<>();
                for (Map.Entry<Step, Binder> entry : stepBinderMap.entrySet()) {
                    entry.getValue().validate();
                    if (entry.getValue().writeBeanIfValid(entry.getKey())) {
                        steps.add(entry.getKey());
                    }
                }
                testDto.setSteps(new Steps(steps));
                binder.validate();
                if (binder.writeBeanIfValid(testDto)) {
                    testController.updateTest(test.getId(), testDto);
                    close();
                    actionClose.run();
                }
            }
        });
        hor = new HorizontalLayout();
        hor.add(cancel, save);
        hor.setId("actions");
        add(hor);
    }


    private void renderStep(Step step) {
        Binder<Step> stepBinder = new Binder<>();
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(new Label(getTranslation("step") + step.getNumber()));
        verticalLayout.getStyle().set("border", "1px solid #e5e5e5");
        HorizontalLayout name = new HorizontalLayout();
        name.setWidthFull();
        Label nameAction = new Label(getTranslation("action"));
        nameAction.getElement().getStyle().set("width", "50%");
        Label nameResult = new Label(getTranslation("expectedResult"));
        nameResult.getElement().getStyle().set("width", "50%");
        name.add(nameAction, nameResult);
        verticalLayout.add(name);
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        MarkdownArea action = new MarkdownArea();
        action.getElement().getStyle().set("width", "50%");
        MarkdownArea result = new MarkdownArea();
        result.getElement().getStyle().set("width", "50%");
        horizontalLayout.add(action);
        horizontalLayout.add(result);
        stepBinder.forField(action.getInput()).bind(Step::getAction, Step::setAction);
        stepBinder.forField(result.getInput()).bind(Step::getExpectedResult, Step::setExpectedResult);
        stepBinderMap.put(step, stepBinder);
        nextStep = step.getNumber() + 1;
        verticalLayout.add(horizontalLayout);
        add(verticalLayout);
        add(this.action);
    }

    private void addStep() {
        Step step = new Step();
        step.setNumber(nextStep);
        Binder<Step> stepBinder = new Binder<>();
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(new Label(getTranslation("step") + nextStep));
        verticalLayout.getStyle().set("border", "1px solid #e5e5e5");
        HorizontalLayout name = new HorizontalLayout();
        name.setWidthFull();
        Label nameAction = new Label(getTranslation("action"));
        nameAction.getElement().getStyle().set("width", "50%");
        Label nameResult = new Label(getTranslation("expectedResult"));
        nameResult.getElement().getStyle().set("width", "50%");
        name.add(nameAction, nameResult);
        verticalLayout.add(name);
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        MarkdownArea action = new MarkdownArea();
        action.getElement().getStyle().set("width", "50%");
        MarkdownArea result = new MarkdownArea();
        result.getElement().getStyle().set("width", "50%");
        horizontalLayout.add(action);
        horizontalLayout.add(result);
        stepBinder.forField(action.getInput()).bind(Step::getAction, Step::setAction);
        stepBinder.forField(result.getInput()).bind(Step::getExpectedResult, Step::setExpectedResult);
        stepBinderMap.put(step, stepBinder);
        nextStep++;
        verticalLayout.add(horizontalLayout);
        add(verticalLayout);
        add(this.action);
    }

}
