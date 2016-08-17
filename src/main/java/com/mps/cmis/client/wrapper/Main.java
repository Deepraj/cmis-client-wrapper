package com.mps.cmis.client.wrapper;

import com.mps.cmis.client.wrapper.enums.Version;

/**
 * Hello world!
 * 
 */
public class Main {
	
	static String path = "/Sites/cmis-wrapper/client11/documents/";
	static String fileName = "test.txt";
	
	
	public static void main(String[] args) {

		CMISUploadRequest cmisUploadRequest = new CMISUploadRequest(path, fileName, content(), Version.MAJOR);
		CMISUploadResponse cmisUploadResponse  = Connector.uploadDocument(cmisUploadRequest);
		if(cmisUploadResponse.isSuccess()){
			System.out.println("Uploaded object id is: "+ cmisUploadResponse.getObjectID());
		}else{
			System.out.println("Error message: "+ cmisUploadResponse.getException());
		}
		
		
		System.out.println("************************************");
		
		CMISDownloadRequest cmisDownloadRequest = new CMISDownloadRequest(path, fileName, "6.0");
		CMISDownloadResponse cmisDownloadResponse = Connector.downloadDocumant(cmisDownloadRequest);
		if(cmisDownloadResponse.isSuccess()){
			System.out.println("Content: "+cmisDownloadResponse.getContent());
		}else{
			System.out.println("Error message: "+ cmisDownloadResponse.getException());
		}
		

	}

	private static byte[] content() {
		String docText = "This is version 9.0";
		byte[] content = docText.getBytes();
		return content;
	}
	
}
