package Arthur.Code.MyTube_website_backend.service;

import Arthur.Code.MyTube_website_backend.dto.request.LoginRequest;
import Arthur.Code.MyTube_website_backend.dto.request.PageableRequest;
import Arthur.Code.MyTube_website_backend.dto.request.RegisterRequest;
import Arthur.Code.MyTube_website_backend.dto.request.SearchUserRequest;
import Arthur.Code.MyTube_website_backend.dto.response.UserResponse;
import Arthur.Code.MyTube_website_backend.model.User;
import Arthur.Code.MyTube_website_backend.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileService fileService;


    @Value("${app.url}")
    private String appUrl;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, FileService fileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileService = fileService;
    }

    public Page<UserResponse> getUsers(PageableRequest request) {
        Pageable pageable = createPageable(request.getPage(), request.getSize());
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::convertToUserDTO);
    }

    public Page<UserResponse> searchUserByUsername(SearchUserRequest request) {
        Pageable pageable = createPageable(request.getPage(), request.getSize());
        Page<User> users = userRepository.findByUsernameContainingIgnoreCase(request.getUsername(), pageable);
        return users.map(this::convertToUserDTO);
    }

    public UserResponse loginUser(LoginRequest loginRequest, HttpServletResponse response) {
        User user = authenticateUser(loginRequest);
        if (loginRequest.isRememberMeChecked()) {
            handleRememberMe(user, response);
        }
        return convertToUserDTO(user);
    }

    private User authenticateUser(LoginRequest loginRequest) {
        User user = getUserByEmail(loginRequest.getEmail());
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalStateException("Invalid password.");
        }
        return user;
    }

    private void handleRememberMe(User user, HttpServletResponse response) {
        String token = generateAndAttachCookie(response);
        updateRememberMeToken(user, token);
    }

    private String generateAndAttachCookie(HttpServletResponse response) {
        String token = generateToken();
        Cookie cookie = createRememberMeCookie(token);
        response.addCookie(cookie);
        return token;
    }

    private Cookie createRememberMeCookie(String token) {
        Cookie cookie = new Cookie("rememberMeToken", token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        return cookie;
    }

    private void updateRememberMeToken(User user, String token) {
        user.setRememberMe(token);
        user.setRememberMeCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void registerUser(RegisterRequest registerRequest) {
        isEmailOrUsernameTaken(registerRequest);
        User user = createUserEntity(registerRequest);
        userRepository.save(user);
    }

    private void isEmailOrUsernameTaken(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already taken.");
        }
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already taken.");
        }
    }

    private User createUserEntity(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(hashPassword(registerRequest.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    public User getUserByToken(String token) {
        User user = userRepository.findByRememberMe(token)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getRememberMeCreatedAt().isBefore(LocalDateTime.now().minusDays(7))) {
            throw new IllegalArgumentException("Token expired");
        }
        return user;
    }

    protected User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    protected User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    protected UserResponse convertToUserDTO(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setProfilePictureUrl(composeUrlPath(user.getPicturePath()));
        return dto;
    }

    protected String composeUrlPath(String path) {
        String correctedPath = path.replace("\\", "/");
        return appUrl + correctedPath;
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public void deleteRememberMe(String token, HttpServletResponse response) {
        if (token != null) {
            clearCookieInBrowser(response);
            clearCookieInDatabase(token);
        }
    }

    private void clearCookieInDatabase(String token) {
        Optional<User> userOptional = userRepository.findByRememberMe(token);
        userOptional.ifPresent(user -> {
            user.setRememberMe(null);
            user.setRememberMeCreatedAt(null);
            userRepository.save(user);
        });
    }

    private static void clearCookieInBrowser(HttpServletResponse response) {
        Cookie cookie = new Cookie("rememberMeToken", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public void uploadProfilePicture(Long id, MultipartFile file) {
        User user = getUserById(id);
        String profilePicturePath = fileService.saveFile(file, FileService.FileType.PROFILE_PICTURE);
        user.setPicturePath(profilePicturePath);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by("createdAt").descending());
    }
}
