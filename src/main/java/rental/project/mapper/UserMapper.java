package rental.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import rental.project.config.MapperConfig;
import rental.project.dto.auth.UserRegisterRequestDto;
import rental.project.dto.auth.UserRegisterResponseDto;
import rental.project.dto.user.UpdateUserDataDto;
import rental.project.dto.user.UserDto;
import rental.project.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toUser(UserRegisterRequestDto dto);

    UserDto toUserDto(User dto);

    UserRegisterResponseDto toResponse(User user);

    void updateFromDto(UpdateUserDataDto dto, @MappingTarget User user);
}
