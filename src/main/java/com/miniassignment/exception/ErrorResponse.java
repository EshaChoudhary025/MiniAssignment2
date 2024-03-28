package com.miniassignment.exception;

public class ErrorResponse {

	private String message;
	private int code;
	private String timestamp;

	public ErrorResponse(String message, int code, String timestamp) {
		this.message = message;
		this.code = code;
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public int getCode() {
		return code;
	}

	public String getTimestamp() {
		return timestamp;
	}
}
