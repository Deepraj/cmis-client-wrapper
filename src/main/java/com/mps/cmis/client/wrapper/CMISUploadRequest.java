package com.mps.cmis.client.wrapper;

import java.io.Serializable;
import java.util.Arrays;

import com.mps.cmis.client.wrapper.enums.Version;

public class CMISUploadRequest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String folderPath; 
	private String fileName; 
	private Version version;
	private byte[] content; 
	
	
	public CMISUploadRequest(String folderPath, String fileName, byte[] content, Version version) {
		super();
		this.folderPath = folderPath;
		this.fileName = fileName;
		this.content = content;
		this.version = version;
	}

	public String getFolderpath() {
		return folderPath;
	}

	public String getFileName() {
		return fileName;
	}

	public Version getVersion() {
		return version;
	}
	
	public byte[] getContent() {
		return content;
	}


	@Override
	public String toString() {
		return "CMISRequest [folderpath=" + folderPath + ", fileName=" + fileName + ", version=" + version + ",content=" + Arrays.toString(content) + "]";
	}	

}
