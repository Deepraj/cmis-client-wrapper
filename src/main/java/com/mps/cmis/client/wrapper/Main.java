package com.mps.cmis.client.wrapper;

import com.mps.cmis.client.wrapper.enums.Version;

/**
 * Hello world!
 * 
 */
public class Main {
	
	static String path = "/Sites/cmis-wrapper/client7/documents/";
	static String fileName = "test.txt";
	
	
	public static void main(String[] args) {

		String objectID = Connector.uploadDocument(path, fileName, content(), Version.MAJOR);
		System.out.println("Uploaded document object id is: "+ objectID);
		
		System.out.println("************************************");
		String content = Connector.downloadDocumant(path, fileName, "1.0");
		System.out.println(content);

	}

	private static byte[] content() {
		String docText = "This is version 1.0";
		byte[] content = docText.getBytes();
		return content;
	}
	
}
