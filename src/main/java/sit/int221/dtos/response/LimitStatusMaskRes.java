package sit.int221.dtos.response;

import lombok.Data;

@Data
public class LimitStatusMaskRes {
    private String  name;
    private Boolean limitMaximumTask;
    private Integer limit;
}
