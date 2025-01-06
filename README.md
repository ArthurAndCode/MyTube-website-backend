# MyTube backend API

It's a YouTube-like application built with Java Spring Boot and PostgreSQL. It utilizes RESTful API for managing videos, users, comments, subscriptions, and moderation tasks. The app allows users to upload videos, comment, subscribe and much more.

## Features

### User Management
- **Register and Login:**
  - User registration with secure password handling.
  - Login with token-based authentication using cookies (supports "remember me" functionality).
- **Search Channels(Users):**
  - Search by username
  - Retrive subscribed channels 
- **Profile Management:**
  - Upload and delete profile pictures.
  - Update email and password securely.
  - Password reset via email link
  - Delete account functionality with ownership verification.

### Video Management
- **Upload Videos:**
  - Upload video and thumbnail files with metadata (title, description, etc.).
- **Search Videos:**
  - Search by title.
  - Retrieve videos uploaded by a specific user.
  - Retrieve videos liked by a user.
- **Update and Delete Videos:**
  - Modify video descriptions.
  - Delete videos.

### Comment Management
- Add, retrieve, and delete comments for videos.

### Subscription Management
- **Subscribe/Unsubscribe:**
  - Users can subscribe/unsubscribe channels.
- **Subscription Details:**
  - Retrieve subscription details between users and channels.

### Moderation Features
- **User Moderation:**
  - Ban and unban users.
  - Promote or demote users to/from moderators.
  - Delete user accounts.
- **Content Moderation:**
  - Delete videos and comments.

### Video Reactions
- React to videos (like/dislike)
- Retrieve detailed reaction data.

## Technologies Used
- **Framework:** Spring Boot 3.4.0
- **Database:** PostgreSQL 17
- **Dependency Management:** Maven

## API Endpoints

### User Endpoints
| Method | Endpoint                          | Description                                  |
|--------|-----------------------------------|----------------------------------------------|
| GET    | `/api/v1/users`                        | Retrieve a paginated list of users.               |
| GET    | `/api/v1/users/search`                 | Search users by username.                         |
| GET    | `/api/v1/users/{id}/my-subscriptions`  | Retrieve a paginated list of subscribed users.    |
| GET    | `/api/v1/users/me`                     | Retrieve user details by token.                   |
| GET    | `/api/v1/users/reset-password`         | Processes the password reset.                     |
| POST   | `/api/v1/users/login`                  | Login a user.                                     |
| POST   | `/api/v1/users/logout`                 | Logout a user.                                    |
| POST   | `/api/v1/users`                        | Register a new user.                              |
| POST   | `/api/v1/users/{id}/profile-pictures`  | Upload profile picture.                           |
| POST   | `/api/v1/users/reset-password`         | Initiate a password reset.                        |
| PUT    | `/api/v1/users/{id}/password`          | Change user password.                             |
| PUT    | `/api/v1/users/{id}/email`             | Change user email.                                |
| DELETE | `/api/v1/users/{id}/profile-pictures`  | Delete profile picture.                           |
| DELETE | `/api/v1/users/{id}`                   | Delete user account.                              |

### Video Endpoints
| Method | Endpoint                          | Description                                  |
|--------|-----------------------------------|----------------------------------------------|
| GET    | `/api/v1/videos`                       | Retrieve a paginated list of videos.        |
| GET    | `/api/v1/videos/users/{id}`            | Get videos uploaded by a specific user.     |
| GET    | `/api/v1/videos/search`                | Search videos by title.                     |
| GET    | `/api/v1/videos/users/{id}/liked`      | Get videos liked by specyfic user.          |
| POST   | `/api/v1/videos`                       | Upload a video.                             |
| PUT    | `/api/v1/videos/{id}`                  | Change video description.                   |
| DELETE | `/api/v1/videos/{id}`                  | Delete a video.                             |

### Comment Endpoints
| Method | Endpoint                          | Description                                  |
|--------|-----------------------------------|----------------------------------------------|
| GET    | `/api/v1/videos/{id}/comments`    | Retrieve comments for a video.               |
| POST   | `/api/v1/videos/{id}/comments`    | Add a comment to a video.                    |
| DELETE | `/api/v1/comments/{id}`           | Delete a comment.                            |

### Moderator Endpoints
| Method | Endpoint                          | Description                                  |
|--------|-----------------------------------|----------------------------------------------|
| PUT    | `/api/v1/moderator/users/{id}/ban`              | Ban a user.                             |
| PUT    | `/api/v1/moderator/users/{id}/unban`            | Unban a user.                           |
| PUT    | `/api/v1/moderator/users/users/{id}/promote`    | Promote a user to moderator.            |
| PUT    | `/api/v1/moderator/users/users/{id}/demote`     | Demote a moderator to user.             |
| DELETE | `/api/v1/moderator/users/{id}`                  | Delete a user account.                  |
| DELETE | `/api/v1/moderator"/comments/{id}`              | Delete a comment.                       |
| DELETE | `/api/v1/moderator/videos/{id}`                 | Delete a video.                         |

### Subscription Endpoints
| Method | Endpoint                          | Description                                  |
|--------|-----------------------------------|----------------------------------------------|
| GET    | `/api/v1/users/{userId}/subscriptions/{channelId}` | Retrieve subscription details. |
| POST   | `/api/v1/users/{userId}/subscriptions/{channelId}` | Toggle subscription.           |

### Video Reaction Endpoints
| Method | Endpoint                          | Description                                  |
|--------|-----------------------------------|----------------------------------------------|
| GET    | `/api/v1/videos/{videoId}/reactions/users/{userId}` | Retrieve reaction details.   |
| POST   | `/api/v1/videos/{videoId}/reactions/{reaction}/users/{userId}` | React to a video. |

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/ArthurAndCode/MyTube-website-backend.git
   cd MyTube-website-backend
   ```

2. Update the application properties file (`application.properties`) with your PostgreSQL database credentials and provide the relevant SMTP server credentials.
3. Set up the upload directories: Make sure the uploads/ directory exists on your system, and inside it, create the subdirectories:
- videos/
- thumbnails/
- profilePictures/

4. Build and run the application:
   ```bash
   mvn spring-boot:run
   ```

5. Access the APIs at `http://localhost:8080`.
