package com.wemeet.dating.model;


import com.wemeet.dating.model.enums.TokenType;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class TokenInfo {
    @NotBlank
    private String accessToken;

    private TokenType tokenType;

}

