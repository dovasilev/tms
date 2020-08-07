package org.tms.tms.web.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.tms.tms.security.CustomRequestCache;
import org.tms.tms.security.SecurityConfiguration;

/**
 * UI content when the user is not logged in yet.
 */
@Route("Login")
@PageTitle("Login")
@CssImport("./styles/shared-styles.css")
public class LoginPage extends FlexLayout {

    public static final String ROUTE = "login";
    AuthenticationManager authenticationManager;
    CustomRequestCache requestCache;


    @Autowired
    public LoginPage(AuthenticationManager authenticationManager, //
                     CustomRequestCache requestCache) {
        this.authenticationManager = authenticationManager;
        this.requestCache  = requestCache;
        buildUI();
    }

    private void buildUI() {
        setSizeFull();
        setClassName("login-screen");

        // login form, centered in the available part of the screen
        LoginForm loginForm = new LoginForm();
        loginForm.addLoginListener(this::login);
        loginForm.addForgotPasswordListener(
                event -> Notification.show("Hint: same as username"));

        // layout to center login form when there is sufficient screen space
        FlexLayout centeringLayout = new FlexLayout();
        centeringLayout.setSizeFull();
        centeringLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        centeringLayout.setAlignItems(Alignment.CENTER);
        centeringLayout.add(loginForm);

        // information text about logging in
        Component loginInformation = buildLoginInformation();

        add(loginInformation);
        add(centeringLayout);
    }

    private Component buildLoginInformation() {
        VerticalLayout loginInformation = new VerticalLayout();
        loginInformation.setClassName("login-information");

        H1 loginInfoHeader = new H1("Login Information");
        loginInfoHeader.setWidth("100%");
        Span loginInfoText = new Span(
                "Log in as \"admin\" to have full access. Log in with any " +
                        "other username to have read-only access. For all " +
                        "users, the password is same as the username.");
        loginInfoText.setWidth("100%");
        loginInformation.add(loginInfoHeader);
        loginInformation.add(loginInfoText);

        return loginInformation;
    }

    private void login(LoginForm.LoginEvent event) {
        /*if (accessControl.signIn(event.getUsername(), event.getPassword())) {
            UI.getCurrent().navigate(MainPage.class);
        } else {
            event.getSource().setError(true);
        }*/

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
    }
}
