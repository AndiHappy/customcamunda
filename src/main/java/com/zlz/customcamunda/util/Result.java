package com.zlz.customcamunda.util;

/**
 * @author zhailz
 *
 * @version 2018年4月18日 下午2:20:45
 */
public class Result {
	
	private int code;
	private Throwable exception;
	
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public Throwable getException() {
		return exception;
	}
	public void setException(Throwable exception) {
		this.exception = exception;
	}
	
	

}
