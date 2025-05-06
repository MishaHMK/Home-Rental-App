package rental.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import rental.project.service.user.UserServiceImpl;
import rental.project.supplier.UserSupplier;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Verify correct user dto")
    public void getCurrentUserData_ShouldReturnUserDto() {
        //Given (Arrange)
        User user = UserSupplier.getUser();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        UserDto userDto = UserSupplier.getUserDto();

        when(userMapper.toUserDto(user)).thenReturn(userDto);
        when(SecurityUtil.getLoggedInUser()).thenReturn(user);

        //When (Act)
        UserDto actual = userService.getCurrentUserData();

        //Then (Assert)
        assertNotNull(actual);
        assertEquals(actual, userDto);

        verify(userMapper).toUserDto(any(User.class));
    }

    @Test
    @DisplayName("Verify correct user by using valid data")
    public void save_WithValidUserData_ShouldReturnUserDto() {
        //Given (Arrange)
        UserRegisterRequestDto registerRequestDto = UserSupplier.getUserRegisterRequestDto();
        User user = UserSupplier.getUser();
        UserRegisterResponseDto registerResponseDto = UserSupplier.getRegisterResponseDto();

        when(userMapper.toUser(registerRequestDto)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());
        when(userMapper.toResponse(user)).thenReturn(registerResponseDto);

        //When (Act)
        UserRegisterResponseDto actual = userService.save(registerRequestDto);

        //Then (Assert)
        assertNotNull(actual);
        assertEquals(actual, registerResponseDto);

        verify(userMapper).toUser(registerRequestDto);
        verify(passwordEncoder).encode(user.getPassword());
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Verify exception thrown by using invalid data")
    public void save_WithInvalidUserData_ThrowsException() {
        //Given (Arrange)
        UserRegisterRequestDto registerRequestDto = UserSupplier.getUserRegisterRequestDto();

        when(userRepository.existsByEmail(registerRequestDto.getEmail()))
                .thenReturn(true);

        //When (Act)
        RegistrationException exception = assertThrows(
                RegistrationException.class,
                () -> userService.save(registerRequestDto));

        //Then (Assert)
        String expectedMessage = "User with email "
                + registerRequestDto.getEmail()
                + " already exists";
        assertEquals(expectedMessage, exception.getMessage());
        verify(userRepository, times(1))
                .existsByEmail(registerRequestDto.getEmail());
    }

    @Test
    @DisplayName("Verify correct user dto by using correct data")
    public void updateUserData_WithValidData_ReturnUserDto() {
        //Given (Arrange)
        User user = UserSupplier.getUser();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        UserDto userDto = UserSupplier.getUserDto();
        UpdateUserDataDto updateUserDataDto = UserSupplier.getUpdateUserDataDto();

        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        //When (Act)
        UserDto actualData = userService.updateUserData(updateUserDataDto);

        //Then (Assert)
        assertEquals(actualData, userDto);
        verify(userRepository, times(1))
                .save(user);
        verify(userMapper, times(1))
                .toUserDto(user);
    }

    @Test
    @DisplayName("Verify correct user dto by using correct data")
    public void changeUserRole_WithValidData_ReturnUserDto() {
        //Given (Arrange)
        UserDto userDto = UserSupplier.getUserDto();
        UpdateUserRoleDto roleDto = UserSupplier.getUpdateUserRoleDto();
        User user = UserSupplier.getUser();
        Long validId = user.getId();

        when(userRepository.findById(validId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        //When (Act)
        UserDto actualData = userService.changeUserRole(validId, roleDto);

        //Then (Assert)
        assertEquals(actualData, userDto);
        verify(userRepository, times(1))
                .findById(validId);
        verify(userRepository, times(1))
                .save(user);
        verify(userMapper, times(1))
                .toUserDto(user);
    }

    @Test
    @DisplayName("Verify exception thrown by using invalid data")
    public void changeUserRole_WithValidData_ThrowsException() {
        //Given (Arrange)
        UpdateUserRoleDto roleDto = UserSupplier.getUpdateUserRoleDto();
        Long invalidId = 3L;

        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        //When (Act)
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.changeUserRole(invalidId, roleDto)
        );

        //Then (Assert)
        String expectedMessage = "User with id " + invalidId + " not found";
        assertEquals(expectedMessage, exception.getMessage());
        verify(userRepository, times(1))
                .findById(invalidId);
    }
}
