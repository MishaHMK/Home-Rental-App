package rental.project.supplier;

import rental.project.dto.auth.UserLoginRequestDto;
import rental.project.dto.auth.UserLoginResponseDto;
import rental.project.dto.auth.UserRegisterRequestDto;
import rental.project.dto.auth.UserRegisterResponseDto;
import rental.project.dto.user.UpdateUserDataDto;
import rental.project.dto.user.UpdateUserRoleDto;
import rental.project.dto.user.UserDto;
import rental.project.model.User;
import rental.project.model.User.Role;

public class UserSupplier {
    public static User getUser() {
        return new User().setId(1L)
                .setFirstName("Jane")
                .setLastName("Doe")
                .setEmail("janedoe@gmail.com")
                .setPassword("password")
                .setRole(Role.ADMIN)
                .setDeleted(false);
    }

    public static User getCustomerUser() {
        return new User().setId(2L)
                .setFirstName("Bob")
                .setLastName("Doe")
                .setEmail("bobdoe@gmail.com")
                .setPassword("password")
                .setRole(Role.CUSTOMER)
                .setDeleted(false);
    }

    public static UserDto getUserDto() {
        return new UserDto(1L, "janedoe@gmail.com",
                "Jane", "Doe", "ADMIN");
    }

    public static UserDto getUpdatedUserDto() {
        return new UserDto(3L, "janedoe@gmail.com",
                "Jane", "Doer", "ADMIN");
    }

    public static UserDto getNewUserDto() {
        return new UserDto(3L, "janedor@gmail.com",
                "Jane", "Doe", "ADMIN");
    }

    public static UserDto getAuthorizedDto() {
        return new UserDto(3L, "janedom@gmail.com",
                "Jane", "Dom", "ADMIN");
    }

    public static UserLoginRequestDto getLoginRequestDto() {
        return new UserLoginRequestDto("janedoe@gmail.com", "password");
    }

    public static UserLoginResponseDto getUserLoginResponseDto() {
        return new UserLoginResponseDto("test_super_token");
    }

    public static UserRegisterRequestDto getUserRegisterRequestDto() {
        return new UserRegisterRequestDto()
                .setFirstName("Jane")
                .setLastName("Doe")
                .setEmail("janedoe@gmail.com")
                .setPassword("password")
                .setConfirmPassword("password")
                .setRole("ADMIN");
    }

    public static UserRegisterResponseDto getRegisterResponseDto() {
        return new UserRegisterResponseDto().setId(1L)
                .setFirstName("Jane")
                .setLastName("Doe")
                .setEmail("janedoe@gmail.com")
                .setRole("ADMIN");
    }

    public static UserDto getNewUpdatedUserDto() {
        return new UserDto(3L, "janedom@gmail.com",
                "Jane", "Dom", "ADMIN");
    }

    public static UpdateUserRoleDto getUpdateUserRoleDto() {
        return new UpdateUserRoleDto().setRole(Role.ADMIN);
    }

    public static UpdateUserDataDto getUpdateUserDataDto() {
        return new UpdateUserDataDto()
                .setFirstName("Jane")
                .setLastName("Doe");
    }

    public static UpdateUserDataDto getNewUpdateUserDataDto() {
        return new UpdateUserDataDto()
                .setFirstName("Jane")
                .setLastName("Dom");
    }
}
