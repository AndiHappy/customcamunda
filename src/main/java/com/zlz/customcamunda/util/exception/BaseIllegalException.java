package com.zlz.customcamunda.util.exception;

public class BaseIllegalException extends IllegalStateException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static BaseIllegalException tooManyBackActivities = new BaseIllegalException(10001,"current task has too many back activities，please user backSpecial method。");

	public static BaseIllegalException backActivitiIsWrongType =new BaseIllegalException(10002,"back activities is wrong type，back activiti must be ActivityImpl and Userbehavior");

	public static final BaseIllegalException noBackActivitiIs = new BaseIllegalException(10003,"current task has none back activities");

	private int code;
	private String msg;

	public BaseIllegalException(int code, String msg) {
		this.code = code;
		this.msg = msg;

	}
	
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
