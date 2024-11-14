package sit.int221.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sit.int221.services.FileService;

@RestController
@RequestMapping("/v3/boards/files")
public class FileController {
    @Autowired
    private FileService fileService;

    @GetMapping("/test")
    public ResponseEntity<Object> testPropertiesMapping() {
        return ResponseEntity.ok(fileService.getFileStorageLocation() +
                " has been created !!!");
    }

    @PostMapping("")
    public ResponseEntity<Object> fileUpload(@RequestParam("file") MultipartFile file) {
        fileService.store(file);
        return ResponseEntity.ok("You successfully uploaded " + file.getOriginalFilename());
    }
}
