package org.tms.tms.web.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.tms.tms.dto.UsersDto;
import org.tms.tms.security.SecurityUtils;
import org.tms.tms.security.dao.Users;
import org.tms.tms.security.service.UserService;
import org.tms.tms.web.ReloadPage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@CssImport("./styles/shared-styles.css")
@Route(value = "profile", layout = MainPage.class)
@PageTitle("Profile")
public class ProfileView extends Div implements LocaleChangeObserver {

    private final UserService userService;
    private Users users;
    private final VerticalLayout verticalLayoutMain;
    private AuthenticationManager authenticationManager;

    public ProfileView(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
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
        verticalLayout.add(new H2(getTranslation("profile")));
        //
        if (users.getImage() != null) {
            verticalLayout.add(convertToImage(Base64.getDecoder().decode(users.getImage())));
        } else {
            verticalLayout.add(new Text("Нет фото"));
        }
        verticalLayout.add(uploadPhoto());
        left.add(verticalLayout);
        HorizontalLayout right = new HorizontalLayout();
        VerticalLayout settings = new VerticalLayout();
        settings.add(changeProfile(), changePass());
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
        String email = users.getUserEmail();
        usersDto.setPass(null);
        Binder<UsersDto> binder = new Binder<>();
        EmailField emailField = new EmailField(getTranslation("email"));
        emailField.setWidth("30em");
        emailField.setValue(usersDto.getEmail());
        binder.forField(emailField)
                .withValidator(new EmailValidator(getTranslation("fillValid") + emailField.getLabel()))
                .bind(UsersDto::getEmail, UsersDto::setEmail);
        TextField fullNameField = new TextField(getTranslation("fullName"));
        fullNameField.setValue(usersDto.getFullName());
        fullNameField.setWidth("30em");
        binder.forField(fullNameField)
                .withValidator(new StringLengthValidator(getTranslation("fill") + fullNameField.getLabel(), 1, 999))
                .bind(UsersDto::getFullName, UsersDto::setFullName);
        Button save = new Button(getTranslation("save"));
        save.addClickListener(buttonClickEvent -> {
            if (binder.writeBeanIfValid(usersDto)) {
                userService.update(email, usersDto);
                Notification.show(getTranslation("successUpdateProfile")).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                if (!email.equals(usersDto.getEmail())) {
                    VaadinSession.getCurrent().getSession().invalidate();
                }
                UI.getCurrent().getPage().reload();
            }
        });
        H4 h3 = new H4(getTranslation("profileSettings"));
        verticalLayout.add(h3, emailField, fullNameField, save);
        return verticalLayout;
    }

    private Component changePass() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        Binder<UsersDto> binder = new Binder<>();
        UsersDto usersDto = new UsersDto(users);
        PasswordField passwordField = new PasswordField(getTranslation("newPassword"));
        passwordField.setWidth("30em");
        binder.forField(passwordField)
                .withValidator(new StringLengthValidator(getTranslation("fill") + passwordField.getLabel(), 1, 999))
                .withValidator(value -> {
                    return !userService.checkPass(usersDto.getPass(), value);
                }, getTranslation("notificationMatchPassword"))
                .bind(UsersDto::getPass, UsersDto::setPass);
        PasswordField repeatPasswordField = new PasswordField(getTranslation("confirmPassword"));
        repeatPasswordField.setWidth("30em");
        binder.forField(repeatPasswordField).withValidator(value -> value.equals(passwordField.getValue()),
                getTranslation("signUp.passwordMessage"))
                .bind(UsersDto::getRepeatPass, UsersDto::setRepeatPass);
        H4 h3 = new H4(getTranslation("changePassword"));
        Button save = new Button(getTranslation("updatePassword"));
        save.addClickListener(buttonClickEvent -> {
            if (binder.writeBeanIfValid(usersDto)) {
                userService.update(usersDto.getEmail(), usersDto);
                Notification.show(getTranslation("successUpdatePassword")).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                UI.getCurrent().getPage().reload();
            }
        });
        verticalLayout.add(h3, passwordField, repeatPasswordField, save);
        return verticalLayout;
    }

    private Image convertToImage(byte[] imageData) {
        StreamResource streamResource = new StreamResource("isr", new InputStreamFactory() {
            @Override
            public InputStream createInputStream() {
                return new ByteArrayInputStream(resize(imageData));
            }
        });
        return new Image(streamResource, "photo");
    }


    private byte[] resize(byte[] bytes) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedImage resizedImage = new BufferedImage(200, 200, image.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, 200, 200, null);
        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean foundWriter = false;
        try {
            foundWriter = ImageIO.write(resizedImage, "jpg", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert foundWriter;
        return baos.toByteArray();
    }

    private Upload uploadPhoto() {
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload singleFileUpload = new Upload(memoryBuffer);
        singleFileUpload.addSucceededListener(event -> {
            UsersDto usersDto = new UsersDto(users);
            usersDto.setPass(null);
            usersDto.setImage(null);
            try {
                usersDto.setImage(Base64.getEncoder().encodeToString(memoryBuffer.getInputStream().readAllBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            userService.update(usersDto.getEmail(), usersDto);
            UI.getCurrent().getPage().reload();
        });
        return singleFileUpload;
    }


    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        ReloadPage.reloadPage(localeChangeEvent, this.getClass());
    }

}
