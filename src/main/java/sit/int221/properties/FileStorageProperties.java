package sit.int221.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "file")
@Component
public class FileStorageProperties {
    private String uploadDir;
    private String fileServiceHostName;
}
