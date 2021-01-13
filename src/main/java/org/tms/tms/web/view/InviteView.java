package org.tms.tms.web.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.tms.tms.dto.UsersDto;
import org.tms.tms.security.service.InviteTokenService;
import org.tms.tms.security.service.UserService;
import org.vaadin.flow.helper.HasUrlParameterMapping;
import org.vaadin.flow.helper.UrlParameter;
import org.vaadin.flow.helper.UrlParameterMapping;

@CssImport("./styles/shared-styles.css")
@Route(value = "invite")
@UrlParameterMapping(":token")
@UrlParameterMapping("")
@PageTitle("Invite")
public class InviteView extends VerticalLayout implements HasUrlParameterMapping {

    String token;

    EmailField emailField;
    TextField fullNameField;
    PasswordField passwordField;
    PasswordField repeatPasswordField;

    private UserService userService;
    private InviteTokenService inviteTokenService;

    public InviteView(UserService userService, InviteTokenService inviteTokenService) {
        this.userService = userService;
        this.inviteTokenService = inviteTokenService;
    }

    @UrlParameter(name = "token")
    public void setToken(String token) {
        this.token = token;
        setSizeFull();
        setClassName("login-screen");
        getStyle().set("padding-top", "10%");
        init();
    }

    public void init() {
        FormLayout formLayout = new FormLayout();
        formLayout.setHeightFull();
        formLayout.getStyle().set("align-self","center");
        formLayout.getElement().setAttribute("part", "vaadin-login-native-form-wrapper");
        formLayout.getElement().getStyle().set("max-width", "calc(var(--lumo-size-m) * 10)");
        Binder<UsersDto> binder = new Binder<>();
        UsersDto usersDto = new UsersDto();
        H2 label = new H2("signUpByInvite");
        String email = inviteTokenService.getEmailByToken(token);
        if (email != null) {
            emailField = new EmailField(getTranslation("email"));
            emailField.setClearButtonVisible(true);
            emailField.setValue(email);
            emailField.setReadOnly(true);
            binder.forField(emailField)
                    .withValidator(new EmailValidator(getTranslation("fillValid") + emailField.getLabel()))
                    .bind(UsersDto::getEmail, UsersDto::setEmail);
            fullNameField = new TextField(getTranslation("fullName"));
            binder.forField(fullNameField)
                    .withValidator(new StringLengthValidator(getTranslation("fill") + fullNameField.getLabel(), 1, 999))
                    .bind(UsersDto::getFullName, UsersDto::setFullName);
            passwordField = new PasswordField("Password");
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
                            inviteTokenService.delInviteByEmail(usersDto.getEmail());
                        } catch (Exception e) {
                            throw e;
                        }
                        UI.getCurrent().navigate(SignInView.class);
                    }
                }
            });
            formLayout.add(label, emailField, fullNameField, passwordField, repeatPasswordField, create);
            add(formLayout);
        }
        else formLayout.add(getTranslation("inviteTokenMessage"));
        add(formLayout);
    }

}
