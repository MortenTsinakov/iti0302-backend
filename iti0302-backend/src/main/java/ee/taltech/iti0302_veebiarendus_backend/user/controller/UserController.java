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
import jakarta.servlet.http.HttpServletRequest;
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
    public void followUser(HttpServletRequest request, @RequestBody FollowRequest followRequest) {
        userService.followUser(request, followRequest);
    }

    @DeleteMapping("/unfollow")
    public void unfollowUser(HttpServletRequest request, @RequestBody FollowRequest followRequest) {
        userService.unfollowUser(request, followRequest);
    }

    @GetMapping("/followers-stats")
    public ResponseEntity<FollowerStatsResponse> getFollowerStats(HttpServletRequest request) {
        return userService.getFollowerStats(request);
    }

    @GetMapping("/followers")
    public ResponseEntity<List<FollowersResponse>> getFollowers(HttpServletRequest request, @RequestParam(name = "page") Integer page) {
        return userService.getFollowers(request, page);
    }

    @GetMapping("/following")
    public ResponseEntity<List<FollowingResponse>> getFollowing(HttpServletRequest request, @RequestParam(name = "page") Integer page) {
        return userService.getFollowing(request, page);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(HttpServletRequest request, @RequestParam(name = "user") Integer id) {
        return userService.getUserProfile(request, id);
    }

    @PutMapping("/username")
    public ResponseEntity<ChangeUsernameResponse> changeUsername(HttpServletRequest request, @RequestBody ChangeUsernameRequest changeUsernameRequest) {
        return userService.changeUsername(request, changeUsernameRequest);
    }

    @PutMapping("/email")
    public void changeEmail(HttpServletRequest request, @RequestBody ChangeEmailRequest changeEmailRequest) {
        userService.changeEmail(request, changeEmailRequest);
    }

    @PutMapping("/password")
    public void changePassword(HttpServletRequest request, @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(request, changePasswordRequest);
    }

    @DeleteMapping("/delete")
    public void deleteUser(HttpServletRequest request, @RequestBody DeleteUserRequest deleteUserRequest) {
        userService.deleteUser(request, deleteUserRequest);
    }
}
