package sit.int221.dtos.response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardResDTO {
    private String id;
    private String name;
    private String visibility;
    private OwnerBoard owner;
}
