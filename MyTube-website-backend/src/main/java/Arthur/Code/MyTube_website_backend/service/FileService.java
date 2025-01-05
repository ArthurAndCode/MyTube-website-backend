package Arthur.Code.MyTube_website_backend.service;

import Arthur.Code.MyTube_website_backend.enums.FileType;
import org.apache.tika.Tika;
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

    public String saveFile(MultipartFile file, FileType fileType) {
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


    protected void validateInput(MultipartFile file, FileType fileType) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }
        String mimeType = detectMimeType(file);
        if (mimeType == null || !isValidMimeTypeForFileType(mimeType, fileType)) {
            throw new IllegalArgumentException("Invalid file type for " + fileType + ": " + mimeType);
        }
    }

    private String detectMimeType(MultipartFile file) {
        Tika tika = new Tika();
        try {
            return tika.detect(file.getInputStream());
        } catch (IOException e) {
            throw new FileStorageException("Failed to detect MIME type", e);
        }
    }

    private boolean isValidMimeTypeForFileType(String mimeType, FileType fileType) {
        return switch (fileType) {
            case VIDEO -> mimeType.equals("video/mp4") || mimeType.equals("video/avi") || mimeType.equals("video/quicktime");
            case THUMBNAIL, PROFILE_PICTURE -> mimeType.equals("image/jpeg") || mimeType.equals("image/png");
        };
    }

    private String resolveDirectory(FileType fileType) {
        return switch (fileType) {
            case VIDEO -> baseDir + videosDir;
            case THUMBNAIL -> baseDir + thumbnailsDir;
            case PROFILE_PICTURE -> baseDir + profilePicturesDir;
        };
    }

    private String generateUniqueFileName(String originalFileName) {
        String sanitizedFileName = originalFileName
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-zA-Z0-9._-]", "")
                .toLowerCase();
        return UUID.randomUUID() + "_" + sanitizedFileName;
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
