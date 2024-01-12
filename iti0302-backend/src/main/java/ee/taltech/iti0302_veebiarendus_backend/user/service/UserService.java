package ee.taltech.iti0302_veebiarendus_backend.user.service;

import ee.taltech.iti0302_veebiarendus_backend.auth.service.AuthenticationService;
import ee.taltech.iti0302_veebiarendus_backend.auth.service.JwtService;
import ee.taltech.iti0302_veebiarendus_backend.constants.AppConstants;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final FollowRepository followRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> searchUser(String searchTerm) {
        Double threshold = 0.5;
        return userMapper.usersToUserDtoList(userRepository.fuzzySearch(searchTerm, threshold));
    }

    public void followUser(HttpServletRequest request, FollowRequest followRequest) {
        User follower = authenticationService.getUserFromRequest(request).orElseThrow(() -> new RuntimeException("Follow request failed: user making the request not found"));
        User followed = userRepository.getUserById(followRequest.userFollowedId()).orElseThrow(() -> new UserNotFoundException("Follow request invalid: user not found"));
        if (follower == followed) {
            throw new InvalidOperationException("Users can't follow themselves");
        }
        Follow follow = new Follow();
        follow.setFollowerId(follower);
        follow.setFollowedId(followed);
        followRepository.save(follow);
    }

    public void unfollowUser(HttpServletRequest request, FollowRequest followRequest) {
        User follower = authenticationService.getUserFromRequest(request).orElseThrow(() -> new RuntimeException("Unfollow request failed: user making the request not found"));
        User followed = userRepository.getUserById(followRequest.userFollowedId()).orElseThrow(() -> new UserNotFoundException("Unfollow request failed: user not found"));
        Follow follow = followRepository.getFollowByFollowerIdAndFollowedId(follower, followed).orElseThrow(() -> new InvalidOperationException("Unfollow failed: user not followed yet"));
        followRepository.deleteById(follow.getId());
    }

    public ResponseEntity<UserProfileResponse> getUserProfile(HttpServletRequest request, Integer id) {
        User userRequesting = authenticationService.getUserFromRequest(request).orElseThrow(() -> new RuntimeException("User profile request failed: user requesting the profile not found"));
        User userRequested = userRepository.getUserById(id).orElseThrow(() -> new UserNotFoundException("User profile request failed: user not found"));
        boolean following = followRepository.existsByFollowerIdAndFollowedId(userRequesting, userRequested);
        UserProfileResponse response = new UserProfileResponse(userRequested.getUsername(), following);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ChangeUsernameResponse> changeUsername(HttpServletRequest request, ChangeUsernameRequest changeUsernameRequest) {
        validateUsername(changeUsernameRequest.newUsername());
        User user = authenticationService.getUserFromRequest(request).orElseThrow(() -> new RuntimeException("Changing username failed: user not found"));
        user.setUsername(changeUsernameRequest.newUsername());
        userRepository.save(user);
        String jwt = jwtService.generateToken(user);
        ChangeUsernameResponse response = userMapper.userToChangeUsernameResponse(user, jwt);
        return ResponseEntity.ok(response);
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {throw new InvalidInputException("Changing username failed: Username is missing.");}
        if (userRepository.existsByUsername(username)) {throw new InvalidInputException("Changing username failed: Username already taken");}
    }

    public void changeEmail(HttpServletRequest request, ChangeEmailRequest changeEmailRequest) {
        validateEmail(changeEmailRequest.newEmail());
        User user = authenticationService.getUserFromRequest(request).orElseThrow(() -> new RuntimeException("Changing email failed: User not found"));
        user.setEmail(changeEmailRequest.newEmail());
        userRepository.save(user);
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {throw new InvalidInputException("Changing email failed: Email is missing");}
        if (userRepository.existsByEmail(email)) {throw new InvalidInputException("Changing email failed: User with given email already exists.");}
    }

    public void changePassword(HttpServletRequest request, ChangePasswordRequest changePasswordRequest) {
        validatePassword(changePasswordRequest.newPassword());
        User user = authenticationService.getUserFromRequest(request).orElseThrow(() -> new RuntimeException("Changing password failed: User not found"));
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        changePasswordRequest.currentPassword()
                )
        );
        user.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
        userRepository.save(user);
    }

    private void validatePassword(String password) {
        if (password == null) {throw new InvalidInputException("Changing password failed: Password is missing");}
        if (password.length() < AppConstants.REQUIRED_PASSWORD_LENGTH) {throw new InvalidInputException("Changing password failed: Password is too short");}
    }

    public void deleteUser(HttpServletRequest request, DeleteUserRequest deleteUserRequest) {
        User user = authenticationService.getUserFromRequest(request).orElseThrow(() -> new RuntimeException("Deleting user failed: User not found"));
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        deleteUserRequest.password()
                )
        );
        userRepository.delete(user);
    }

    public ResponseEntity<FollowerStatsResponse> getFollowerStats(HttpServletRequest request) {
        User user = authenticationService.getUserFromRequest(request).orElseThrow(() -> new RuntimeException("Getting follower stats failed: User not found"));
        Integer follows = followRepository.countAllByFollowerId(user);
        Integer followers = followRepository.countAllByFollowedId(user);
        FollowerStatsResponse response = new FollowerStatsResponse(followers, follows);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<FollowersResponse>> getFollowers(HttpServletRequest request, Integer page) {
        User user = authenticationService.getUserFromRequest(request).orElseThrow(() -> new RuntimeException("Fetching followers failed: user not found"));

        Sort sort = Sort.by("id").ascending();
        Pageable pageRequest = PageRequest.of(page, AppConstants.FRIENDS_PAGE_SIZE, sort);
        List<User> followers = followRepository.findAllByFollowedId(user, pageRequest).getContent().stream().map(Follow::getFollowerId).toList();
        return ResponseEntity.ok(userMapper.usersToFollowerResponseList(followers));
    }

    public ResponseEntity<List<FollowingResponse>> getFollowing(HttpServletRequest request, Integer page) {
        User user = authenticationService.getUserFromRequest(request).orElseThrow(() -> new RuntimeException("Fetching followers failed: user not found"));

        Sort sort = Sort.by("id").ascending();
        Pageable pageRequest = PageRequest.of(page, AppConstants.FRIENDS_PAGE_SIZE, sort);
        List<User> followers = followRepository.findAllByFollowerId(user, pageRequest).getContent().stream().map(Follow::getFollowedId).toList();
        return ResponseEntity.ok(userMapper.usersToFollowingResponseList(followers));
    }
}
