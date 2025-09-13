package com.fitness.tracker.exception;

import com.fitness.tracker.dto.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse<Object>> generic(Exception exception) {
    exception.printStackTrace();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new BaseResponse<>("Something Went Wrong :("));
  }
}
