package sit.int221.dtos.response;

import lombok.Data;

import java.time.Instant;


@Data
public class FileMetadataDTO {
    private String fileName;
    private long fileSize; // Size in bytes
    private Instant lastModified; // Last modified timestamp

    // Constructor
    public FileMetadataDTO(String fileName, long fileSize, Instant lastModified) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.lastModified = lastModified;
    }
}
