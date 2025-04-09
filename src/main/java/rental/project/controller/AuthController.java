package rental.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rental.project.dto.auth.UserLoginRequestDto;
import rental.project.dto.auth.UserLoginResponseDto;
import rental.project.dto.auth.UserRegisterRequestDto;
import rental.project.dto.auth.UserRegisterResponseDto;
import rental.project.security.AuthenticationService;
import rental.project.service.user.UserService;

@Tag(name = "Auth controller", description = "User authorization endpoint")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationService authService;

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Create new user in system with provided data")
    public UserRegisterResponseDto register(
            @Valid @RequestBody UserRegisterRequestDto registerRequestDto) {
        return userService.save(registerRequestDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Login user to receive JWT Token")
    public UserLoginResponseDto login(
            @Valid @RequestBody UserLoginRequestDto loginRequestDto) {
        return authService.login(loginRequestDto);
    }
}
