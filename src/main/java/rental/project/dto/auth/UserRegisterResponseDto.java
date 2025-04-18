package rental.project.dto.auth;

import lombok.Data;

@Data
public class UserRegisterResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
}
