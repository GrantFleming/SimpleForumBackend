package co.uk.grantgfleming.SimpleForumBackend.security;

import co.uk.grantgfleming.SimpleForumBackend.security.JWT.JWTService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class SecurityController {

    private final JWTService jwtService;

    public SecurityController(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/token")
    String getJWTToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return jwtService.generateJWT(authentication);
    }

}
