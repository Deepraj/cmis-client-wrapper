package com.mps.cmis.client.wrapper;

import org.apache.log4j.Logger;

import com.mps.cmis.client.wrapper.enums.Version;

/**
 * Hello world!
 * 
 */
public class Main {
	static Logger LOGGER = Logger.getLogger(Main.class);
	static String path = "/Sites/cmis-wrapper/client11/documents/";
	static String fileName = "test.txt";
	
	
	public static void main(String[] args) {

		LOGGER.info("*********Cmis Application Started**********");
		LOGGER.info("*********Entered In Main**********");	
		CMISUploadRequest cmisUploadRequest = new CMISUploadRequest(path, fileName, content(), Version.MAJOR);
		CMISUploadResponse cmisUploadResponse  = Connector.uploadDocument(cmisUploadRequest);
		if(cmisUploadResponse.isSuccess()){
			System.out.println("Uploaded object id is: "+ cmisUploadResponse.getObjectID());
			LOGGER.info("Uploaded object id is: "+ cmisUploadResponse.getObjectID());
		}else{
			LOGGER.error("Error message: "+ cmisUploadResponse.getException());
			System.out.println("Error message: "+ cmisUploadResponse.getException());
		}
		
		
		System.out.println("************************************");
		
		CMISDownloadRequest cmisDownloadRequest = new CMISDownloadRequest(path, fileName, "20.0");
		CMISDownloadResponse cmisDownloadResponse = Connector.downloadDocument(cmisDownloadRequest);
		if(cmisDownloadResponse.isSuccess()){
			System.out.println("Content: "+cmisDownloadResponse.getContent());
			LOGGER.info("Content:"+cmisDownloadResponse.getContent());
		}else{
			System.out.println("Error message: "+ cmisDownloadResponse.getException());
			LOGGER.error("Error message"+cmisDownloadResponse.getException());
		}
		

	}

	private static byte[] content() {
		String docText = "This is version 9.0";
		byte[] content = docText.getBytes();
		return content;
	}
	
}
