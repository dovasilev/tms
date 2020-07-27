package org.tms.tms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"org.tms.tms*"})
@EntityScan("org.tms.tms*")
@EnableJpaRepositories("org.tms.tms*")
public class TmsApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(TmsApplication.class, args);
	}

}
