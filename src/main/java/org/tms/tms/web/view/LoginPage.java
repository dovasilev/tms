package org.tms.tms.web.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.Arrays;

/**
 * UI content when the user is not logged in yet.
 */
@Route("Login")
@PageTitle("Login")
@CssImport("./styles/shared-styles.css")
@Theme(Lumo.class)
public class LoginPage extends VerticalLayout implements RouterLayout, BeforeEnterObserver {

    public static final String ROUTE = "login";
    private Tabs tabs;

    public LoginPage() {
        setSizeFull();
        setClassName("login-screen");
        getStyle().set("margin-top","10%");
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

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (LoginPage.class.equals(beforeEnterEvent.getNavigationTarget()))
            beforeEnterEvent.rerouteTo(SignInView.class);
    }

    /*@Override
    public Element getElement() {
        return UI.getCurrent().getElement();
    }*/

/*
    private void buildUI() {
        //setSizeFull();
        //setClassName("login-screen");

        // login form, centered in the available part of the screen
        loginForm = new LoginForm();
        loginForm.addLoginListener(this::login);
        loginForm.addForgotPasswordListener(
                event -> Notification.show("Hint: same as username"));

        // layout to center login form when there is sufficient screen space
        FlexLayout centeringLayout = new FlexLayout();
        centeringLayout.setSizeFull();
        centeringLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        centeringLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        centeringLayout.add(createMenu(loginForm,registrationForm()));

        //add(loginInformation);
        ChangeThemeComponent changeThemeComponent = new ChangeThemeComponent(null);
        //add(changeThemeComponent);
        setContent(centeringLayout);
    }

    private Component registrationForm(){
        FormLayout verticalLayout = new FormLayout();
        verticalLayout.setSizeFull();
        EmailField emailField = new EmailField("Email");
        emailField.setRequiredIndicatorVisible(true);
        TextField loginField = new TextField("Login");
        loginField.setWidthFull();
        loginField.setRequired(true);
        TextField fullNameField = new TextField("FullName");
        fullNameField.setWidthFull();
        loginField.setRequired(true);
        PasswordField passwordField = new PasswordField("Password");
        passwordField.setWidthFull();
        passwordField.setRequired(true);
        PasswordField repeatPasswordField = new PasswordField("Repeat Password");
        repeatPasswordField.setWidthFull();
        repeatPasswordField.setRequired(true);
        verticalLayout.add(emailField,loginField,fullNameField,passwordField,repeatPasswordField);
        verticalLayout.getElement().setAttribute("part","vaadin-login-native-form");
        return verticalLayout;
    }

    private Component createMenu(Component login, Component registration) {
        Tab signIn = new Tab("Вход");
        Tab signUp = new Tab("Регистрация");
        Div divSignIn = new Div();
        Div divSignUp = new Div();
        divSignUp.setVisible(false);
        VerticalLayout layoutSignIn = new VerticalLayout();
        layoutSignIn.setSizeFull();
        layoutSignIn.add(login);
        divSignIn.add(layoutSignIn);

        VerticalLayout layoutSignUp = new VerticalLayout();
        layoutSignUp.setSizeFull();
        layoutSignUp.add(registration);
        divSignUp.add(layoutSignUp);

        Map<Tab, Component> tabsToPages = new HashMap<>();
        tabsToPages.put(signIn, divSignIn);
        tabsToPages.put(signUp, divSignUp);
        Tabs tabs = new Tabs(signIn,signUp);
        tabs.setFlexGrowForEnclosedTabs(1);
        tabs.getElement().getShadowRoot();
        Div pages = new Div(divSignIn,divSignUp);
        Set<Component> pagesShown = Stream.of(divSignIn)
                .collect(Collectors.toSet());
        tabs.addSelectedChangeListener(event -> {
            pagesShown.forEach(page -> page.setVisible(false));
            pagesShown.clear();
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            selectedPage.setVisible(true);
            pagesShown.add(selectedPage);
        });
        Div all = new Div();
        all.add(tabs,pages);
        return all;
    }

    private void login(LoginForm.LoginEvent event) {
        try {
            // try to authenticate with given credentials, should always return not null or throw an {@link AuthenticationException}
            final Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(event.getUsername(), event.getPassword())); //

            // if authentication was successful we will update the security context and redirect to the page requested first
            SecurityContextHolder.getContext().setAuthentication(authentication); //
            UI.getCurrent().navigate(requestCache.resolveRedirectUrl()); //

        } catch (AuthenticationException ex) { //
            // show default error message
            // Note: You should not expose any detailed information here like "username is known but password is wrong"
            // as it weakens security.
            //login.setError(true);
            event.getSource().setError(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
