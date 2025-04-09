package rental.project.service.user;

import rental.project.dto.auth.UserRegisterRequestDto;
import rental.project.dto.auth.UserRegisterResponseDto;
import rental.project.dto.user.UpdateUserDataDto;
import rental.project.dto.user.UpdateUserRoleDto;
import rental.project.dto.user.UserDto;
import rental.project.exception.RegistrationException;

public interface UserService {
    UserRegisterResponseDto save(UserRegisterRequestDto userRegisterRequestDto)
            throws RegistrationException;

    UserDto changeUserRole(Long userId, UpdateUserRoleDto roleUpdateDto);

    UserDto getCurrentUserData();

    UserDto updateUserData(UpdateUserDataDto userDto);
}
