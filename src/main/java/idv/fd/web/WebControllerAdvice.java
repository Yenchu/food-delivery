package idv.fd.web;

import idv.fd.error.AppError;
import idv.fd.error.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class WebControllerAdvice
        extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebControllerAdvice.class);

    protected HttpStatus getStatus(HttpServletRequest request) {

        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> appException(AppException e, HttpServletRequest request) {

        log.error(e.getMessage());
        HttpStatus status;
        AppError body;

        if (e.getError() != null) {
            AppError error = e.getError();

            status = error.getStatus() != null ? HttpStatus.resolve(error.getStatus()) : getStatus(request);

            body = error;
        } else {
            status = getStatus(request);

            body = AppError.builder()
                    .status(status.value())
                    .msg(status.getReasonPhrase())
                    .build();
        }
        return new ResponseEntity<>(body, new HttpHeaders(), status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception e, HttpServletRequest httpRequest, WebRequest request) {

        return handleExceptionInternal(e, null, new HttpHeaders(), getStatus(httpRequest), request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request
    ) {

        log.error(ex.getMessage(), ex);
        AppError respBody = AppError.builder()
                .status(status.value())
                .msg(body != null ? body.toString() : status.getReasonPhrase())
                .build();

        return new ResponseEntity<>(respBody, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request
    ) {

        Map<String, String> body = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach((error) -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    body.put(fieldName, errorMessage);
                });

        return handleExceptionInternal(ex, body, headers, status, request);
    }
}
