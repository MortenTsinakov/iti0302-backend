package ee.taltech.iti0302_veebiarendus_backend.user.controller;

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
import ee.taltech.iti0302_veebiarendus_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUser(@RequestParam(name = "user") String searchTerm) {
        return ResponseEntity.ok(userService.searchUser(searchTerm));
    }

    @PostMapping("/follow")
    public void followUser(@RequestBody FollowRequest followRequest) {
        userService.followUser(followRequest);
    }

    @DeleteMapping("/unfollow")
    public void unfollowUser(@RequestBody FollowRequest followRequest) {
        userService.unfollowUser(followRequest);
    }

    @GetMapping("/followers-stats")
    public ResponseEntity<FollowerStatsResponse> getFollowerStats() {
        return userService.getFollowerStats();
    }

    @GetMapping("/followers")
    public ResponseEntity<List<FollowersResponse>> getFollowers(@RequestParam(name = "page") Integer page) {
        return userService.getFollowers(page);
    }

    @GetMapping("/following")
    public ResponseEntity<List<FollowingResponse>> getFollowing(@RequestParam(name = "page") Integer page) {
        return userService.getFollowing(page);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@RequestParam(name = "user") Integer id) {
        return userService.getUserProfile(id);
    }

    @PutMapping("/username")
    public ResponseEntity<ChangeUsernameResponse> changeUsername(@RequestBody ChangeUsernameRequest changeUsernameRequest) {
        return userService.changeUsername(changeUsernameRequest);
    }

    @PutMapping("/email")
    public void changeEmail(@RequestBody ChangeEmailRequest changeEmailRequest) {
        userService.changeEmail(changeEmailRequest);
    }

    @PutMapping("/password")
    public void changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(changePasswordRequest);
    }

    @DeleteMapping("/delete")
    public void deleteUser(@RequestBody DeleteUserRequest deleteUserRequest) {
        userService.deleteUser(deleteUserRequest);
    }
}
