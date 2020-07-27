package org.tms.tms.web.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.flow.helper.HasUrlParameterMapping;

@Route(value = "projects", layout = MainUi.class)
@PageTitle("Проекты")
public class ProjectsView extends Div {

    public ProjectsView() {
        add(new Label("Текст текст"));
    }
}
