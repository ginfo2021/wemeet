package com.wemeet.dating.api;


import com.wemeet.dating.dao.ErrorRepository;
import com.wemeet.dating.exception.*;
import com.wemeet.dating.model.entity.WeMeetError;
import com.wemeet.dating.model.response.ApiResponse;
import com.wemeet.dating.model.response.ResponseCode;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    protected ApiResponse apiResponse = null;
    protected String message = "";
    protected String defaultErrorMessage = "An Error Occurred while processing your request  ";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        ex.printStackTrace();
        message = ex.getMessage();
        ResponseCode responseCode = ResponseCode.ERROR;

        if (status == HttpStatus.NOT_FOUND) {
            responseCode = ResponseCode.RESOURCE_NOT_FOUND;
        }

        apiResponse = buildErrorResponse(message, new ArrayList<>(), responseCode, ex, status);

        return new ResponseEntity<>(apiResponse, status);

    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(fieldName + ": " + errorMessage);
        });

        if (errors.size() <= 0) {
            message = defaultErrorMessage;
        } else {
            message = errors.get(0);
        }
        apiResponse = buildErrorResponse(message, errors, ResponseCode.VALIDATION_ERROR, ex, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(InvalidJwtAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<Object> handleInvalidJwtException(InvalidJwtAuthenticationException ex) {
        ex.printStackTrace();

        apiResponse = buildErrorResponse(ex.getMessage(), new ArrayList<>(), ResponseCode.INVALID_TOKEN, ex, HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidCredentialException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<Object> handleInvalidCredentialException(InvalidCredentialException ex) {
        ex.printStackTrace();

        apiResponse = buildErrorResponse(ex.getMessage(), new ArrayList<>(), ResponseCode.INVALID_USERNAME_PASSWORD, ex, HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(SuspendedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<Object> handleSuspendedUserException(SuspendedUserException ex) {
        ex.printStackTrace();

        apiResponse = buildErrorResponse(ex.getMessage(), new ArrayList<>(), ResponseCode.SUSPENDED_USER, ex, HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);

    }


    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<Object> handleDuplicateKeyExceptionException(DuplicateKeyException ex) {
        ex.printStackTrace();

        apiResponse = buildErrorResponse(ex.getMessage(), new ArrayList<>(), ResponseCode.ERROR, ex, HttpStatus.CONFLICT);
        return new ResponseEntity<>(apiResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<Object> handleEntityNotFoundExceptionException(EntityNotFoundException ex) {
        ex.printStackTrace();

        apiResponse = buildErrorResponse(ex.getMessage(), new ArrayList<>(), ResponseCode.ENTITY_NOT_FOUND, ex, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InactiveUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleInactiveUserExceptionException(InactiveUserException ex) {
        ex.printStackTrace();

        apiResponse = buildErrorResponse(ex.getMessage(), new ArrayList<>(), ResponseCode.USER_NOT_VERIFIED, ex, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleBadRequestExceptionException(BadRequestException ex) {
        ex.printStackTrace();

        apiResponse = buildErrorResponse(ex.getMessage(), new ArrayList<>(), ResponseCode.ERROR, ex, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsersNotMatchedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleUsersNotMatchedException(UsersNotMatchedException ex) {
        ex.printStackTrace();

        apiResponse = buildErrorResponse(ex.getMessage(), new ArrayList<>(), ResponseCode.USERS_NOT_MATCHED, ex, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotPremiumException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleUserNotPremiumException(UserNotPremiumException ex) {
        ex.printStackTrace();

        apiResponse = buildErrorResponse(ex.getMessage(), new ArrayList<>(), ResponseCode.USER_NOT_PREMIUM, ex, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BlockedUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleBlockedUserException(BlockedUserException ex) {
        ex.printStackTrace();

        apiResponse = buildErrorResponse(ex.getMessage(), new ArrayList<>(), ResponseCode.BLOCKED_USER, ex, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PreferenceNotSetException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handlePreferenceNotSetExceptionException(PreferenceNotSetException ex) {
        ex.printStackTrace();

        apiResponse = buildErrorResponse(ex.getMessage(), new ArrayList<>(), ResponseCode.PREFERENCE_NOT_SET, ex, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFileTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleInvalidFileTypeException(InvalidFileTypeException ex) {
        ex.printStackTrace();

        apiResponse = buildErrorResponse(ex.getMessage(), new ArrayList<>(), ResponseCode.ERROR, ex, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleConstraintViolationExceptionException(ConstraintViolationException ex) {
        ex.printStackTrace();

        apiResponse = buildErrorResponse(ex.getMessage(), new ArrayList<>(), ResponseCode.VALIDATION_ERROR, ex, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WeMeetServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<Object> handleWeMeetServerException(WeMeetServerException ex) {
        ex.printStackTrace();

        String message = (ex.getMessage() != null && StringUtils.hasText(ex.getMessage())) ? ex.getMessage() : defaultErrorMessage;
        apiResponse = buildErrorResponse(message, new ArrayList<>(), ResponseCode.ERROR, ex, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<Object> handleResourceNotFoundExceptionException(ResourceNotFoundException ex) {
        ex.printStackTrace();

        apiResponse = buildErrorResponse(ex.getMessage(), new ArrayList<>(), ResponseCode.RESOURCE_NOT_FOUND, ex, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<Object> handleException(Exception ex) {
        ex.printStackTrace();

        apiResponse = buildErrorResponse(defaultErrorMessage, new ArrayList<>(), ResponseCode.ERROR, ex, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }


    private ApiResponse buildErrorResponse(String message, List<String> errorList, ResponseCode responseCode, Exception exception, HttpStatus httpStatus) {
        String logId = UUID.randomUUID().toString();
        String fullUrl = null;
        if (httpServletRequest != null) {
            fullUrl = httpServletRequest.getRequestURL().toString();
            if (StringUtils.hasText(httpServletRequest.getQueryString()))
                fullUrl += "?" + httpServletRequest.getQueryString();
        }
        try {
            ErrorRepository errorRepository = applicationContext.getBean("errorRepository", ErrorRepository.class);
            errorRepository.save(

                    WeMeetError.builder()
                            .logId(logId)
                            .message(message)
                            .path(fullUrl)
                            .responseCode(responseCode)
                            .httpResponseCode(httpStatus.value())
                            .stackTrace(ExceptionUtils.getStackTrace(exception))
                            .build()
            );


        } catch (Exception ex) {

        }

        return ApiResponse.builder()
                .message(message)
                .responseCode(responseCode)
                .logId(logId)
                .errors(errorList)
                .build();
    }


}
