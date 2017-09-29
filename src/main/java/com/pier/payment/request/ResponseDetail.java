package com.pier.payment.request;

import java.util.List;

public class ResponseDetail {
	String message;
	String error;
	Integer status;
	List<ErrorCause> cause;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<ErrorCause> getCause() {
		return cause;
	}

	public void setCause(List<ErrorCause> cause) {
		this.cause = cause;
	}

}
