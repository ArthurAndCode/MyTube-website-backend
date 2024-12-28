package Arthur.Code.MyTube_website_backend.controller;

import Arthur.Code.MyTube_website_backend.dto.request.LoginRequest;
import Arthur.Code.MyTube_website_backend.dto.request.PageableRequest;
import Arthur.Code.MyTube_website_backend.dto.request.RegisterRequest;
import Arthur.Code.MyTube_website_backend.dto.request.SearchUserRequest;
import Arthur.Code.MyTube_website_backend.dto.response.UserResponse;
import Arthur.Code.MyTube_website_backend.model.User;
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
    public ResponseEntity<?> getUserByToken(@CookieValue(value = "rememberMeToken", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No valid session token was found.");
        }
        User user = userService.getUserByToken(token);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        userService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadProfilePicture(@PathVariable Long id, @RequestBody MultipartFile file) {
        userService.uploadProfilePicture(id, file);
        return ResponseEntity.status(HttpStatus.OK).body("Profile picture uploaded successfully");
    }
}
