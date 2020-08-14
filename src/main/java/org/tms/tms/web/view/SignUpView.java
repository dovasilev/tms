package org.tms.tms.web.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(value = "SignUp",layout = LoginPage.class)
@PageTitle("SignUp")
public class SignUpView extends Div {

    String email;
    String fullName;
    String login;
    String pass;
    String repeatPass;


    EmailField emailField;
    TextField fullNameField;
    TextField loginField;
    PasswordField passwordField;
    PasswordField repeatPasswordField;


    public SignUpView() {
        FormLayout formLayout = new FormLayout();
        formLayout.setHeightFull();
        formLayout.getElement().setAttribute("part","vaadin-login-native-form-wrapper");
        formLayout.getElement().getStyle().set("max-width","calc(var(--lumo-size-m) * 10)");
        Binder<SignUpView> binder = new Binder<>(SignUpView.class);
        H2 label = new H2("Sign up");
        emailField = new EmailField("Email");
        emailField.setRequiredIndicatorVisible(true);
        binder.forField(emailField)
                .withValidator(new StringLengthValidator("Fill "+emailField.getLabel(),1,999))
                .bind(SignUpView::getEmail,SignUpView::setEmail);
        fullNameField = new TextField("FullName");
        binder.forField(fullNameField)
                .withValidator(new StringLengthValidator("Fill "+fullNameField.getLabel(),1,999))
                .bind(SignUpView::getFullName,SignUpView::setFullName);
        loginField = new TextField("Username");
        binder.forField(loginField)
                .withValidator(new StringLengthValidator("Fill "+loginField.getLabel(),1,999))
                .bind(SignUpView::getLogin,SignUpView::setLogin);
        passwordField = new PasswordField("Password");
        binder.forField(passwordField)
                .withValidator(new StringLengthValidator("Fill "+passwordField.getLabel(),1,999))
                .bind(SignUpView::getPass,SignUpView::setPass);
        repeatPasswordField = new PasswordField("Repeat Password");
        binder.forField(repeatPasswordField).withValidator(value -> value.equals(passwordField.getValue()),
                "Password and Repeat Password not equals")
                .bind(SignUpView::getRepeatPass,SignUpView::setRepeatPass);
        Button create = new Button("Registration");
        create.getElement().getThemeList().set("primary contained",true);
        create.addClickListener(buttonClickEvent -> {
            if (binder.writeBeanIfValid(this)){
                String s = "";
            }
        });
        formLayout.add(label,emailField,fullNameField,loginField,passwordField,repeatPasswordField,create);
        add(formLayout);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getRepeatPass() {
        return repeatPass;
    }

    public void setRepeatPass(String repeatPass) {
        this.repeatPass = repeatPass;
    }
}
