package com.mps.cmis.client.wrapper.operations;

import java.io.IOException;
import java.io.InputStream;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.cmis.client.wrapper.CMISDownloadResponse;
import com.mps.cmis.client.wrapper.session.CMISSession;

public class DownloadDocument {

	private final static Logger LOGGER = LoggerFactory.getLogger(DownloadDocument.class);
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
		
		String fileAbsolutePath = getFilePath(folderPath, fileName);
		Document doc = getAppropriateDocument(fileAbsolutePath,version);
		LOGGER.info("Downloading the file: "+fileName+" from location: "+folderPath+" with objectId "+doc.getId());
		byte[] content = download(doc);	
		CMISDownloadResponse cmisDownloadResponse = new CMISDownloadResponse();
		cmisDownloadResponse.setSuccess(true);	
		cmisDownloadResponse.setContent(content);
		cmisDownloadResponse.setObjectID(doc.getId());
		return cmisDownloadResponse;
	}
	
	private String getFilePath(String folderpath, String fileName){
		
		if(!folderpath.endsWith("/")){
			folderpath = folderpath + "/";
		}	
		return folderpath + fileName;
		
	}
	
	private Document getAppropriateDocument(String fileAbsolutePath, String version) {
		
		Document doc = (Document) session.getObjectByPath(fileAbsolutePath);
		if (!(version == null || doc.getVersionLabel().equals(version))) {
			String objectID = createNewObjectId(doc.getId(), version);
			doc = (Document) session.getObject(objectID);
		} 
		return doc;

	}
	
	private String createNewObjectId(String previousId, String version) {
		String newObjectID = null;
		String[] splittedObjectID = previousId.split(";");
		newObjectID = splittedObjectID[0] + ";" + version;
		return newObjectID;
	}

	private byte[] download(Document doc) throws IOException{
		
		ContentStream contentStream = doc.getContentStream();
		byte[] content = getContentInBytes(contentStream.getStream());
		return content;
	}	
	
	private static byte[] getContentInBytes(InputStream inputStream) throws IOException {
		return IOUtils.toByteArray(inputStream);
	}
}


