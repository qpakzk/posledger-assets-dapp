/*===============================================
 *Copyright(c) 2014 POSCO/POSDATA
 *
 *@ProcessChain   : FEMS
 *@File           : ParameterRequiredException.java
 *@FileName       : ParameterRequiredException
 *
 *Change history
 *@LastModifier : 이재만
 *@수정 날짜; SCR_NO; 수정자; 수정내용
 * 2014-04-10; 이재만; Initial Version
 ===============================================*/

package com.poscoict.assets.exception;

/**
 * 필수 파라미터 누락인 경우 발생하는 exception
 * @author JM
 */
public class ParameterRequiredException extends RuntimeException {

	private static final long serialVersionUID = 4806747337714843894L;

	public final static String ERROR_CODE = "parameter_required";
	
	private String model;

	public ParameterRequiredException() {
		super(ERROR_CODE);
	}

	public ParameterRequiredException(String s) {
		super(s);
	}
	
	public ParameterRequiredException(String s, String model) {
		super(s);
		this.setModel(model);
	}

	public ParameterRequiredException(Throwable t) {
		super(t);
	}

	public ParameterRequiredException(String s, Throwable t) {
		super(s, t);
	}

	public String getErrorCode() {
		return ERROR_CODE;
	}
	
	public String getModel() {
		return this.model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}

	public void setLoginFailCount(String model) {
		this.model = model;
	}
}
