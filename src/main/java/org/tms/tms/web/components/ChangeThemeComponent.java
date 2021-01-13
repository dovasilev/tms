package org.tms.tms.web.components;

import com.gmail.umit.PaperToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.lumo.Lumo;

import javax.servlet.http.Cookie;
import java.util.Locale;

public class ChangeThemeComponent extends HorizontalLayout{

    public ChangeThemeComponent(Component headerComponent) {
        Icon moon = new Icon(VaadinIcon.MOON);
        Icon sun = new Icon(VaadinIcon.SUN_O);
        PaperToggleButton choiceTheme = new PaperToggleButton();
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        choiceTheme.addValueChangeListener(valueChangedEvent -> {
            if (valueChangedEvent.getValue()) {
                themeList.remove(Lumo.LIGHT);
                themeList.add(Lumo.DARK);
                if (headerComponent!=null)
                    headerComponent.getElement().getThemeList().set("dark", true);
            } else {
                themeList.remove(Lumo.DARK);
                themeList.add(Lumo.LIGHT);
                if (headerComponent!=null)
                    headerComponent.getElement().getThemeList().set("dark", false);
            }
        }); Cookie themeCookie = new Cookie("PreferredTheme", themeList.toString());
        themeCookie.setMaxAge(31449600);
        themeCookie.setPath("/");
        VaadinService.getCurrentResponse().addCookie(themeCookie);
        add(sun, choiceTheme,moon);
        setAlignItems(FlexComponent.Alignment.CENTER);
    }
}
