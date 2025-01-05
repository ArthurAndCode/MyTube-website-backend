package Arthur.Code.MyTube_website_backend.controller;

import Arthur.Code.MyTube_website_backend.dto.request.*;
import Arthur.Code.MyTube_website_backend.dto.response.UserResponse;
import Arthur.Code.MyTube_website_backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getUsers(@RequestBody PageableRequest request) {
        Page<UserResponse> users = userService.getUsers(request);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserResponse>> searchUserByUsername(@RequestBody SearchUserRequest request) {
        Page<UserResponse> users = userService.searchUserByUsername(request);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}/my-subscriptions")
    public ResponseEntity<Page<UserResponse>> getMySubscriptions(@PathVariable Long id, @RequestBody PageableRequest request) {
        Page<UserResponse> users = userService.getMySubscriptions(id, request);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        UserResponse userResponse = userService.loginUser(loginRequest, response);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(value = "rememberMeToken", required = false) String token, HttpServletResponse response) {
        userService.deleteRememberMe(token, response);
        return ResponseEntity.status(HttpStatus.OK).body("You have been successfully logged out.");
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUserByToken(@CookieValue(value = "rememberMeToken", required = false) String token) {
        UserResponse user = userService.getUserByToken(token);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        userService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
    }

    @PostMapping("/{id}/profile-pictures")
    public ResponseEntity<String> uploadProfilePicture(@PathVariable Long id, @RequestBody MultipartFile file) {
        userService.uploadProfilePicture(id, file);
        return ResponseEntity.status(HttpStatus.OK).body("Profile picture uploaded successfully");
    }

    @DeleteMapping("/{id}/profile-pictures")
    public ResponseEntity<String> deleteProfilePicture(@PathVariable Long id) {
        userService.deleteProfilePicture(id);
        return ResponseEntity.status(HttpStatus.OK).body("Profile picture deleted successfully.");
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<String> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        userService.updatePassword(id, request);
        return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully.");
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<String> changeEmail(@PathVariable Long id, @RequestBody ChangeEmailRequest request) {
        userService.updateEmail(id, request);
        return ResponseEntity.status(HttpStatus.OK).body("Email changed successfully.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        userService.handleResetPasswordRequest(email);
        return ResponseEntity.ok("Password reset link sent successfully to your email.");
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token) {
        userService.handleResetPassword(token);
        return ResponseEntity.ok("Your password has been successfully reset. A new temporary password has been sent to your email. ");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, @RequestBody DeleteAccountRequest request, HttpServletResponse response) {
        userService.deleteUserAsOwner(id, request, response);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully.");
    }
}
