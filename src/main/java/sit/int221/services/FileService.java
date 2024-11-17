package sit.int221.services;

import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.entities.task_base.Board;
import sit.int221.entities.task_base.Collaborator;
import sit.int221.entities.task_base.Tasks3;
import sit.int221.properties.FileStorageProperties;
import sit.int221.repositories.task_base.CollaboratorRepository;
import sit.int221.repositories.task_base.Tasks3Repository;
import sit.int221.services.itbkk_shared.AuthorizationService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Service
public class FileService {
    private final Path fileStorageLocation;

    @Autowired
    private CollaboratorRepository collaboratorRepository;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private Tasks3Repository tasks3Repository;

    @Autowired
    public FileService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties
                .getUploadDir()).toAbsolutePath().normalize();
        try {
            if (!Files.exists(this.fileStorageLocation)) {
                Files.createDirectories(this.fileStorageLocation);
            }
        } catch (IOException ex) {
            throw new RuntimeException(
                    "Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public List<String> store(Claims claims, String boardId, Integer taskId, List<MultipartFile> files) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        Optional<Collaborator> collaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(boardId, oid);

        Tasks3 tasks3 = tasks3Repository.findTasks3ByBoardIdAndId(boardId, taskId);

        if (tasks3 == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found for boardId " + boardId + " and taskId " + taskId);
        }

        if (oid.equals(board.getOwnerId()) ||
                collaborator.isPresent() && collaborator.get().getAccessRight().equals("WRITE")) {
            List<String> uploadedFiles = new ArrayList<>();
            for (MultipartFile file : files) {
                // Ensure filename is clean
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                if (fileName.contains("..")) {
                    throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
                }

                // Construct file storage path using boardId and taskId
                Path taskStorageLocation = this.fileStorageLocation.resolve(board.getId()).resolve(String.valueOf(tasks3.getId()));
                try {
                    // Create directories if they don't exist
                    Files.createDirectories(taskStorageLocation);

                    // Copy file to target location
                    Path targetLocation = taskStorageLocation.resolve(fileName);
                    Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException ex) {
                    throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
                }
            }
            return uploadedFiles;

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to upload files on this task");
        }
    }

    public Resource loadFileAsResource(String directory, String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(directory).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File operation error: " + fileName, ex);
        }
    }

    public void removeResource(Claims claims, String boardId, Integer taskId, String fileName) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        Optional<Collaborator> collaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(boardId, oid);

        Tasks3 tasks3 = tasks3Repository.findTasks3ByBoardIdAndId(boardId, taskId);

        if (tasks3 == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found for boardId " + boardId + " and taskId " + taskId);
        }

        if (oid.equals(board.getOwnerId()) || collaborator.isPresent() && collaborator.get().getAccessRight().equals("WRITE")) {
            try {
                Path taskStorageLocation = this.fileStorageLocation.resolve(board.getId()).resolve(String.valueOf(tasks3.getId()));
                Path filePath = taskStorageLocation.resolve(fileName).normalize();
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                } else {
                    throw new RuntimeException("File not found " + fileName);
                }
            } catch (IOException ex) {
                throw new RuntimeException("File operation error: " + fileName, ex);
            }
        } else {
            // If the user is not authorized to delete, throw a FORBIDDEN error
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete file from this task");
        }
    }
}
