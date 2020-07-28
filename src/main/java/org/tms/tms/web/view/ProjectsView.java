package org.tms.tms.web.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "projects", layout = MainPage.class)
@PageTitle("Проекты")
public class ProjectsView extends Div {

    public ProjectsView() {
        add(new Label("Текст текст"));
    }
}
