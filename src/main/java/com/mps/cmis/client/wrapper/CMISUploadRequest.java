package com.mps.cmis.client.wrapper;

import java.util.Arrays;

import com.mps.cmis.client.wrapper.enums.Version;

public class CMISUploadRequest implements CMISRequest{
	
	private String folderpath; 
	private String fileName; 
	private byte[] content; 
	private Version version;
	
	public CMISUploadRequest(String folderpath, String fileName, byte[] content, Version version) {
		super();
		this.folderpath = folderpath;
		this.fileName = fileName;
		this.content = content;
		this.version = version;
	}

	public String getFolderpath() {
		return folderpath;
	}

	public String getFileName() {
		return fileName;
	}

	public byte[] getContent() {
		return content;
	}

	public Version getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return "CMISRequest [folderpath=" + folderpath + ", fileName=" + fileName + ", content="
				+ Arrays.toString(content) + ", version=" + version + "]";
	}	

}
