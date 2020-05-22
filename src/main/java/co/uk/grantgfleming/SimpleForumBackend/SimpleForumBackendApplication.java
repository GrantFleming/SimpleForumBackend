package co.uk.grantgfleming.SimpleForumBackend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
		return new WebMvcConfigurer() {
			//lol
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/forums/**").allowedOrigins(frontendOrigin);
				registry.addMapping("/posts/**").allowedOrigins(frontendOrigin);
			}
		};
	}
}
