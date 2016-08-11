package com.mps.cmis.client.wrapper;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.chemistry.opencmis.client.api.Session;

import com.mps.cmis.client.wrapper.enums.Version;
import com.mps.cmis.client.wrapper.operations.DownloadDocument;
import com.mps.cmis.client.wrapper.operations.UploadDocument;
import com.mps.cmis.client.wrapper.session.CMISSession;

/**
 * Hello world!
 * 
 */
public class Main {
	
	static String path = "/Sites/cmis-wrapper/client3/documents/";
	static String fileName = "test1.txt";
	
	
	public static void main(String[] args) throws Exception {

		CMISSession cmisSession = new CMISSession();
		Session session = cmisSession.retrieveSession();
		String objectID = uploadDocument(session);
		//downloadDocumentByObjectID(session, objectID);
		

		downloadDocumantByFileName(session, path, fileName, "1.0");
		    
	}

	public static String uploadDocument(Session session) {

		UploadDocument uploadDocument = new UploadDocument(session);
		byte[] content = content();
		Version version = Version.MAJOR;
		String objectID = uploadDocument.uploadDoc(path, fileName, content, version);
		return objectID;

	}

	public static void downloadDocumantByFileName(Session session, String folderPath, String fileName, String version) throws Exception {

		DownloadDocument downloadDocument = new DownloadDocument(session);
		String downloadedContent = downloadDocument.downloadDoc(folderPath, fileName, version);
		System.out.println("downloaded content by file name ");
		System.out.println(downloadedContent);
	}

	private static byte[] content() {
		String docText = "This is version 3.0";
		byte[] content = docText.getBytes();
		return content;
	}
	
}
