package sit.int221.dtos.request;

import lombok.Data;

@Data
public class LimitStatusMaskReq {
    private Boolean limitMaximumTask;
    private Integer limit;
}
