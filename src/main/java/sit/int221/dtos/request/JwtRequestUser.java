package sit.int221.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JwtRequestUser {

    @NotBlank
    @Size(max = 50)
    private String userName;
    @NotBlank
    @Size(max = 14)
    private String password;
}
