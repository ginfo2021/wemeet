package com.wemeet.dating.model.response;

import java.util.List;

public class ApiResponse {

    private String message;
    private Object data;
    private ResponseCode responseCode;
    private List<String> errors;
    private String logId;

    public static class ResponseBuilder {
        private String message;
        private Object data;
        private ResponseCode responseCode;
        private List<String> errors;
        private String logId;

        public ResponseBuilder() {
        }

        public ResponseBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public ResponseBuilder setData(Object data) {
            this.data = data;
            return this;
        }


        public ResponseBuilder setResponseCode(ResponseCode responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public ResponseBuilder setLogId(String logId) {
            this.logId = logId;
            return this;
        }

        public ResponseBuilder setErrors(List<String> errors) {
            this.errors = errors;
            return this;
        }

        public ApiResponse build() {
            return new ApiResponse(this);
        }
    }


    private ApiResponse(final ResponseBuilder builder) {
        this.message = builder.message;
        this.data = builder.data;
        this.responseCode = builder.responseCode;
        this.errors = builder.errors;
        this.logId = builder.logId;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }
}
