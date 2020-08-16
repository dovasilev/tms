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
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.tms.tms.dto.SignUpField;
import org.tms.tms.security.service.UserService;

@Route(value = "SignUp", layout = LoginPage.class)
@PageTitle("SignUp")
public class SignUpView extends Div {

    EmailField emailField;
    TextField fullNameField;
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
        binder.forField(emailField)
                .withValidator(new EmailValidator("Fill valid "+emailField.getLabel()))
                .bind(SignUpField::getEmail, SignUpField::setEmail);
        fullNameField = new TextField("FullName");
        binder.forField(fullNameField)
                .withValidator(new StringLengthValidator("Fill " + fullNameField.getLabel(), 1, 999))
                .bind(SignUpField::getFullName, SignUpField::setFullName);
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
                    try {
                        userService.newUser(signUpField);
                        System.out.print("Юзер создан");
                    }
                    catch (Exception e) {
                        throw e;
                    }
                    UI.getCurrent().navigate(SignInView.class);
                }
            }
        });
        formLayout.add(label,emailField,fullNameField,passwordField,repeatPasswordField,create);
        add(formLayout);
        }


    }
