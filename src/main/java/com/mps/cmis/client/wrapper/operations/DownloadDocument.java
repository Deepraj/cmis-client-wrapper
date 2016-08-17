package com.mps.cmis.client.wrapper.operations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.mps.cmis.client.wrapper.CMISDownloadResponse;
import com.mps.cmis.client.wrapper.session.CMISSession;

public class DownloadDocument {
	static Logger LOGGER = Logger.getLogger(DownloadDocument.class);
	private Session session;

	public DownloadDocument(CMISSession cmisSession) throws Exception {
		this.session = cmisSession.retrieveSession();
	}

	public CMISDownloadResponse downloadDoc(String folderPath, String fileName, String version) throws IOException {
		
		String path = folderPath + "/" + fileName;
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
		LOGGER.info("Download the document having object ID:"+newObjectID);
		return newObjectID;
	}

	private File download(String objectID) throws IOException{
	
		Document doc = (Document) session.getObject(objectID);

		LOGGER.info("**********Properties of downloading document**************");
		List<Property<?>> props = doc.getProperties();
		for (Property<?> p : props) {
			System.out.println(p.getDefinition().getDisplayName() + "=" + p.getValuesAsString());
			LOGGER.info(p.getDefinition().getDisplayName() + "=" + p.getValuesAsString());
		}
		LOGGER.info("***********Properties Ends*************");
		
		ContentStream contentStream = doc.getContentStream();
		File file=new File(doc.getName());
		FileOutputStream fileOutputStream=new FileOutputStream(file);
		IOUtils.copy(contentStream.getStream(),fileOutputStream);
		LOGGER.info("Doument has been downloaded having name:"+doc.getName());
		
		return file;
	}	
	
}


