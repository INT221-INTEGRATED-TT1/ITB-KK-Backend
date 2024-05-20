package sit.int221.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

@Data
public class NewStatusDTO {
    @NotNull
    @NotEmpty
    @Size(max = 50)
    private String name;
    @Size(max = 200)
    private String description;
    private String color;
}
