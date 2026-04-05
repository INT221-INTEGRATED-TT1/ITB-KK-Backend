package sit.int221.controllers;

import io.jsonwebtoken.Claims;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sit.int221.dtos.response.FileMetadataDTO;
import sit.int221.dtos.response.FileNameResDTO;
import sit.int221.dtos.response.FileUploadResponse;
import sit.int221.entities.task_base.Board;
import sit.int221.services.FileService;
import sit.int221.services.itbkk_shared.AuthorizationService;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/v3/boards")
@CrossOrigin(origins = {"http://localhost:5173", "https://intproj23.sit.kmutt.ac.th", "http://localhost:80", "https://ip23tt1.sit.kmutt.ac.th"})
public class FileController {
    @Autowired
    private FileService fileService;
    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/{boardId}/tasks/{taskId}/attachments")
    public ResponseEntity<Object> fileUpload(@RequestHeader("Authorization") String token,
                                             @PathVariable String boardId,
                                             @PathVariable Integer taskId,
                                             @RequestParam("file") List<MultipartFile> file) {
        Claims claims = authorizationService.validateToken(token);
        List<String> uploadedFiles = fileService.store(claims, boardId, taskId, file);
        return ResponseEntity.ok("You successfully uploaded " + uploadedFiles);
    }

    @GetMapping("/{boardId}/tasks/{taskId}/attachments")
    public ResponseEntity<Object> getFileFromTask(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable String boardId, @PathVariable Integer taskId) {
        Board board = authorizationService.getBoardId(boardId);
        if(board.getVisibility().equalsIgnoreCase("PUBLIC")){
            return ResponseEntity.ok(fileService.getFileMetadataInDirectory(boardId,taskId));
        }
        authorizationService.validateClaims(token);
        Claims claims = authorizationService.validateToken(token);
        return ResponseEntity.ok(fileService.getFileMetadataInDirectory(claims, boardId, taskId));
    }


    @GetMapping("/{boardId}/tasks/{taskId}/{filename:.+}/attachments")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@RequestHeader("Authorization") String token,
                                              @PathVariable String boardId,
                                              @PathVariable Integer taskId,
                                              @PathVariable String filename) {
        // Construct directory structure
        String directory = boardId + "/" + taskId;
        Resource file = fileService.loadFileAsResource(directory, filename);
        String contentType;
        String extension = filename.substring(filename.lastIndexOf('.')).toLowerCase();

        try {
            contentType = Files.probeContentType(file.getFile().toPath());
        } catch (IOException ex) {
            contentType = "application/octet-stream";
        }

        System.out.println(contentType);

        HttpHeaders headers = new HttpHeaders();

        switch (extension) {
            case ".pdf":
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDisposition(ContentDisposition.inline().filename(file.getFilename()).build());
                break;
            case ".png":
                headers.setContentType(MediaType.IMAGE_PNG);
                headers.setContentDisposition(ContentDisposition.inline().filename(file.getFilename()).build());
                break;
            case ".jpeg":
            case ".jpg":
                headers.setContentType(MediaType.IMAGE_JPEG);
                headers.setContentDisposition(ContentDisposition.inline().filename(file.getFilename()).build());
                break;
            case ".gif":
            case ".jfif":
                headers.setContentType(MediaType.IMAGE_GIF);
                break;
            case ".txt":
                headers.setContentDisposition(ContentDisposition.inline().filename(file.getFilename()).build());
                headers.setContentType(MediaType.TEXT_PLAIN);
                break;
            case ".log":
                headers.setContentType(MediaType.TEXT_PLAIN);
                break;
            case ".html":
            case ".htm":
                headers.setContentType(MediaType.TEXT_HTML);
                break;
            case ".json":
                headers.setContentType(MediaType.APPLICATION_JSON);
                break;
            case ".xml":
                headers.setContentType(MediaType.APPLICATION_XML);
                break;
            case ".rtf":
                headers.setContentDisposition(ContentDisposition.inline().filename(file.getFilename()).build());
                headers.setContentType(MediaType.parseMediaType("application/rtf"));
                break;

//            case ".zip":
//            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//            headers.setContentDisposition(ContentDisposition.attachment().filename(file.getFilename()).build());
//            break;
            default:
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDisposition(ContentDisposition.attachment().filename(file.getFilename()).build());
                break;
        }
        return ResponseEntity.ok().headers(headers).body(file);
    }

    @DeleteMapping("/{boardId}/tasks/{taskId}/{filename:.+}/attachments")
    @ResponseBody
    public ResponseEntity<Object> removeFile(@RequestHeader("Authorization") String token,
                                             @PathVariable String boardId,
                                             @PathVariable Integer taskId,
                                             @PathVariable String filename) {
        Claims claims = authorizationService.validateToken(token);

        fileService.removeResource(claims, boardId, taskId, filename);
        return ResponseEntity.ok(filename + " has been deleted !!!");
    }
}
