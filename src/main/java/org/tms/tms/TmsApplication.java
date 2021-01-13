package org.tms.tms;

import ch.carnet.kasparscherrer.LanguageSelect;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.LoadingIndicatorConfiguration;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.tms.tms.config.VaadinI18NProvider;
import org.tms.tms.security.SecurityUtils;
import org.tms.tms.web.view.InviteView;
import org.tms.tms.web.view.ResetPasswordView;
import org.tms.tms.web.view.SignInView;
import org.tms.tms.web.view.SignUpView;

import static java.lang.System.setProperty;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@ComponentScan({"org.tms.tms*"})
@EntityScan("org.tms.tms*")
@EnableJpaRepositories("org.tms.tms*")
@PWA(name = "TMS", shortName = "TMS")
public class TmsApplication extends SpringBootServletInitializer implements VaadinServiceInitListener,
        AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(TmsApplication.class, args);
    }

    @Override
    public void serviceInit(ServiceInitEvent initEvent) {
		/*final AccessControl accessControl = AccessControlFactory.getInstance()
				.createAccessControl();*/
        setProperty("vaadin.i18n.provider", VaadinI18NProvider.class.getName());
        LanguageSelect.readLanguageCookies(initEvent);
        initEvent.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
            ui.addBeforeEnterListener(this::beforeEnter);
        });

        initEvent.getSource().addUIInitListener(uiInitEvent -> {
            LoadingIndicatorConfiguration conf = uiInitEvent.getUI().getLoadingIndicatorConfiguration();

            // disable default theme -> loading indicator will not be shown
            conf.setApplyDefaultTheme(true);
        });
    }

    private void beforeEnter(BeforeEnterEvent event) {
        if (!isLoggedIn()) {
            String target = event.getLocation().getPath().toLowerCase();
            if (getPathView(SignUpView.class).equals(target)) {
                event.rerouteTo(SignUpView.class);
            } else if (getPathView(SignInView.class).equals(target)) {
                event.rerouteTo(SignInView.class);
            } else if (target.contains(getPathView(ResetPasswordView.class))) {

            } else if (target.contains(getPathView(InviteView.class))) {

            } else event.rerouteTo(SignInView.class);
        }
        else {
            String s = "";
        }
    }

    private boolean isLoggedIn() {
        return SecurityUtils.isUserLoggedIn();
    }

    private String getPathView(Class<? extends Component> navigationTarget) {
        return RouteConfiguration.forSessionScope().getUrlBase(navigationTarget).get().toLowerCase();
    }
}
