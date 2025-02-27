package Arthur.Code.MyTube_website_backend.service;

import Arthur.Code.MyTube_website_backend.dto.request.*;
import Arthur.Code.MyTube_website_backend.dto.response.UserResponse;
import Arthur.Code.MyTube_website_backend.enums.FileType;
import Arthur.Code.MyTube_website_backend.enums.Role;
import Arthur.Code.MyTube_website_backend.model.PasswordResetToken;
import Arthur.Code.MyTube_website_backend.model.Subscription;
import Arthur.Code.MyTube_website_backend.model.User;
import Arthur.Code.MyTube_website_backend.model.Video;
import Arthur.Code.MyTube_website_backend.repository.PasswordResetRepository;
import Arthur.Code.MyTube_website_backend.repository.SubscriptionRepository;
import Arthur.Code.MyTube_website_backend.repository.UserRepository;
import Arthur.Code.MyTube_website_backend.repository.VideoRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final EmailService emailService;

    @Value("${app.url}")
    private String appUrl;

    public UserService(UserRepository userRepository, VideoRepository videoRepository, PasswordResetRepository passwordResetRepository, SubscriptionRepository subscriptionRepository, BCryptPasswordEncoder passwordEncoder, FileService fileService, EmailService emailService) {
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileService = fileService;
        this.emailService = emailService;
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
        if (user.isBanned()) {
            throw new IllegalStateException("This user is already banned.");
        }
        comparePasswords(loginRequest.getPassword(), user.getPassword());
        return user;
    }

    private void comparePasswords(String firstPassword, String secondPassword) {
        if (!passwordEncoder.matches(firstPassword, secondPassword)) {
            throw new IllegalStateException("Invalid password.");
        }
    }

    private void handleRememberMe(User user, HttpServletResponse response) {
        String token = generateAndAttachCookie(response);
        updateRememberMeToken(user, token);
    }

    private String generateAndAttachCookie(HttpServletResponse response) {
        String token = generateUniqueId();
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
        user.setRole(Role.USER);
        user.setBanned(false);
        return user;
    }

    public UserResponse getUserByToken(String token) {
        if (token == null) {
            throw new IllegalStateException("No valid session token was found.");
        }
        User user = userRepository.findByRememberMe(token)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getRememberMeCreatedAt().isBefore(LocalDateTime.now().minusDays(7))) {
            throw new IllegalArgumentException("Token expired");
        }
        return convertToUserDTO(user);
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
        dto.setRole(user.getRole());
        return dto;
    }

    protected String composeUrlPath(String path) {
        if (path == null) {
            return null;
        }
        String correctedPath = path.replace("\\", "/");
        return appUrl + correctedPath;
    }

    private String generateUniqueId() {
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
        fileService.validateInput(file, FileType.PROFILE_PICTURE);
        String profilePicturePath = fileService.saveFile(file, FileType.PROFILE_PICTURE);
        deleteExistingProfilePicture(user);
        user.setPicturePath(profilePicturePath);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private void deleteExistingProfilePicture(User user) {
        if(user.getPicturePath() != null) {
            fileService.deleteFile(user.getPicturePath());
        }
    }

    private Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by("createdAt").descending());
    }

    public Page<UserResponse> getMySubscriptions(Long subscriberId, PageableRequest request) {
        Pageable pageable = createPageable(request.getPage(), request.getSize());
        Page<Subscription> subscriptions = subscriptionRepository.findAllBySubscriberId(subscriberId, pageable);
        return convertToUserResponses(subscriptions);
    }

    private Page<UserResponse> convertToUserResponses(Page<Subscription> subscriptions) {
        return subscriptions.map(subscription -> convertToUserDTO(subscription.getChannel()));
    }

    public void updatePassword(Long id, ChangePasswordRequest request) {
        User user = getUserById(id);
        comparePasswords(request.getOldPassword(), user.getPassword());
        user.setPassword(hashPassword(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void updateEmail(Long id, ChangeEmailRequest request) {
        User user = getUserById(id);
        comparePasswords(request.getPassword(), user.getPassword());
        user.setEmail(request.getEmail());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void handleResetPasswordRequest(String email) {
        User user = getUserByEmail(email);
        deleteExistingToken(user);
        PasswordResetToken tokenEntity = createPasswordResetTokenEntity(user);
        passwordResetRepository.save(tokenEntity);
        emailService.sendPasswordResetLink(email, tokenEntity);
    }

    private void deleteExistingToken(User user) {
        PasswordResetToken existingToken = passwordResetRepository.findByUserId(user.getId());
        if (existingToken != null) {
            passwordResetRepository.delete(existingToken);
        }
    }

    private PasswordResetToken createPasswordResetTokenEntity(User user) {
        String token = generateUniqueId();
        PasswordResetToken tokenEntity = new PasswordResetToken();
        tokenEntity.setUser(user);
        tokenEntity.setToken(token);
        tokenEntity.setCreatedAt(LocalDateTime.now());
        return tokenEntity;
    }

    public void handleResetPassword(String token) {
        User user = getUserByPasswordResetToken(token);
        String temporaryPassword = generateUniqueId().substring(0, 8);
        user.setPassword(hashPassword(temporaryPassword));
        user.setUpdatedAt(LocalDateTime.now());
        emailService.sendTemporaryPassword(user.getEmail(), temporaryPassword);
        deleteExistingToken(user);

    }

    private User getUserByPasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or non-existent token"));

        validateTokenExpiry(passwordResetToken);

        return passwordResetToken.getUser();
    }

    private void validateTokenExpiry(PasswordResetToken token) {
        LocalDateTime expiryTime = token.getCreatedAt().plusMinutes(10); //10min
        if (expiryTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token has expired");
        }
    }

    public void deleteProfilePicture(Long id) {
        User user = getUserById(id);
        deleteExistingProfilePicture(user);
        user.setPicturePath(null);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void deleteUserAsOwner(Long id, DeleteAccountRequest request, HttpServletResponse response) {
        User user = getUserById(id);
        comparePasswords(request.getPassword(), user.getPassword());
        clearCookieInBrowser(response);
        deleteUserAndRelatedData(user);

    }

    public void deleteUserAsModerator(Long id) {
        User user = getUserById(id);
        deleteUserAndRelatedData(user);

    }

    private void deleteUserAndRelatedData(User user) {
        deleteAllUserVideos(user);
        deleteExistingToken(user);
        deleteExistingProfilePicture(user);
        userRepository.delete(user);
    }

    private void deleteAllUserVideos(User user) {
        List<Video> videos = videoRepository.findAllByUserId(user.getId());
        videos.forEach(video -> {
            try {
                fileService.deleteFile(video.getFilePath());
                fileService.deleteFile(video.getThumbnailPath());
            } catch (FileService.FileStorageException e) {
                throw new RuntimeException("Failed to delete associated files for video ID" + video.getId());
            }
        });
    }

    public void banUser(Long id) {
        User user = getUserById(id);
        user.setBanned(true);
        user.setRememberMe(null);
        user.setRememberMeCreatedAt(null);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void unbanUser(Long id) {
        User user = getUserById(id);
        user.setBanned(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void promoteToModerator(Long id) {
        User user = getUserById(id);
        if (user.getRole() == Role.MODERATOR) {
            throw new IllegalArgumentException("This user is already promoted");
        }
        user.setRole(Role.MODERATOR);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void demoteFromModerator(Long id) {
        User user = getUserById(id);
        if (user.getRole() == Role.USER) {
            throw new IllegalArgumentException("This user is already demoted");
        }
        user.setRole(Role.USER);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

}
