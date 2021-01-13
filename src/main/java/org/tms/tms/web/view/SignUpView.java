package org.tms.tms.web.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
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
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.tms.tms.dto.UsersDto;
import org.tms.tms.security.service.UserService;
import org.tms.tms.web.ReloadPage;

@Route(value = "signUp", layout = LoginPage.class)
@PageTitle("SignUp")
public class SignUpView extends Div implements LocaleChangeObserver {

    H2 label;
    EmailField emailField;
    TextField fullNameField;
    PasswordField passwordField;
    PasswordField repeatPasswordField;
    Component content;
    UserService userService;


    public SignUpView(UserService userService) {
        this.userService = userService;
        removeAll();
        init();
        add(content);
    }

    private void init(){
        FormLayout formLayout = new FormLayout();
        formLayout.setHeightFull();
        formLayout.getElement().setAttribute("part", "vaadin-login-native-form-wrapper");
        formLayout.getElement().getStyle().set("max-width", "calc(var(--lumo-size-m) * 10)");
        Binder<UsersDto> binder = new Binder<>();
        UsersDto usersDto = new UsersDto();
        label = new H2(getTranslation("signUp.title"));
        emailField = new EmailField(getTranslation("email"));
        emailField.setClearButtonVisible(true);
        binder.forField(emailField)
                .withValidator(new EmailValidator(getTranslation("fillValid") + emailField.getLabel()))
                .bind(UsersDto::getEmail, UsersDto::setEmail);
        fullNameField = new TextField(getTranslation("fullName"));
        binder.forField(fullNameField)
                .withValidator(new StringLengthValidator(getTranslation("fill") + fullNameField.getLabel(), 1, 999))
                .bind(UsersDto::getFullName, UsersDto::setFullName);
        passwordField = new PasswordField(getTranslation("password"));
        binder.forField(passwordField)
                .withValidator(new StringLengthValidator(getTranslation("fill") + passwordField.getLabel(), 1, 999))
                .bind(UsersDto::getPass, UsersDto::setPass);
        repeatPasswordField = new PasswordField(getTranslation("confirmPassword"));
        binder.forField(repeatPasswordField).withValidator(value -> value.equals(passwordField.getValue()),
                getTranslation("signUp.passwordMessage"))
                .bind(UsersDto::getRepeatPass, UsersDto::setRepeatPass);
        Button create = new Button(getTranslation("signUp.registration"));
        create.getElement().getThemeList().set("primary contained", true);
        create.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                if (binder.writeBeanIfValid(usersDto)) {
                    try {
                        userService.newUser(usersDto);
                    } catch (Exception e) {
                        throw e;
                    }
                    UI.getCurrent().navigate(SignInView.class);
                }
            }
        });
        formLayout.add(label, emailField, fullNameField, passwordField, repeatPasswordField, create);
        content = formLayout;
    }


    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        ReloadPage.reloadPage(localeChangeEvent, this.getClass());
    }
}
