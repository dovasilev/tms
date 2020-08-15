package org.tms.tms.web.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.tms.tms.dto.SignUpField;
import org.tms.tms.security.UserService;

@Route(value = "SignUp", layout = LoginPage.class)
@PageTitle("SignUp")
public class SignUpView extends Div {

    EmailField emailField;
    TextField fullNameField;
    TextField loginField;
    PasswordField passwordField;
    PasswordField repeatPasswordField;


    public SignUpView(UserService userService) {
        FormLayout formLayout = new FormLayout();
        formLayout.setHeightFull();
        formLayout.getElement().setAttribute("part", "vaadin-login-native-form-wrapper");
        formLayout.getElement().getStyle().set("max-width", "calc(var(--lumo-size-m) * 10)");
        Binder<SignUpField> binder = new Binder<>();
        SignUpField signUpField = new SignUpField();
        H2 label = new H2("Sign up");
        emailField = new EmailField("Email");
        emailField.setClearButtonVisible(true);
        emailField.setErrorMessage("Please enter a valid email address");
        binder.forField(emailField)
                .withValidator(new StringLengthValidator("Fill " + emailField.getLabel(), 1, 999))
                .bind(SignUpField::getEmail, SignUpField::setEmail);
        fullNameField = new TextField("FullName");
        binder.forField(fullNameField)
                .withValidator(new StringLengthValidator("Fill " + fullNameField.getLabel(), 1, 999))
                .bind(SignUpField::getFullName, SignUpField::setFullName);
        loginField = new TextField("Username");
        binder.forField(loginField)
                .withValidator(new StringLengthValidator("Fill " + loginField.getLabel(), 1, 999))
                .bind(SignUpField::getLogin, SignUpField::setLogin);
        passwordField = new PasswordField("Password");
        binder.forField(passwordField)
                .withValidator(new StringLengthValidator("Fill " + passwordField.getLabel(), 1, 999))
                .bind(SignUpField::getPass, SignUpField::setPass);
        repeatPasswordField = new PasswordField("Repeat Password");
        binder.forField(repeatPasswordField).withValidator(value -> value.equals(passwordField.getValue()),
                "Password and Repeat Password not equals")
                .bind(SignUpField::getRepeatPass, SignUpField::setRepeatPass);
        Button create = new Button("Registration");
        create.getElement().getThemeList().set("primary contained", true);
        create.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                if (binder.writeBeanIfValid(signUpField)) {
                    userService.newUser(signUpField);
                    System.out.print("Юзер создан");
                    UI.getCurrent().navigate(SignInView.class);
                }
            }
        });
        formLayout.add(label,emailField,fullNameField,loginField,passwordField,repeatPasswordField,create);
        add(formLayout);
        }


    }
