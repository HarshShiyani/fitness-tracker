package com.fitness.tracker.exception;

import com.fitness.tracker.dto.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(value = CustomException.class)
  public ResponseEntity<BaseResponse<Void>> customExceptionHandler(CustomException exception) {
    log.error("Custom Exception :: {}", exception.getMessage());
    return ResponseEntity.status(exception.getStatusCode())
        .body(new BaseResponse<>(exception.getErrorMessage()));
  }

  @ExceptionHandler(value = IllegalArgumentException.class)
  public ResponseEntity<BaseResponse<Object>> illegalArgumentExceptionHandler(
      IllegalArgumentException exception) {
    log.error("Illegal Argument Exception :: {}", exception.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new BaseResponse<>(exception.getMessage()));
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<BaseResponse<Object>> methodArgumentNotValidExceptionHandler(
      MethodArgumentNotValidException exception) {
    log.error("Method Argument Not Valid :: {}", exception.getMessage());
    String errorMessage = exception.getBindingResult().getAllErrors().stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .reduce((msg1, msg2) -> msg1 + ", " + msg2)
        .orElse("Invalid Data");

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new BaseResponse<>(errorMessage));
  }

  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<BaseResponse<Void>> handleAccessDenied(AuthorizationDeniedException exception) {
    log.error("Access Denied :: {}", exception.getMessage());
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(new BaseResponse<>("Access Denied: You don’t have permission to perform this action"));
  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse<Object>> generic(Exception exception) {
    exception.printStackTrace();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new BaseResponse<>("Something Went Wrong :("));
  }
}
