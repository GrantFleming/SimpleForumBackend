package co.uk.grantgfleming.SimpleForumBackend.security;

import co.uk.grantgfleming.SimpleForumBackend.security.JWT.BearerAuthenticationFilter;
import co.uk.grantgfleming.SimpleForumBackend.security.JWT.JWTAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
        Basic authentication to get the JWT token
     */
    @Configuration
    @Order(1)
    public static class JWTSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/auth/token")
                    .csrf()
                    .disable()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .anonymous()
                    .disable()
                    .authorizeRequests(authorize -> authorize.anyRequest().authenticated())
                    .httpBasic(withDefaults());
        }
    }


    /*
        Custom JWT authentication on all /api endpoints
        Registration endpoint open to all
     */
    @Configuration
    public static class APISecurityConfig extends WebSecurityConfigurerAdapter {

        String protectedEndpoints = "/api/**";
        AuthenticationManager authenticationManager;

        APISecurityConfig(JWTAuthenticationManager authenticationManager) {
            this.authenticationManager = authenticationManager;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            BearerAuthenticationFilter jwtAuthenticationFilter = getJWTAuthenticationFilter();

            http.antMatcher(protectedEndpoints)
                    .csrf()
                    .disable()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .requestCache()
                    .disable()
                    .logout()
                    .disable()
                    .addFilterBefore(jwtAuthenticationFilter, SecurityContextHolderAwareRequestFilter.class)
                    .authorizeRequests(authorize -> authorize.antMatchers(HttpMethod.POST, "/user/register**")
                            .permitAll()
                            .antMatchers(HttpMethod.GET, "/api/forums/**", "/api/posts/**")
                            .permitAll()
                            .antMatchers(HttpMethod.POST, "/api/forums", "/api/posts")
                            .hasRole("USER")
                            .anyRequest()
                            .denyAll());
        }

        private BearerAuthenticationFilter getJWTAuthenticationFilter() {
            BearerAuthenticationFilter bearerAuthenticationFilter = new BearerAuthenticationFilter(protectedEndpoints);
            bearerAuthenticationFilter.setAuthenticationSuccessHandler((req, res, auth) -> {
            });
            bearerAuthenticationFilter.setAuthenticationManager(authenticationManager);
            return bearerAuthenticationFilter;
        }
    }


}
