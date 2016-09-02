package com.mps.cmis.client.wrapper;

import java.io.Serializable;

public class CMISDownloadRequest implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String folderPath;
	private String fileName; 
	private String version;
	
	public CMISDownloadRequest(String folderPath, String fileName, String version) {
		super();
		this.folderPath = folderPath;
		this.fileName = fileName;
		this.version = version;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public String getFileName() {
		return fileName;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return "CMISDownloadRequest [folderPath=" + folderPath + ", fileName=" + fileName + ", version=" + version
				+ "]";
	}
	
	
	

}
