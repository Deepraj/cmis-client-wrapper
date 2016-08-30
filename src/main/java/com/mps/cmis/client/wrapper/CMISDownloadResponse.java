package com.mps.cmis.client.wrapper;

public class CMISDownloadResponse implements CMISResponse {
	
	private boolean isSuccess;
	private byte[] content;
	private String objectID;
	private String errorMessage;
	private Exception exception;
	public boolean isSuccess() {
		return isSuccess;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getObjectID() {
		return objectID;
	}
	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}	
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	
}
