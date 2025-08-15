package com.nilgil.book.core.api.error;

import com.nilgil.book.share.CoreException;
import com.nilgil.book.share.ErrorResponse;
import com.nilgil.book.share.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String GENERATED_ERROR_CODE_FORMAT = "C%d";

    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ErrorResponse> handleCoreException(CoreException ex) {
        log.warn("Handling CoreException: {}", ex.getMessage());

        ErrorType errorType = ex.getErrorType();
        String errorMessage = ex.getMessage() == null ? errorType.getMessage() : ex.getMessage();
        ErrorResponse errorResponse = new ErrorResponse(errorType.getErrorCode(), errorMessage);

        return ResponseEntity.status(errorType.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        log.error("Unhandled exception occurred", ex);

        HttpStatusCode statusCode = resolveHttpStatusCode(ex);
        ErrorResponse errorResponse = new ErrorResponse(makeErrorCode(statusCode), ex.getMessage());

        return ResponseEntity.status(statusCode.value()).body(errorResponse);
    }

    private HttpStatusCode resolveHttpStatusCode(Throwable e) {
        if (e instanceof org.springframework.web.ErrorResponse) {
            return ((org.springframework.web.ErrorResponse) e).getStatusCode();
        }

        ResponseStatus annotation = e.getClass().getAnnotation(ResponseStatus.class);
        if (annotation != null) {
            return annotation.code();
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private static String makeErrorCode(HttpStatusCode statusCode) {
        return GENERATED_ERROR_CODE_FORMAT.formatted(statusCode.value());
    }
}
