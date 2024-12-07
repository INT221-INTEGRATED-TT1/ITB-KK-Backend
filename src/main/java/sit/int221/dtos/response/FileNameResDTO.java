package sit.int221.dtos.response;

import lombok.Data;

import java.util.List;
@Data
public class FileNameResDTO {
    private List<String> filenames;

    public FileNameResDTO(List<String> filenames){
        this.filenames = filenames;
    }
}
