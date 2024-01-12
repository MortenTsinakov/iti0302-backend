package ee.taltech.iti0302_veebiarendus_backend.user.mapper;

import ee.taltech.iti0302_veebiarendus_backend.user.dto.ChangeUsernameResponse;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.FollowersResponse;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.FollowingResponse;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.UserDto;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto userToUserDto(User user);
    List<UserDto> usersToUserDtoList(List<User> users);
    ChangeUsernameResponse userToChangeUsernameResponse(User user, String jwt);
    List<FollowersResponse> usersToFollowerResponseList(List<User> users);
    List<FollowingResponse> usersToFollowingResponseList(List<User> users);
}
