package rental.project.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import rental.project.model.User;

@Data
public class UpdateUserRoleDto {
    @NotBlank(message = "New role is required")
    private User.Role role;
}
