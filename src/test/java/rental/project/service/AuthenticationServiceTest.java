package rental.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import rental.project.dto.auth.UserLoginRequestDto;
import rental.project.dto.auth.UserLoginResponseDto;
import rental.project.security.AuthenticationService;
import rental.project.security.jwt.JwtUtil;
import rental.project.supplier.UserSupplier;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("Login returns token with valid credentials")
    void login_ValidCredentials_ReturnsToken() {
        //Given (Arrange)
        UserLoginRequestDto loginDto = UserSupplier.getLoginRequestDto();
        String email = loginDto.email();
        String expectedToken = UserSupplier.getUserLoginResponseDto().token();

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        when(jwtUtil.generateToken(email)).thenReturn(expectedToken);

        //When (Act)
        UserLoginResponseDto responseDto = authenticationService.login(loginDto);

        //Then (Assert)
        assertNotNull(responseDto);
        assertEquals(expectedToken, responseDto.token());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(email);
    }
}
