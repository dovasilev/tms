package org.tms.tms.web.view;

import com.gmail.umit.PaperToggleButton;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.tms.tms.authentication.AccessControl;
import org.tms.tms.authentication.AccessControlFactory;

import java.util.Arrays;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
//@JsModule("./styles/shared-styles.js")
@CssImport("./styles/views/main/main-view.css")
@Theme(value = Lumo.class)
@PWA(name = "TMS", shortName = "TMS", enableInstallPrompt = false)
@PageTitle("TMS")
@Route("")
public class MainPage extends AppLayout implements RouterLayout {

    private final Tabs menu;
    private H1 viewTitle;
    private Button logoutButton;
    private HorizontalLayout header;

    public MainPage() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        menu = createMenu();
        addToDrawer(createDrawerContent(menu));
    }

    private Component createHeaderContent() {
        header = new HorizontalLayout();
        header.setId("header");
        header.setWidthFull();
        header.setSpacing(false);
        header.getThemeList().set("dark", true);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.add(new DrawerToggle());
        viewTitle = new H1();
        header.add(viewTitle);
        logoutButton = createMenuButton("Logout", VaadinIcon.SIGN_OUT.create());
        logoutButton.addClickListener(e -> logout());
        logoutButton.getElement().setAttribute("title", "Logout (Ctrl+L)");
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
        RouterLink[] links = new RouterLink[] {
                new RouterLink("Проекты", ProjectsView.class)
        };
        return Arrays.stream(links).map(MainPage::createTab).toArray(Tab[]::new);
    }

    private static Tab createTab(Component content) {
        final Tab tab = new Tab();
        tab.add(content);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
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

    private String getCurrentPageTitle() {
        return getContent().getClass().getAnnotation(PageTitle.class).value();
    }


    private Button createMenuButton(String caption, Icon icon) {
        final Button routerButton = new Button(caption);
        routerButton.setClassName("menu-button");
        routerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        routerButton.setIcon(icon);
        icon.setSize("24px");
        return routerButton;
    }

    private void logout() {
        AccessControlFactory.getInstance().createAccessControl().signOut();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        // User can quickly activate logout with Ctrl+L
        attachEvent.getUI().addShortcutListener(() -> logout(), Key.KEY_L,
                KeyModifier.CONTROL);

        // add the admin view menu item if user has admin role
        final AccessControl accessControl = AccessControlFactory.getInstance()
                .createAccessControl();
        if (accessControl.isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {

        }

        // Finally, add logout button for all users
        Button label = new Button();
        label.setIcon(VaadinIcon.OPEN_BOOK.create());
        label.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                UI.getCurrent().getPage().open("/swagger");
            }
        });
        Icon moon = new Icon(VaadinIcon.MOON);
        Icon sun = new Icon(VaadinIcon.SUN_O);
        PaperToggleButton choiseTheme = new PaperToggleButton();
        choiseTheme.addValueChangeListener(valueChangedEvent -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            if (valueChangedEvent.getValue()) {
                themeList.remove(Lumo.LIGHT);
                themeList.add(Lumo.DARK);
                header.getThemeList().set("dark", true);
            } else {
                themeList.remove(Lumo.DARK);
                themeList.add(Lumo.LIGHT);
                header.getThemeList().set("dark", false);
            }
        });
        HorizontalLayout horizontalLayout = new HorizontalLayout(sun,choiseTheme,moon);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        addToNavbar(horizontalLayout,logoutButton);
    }
}