package com.fitness.tracker.dto;

public record BaseResponse<T>(String message, T data) {
  public BaseResponse(String resultMessage) {
    this(resultMessage, null);
  }

  public BaseResponse(String message, T data) {
    this.message = message;
    this.data = data;
  }
}