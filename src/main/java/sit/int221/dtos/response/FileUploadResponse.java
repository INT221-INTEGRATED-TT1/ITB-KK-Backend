package sit.int221.dtos.response;

import lombok.Data;

import java.util.List;

@Data
public class FileUploadResponse {
    private List<String> uploadedFiles;
    private List<String> notAdded_MAX_FILES;
    private List<String> notAdded_MAX_FILE_SIZE;
    private String message;
}
