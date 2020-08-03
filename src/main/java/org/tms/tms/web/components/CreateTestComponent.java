package org.tms.tms.web.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.tms.tms.api.SuiteController;
import org.tms.tms.api.TestController;
import org.tms.tms.dao.Step;
import org.tms.tms.dao.Steps;
import org.tms.tms.dto.TestDto;
import org.tms.tms.web.view.ProjectView;
import org.vaadin.maxime.MarkdownArea;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@CssImport("./styles/shared-styles.css")
public class CreateTestComponent extends Dialog {

    private VerticalLayout verticalStep = new VerticalLayout();
    private Binder<TestDto> binder;
    private TestDto testDto;
    int i = 1;
    private HorizontalLayout action;
    Select<ProjectView.SuiteDiv> select;
    TestController testController;
    private HorizontalLayout hor;
    private Map<Step,Binder> stepBinderMap;

    public CreateTestComponent(Long projectId, SuiteController suiteController, TestController testController) {
        this.stepBinderMap = new LinkedHashMap<>();
        setSizeFull();
        setAriaLabel("Создание сьюта");
        getElement().getStyle().set("scrolling","auto");
        this.testController = testController;
        testDto = new TestDto();
        binder = new Binder<>();
        VerticalLayout content = new VerticalLayout();
        TextField title = new TextField("Название");
        binder.forField(title).asRequired().bind(TestDto::getTitle, TestDto::setTitle);
        TextField description = new TextField("Описание");
        binder.forField(description).bind(TestDto::getDescription, TestDto::setDescription);
        testDto.setProjectId(projectId);
        select = new Select<>();
        select.setLabel("Родительский сьют");
        List<ProjectView.SuiteDiv> suiteList = new LinkedList<>();
        suiteController.getAllSuitesByProject(projectId)
                .forEach(x -> {
                    suiteList.add(new ProjectView.SuiteDiv(x));
                });
        select.setItemLabelGenerator(ProjectView.SuiteDiv::getAllTitle);
        select.setItems(suiteList);
        Checkbox isAutomated = new Checkbox();
        isAutomated.setLabel("Автоматизирон");
        binder.forField(isAutomated).bind(TestDto::getAutomated,TestDto::setAutomated);
        TextField status = new TextField("Статус");
        binder.forField(status).bind(TestDto::getStatus,TestDto::setStatus);

        FormLayout gridLayout = new FormLayout();
        gridLayout.setWidthFull();
        init();
        gridLayout.add(select,0);
        gridLayout.add(title,1);
        gridLayout.add(description,3);
        gridLayout.add(isAutomated,4);
        gridLayout.add(status,5);
        gridLayout.add(verticalStep,6);
        gridLayout.add(action,7);
        content.add(gridLayout);
        add(content);
        cancelSave();
    }

    private void cancelSave(){
        Button cancel = new Button("Отмена");
        Button save = new Button("Сохранить");
        save.setIcon(VaadinIcon.PENCIL.create());
        cancel.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                close();
            }
        });
        save.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                List<Step> steps = new LinkedList<>();
                for (Map.Entry<Step, Binder> entry:stepBinderMap.entrySet()){
                    entry.getValue().validate();
                    if (entry.getValue().writeBeanIfValid(entry.getKey())){
                        steps.add(entry.getKey());
                    }
                }
                testDto.setSteps(new Steps(steps));
                binder.validate();
                if (binder.writeBeanIfValid(testDto)) {
                    if (select.getValue() != null) {
                        testDto.setSuiteId(select.getValue().getId());
                    }
                    testController.addTest(testDto);
                    close();
                }
            }
        });
        hor = new HorizontalLayout();
        hor.add(cancel, save);
        hor.setId("actions");
        add(hor);

    }

    private void init() {
        action = new HorizontalLayout();
        Button createStep = new Button("Создать шаг");
        action.add(createStep);
        createStep.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                addStep();
            }
        });
    }
    private void addStep(){
        Step step = new Step();
        step.setNumber(i);
        Binder<Step> stepBinder = new Binder<>();
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(new Text("Шаг № "+i));
        verticalLayout.getStyle().set("border","1px solid #e5e5e5");
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        MarkdownArea action = new MarkdownArea();
        action.getElement().getStyle().set("width","50%");
        MarkdownArea result = new MarkdownArea();
        result.getElement().getStyle().set("width","50%");
        horizontalLayout.add(action);
        horizontalLayout.add(result);
        stepBinder.forField(action.getInput()).bind(Step::getAction,Step::setAction);
        stepBinder.forField(result.getInput()).bind(Step::getExpectedResult,Step::setExpectedResult);
        stepBinderMap.put(step,stepBinder);
        i++;
        verticalLayout.add(horizontalLayout);
        add(verticalLayout);
        add(this.action);
        init();
    }
}
