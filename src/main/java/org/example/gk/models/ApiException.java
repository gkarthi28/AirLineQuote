package org.example.gk.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class ApiException extends  RuntimeException {
	public int statusCode;
	public String errorCode;

	public ApiException(int statusCode, String errorCode, String message) {
		super(message);
		this.statusCode = statusCode;
		this.errorCode = errorCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getErrorCode() {
		return errorCode;
	}
}
