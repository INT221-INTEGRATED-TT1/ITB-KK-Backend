package sit.int221.dtos.response;

import lombok.Data;

@Data
public class AccessTokenDTORes {
    private String access_token;
    private String refresh_token;
}
