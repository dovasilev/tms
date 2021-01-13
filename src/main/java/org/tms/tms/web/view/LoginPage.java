package org.tms.tms.web.view;

import ch.carnet.kasparscherrer.LanguageSelect;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.*;
import org.tms.tms.web.ReloadPage;
import org.tms.tms.web.components.LanguageSelectView;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * UI content when the user is not logged in yet.
 */
@Route("Login")
@PageTitle("Login")
@CssImport("./styles/shared-styles.css")
@PreserveOnRefresh
public class LoginPage extends VerticalLayout implements RouterLayout, AfterNavigationObserver, LocaleChangeObserver, BeforeEnterObserver {

    public static final String ROUTE = "login";
    private Tabs tabs;
    private LanguageSelect langSelect;

    public LoginPage() {
        setSizeFull();
        setClassName("login-screen");
        getStyle().set("padding-top", "10%");
        init();

    }

    void init() {
        langSelect = new LanguageSelectView().getLangSelect();
        setAlignItems(FlexComponent.Alignment.CENTER);
        add(langSelect);
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
                new RouterLink(getTranslation("loginPage.signIn"), SignInView.class),
                new RouterLink(getTranslation("loginPage.signUp"), SignUpView.class)
        };
        return Arrays.stream(links).map(x -> createTab(x)).toArray(Tab[]::new);
    }

    private Tab createTab(RouterLink content) {
        final Tab tab = new Tab();
        tab.add(content);
        return tab;
    }

    private void selectTab(Router router, Location location) {
        RoutePrefix routePrefix = UI.getCurrent().getClass().getAnnotation(RoutePrefix.class);
        if (routePrefix != null) {
            Optional<Component> tabToSelect = tabs.getChildren().filter(tab -> {
                Component child = tab.getChildren().findFirst().get();
                return child instanceof RouterLink && routePrefix.value().equals(((RouterLink) child).getHref());
            }).findFirst();
            tabToSelect.ifPresent(tab -> tabs.setSelectedTab((Tab) tab));
        } else {
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
        selectTab(afterNavigationEvent.getSource(), afterNavigationEvent.getLocation());
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        ReloadPage.reloadPage(localeChangeEvent, this.getClass());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getNavigationTarget().equals(SignInView.class)){
            beforeEnterEvent.rerouteTo(SignInView.class);
        }
        else if (beforeEnterEvent.getNavigationTarget().equals(this.getClass())){
            beforeEnterEvent.rerouteTo(SignInView.class);
        }
        else if (beforeEnterEvent.getNavigationTarget().equals(SignUpView.class)){
            beforeEnterEvent.rerouteTo(SignUpView.class);
        }
    }
}
