package org.tms.tms.web.view;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import lombok.SneakyThrows;
import org.tms.tms.security.SecurityUtils;
import org.tms.tms.security.service.UserService;
import org.tms.tms.web.ReloadPage;
import org.tms.tms.web.components.ChangeThemeComponent;
import org.tms.tms.web.components.LanguageSelectView;

import java.util.Arrays;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
@CssImport("./styles/views/main/main-view.css")
@CssImport("./styles/shared-styles.css")
@PageTitle("TMS")
@Route("")
public class MainPage extends AppLayout implements LocaleChangeObserver {

    private final Tabs menu;
    private H1 viewTitle;
    private Button logoutButton;
    private HorizontalLayout header;
    private Component headerContent;
    private UserService userService;

    public MainPage(UserService userService) {
        //setPrimarySection(Section.DRAWER);
        setPrimarySection(Section.NAVBAR);
        headerContent = createHeaderContent();
        addToNavbar(true, createHeaderContent());
        menu = createMenu();
        addToDrawer(createDrawerContent(menu));
        this.userService = userService;
    }

    private Component createHeaderContent() {
        header = new HorizontalLayout();
        header.setId("header");
        header.setWidthFull();
        header.setSpacing(false);
        //header.getThemeList().set("dark", true);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.add(new DrawerToggle());
        viewTitle = new H1();
        header.add(viewTitle);
        logoutButton = createMenuButton(getTranslation("logout"), VaadinIcon.SIGN_OUT.create());
        logoutButton.addClickListener(e -> logout());
        logoutButton.getElement().setAttribute("title", getTranslation("logout") + " (Ctrl+L)");
        return header;
    }

    private Component createDrawerContent(Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.add(new Image("images/logos/18.png", "TMS"));
        logoLayout.add(new H1("TMS"));
        layout.add(logoLayout, menu);
        return layout;
    }

    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_SMALL);
        tabs.setId("tabs");
        tabs.add(createMenuItems());
        return tabs;
    }

    private Component[] createMenuItems() {
        RouterLink[] links = new RouterLink[]{
                new RouterLink(getTranslation("projects"), ProjectsView.class)
        };
        return Arrays.stream(links).map(MainPage::createTab).toArray(Tab[]::new);
    }

    private static Tab createTab(Component content) {
        final Tab tab = new Tab();
        tab.add(content);
        return tab;
    }

    @Override
    public void afterNavigation() {
        updateChrome();
    }

    private void updateChrome() {
        getTabWithCurrentRoute().ifPresent(menu::setSelectedTab);
        viewTitle.setText(getCurrentPageTitle());
    }

    private Optional<Tab> getTabWithCurrentRoute() {
        String currentRoute = RouteConfiguration.forSessionScope()
                .getUrl(getContent().getClass());
        return menu.getChildren().filter(tab -> hasLink(tab, currentRoute))
                .findFirst().map(Tab.class::cast);
    }

    private boolean hasLink(Component tab, String currentRoute) {
        return tab.getChildren().filter(RouterLink.class::isInstance)
                .map(RouterLink.class::cast).map(RouterLink::getHref)
                .anyMatch(currentRoute::equals);
    }

    @SneakyThrows
    private String getCurrentPageTitle() {
        PageTitle pageTitle = getContent().getClass().getAnnotation(PageTitle.class);
        if (pageTitle == null) {
            return "";
        } else return getContent().getClass().getAnnotation(PageTitle.class).value();
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private String title = "";

    private Button createMenuButton(String caption, Icon icon) {
        final Button routerButton = new Button(caption);
        routerButton.setClassName("menu-button");
        routerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        routerButton.setIcon(icon);
        icon.setSize("24px");
        return routerButton;
    }

    private void logout() {
        VaadinSession.getCurrent().getSession().invalidate();
        UI.getCurrent().navigate(LoginPage.class);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        // User can quickly activate logout with Ctrl+L
        attachEvent.getUI().addShortcutListener(() -> logout(), Key.KEY_L,
                KeyModifier.CONTROL);

        // add the admin view menu item if user has admin role
        Button label = new Button();
        label.setIcon(VaadinIcon.OPEN_BOOK.create());
        label.getStyle().set("padding-left", "1em");
        label.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                UI.getCurrent().getPage().open("swagger-ui.html");
            }
        });
        ChangeThemeComponent changeThemeComponent = new ChangeThemeComponent(headerContent);
        HorizontalLayout profile = new HorizontalLayout();
        Label fullName = new Label(userService.getUserByEmail(SecurityUtils.getLoggedUser().getUsername()).getFullName());
        Icon icon = new Icon(VaadinIcon.EDIT);
        icon.getStyle().set("margin", "auto");
        profile.add(icon, fullName);
        profile.addClickListener(horizontalLayoutClickEvent ->
                UI.getCurrent().navigate(ProfileView.class));
        fullName.getStyle().set("padding-right", "1em");
        addToNavbar(profile, changeThemeComponent, label, new LanguageSelectView().getLangSelect(), logoutButton);
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        ReloadPage.reloadPage(localeChangeEvent, this.getClass());
    }
}