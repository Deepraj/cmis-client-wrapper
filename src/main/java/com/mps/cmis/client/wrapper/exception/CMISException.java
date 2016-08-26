package com.mps.cmis.client.wrapper.exception;

public class CMISException extends Exception {

	private static final long serialVersionUID = 1L;
	private String message = null;
	private Exception exception = null;

	public CMISException() {
		super();
	}

	public CMISException(String message, Exception exception) {
		super(message, exception);
		this.message = message;
		this.exception = exception;
	}

	@Override
	public String toString() {
		return message + exception.toString();
	}

}
