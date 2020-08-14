package org.tms.tms.web.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.tms.tms.security.CustomRequestCache;

@Route(value = "SignIn",layout = LoginPage.class)
@PageTitle("SignIn")
public class SignInView extends Div {

    AuthenticationManager authenticationManager;
    CustomRequestCache requestCache;
    private LoginForm loginForm;

    @Autowired
    public SignInView(AuthenticationManager authenticationManager,
                     CustomRequestCache requestCache) {
        this.authenticationManager = authenticationManager;
        this.requestCache  = requestCache;
        init();
    }

    void init(){
        loginForm = new LoginForm();
        loginForm.addLoginListener(this::login);
        loginForm.addForgotPasswordListener(
                event -> Notification.show("Hint: same as username"));
        add(loginForm);
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
    }

}
