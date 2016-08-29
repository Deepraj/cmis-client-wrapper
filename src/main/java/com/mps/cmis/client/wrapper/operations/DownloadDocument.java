package com.mps.cmis.client.wrapper.operations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;

import com.mps.cmis.client.wrapper.CMISDownloadResponse;
import com.mps.cmis.client.wrapper.session.CMISSession;

public class DownloadDocument {

	private static DownloadDocument downloadDocumentSingletonInstance;

	public static DownloadDocument getInstance(CMISSession cmisSession) throws Exception {

		if (downloadDocumentSingletonInstance == null) {
			synchronized (UploadDocument.class) {
				if (downloadDocumentSingletonInstance == null) {
					downloadDocumentSingletonInstance = new DownloadDocument(cmisSession);
				}
			}
		}
		return downloadDocumentSingletonInstance;
	}

	private Session session;

	private DownloadDocument(CMISSession cmisSession) throws Exception {
		this.session = cmisSession.retrieveSession();
	}

	public CMISDownloadResponse downloadDoc(String folderPath, String fileName, String version) throws IOException {
		
		String path = getFilePath(folderPath, fileName);
		Document doc = (Document) session.getObjectByPath(path);
		String objectID = doc.getId();
		objectID = createObjectId(objectID, version);
		File content = download(objectID);
		CMISDownloadResponse cmisDownloadResponse = new CMISDownloadResponse();
		cmisDownloadResponse.setSuccess(true);	
		cmisDownloadResponse.setContent(content);
		cmisDownloadResponse.setObjectID(objectID);
		return cmisDownloadResponse;
	}
	
	private String createObjectId(String previousId, String version) {
		String newObjectID = null;
		String[] splittedObjectID = previousId.split(";");
		newObjectID = splittedObjectID[0] + ";" + version;
		return newObjectID;
	}

	private File download(String objectID) throws IOException{
	
		Document doc = (Document) session.getObject(objectID);
		ContentStream contentStream = doc.getContentStream();
		File file=new File(doc.getName());
		FileOutputStream fileOutputStream=new FileOutputStream(file);
		IOUtils.copy(contentStream.getStream(),fileOutputStream);
		
		return file;
	}	
		
	private String getFilePath(String folderpath, String fileName){
		
		if(!folderpath.endsWith("/")){
			folderpath = folderpath + "/";
		}		
		return folderpath + fileName;
		
	}
}


