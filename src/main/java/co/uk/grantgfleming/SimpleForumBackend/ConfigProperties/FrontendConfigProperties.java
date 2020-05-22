package co.uk.grantgfleming.SimpleForumBackend.ConfigProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "frontend")
public class FrontendConfigProperties {

    private String url;

}
