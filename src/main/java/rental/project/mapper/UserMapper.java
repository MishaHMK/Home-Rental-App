package rental.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import rental.project.config.MapperConfig;
import rental.project.dto.auth.UserRegisterRequestDto;
import rental.project.dto.auth.UserRegisterResponseDto;
import rental.project.dto.user.UpdateUserDataDto;
import rental.project.dto.user.UserDto;
import rental.project.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toUser(UserRegisterRequestDto dto);

    UserDto toUserDto(User user);

    UserRegisterResponseDto toResponse(User user);

    void updateFromDto(UpdateUserDataDto dto, @MappingTarget User user);

    @Named("userFromId")
    default User userFromId(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
