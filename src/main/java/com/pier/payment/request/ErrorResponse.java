package com.pier.payment.request;

public class ErrorResponse {
	Integer status;
	ResponseDetail response;
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public ResponseDetail getResponse() {
		return response;
	}

	public void setResponse(ResponseDetail response) {
		this.response = response;
	}
	
	
	

}
