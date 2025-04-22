package rental.project.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;
import rental.project.model.User;

@Data
@Accessors(chain = true)
public class UpdateUserRoleDto {
    @NotBlank(message = "New role is required")
    private User.Role role;
}
