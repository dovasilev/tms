package org.tms.tms.web.view;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.material.Material;

import java.util.Arrays;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@CssImport("styles/views/main/main-view.css")
@Theme(value = Lumo.class, variant = Lumo.DARK)
@Route("")
public class MainUi extends AppLayout {

    private final Tabs menu;
    private H1 viewTitle;

    public MainUi() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        menu = createMenu();
        addToDrawer(createDrawerContent(menu));
    }

    private Component createHeaderContent() {
        HorizontalLayout layout = new HorizontalLayout();
        H1 h1 = new H1("TMS");
        layout.setId("header");
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.getThemeList().set("dark", true);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(new DrawerToggle());
        viewTitle = new H1();
        Button label = new Button();
        label.setIcon(VaadinIcon.OPEN_BOOK.create());
        label.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                UI.getCurrent().getPage().open("/swagger");
            }
        });
        label.getStyle().set("cursor", "pointer");
        Select<String> theme = new Select<>();
        theme.setLabel("Тема");
        theme.setItems("Светлая", "Темная");
        String nameTheme = this.getClass().getAnnotation(Theme.class).variant();
        if (nameTheme.equals("dark"))
            theme.setValue("Темная");
        else if (nameTheme.equals("light"))
            theme.setValue("Светлая");
        theme.addValueChangeListener(new HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<Select<String>, String>>() {
            @Override
            public void valueChanged(AbstractField.ComponentValueChangeEvent<Select<String>, String> selectStringComponentValueChangeEvent) {
                if (selectStringComponentValueChangeEvent.getValue().equals("Светлая")) {
                    UI.getCurrent().getPage().executeJs("document.querySelector('html').setAttribute('theme','light');");
                } else if (selectStringComponentValueChangeEvent.getValue().equals("Темная")) {
                    UI.getCurrent().getPage().executeJs("document.querySelector('html').setAttribute('theme','dark');");
                }
            }
        });
        //layout.add(viewTitle,label,theme);
        layout.add(viewTitle);
        return layout;
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
        return Arrays.stream(links).map(MainUi::createTab).toArray(Tab[]::new);
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

    /*public MainUi() {
        menu = createMenuTabs();
        Button label = new Button();
        label.setIcon(VaadinIcon.OPEN_BOOK.create());
        label.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                UI.getCurrent().getPage().open("/swagger");
            }
        });
        label.getStyle().set("cursor", "pointer");
        Select<String> theme = new Select<>();
        theme.setLabel("Тема");
        theme.setItems("Светлая", "Темная");
        String nameTheme = this.getClass().getAnnotation(Theme.class).variant();
        if (nameTheme.equals("dark"))
            theme.setValue("Темная");
        else if (nameTheme.equals("light"))
            theme.setValue("Светлая");
        theme.addValueChangeListener(new HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<Select<String>, String>>() {
            @Override
            public void valueChanged(AbstractField.ComponentValueChangeEvent<Select<String>, String> selectStringComponentValueChangeEvent) {
                if (selectStringComponentValueChangeEvent.getValue().equals("Светлая")) {
                    UI.getCurrent().getPage().executeJs("document.querySelector('html').setAttribute('theme','light');");
                } else if (selectStringComponentValueChangeEvent.getValue().equals("Темная")) {
                    UI.getCurrent().getPage().executeJs("document.querySelector('html').setAttribute('theme','dark');");
                }
            }
        });
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(label, theme);
        horizontalLayout.setSpacing(false);
        addToNavbar(menu, horizontalLayout);
    }

    private static Tabs createMenuTabs() {
        final Tabs tabs = new Tabs();
        tabs.getStyle().set("display", "contents");
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.add(getAvailableTabs());
        return tabs;
    }

    private static Tab[] getAvailableTabs() {
        final List<Tab> tabs = new ArrayList<>();
        tabs.add(createTab("Проекты", ProjectsView.class));
        tabs.add(createTab("Проекты 1", ProjectsView.class));
        tabs.add(createTab("Проекты 2", ProjectsView.class));
        tabs.add(createTab("Проекты 3", ProjectsView.class));
        tabs.add(createTab("Проекты 4", ProjectsView.class));
        return tabs.toArray(new Tab[tabs.size()]);
    }

    private static Tab createTab(String title, Class<? extends Component> viewClass) {
        return createTab(populateLink(new RouterLink(null, viewClass), title));
    }

    private static Tab createTab(Component content) {
        final Tab tab = new Tab();
        tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        tab.add(content);
        return tab;
    }

    private static <T extends HasComponents> T populateLink(T a, String title) {
        a.add(title);
        return a;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        selectTab();
    }

    private void selectTab() {
        RoutePrefix routePrefix = getContent().getClass().getAnnotation(RoutePrefix.class);
        if (routePrefix != null) {
            Optional<Component> tabToSelect = menu.getChildren().filter(tab -> {
                Component child = tab.getChildren().findFirst().get();
                return child instanceof RouterLink && routePrefix.value().equals(((RouterLink) child).getHref());
            }).findFirst();
            tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
        } else {
            String target = RouteConfiguration.forSessionScope().getUrl(getContent().getClass());
            Optional<Component> tabToSelect = menu.getChildren().filter(tab -> {
                Component child = tab.getChildren().findFirst().get();
                return child instanceof RouterLink && target.contains(((RouterLink) child).getHref());
            }).findFirst();
            tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
        }
    }

    public Tabs getMenu() {
        return menu;
    }*/
}