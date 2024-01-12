package ee.taltech.iti0302_veebiarendus_backend.user.service;

import ee.taltech.iti0302_veebiarendus_backend.album.entity.Like;
import ee.taltech.iti0302_veebiarendus_backend.auth.service.AuthenticationService;
import ee.taltech.iti0302_veebiarendus_backend.auth.service.JwtService;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.InvalidInputException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.InvalidOperationException;
import ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions.UserNotFoundException;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.ChangeEmailRequest;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.ChangePasswordRequest;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.ChangeUsernameRequest;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.ChangeUsernameResponse;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.DeleteUserRequest;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.FollowRequest;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.FollowerStatsResponse;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.FollowersResponse;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.FollowingResponse;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.UserDto;
import ee.taltech.iti0302_veebiarendus_backend.user.dto.UserProfileResponse;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.Follow;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import ee.taltech.iti0302_veebiarendus_backend.user.mapper.UserMapper;
import ee.taltech.iti0302_veebiarendus_backend.user.repository.FollowRepository;
import ee.taltech.iti0302_veebiarendus_backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FollowRepository followRepository;
    @Mock
    private AuthenticationManager authManager;
    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    @Test
    void searchUser() {
        String searchTerm = "user";
        Double threshold = 0.5;

        User user1 = new User();
        user1.setId(1);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2);
        user2.setUsername("user2");

        List<User> mockUserList = List.of(user1, user2);
        List<UserDto> mockUserDtoList = List.of(
                new UserDto(user1.getId(), user1.getUsername()),
                new UserDto(user2.getId(), user2.getUsername())
        );

        when(userRepository.fuzzySearch(searchTerm, threshold)).thenReturn(mockUserList);
        when(userMapper.usersToUserDtoList(mockUserList)).thenReturn(mockUserDtoList);

        List<UserDto> result = userService.searchUser(searchTerm);

        verify(userRepository).fuzzySearch(searchTerm, threshold);
        verify(userMapper).usersToUserDtoList(mockUserList);

        assertEquals(mockUserDtoList, result);
    }

    @Test
    void followUser() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        FollowRequest mockFollowRequest = new FollowRequest(2);

        User follower = new User();
        follower.setId(1);

        User followed = new User();
        followed.setId(2);

        when(authenticationService.getUserFromRequest(mockRequest)).thenReturn(Optional.of(follower));
        when(userRepository.getUserById(mockFollowRequest.userFollowedId())).thenReturn(Optional.of(followed));

        userService.followUser(mockRequest, mockFollowRequest);

        verify(authenticationService).getUserFromRequest(mockRequest);
        verify(userRepository).getUserById(mockFollowRequest.userFollowedId());
        verify(followRepository).save(argThat(follow -> follow.getFollowerId() == follower && follow.getFollowedId() == followed));
    }

    @Test
    void followUserWhenFollowerNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        FollowRequest followRequest = new FollowRequest(3);

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.followUser(request, followRequest));

        verify(authenticationService).getUserFromRequest(request);
    }

    @Test
    void followUserWhenFollowedNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        FollowRequest followRequest = new FollowRequest(3);

        User follower = new User();
        follower.setId(1);

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.of(follower));
        when(userRepository.getUserById(followRequest.userFollowedId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.followUser(request, followRequest));

        verify(authenticationService).getUserFromRequest(request);
        verify(userRepository).getUserById(followRequest.userFollowedId());
    }

    @Test
    void followUserWhenFollowerEqualsFollowed() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        FollowRequest followRequest = new FollowRequest(1);

        User user1 = new User();
        user1.setId(1);

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.of(user1));
        when(userRepository.getUserById(user1.getId())).thenReturn(Optional.of(user1));

        assertThrows(InvalidOperationException.class, () -> userService.followUser(request, followRequest));

        verify(authenticationService).getUserFromRequest(request);
        verify(userRepository).getUserById(user1.getId());
    }

    @Test
    void unfollowUser() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        FollowRequest followRequest = new FollowRequest(2);

        User follower = new User();
        follower.setId(1);

        User followed = new User();
        followed.setId(2);

        Follow follow = new Follow();
        follow.setId(1);
        follow.setFollowerId(follower);
        follow.setFollowedId(followed);

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.of(follower));
        when(userRepository.getUserById(followRequest.userFollowedId())).thenReturn(Optional.of(followed));
        when(followRepository.getFollowByFollowerIdAndFollowedId(follower, followed)).thenReturn(Optional.of(follow));

        userService.unfollowUser(request, followRequest);

        verify(authenticationService).getUserFromRequest(request);
        verify(userRepository).getUserById(followRequest.userFollowedId());
        verify(followRepository).getFollowByFollowerIdAndFollowedId(follower, followed);
        verify(followRepository).deleteById(follow.getId());
    }

    @Test
    void unfollowUserWhenFollowerNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        FollowRequest followRequest = new FollowRequest(2);

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.unfollowUser(request, followRequest));

        verify(authenticationService).getUserFromRequest(request);
    }

    @Test
    void unfollowUserWhenFollowedNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        FollowRequest followRequest = new FollowRequest(2);

        User follower = new User();
        follower.setId(1);

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.of(follower));
        when(userRepository.getUserById(followRequest.userFollowedId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.unfollowUser(request, followRequest));

        verify(authenticationService).getUserFromRequest(request);
        verify(userRepository).getUserById(followRequest.userFollowedId());
    }

    @Test
    void unfollowUserWhenNotFollows() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        FollowRequest followRequest = new FollowRequest(2);

        User follower = new User();
        follower.setId(1);

        User followed = new User();
        followed.setId(2);

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.of(follower));
        when(userRepository.getUserById(followRequest.userFollowedId())).thenReturn(Optional.of(followed));
        when(followRepository.getFollowByFollowerIdAndFollowedId(follower, followed)).thenReturn(Optional.empty());

        assertThrows(InvalidOperationException.class, () -> userService.unfollowUser(request, followRequest));

        verify(authenticationService).getUserFromRequest(request);
        verify(userRepository).getUserById(followRequest.userFollowedId());
        verify(followRepository).getFollowByFollowerIdAndFollowedId(follower, followed);
    }

    @Test
    void getUserProfile() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Integer id = 2;

        User userRequesting = new User();
        userRequesting.setId(1);

        User userRequested = new User();
        userRequested.setId(2);

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.of(userRequesting));
        when(userRepository.getUserById(id)).thenReturn(Optional.of(userRequested));
        when(followRepository.existsByFollowerIdAndFollowedId(userRequesting, userRequested)).thenReturn(true);

        ResponseEntity<UserProfileResponse> result = userService.getUserProfile(request, id);

        verify(authenticationService).getUserFromRequest(request);
        verify(userRepository).getUserById(id);
        verify(followRepository).existsByFollowerIdAndFollowedId(userRequesting, userRequested);

        assertEquals(ResponseEntity.ok(new UserProfileResponse(userRequested.getUsername(), true)), result);
    }

    @Test
    void getUserProfileWhenRequesterNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Integer id = 1;

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserProfile(request, id));

        verify(authenticationService).getUserFromRequest(request);
    }

    @Test
    void getUserProfileWhenRequestedNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Integer id = 2;

        User requester = new User();
        requester.setId(1);

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.of(requester));
        when(userRepository.getUserById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserProfile(request, id));

        verify(authenticationService).getUserFromRequest(request);
        verify(userRepository).getUserById(id);
    }

    @Test
    void getUserProfileWhenNotFollowing() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Integer id = 2;

        User userRequesting = new User();
        userRequesting.setId(1);

        User userRequested = new User();
        userRequested.setId(2);

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.of(userRequesting));
        when(userRepository.getUserById(id)).thenReturn(Optional.of(userRequested));
        when(followRepository.existsByFollowerIdAndFollowedId(userRequesting, userRequested)).thenReturn(false);

        ResponseEntity<UserProfileResponse> result = userService.getUserProfile(request, id);

        verify(authenticationService).getUserFromRequest(request);
        verify(userRepository).getUserById(id);
        verify(followRepository).existsByFollowerIdAndFollowedId(userRequesting, userRequested);

        assertEquals(ResponseEntity.ok(new UserProfileResponse(userRequested.getUsername(), false)), result);
    }

    @Test
    void changeUsername() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ChangeUsernameRequest changeUsernameRequest = new ChangeUsernameRequest("user");
        User user = new User();
        user.setId(1);
        String jwt = "jwt";
        ChangeUsernameResponse response = new ChangeUsernameResponse(user.getId(), changeUsernameRequest.newUsername(), jwt);

        when(userRepository.existsByUsername(changeUsernameRequest.newUsername())).thenReturn(false);
        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn(jwt);
        when(userMapper.userToChangeUsernameResponse(any(User.class), eq(jwt))).thenReturn(response);

        ResponseEntity<ChangeUsernameResponse> expected = ResponseEntity.ok(response);
        ResponseEntity<ChangeUsernameResponse> actual = userService.changeUsername(request, changeUsernameRequest);

        verify(userRepository).existsByUsername(changeUsernameRequest.newUsername());
        verify(authenticationService).getUserFromRequest(request);
        verify(jwtService).generateToken(any(User.class));
        verify(userMapper).userToChangeUsernameResponse(any(User.class), eq(jwt));
        verify(userRepository).save(argThat(u -> u.getUsername() == changeUsernameRequest.newUsername()));

        assertEquals(expected, actual);
    }

    @Test
    void changeUsernameUsernameIsBlank() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ChangeUsernameRequest changeUsernameRequest = new ChangeUsernameRequest("");

        assertThrows(InvalidInputException.class, () -> userService.changeUsername(request, changeUsernameRequest));
    }

    @Test
    void changeUsernameUsernameIsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ChangeUsernameRequest changeUsernameRequest = new ChangeUsernameRequest(null);

        assertThrows(InvalidInputException.class, () -> userService.changeUsername(request, changeUsernameRequest));
    }

    @Test
    void changeUsernameUsernameAlreadyInUse() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ChangeUsernameRequest changeUsernameRequest = new ChangeUsernameRequest("user");

        when(userRepository.existsByUsername(changeUsernameRequest.newUsername())).thenReturn(true);

        assertThrows(InvalidInputException.class, () -> userService.changeUsername(request, changeUsernameRequest));
        verify(userRepository).existsByUsername(changeUsernameRequest.newUsername());
    }

    @Test
    void changeUsernameUserNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ChangeUsernameRequest changeUsernameRequest = new ChangeUsernameRequest("user");

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.changeUsername(request, changeUsernameRequest));
    }

    @Test
    void changeEmail() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest("email@email.com");
        User user = new User();
        user.setId(1);
        String jwt = "jwt";

        when(userRepository.existsByEmail(changeEmailRequest.newEmail())).thenReturn(false);
        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.of(user));

        userService.changeEmail(request, changeEmailRequest);

        verify(userRepository).existsByEmail(changeEmailRequest.newEmail());
        verify(authenticationService).getUserFromRequest(request);
        verify(userRepository).save(argThat(u -> u.getEmail().equals(changeEmailRequest.newEmail())));
    }

    @Test
    void changeEmailUserNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest("email@email.com");

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.changeEmail(request, changeEmailRequest));
        verify(authenticationService).getUserFromRequest(request);
    }

    @Test
    void changeEmailEmailIsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest(null);

        assertThrows(InvalidInputException.class, () -> userService.changeEmail(request, changeEmailRequest));
    }

    @Test
    void changeEmailEmailIsBlank() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest("");

        assertThrows(InvalidInputException.class, () -> userService.changeEmail(request, changeEmailRequest));
    }

    @Test
    void changeEmailEmailAlreadyInUse() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest("email@email.com");

        when(userRepository.existsByEmail(changeEmailRequest.newEmail())).thenReturn(true);

        assertThrows(InvalidInputException.class, () -> userService.changeEmail(request, changeEmailRequest));
        verify(userRepository).existsByEmail(changeEmailRequest.newEmail());
    }

    @Test
    void changePassword() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password", "PASSWORD");
        Authentication authentication = mock(Authentication.class);
        String encodedPassword = "encodedPassword";

        User user = new User();

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(encoder.encode(changePasswordRequest.newPassword())).thenReturn(encodedPassword);

        userService.changePassword(request, changePasswordRequest);

        verify(authenticationService).getUserFromRequest(request);
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(encoder).encode(changePasswordRequest.newPassword());
        verify(userRepository).save(argThat(u -> u.getPassword().equals(encodedPassword)));
    }

    @Test
    void changePasswordPasswordIsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password", null);

        assertThrows(InvalidInputException.class, () -> userService.changePassword(request, changePasswordRequest));
    }

    @Test
    void changePasswordPasswordIsTooShort() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password", "pass");

        assertThrows(InvalidInputException.class, () -> userService.changePassword(request, changePasswordRequest));
    }

    @Test
    void deleteUser() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest("password");
        User user = new User();
        Authentication authentication = mock(Authentication.class);

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(authManager.authenticate(any(Authentication.class))).thenReturn(authentication);

        userService.deleteUser(request, deleteUserRequest);

        verify(authenticationService).getUserFromRequest(request);
        verify(authManager).authenticate(any(Authentication.class));
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserUserNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.deleteUser(request, new DeleteUserRequest("password")));
        verify(authenticationService).getUserFromRequest(request);
    }

    @Test
    void getFollowerStats() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Integer follows = 5;
        Integer followers = 2;
        FollowerStatsResponse response = new FollowerStatsResponse(followers, follows);

        ResponseEntity<FollowerStatsResponse> expected = ResponseEntity.ok(response);

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(followRepository.countAllByFollowerId(user)).thenReturn(follows);
        when(followRepository.countAllByFollowedId(user)).thenReturn(followers);

        ResponseEntity<FollowerStatsResponse> actual = userService.getFollowerStats(request);

        verify(authenticationService).getUserFromRequest(request);
        verify(followRepository).countAllByFollowerId(user);
        verify(followRepository).countAllByFollowedId(user);

        assertEquals(expected, actual);
    }

    @Test
    void getFollowerStatsUserNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getFollowerStats(request));
        verify(authenticationService).getUserFromRequest(request);
    }

    @Test
    void getFollowers() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Integer pageNr = 0;
        Page<Follow> follows = new PageImpl<>(List.of());

        ResponseEntity<List<FollowersResponse>> expected = ResponseEntity.ok(List.of());

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(followRepository.findAllByFollowedId(eq(user), any(Pageable.class))).thenReturn(follows);
        when(userMapper.usersToFollowerResponseList(List.of())).thenReturn(List.of());
        ResponseEntity<List<FollowersResponse>> actual = userService.getFollowers(request, pageNr);

        verify(authenticationService).getUserFromRequest(request);
        verify(followRepository).findAllByFollowedId(eq(user), any(Pageable.class));
        verify(userMapper).usersToFollowerResponseList(List.of());

        assertEquals(expected, actual);
    }

    @Test
    void getFollowersUserNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getFollowers(request, 0));
        verify(authenticationService).getUserFromRequest(request);
    }

    @Test
    void getFollowing() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Integer pageNr = 0;
        Page<Follow> follows = new PageImpl<>(List.of());

        ResponseEntity<List<FollowingResponse>> expected = ResponseEntity.ok(List.of());

        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.of(user));
        when(followRepository.findAllByFollowerId(eq(user), any(Pageable.class))).thenReturn(follows);
        when(userMapper.usersToFollowingResponseList(List.of())).thenReturn(List.of());
        ResponseEntity<List<FollowingResponse>> actual = userService.getFollowing(request, pageNr);

        verify(authenticationService).getUserFromRequest(request);
        verify(followRepository).findAllByFollowerId(eq(user), any(Pageable.class));
        verify(userMapper).usersToFollowingResponseList(List.of());

        assertEquals(expected, actual);
    }

    @Test
    void getFollowingUserNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(authenticationService.getUserFromRequest(request)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getFollowing(request, 0));
        verify(authenticationService).getUserFromRequest(request);
    }
}