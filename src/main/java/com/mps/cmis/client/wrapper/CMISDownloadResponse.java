package com.mps.cmis.client.wrapper;

import java.io.Serializable;

import com.mps.cmis.client.wrapper.exception.CMISException;

public class CMISDownloadResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isSuccess;
	private byte[] content;
	private String objectID;
	private CMISException cmisException;
	
	public CMISException getCmisException() {
		return cmisException;
	}
	public void setCmisException(CMISException cmisException) {
		this.cmisException = cmisException;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
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
