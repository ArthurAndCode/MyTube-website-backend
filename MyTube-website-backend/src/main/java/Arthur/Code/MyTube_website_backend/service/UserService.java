package Arthur.Code.MyTube_website_backend.service;

import Arthur.Code.MyTube_website_backend.dto.request.*;
import Arthur.Code.MyTube_website_backend.dto.response.UserResponse;
import Arthur.Code.MyTube_website_backend.model.PasswordResetToken;
import Arthur.Code.MyTube_website_backend.model.Subscription;
import Arthur.Code.MyTube_website_backend.model.User;
import Arthur.Code.MyTube_website_backend.repository.PasswordResetRepository;
import Arthur.Code.MyTube_website_backend.repository.SubscriptionRepository;
import Arthur.Code.MyTube_website_backend.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final EmailService emailService;

    @Value("${app.url}")
    private String appUrl;

    public UserService(UserRepository userRepository, PasswordResetRepository passwordResetRepository, SubscriptionRepository subscriptionRepository, BCryptPasswordEncoder passwordEncoder, FileService fileService, EmailService emailService) {
        this.userRepository = userRepository;
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
        String profilePicturePath = fileService.saveFile(file, FileService.FileType.PROFILE_PICTURE);
        user.setPicturePath(profilePicturePath);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by("createdAt").descending());
    }

    public Page<UserResponse> getMySubscriptions(Long id, PageableRequest request) {
        Pageable pageable = createPageable(request.getPage(), request.getSize());
        Page<Subscription> subscriptions = subscriptionRepository.findAllBySubscriberId(id, pageable);
        List<Long> channelIds = getChannelsIds(subscriptions);
        List<User> users = userRepository.findAllByIdIn(channelIds);
        List<UserResponse> userResponses = getSortedUserResponses(subscriptions, users);
        return new PageImpl<>(userResponses, pageable, subscriptions.getTotalElements());
    }

    private List<UserResponse> getSortedUserResponses(Page<Subscription> subscriptions, List<User> users) {
        List<UserResponse> userResponses = new ArrayList<>();
        for (Subscription subscription : subscriptions) {
            Long channelId = subscription.getChannelId();
            users.stream()
                    .filter(user -> user.getId().equals(channelId))
                    .findFirst()
                    .ifPresent(user -> userResponses.add(convertToUserDTO(user)));
        }
        return userResponses;
    }

    private static List<Long> getChannelsIds(Page<Subscription> subscriptions) {
        return subscriptions.stream()
                .map(Subscription::getChannelId)
                .collect(Collectors.toList());
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
        System.out.println(email);
        User user = getUserByEmail(email);
        deleteOldToken(user);
        PasswordResetToken tokenEntity = createPasswordResetTokenEntity(user);
        passwordResetRepository.save(tokenEntity);
        emailService.sendPasswordResetLink(email, tokenEntity);
    }

    private void deleteOldToken(User user) {
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
        deleteOldToken(user);

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

}
