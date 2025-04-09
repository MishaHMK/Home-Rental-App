package rental.project.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @NotBlank(message = "Email is required")
        @Length(min = 8, max = 30, message = "Email size must be between "
            + "8 and 30 symbols inclusively")
        @Email(message = "Invalid email format")
        String email,
        @NotBlank(message = "Password is required")
        @Length(min = 8, max = 20, message = "Password size must be between "
            + "8 and 20 symbols inclusively")
        String password) {
}
