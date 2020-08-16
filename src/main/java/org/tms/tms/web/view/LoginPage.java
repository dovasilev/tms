package org.tms.tms.web.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.internal.AfterNavigationHandler;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.vaadin.flow.helper.UrlParameterMapping;

import java.util.Arrays;
import java.util.Optional;

/**
 * UI content when the user is not logged in yet.
 */
@Route("Login")
@PageTitle("Login")
@CssImport("./styles/shared-styles.css")
@Theme(Lumo.class)
public class LoginPage extends VerticalLayout implements RouterLayout, AfterNavigationObserver {

    public static final String ROUTE = "login";
    private Tabs tabs;

    public LoginPage() {
        setSizeFull();
        setClassName("login-screen");
        getStyle().set("padding-top","10%");
        init();

    }

    void init() {
        setAlignItems(FlexComponent.Alignment.CENTER);
        add(createMenu());

    }

    private VerticalLayout createMenu() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.addThemeVariants(TabsVariant.LUMO_CENTERED);
        tabs.setId("tabs");
        tabs.add(createMenuItems());
        verticalLayout.add(tabs);
        return verticalLayout;
    }

    private Component[] createMenuItems() {
        RouterLink[] links = new RouterLink[]{
                new RouterLink("Sign-in", SignInView.class),
                new RouterLink("Sign-up", SignUpView.class)
        };
        return Arrays.stream(links).map(x->createTab(x)).toArray(Tab[]::new);
    }

    private Tab createTab(RouterLink content) {
        final Tab tab = new Tab();
        tab.add(content);
        return tab;
    }

    private void selectTab(Router router,Location location) {
        RoutePrefix routePrefix = UI.getCurrent().getClass().getAnnotation(RoutePrefix.class);
        if (routePrefix!=null) {
            Optional<Component> tabToSelect = tabs.getChildren().filter(tab -> {
                Component child = tab.getChildren().findFirst().get();
                return child instanceof RouterLink && routePrefix.value().equals(((RouterLink) child).getHref());
            }).findFirst();
            tabToSelect.ifPresent(tab -> tabs.setSelectedTab((Tab) tab));
        }
        else {
            String target = RouteConfiguration.forSessionScope().getUrl(router.resolveNavigationTarget(location).get().getNavigationTarget());
            Optional<Component> tabToSelect = tabs.getChildren().filter(tab -> {
                Component child = tab.getChildren().findFirst().get();
                return child instanceof RouterLink && target.contains(((RouterLink) child).getHref());
            }).findFirst();
            tabToSelect.ifPresent(tab -> tabs.setSelectedTab((Tab) tab));
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        selectTab(afterNavigationEvent.getSource(),afterNavigationEvent.getLocation());
    }
}
