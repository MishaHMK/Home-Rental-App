package rental.project.dto.user;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String role) {
}
