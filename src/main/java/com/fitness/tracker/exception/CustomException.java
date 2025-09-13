package com.fitness.tracker.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

  private final HttpStatus statusCode;
  private final String errorMessage;

  public CustomException(HttpStatus statusCode, String errorMessage) {
    this.statusCode = statusCode;
    this.errorMessage = errorMessage;
  }

  public HttpStatus getStatusCode() {
    return this.statusCode;
  }

  public String getErrorMessage() {
    return this.errorMessage;
  }
}