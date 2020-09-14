package com.wemeet.dating.model.response;

import lombok.Builder;

import java.util.List;

@Builder
public class ApiResponse {
    private String message;
    private Object data;
    private ResponseCode responseCode;
    private List<String> errors;
    private String logId;
}
