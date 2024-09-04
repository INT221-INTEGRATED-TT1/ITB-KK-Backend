package sit.int221.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class LocalUserDto implements Serializable {
    @NotNull
    @Size(max = 45)
    @NotBlank
    String oid;
    @NotNull
    @Size(max = 45)
    @NotBlank
    String userName;
}