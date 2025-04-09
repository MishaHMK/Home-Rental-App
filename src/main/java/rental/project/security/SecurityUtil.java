package rental.project.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import rental.project.model.User;

public class SecurityUtil {
    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static User getLoggedInUser() {
        return ((User) getAuthentication().getPrincipal());
    }
}
