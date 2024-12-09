package sit.int221.services;

import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.dtos.response.FileMetadataDTO;
import sit.int221.entities.task_base.Board;
import sit.int221.entities.task_base.Collaborator;
import sit.int221.entities.task_base.Tasks3;
import sit.int221.exceptions.UploadForbiddenException;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
//    @Value("${spring.servlet.multipart.max-request-size}")
//    private int MAX_FILES;

    @Autowired
    ModelMapper modelMapper;


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
        final int MAX_FILES = 10;
        final long MAX_FILE_SIZE = 20 * 1024 * 1024;

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
            List<String> notAdded_MAX_FILES = new ArrayList<>();
            List<String> notAdded_MAX_FILE_SIZE = new ArrayList<>();
            List<String> duplicateFilesNames = new ArrayList<>();

            Path taskStorageLocation = this.fileStorageLocation.resolve(board.getId()).resolve(String.valueOf(tasks3.getId()));
            List<String> existingFiles = new ArrayList<>();
            try {
                if (Files.exists(taskStorageLocation)) {
                    existingFiles = Files.list(taskStorageLocation)
                            .filter(Files::isRegularFile)
                            .map(path -> path.getFileName().toString())
                            .toList();
                }
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error accessing existing files", e);
            }

            // Calculate the available space for new files
            int availableSpace = MAX_FILES - existingFiles.size();

            if (files.size() > availableSpace) {
                notAdded_MAX_FILES.addAll(files.subList(availableSpace, files.size()).stream()
                        .map(file -> file.getOriginalFilename())
                        .toList());
                files = files.subList(0, availableSpace); // Limit to the available space
            }

            for (MultipartFile file : files) {
                // Ensure filename is clean
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                if (fileName.contains("..")) {
                    throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
                }

                // Check for duplicate file names
                if(existingFiles.contains(fileName)){
                    duplicateFilesNames.add(fileName);
                    continue;
                }

                // check maximum file size(20MB)
                if(file.getSize()> MAX_FILE_SIZE){
                    notAdded_MAX_FILE_SIZE.add(fileName);
                    continue;
                }

                // Construct file storage path using boardId and taskId
                try {
                    // Create directories if they don't exist
                    Files.createDirectories(taskStorageLocation);

                    // Copy file to target location
                    Path targetLocation = taskStorageLocation.resolve(fileName);
                    Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                    // Add the file name to the list of uploaded files
                    uploadedFiles.add(fileName);

                } catch (IOException ex) {
                    throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
                }
            }


            if (!notAdded_MAX_FILES.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each task can have at most " + MAX_FILES + " files. The following files are not added: "
                        + String.join(", ", notAdded_MAX_FILES));
            }

            if (!notAdded_MAX_FILE_SIZE.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Each file cannot be larger than " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB. " +
                                "The following files are not added: " + String.join(", ", notAdded_MAX_FILE_SIZE)
                );
            }

            if (!duplicateFilesNames.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "File with the same filename cannot be added or updated to the attachments. "
                                + "Please delete the attachment and add again to update the file. Duplicate files: "
                                + String.join(", ", duplicateFilesNames));
            }

            return uploadedFiles;

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to upload files on this task");
        }
    }

//    private int countExistingFiles(String boardId, Integer taskId) {
//        Path taskStorageLocation = this.fileStorageLocation.resolve(boardId).resolve(String.valueOf(taskId));
//        try {
//            if (Files.exists(taskStorageLocation)) {
//                // Count only the regular files in the directory
//                return (int) Files.list(taskStorageLocation)
//                        .filter(Files::isRegularFile)
//                        .count();
//            }
//        } catch (IOException ex) {
//            throw new RuntimeException("Error counting existing files for taskId: " + taskId, ex);
//        }
//        return 0; // No existing files
//    }

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
            // If the user is not authorized to delete, throw a 403 error
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete file from this task");
        }
    }

    public List<FileMetadataDTO> getFileMetadataInDirectory(Claims claims, String boardId, Integer taskId) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        Optional<Collaborator> collaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(boardId, oid);

        Tasks3 tasks3 = tasks3Repository.findTasks3ByBoardIdAndId(boardId, taskId);
        Path _PATH_task = this.fileStorageLocation.resolve(boardId).resolve(String.valueOf(taskId));

        if (tasks3 == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found for boardId " + boardId + " and taskId " + taskId);
        }

        if (oid.equals(board.getOwnerId()) ||
                collaborator.isPresent() && collaborator.get().getAccessRight().equals("WRITE")) {

            try {
                // Ensure the directory exists
                if (!Files.exists(_PATH_task) || !Files.isDirectory(_PATH_task)) {
                    return new ArrayList<>();
                }

                // List all file metadata in the directory
                return Files.list(_PATH_task)
                        .filter(Files::isRegularFile) // Filter only regular files
                        .map(path -> {
                            try {
                                // Get file metadata
                                String fileName = path.getFileName().toString();
                                long fileSize = Files.size(path); // File size in bytes
                                Instant lastModified = Files.getLastModifiedTime(path).toInstant(); // Last modified time

                                // Return as DTO
                                return new FileMetadataDTO(fileName, fileSize, lastModified);
                            } catch (IOException e) {
                                throw new RuntimeException("Failed to retrieve metadata for file: " + path, e);
                            }
                        })
                        .collect(Collectors.toList());

            } catch (IOException e) {
                throw new RuntimeException("Failed to retrieve file metadata from directory: " + _PATH_task, e);
            }

        } else {
            throw new UploadForbiddenException("Fail to upload file to this task");
        }
    }

}
