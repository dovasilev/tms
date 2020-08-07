package org.tms.tms;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.LoadingIndicatorConfiguration;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.tms.tms.security.SecurityUtils;
import org.tms.tms.web.view.LoginPage;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@ComponentScan({"org.tms.tms*"})
@EntityScan("org.tms.tms*")
@EnableJpaRepositories("org.tms.tms*")
public class TmsApplication extends SpringBootServletInitializer implements VaadinServiceInitListener {

	public static void main(String[] args) {
		SpringApplication.run(TmsApplication.class, args);
	}

	@Override
	public void serviceInit(ServiceInitEvent initEvent) {
		/*final AccessControl accessControl = AccessControlFactory.getInstance()
				.createAccessControl();*/

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
		if (!LoginPage.class.equals(event.getNavigationTarget())
				&& !SecurityUtils.isUserLoggedIn()) {
			event.rerouteTo(LoginPage.class);
		}
	}

	

}
