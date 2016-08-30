package com.mps.cmis.client.wrapper.operations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
		
		String path = getFilePath(folderPath, fileName);
		Document doc = getAppropriateDocument(version,path);
		LOGGER.info("Downloading the file having name: "+fileName+" from location: "+folderPath+" with objectId "+doc.getId());
		File content = download(doc);	
		CMISDownloadResponse cmisDownloadResponse = new CMISDownloadResponse();
		cmisDownloadResponse.setSuccess(true);	
		cmisDownloadResponse.setContent(content);
		cmisDownloadResponse.setObjectID(doc.getId());
		return cmisDownloadResponse;
	}
	
	private String createObjectId(String previousId, String version) {
		String newObjectID = null;
		String[] splittedObjectID = previousId.split(";");
		newObjectID = splittedObjectID[0] + ";" + version;
		LOGGER.info("New ObjectId for the downloading document is: "+newObjectID);
		return newObjectID;
	}

	private File download(Document doc) throws IOException{
		
		ContentStream contentStream = doc.getContentStream();
		File file=new File(doc.getName());
		FileOutputStream fileOutputStream=new FileOutputStream("");
		IOUtils.copy(contentStream.getStream(),fileOutputStream);
		LOGGER.info("File Object For the requested Document: "+file.getName() +" has been returned.");
		
		return file;
	}	
		
	private String getFilePath(String folderpath, String fileName){
		
		if(!folderpath.endsWith("/")){
			folderpath = folderpath + "/";
		}	
		LOGGER.info("File Path for the document to be downloaded is: "+folderpath + fileName);
		return folderpath + fileName;
		
	}
	
	private Document getAppropriateDocument(String version, String path) {
		
		Document doc = (Document) session.getObjectByPath(path);
		String objectID = null;
		if ((doc.getVersionLabel().equals(version) || version == null)) {
			objectID = doc.getId();
			LOGGER.info("Object ID for the document having latest version or if version is null : "+objectID);
		} else {
			objectID = createObjectId(doc.getId(), version);
			LOGGER.info("Object Id for the requested document that has to be downloaded :"+objectID);
			doc = (Document) session.getObject(objectID);
		}
		return doc;

	}
}


