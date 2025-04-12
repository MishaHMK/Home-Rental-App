package rental.project.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import rental.project.model.User;

@Component("securityUtil")
public class SecurityUtil {
    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static User getLoggedInUser() {
        return ((User) getAuthentication().getPrincipal());
    }

    public static Long getLoggedInUserId() {
        return ((User) getAuthentication().getPrincipal()).getId();
    }
}
