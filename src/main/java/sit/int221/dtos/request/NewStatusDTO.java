package sit.int221.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

@Data
public class NewStatusDTO {
    @NotEmpty
    private String name;
    private String description;
    private String color;
}
