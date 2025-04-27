package rental.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rental.project.dto.user.UpdateUserDataDto;
import rental.project.dto.user.UpdateUserRoleDto;
import rental.project.dto.user.UserDto;
import rental.project.service.user.UserService;

@Tag(name = "User controller", description = "User management endpoint")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("{userId}/role")
    @Operation(summary = "Update user role",
            description = "Update role of the selected user by his id")
    public UserDto updateUserRole(@PathVariable Long userId,
                                  @RequestBody UpdateUserRoleDto userDto) {
        return userService.changeUserRole(userId, userDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping("me")
    @Operation(summary = "Get user info",
            description = "Receive currently logged in user info")
    public UserDto receiveCurrentUserInfo() {
        return userService.getCurrentUserData();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @PutMapping("update")
    @Operation(summary = "Update user info",
            description = "Update currently logged in user profile info")
    public UserDto updateCurrentUserInfo(@RequestBody UpdateUserDataDto userDto) {
        return userService.updateUserData(userDto);
    }
}
