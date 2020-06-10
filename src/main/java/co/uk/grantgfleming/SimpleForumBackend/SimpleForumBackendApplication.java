package co.uk.grantgfleming.SimpleForumBackend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Clock;

@SpringBootApplication
public class SimpleForumBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleForumBackendApplication.class, args);
	}

	@Value("${frontend.url}")
	private String frontendOrigin;

	/**
	 * Configure CORS to allow access from the frontEnd
	 */
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new MvcConfig();
	}

	@Bean
	public Clock applicationClock() {
		return Clock.systemDefaultZone();
	}

	private class MvcConfig implements WebMvcConfigurer {
		@Override
		public void addCorsMappings(CorsRegistry registry) {
			registry.addMapping("/**").allowedOrigins(frontendOrigin);
		}
	}
}
