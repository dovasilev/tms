package org.tms.tms.web.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.tms.tms.security.CustomRequestCache;
import org.tms.tms.security.service.ResetTokenService;

@Route(value = "SignIn",layout = LoginPage.class)
@PageTitle("SignIn")
public class SignInView extends Div {

    AuthenticationManager authenticationManager;
    CustomRequestCache requestCache;
    private LoginForm loginForm;
    ResetTokenService resetTokenService;

    @Autowired
    public SignInView(AuthenticationManager authenticationManager,
                     CustomRequestCache requestCache,
                      ResetTokenService resetTokenService) {
        this.authenticationManager = authenticationManager;
        this.requestCache  = requestCache;
        this.resetTokenService = resetTokenService;
        init();
    }

    void init(){
        loginForm = new LoginForm();
        loginForm.addLoginListener(this::login);
        loginForm.addForgotPasswordListener(forgotPasswordEvent -> {
            Dialog dialog = new Dialog();
            dialog.add(new H3("Reset password"));
            VerticalLayout content = new VerticalLayout();
            content.setPadding(false);
            dialog.add(content);

            Label notification = new Label();
            notification.setText("Instructions on how to reset your password have been sent to your email");
            notification.setVisible(false);
            content.add(notification);

            TextField userName = new TextField("Username");
            content.add(userName);

            HorizontalLayout horizontalLayout = new HorizontalLayout();

            Button resetButton = new Button("Reset");
            resetButton.addClickListener(buttonClickEvent -> {
                resetTokenService.newToken(userName.getValue());
                notification.setVisible(true);
            });

            Button cancel = new Button("Cancel");
            cancel.addClickListener(buttonClickEvent -> dialog.close());
            horizontalLayout.add(cancel, resetButton);
            content.add(horizontalLayout);

            dialog.open();


        });
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
