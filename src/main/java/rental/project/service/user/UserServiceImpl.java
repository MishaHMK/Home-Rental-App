package rental.project.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import rental.project.dto.auth.UserRegisterRequestDto;
import rental.project.dto.auth.UserRegisterResponseDto;
import rental.project.dto.user.UpdateUserDataDto;
import rental.project.dto.user.UpdateUserRoleDto;
import rental.project.dto.user.UserDto;
import rental.project.exception.RegistrationException;
import rental.project.mapper.UserMapper;
import rental.project.model.User;
import rental.project.repository.user.UserRepository;
import rental.project.security.SecurityUtil;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserRegisterResponseDto save(UserRegisterRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User with email "
                    + requestDto.getEmail() + " already exists");
        }
        User user = userMapper.toUser(requestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public UserDto changeUserRole(Long userId, UpdateUserRoleDto roleUpdateDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with id " + userId + " not found")
        );
        user.setRole(roleUpdateDto.getRole());
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getCurrentUserData() {
        return userMapper.toUserDto(SecurityUtil.getLoggedInUser());
    }

    @Override
    public UserDto updateUserData(UpdateUserDataDto updateDto) {
        User currentUser = SecurityUtil.getLoggedInUser();
        userMapper.updateFromDto(updateDto, currentUser);
        return userMapper.toUserDto(userRepository.save(currentUser));
    }
}
