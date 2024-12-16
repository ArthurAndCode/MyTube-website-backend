package Arthur.Code.MyTube_website_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${uploads.base-dir}")
    private String baseDir;

    @Value("${uploads.thumbnails}")
    private String thumbnailsDir;

    @Value("${uploads.videos}")
    private String videosDir;

    @Value("${uploads.profile-pictures}")
    private String profilePicturesDir;

    public enum FileType {
        VIDEO, THUMBNAIL, PROFILE_PICTURE
    }

    public String saveFile(MultipartFile file, FileType fileType) {
        validateInput(file);
        String directory = resolveDirectory(fileType);
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        Path filePath = Paths.get(directory, fileName);

        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            throw new FileStorageException("Failed to save file", e);
        }
        return filePath.toString();
    }

    public void deleteFile(String filePath) {
        Path path = resolveFilePath(filePath);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete file at path: " + filePath, e);
        }
    }

    private static Path resolveFilePath(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("File path cannot be null or blank.");
        }
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileStorageException("File does not exist: " + filePath);
        }
        return path;
    }


    private void validateInput(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }
        // sprawdzić czy plik jest odpowiedni
    }

    private String resolveDirectory(FileType fileType) {
        return switch (fileType) {
            case VIDEO -> baseDir + videosDir;
            case THUMBNAIL -> baseDir + thumbnailsDir;
            case PROFILE_PICTURE -> baseDir + profilePicturesDir;
        };
    }

    private String generateUniqueFileName(String originalFileName) {
        return UUID.randomUUID() + "_" + originalFileName;
    }

    public static class FileStorageException extends RuntimeException {
        public FileStorageException(String message, Throwable cause) {
            super(message, cause);
        }
        public FileStorageException(String message) {
            super(message);
        }
    }
}
