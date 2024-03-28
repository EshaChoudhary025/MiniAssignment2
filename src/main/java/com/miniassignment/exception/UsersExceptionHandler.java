package com.miniassignment.exception;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class UsersExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {

		ErrorResponse e = new ErrorResponse(ex.getMessage(), ex.getCode(),

				new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(new Date()));

		return new ResponseEntity<>(e, HttpStatus.valueOf(ex.getCode()));
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		ErrorResponse e = new ErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(),
				new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(new Date()));

		return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}