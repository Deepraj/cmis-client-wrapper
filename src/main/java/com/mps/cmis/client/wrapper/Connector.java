package com.mps.cmis.client.wrapper;

import org.apache.chemistry.opencmis.client.api.Session;

import com.mps.cmis.client.wrapper.enums.Version;
import com.mps.cmis.client.wrapper.operations.DownloadDocument;
import com.mps.cmis.client.wrapper.operations.UploadDocument;
import com.mps.cmis.client.wrapper.session.CMISSession;

public class Connector {
	
	private static Session session;
	
	private Connector(){}
	
	static{		
		session = new CMISSession().retrieveSession();
	}
	
	public static String uploadDocument(String path, String fileName, byte[] content, Version version) {

		UploadDocument uploadDocument = new UploadDocument(session);
		String objectID = uploadDocument.uploadDoc(path, fileName, content, version);
		return objectID;
	}
	
	public static String downloadDocumant(String folderPath, String fileName, String version) {

		DownloadDocument downloadDocument = new DownloadDocument(session);
		String downloadedContent = downloadDocument.downloadDoc(folderPath, fileName, version);
		return downloadedContent;
	}

}
