package sit.int221.dtos.response;

import lombok.Data;

@Data
public class StatusHomeCountDTO {
    private Integer id;
    private String name;
    private String description;
    private String color;
    private Integer count;
}
