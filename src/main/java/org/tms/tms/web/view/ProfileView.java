package org.tms.tms.web.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
import org.tms.tms.security.SecurityUtils;
import org.tms.tms.security.dao.Users;
import org.tms.tms.security.service.UserService;

@CssImport("./styles/shared-styles.css")
@Route(value = "profile", layout = MainPage.class)
@PageTitle("Profile")
public class ProfileView extends Div {

    UserService userService;
    private Users users;
    VerticalLayout verticalLayoutMain;


    public ProfileView(UserService userService) {
        this.userService = userService;
        verticalLayoutMain = new VerticalLayout();
        verticalLayoutMain.setSizeFull();
        init();
        add(verticalLayoutMain);
    }

    public void init() {
        verticalLayoutMain.removeAll();
        users = userService.getUserByEmail(SecurityUtils.getLoggedUser().getUsername());
        HorizontalLayout left = new HorizontalLayout();
        left.setWidth("15%");
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(new H2("Profile"));
        verticalLayout.add(new Text("Тут будет ваше фото"));
        left.add(verticalLayout);
        HorizontalLayout right = new HorizontalLayout();
        VerticalLayout settings = new VerticalLayout();
        settings.add(changeProfile(),changePass());
        right.setWidth("85%");
        right.add(settings);
        HorizontalLayout all = new HorizontalLayout();
        all.setSizeFull();
        all.add(left, right);
        verticalLayoutMain.add(all);
    }

    private Component changeProfile() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        UsersDto usersDto = new UsersDto(users);
        usersDto.setPass(null);
        Binder<UsersDto> binder = new Binder<>();
        EmailField emailField = new EmailField("Email");
        emailField.setWidth("30em");
        emailField.setValue(usersDto.getEmail());
        binder.forField(emailField)
                .withValidator(new EmailValidator("Fill valid " + emailField.getLabel()))
                .bind(UsersDto::getEmail, UsersDto::setEmail);
        TextField fullNameField = new TextField("Full Name");
        fullNameField.setValue(usersDto.getFullName());
        fullNameField.setWidth("30em");
        binder.forField(fullNameField)
                .withValidator(new StringLengthValidator("Fill " + fullNameField.getLabel(), 1, 999))
                .bind(UsersDto::getFullName, UsersDto::setFullName);
        Button save = new Button("Save");
        save.addClickListener(buttonClickEvent -> {
            if(binder.writeBeanIfValid(usersDto)){
                userService.update(usersDto.getEmail(),usersDto);
                Notification.show("Profile updated successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                init();
            }
        });
        H4 h3 = new H4("Profile settings");
        verticalLayout.add(h3, emailField, fullNameField, save);
        return verticalLayout;
    }

    private Component changePass() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        Binder<UsersDto> binder = new Binder<>();
        UsersDto usersDto = new UsersDto(users);
        PasswordField passwordField = new PasswordField("New Password");
        passwordField.setWidth("30em");
        binder.forField(passwordField)
                .withValidator(new StringLengthValidator("Fill " + passwordField.getLabel(), 1, 999))
                .withValidator(value -> {
                    return !userService.checkPass(usersDto.getPass(), value);
                }, "Password match the current password")
                .bind(UsersDto::getPass, UsersDto::setPass);
        PasswordField repeatPasswordField = new PasswordField("Repeat New Password");
        repeatPasswordField.setWidth("30em");
        binder.forField(repeatPasswordField).withValidator(value -> value.equals(passwordField.getValue()),
                "Password and Repeat Password not equals")
                .bind(UsersDto::getRepeatPass, UsersDto::setRepeatPass);
        H4 h3 = new H4("Change password");
        Button save = new Button("Update password");
        save.addClickListener(buttonClickEvent -> {
            if(binder.writeBeanIfValid(usersDto)){
                userService.update(usersDto.getEmail(),usersDto);
                Notification.show("Password updated successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                init();
            }
        });
        verticalLayout.add(h3,passwordField, repeatPasswordField,save);
        return verticalLayout;
    }


}
