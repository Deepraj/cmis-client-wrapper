package com.mps.cmis.client.wrapper.exception;

@SuppressWarnings("serial")
public class CMISException extends Exception {

	
	private String message = null;
	private Exception exception = null;
	
	public String getMessage() {
		return message;
	}

	public Exception getException() {
		return exception;
	}

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
