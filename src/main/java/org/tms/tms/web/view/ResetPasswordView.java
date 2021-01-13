package org.tms.tms.web.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.tms.tms.dto.UsersDto;
import org.tms.tms.security.dao.Users;
import org.tms.tms.security.service.ResetTokenService;
import org.tms.tms.security.service.UserService;
import org.vaadin.flow.helper.HasUrlParameterMapping;
import org.vaadin.flow.helper.UrlParameter;
import org.vaadin.flow.helper.UrlParameterMapping;

@CssImport("./styles/shared-styles.css")
@Route(value = "resetPassword")
@UrlParameterMapping(":token")
@UrlParameterMapping("")
@PageTitle("Reset Password")
public class ResetPasswordView extends VerticalLayout implements HasUrlParameterMapping {

    ResetTokenService resetTokenService;
    UserService userService;

    String token;

    EmailField emailField;
    PasswordField passwordField;
    PasswordField repeatPasswordField;

    public ResetPasswordView(ResetTokenService resetTokenService, UserService userService) {
        this.resetTokenService = resetTokenService;
        this.userService = userService;
    }

    @UrlParameter(name = "token")
    public void setToken(String token) {
        this.token = token;
        setSizeFull();
        setClassName("login-screen");
        getStyle().set("padding-top","10%");
        init();
    }

    private void init() {
        removeAll();
        FormLayout formLayout = new FormLayout();
        formLayout.setHeightFull();
        formLayout.getStyle().set("align-self","center");
        formLayout.getElement().setAttribute("part", "vaadin-login-native-form-wrapper");
        formLayout.getElement().getStyle().set("max-width", "calc(var(--lumo-size-m) * 10)");
        H2 label = new H2(getTranslation("changePassword"));
        Users users = resetTokenService.getUserByToken(token);
        if (users!=null){
            Binder<UsersDto> binder = new Binder<>();
            UsersDto usersDto = new UsersDto();
            emailField = new EmailField(getTranslation("email"));
            emailField.setReadOnly(true);
            emailField.setValue(users.getUserEmail());
            passwordField = new PasswordField(getTranslation("password"));
            binder.forField(passwordField)
                    .withValidator(new StringLengthValidator(getTranslation("fill") + passwordField.getLabel(), 1, 999))
                    .bind(UsersDto::getPass, UsersDto::setPass);
            repeatPasswordField = new PasswordField(getTranslation("confirmPassword"));
            binder.forField(repeatPasswordField).withValidator(value -> value.equals(passwordField.getValue()),
                    getTranslation("signUp.passwordMessage"))
                    .bind(UsersDto::getRepeatPass, UsersDto::setRepeatPass);
            Button reset = new Button(getTranslation("updatePassword"));
            reset.getElement().getThemeList().set("primary contained", true);
            reset.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
                @Override
                public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                    if (binder.writeBeanIfValid(usersDto)) {
                        try {
                            resetTokenService.resetPassword(users.getUserEmail(),usersDto.getPass());
                        }
                        catch (Exception e) {
                            throw e;
                        }
                        UI.getCurrent().navigate(SignInView.class);
                    }
                }
            });
            formLayout.add(label,emailField,passwordField,repeatPasswordField, reset);
            add(formLayout);
        }
        else formLayout.add(getTranslation("resetTokenMessage"));
        add(formLayout);
    }


}
