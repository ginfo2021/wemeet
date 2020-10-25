package com.wemeet.dating.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {
    @NotNull
    private String authorization_url;

    @NotNull
    private String access_code;

    @NotNull
    private String reference;

    public PaymentResponse(){
    }

    @JsonProperty("data")
    private void unpackNameFromNestedObject(Map<String, String> data) {
        access_code = data.get("access_code");
        authorization_url = data.get("authorization_url");
        reference = data.get("reference");
    }
}
