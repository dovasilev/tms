package org.tms.tms.web.components;

import com.vaadin.flow.component.avatar.Avatar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tms.tms.security.SecurityUtils;
import org.tms.tms.security.service.UserService;

@Component
public class AvatarComponent {

    @Autowired
    private UserService userService;

    private final Avatar avatar;

    public AvatarComponent() {
        avatar = new Avatar();
        avatar.getStyle()
                .set("margin-right", "1em")
                .set("margin-left", "1em");
    }

    public Avatar updateFullName() {
        String fullName = userService.getUserByEmail(SecurityUtils.getLoggedUser().getUsername()).getFullName();
        avatar.setName(fullName);
        return avatar;
    }
}
