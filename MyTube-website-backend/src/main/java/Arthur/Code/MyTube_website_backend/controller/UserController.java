package Arthur.Code.MyTube_website_backend.controller;

import Arthur.Code.MyTube_website_backend.dto.LoginRequest;
import Arthur.Code.MyTube_website_backend.dto.RegisterRequest;
import Arthur.Code.MyTube_website_backend.dto.UserDTO;
import Arthur.Code.MyTube_website_backend.model.User;
import Arthur.Code.MyTube_website_backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
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

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        UserDTO userDTO = userService.loginUser(loginRequest, response);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(value = "rememberMeToken", required = false) String token, HttpServletResponse response) {
        if (token != null) {
            userService.deleteRememberMe(token, response);
        }
        return ResponseEntity.status(HttpStatus.OK).body("You have been successfully logged out.");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserByToken(@CookieValue(value = "rememberMeToken", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No valid session token was found.");
        }
        User user = userService.getUserByToken(token);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        userService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
    }

    @PostMapping("/{id}/upload-picture")
    public ResponseEntity<String> uploadProfilePicture(@PathVariable Long id, @RequestBody MultipartFile file) {
        userService.uploadProfilePicture(id, file);
        return ResponseEntity.ok("Profile picture uploaded successfully");
    }
}
