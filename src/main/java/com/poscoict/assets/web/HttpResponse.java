package com.poscoict.assets.web;

public class HttpResponse {
	
	public static final String success = "success";
	
	public static final String fail = "generic_fail";
	
	public static final String fail_403 = "403";

	private String code;

	private String message;
	
	public HttpResponse() {}
	
	public HttpResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OauthResponse [code=").append(code)
				.append(", message=").append(message).append("]");
		return builder.toString();
	}
}
