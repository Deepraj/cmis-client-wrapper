package com.mps.cmis.client.wrapper;

public class CMISDownloadRequest implements CMISRequest {
	
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
