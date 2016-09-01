package com.mps.cmis.client.wrapper.operations;

import java.io.ByteArrayInputStream;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.cmis.client.wrapper.enums.Version;

public class UploadNewVersion {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(UploadDocument.class);
	private Session session;
	private volatile int threadCounter = 0;
	
	public UploadNewVersion(Session session){
		this.session = session;
	}
	
	public String upload(Folder folder, String fileName, byte[] content, Version version ) {
		
		ObjectId objectId = null;
		String filePath = folder.getPath() + "/" + fileName;
		Document document = (Document) session.getObjectByPath(filePath);
		
		if (document.getAllowableActions().getAllowableActions()
				.contains(org.apache.chemistry.opencmis.commons.enums.Action.CAN_CHECK_OUT)) {
			ObjectId idOfCheckedOutDocument = document.checkOut();
			Document pwc = (Document) session.getObject(idOfCheckedOutDocument);
			ByteArrayInputStream stream = new ByteArrayInputStream(content);
			ContentStream contentStream = session.getObjectFactory().createContentStream(document.getName(),
					Long.valueOf(content.length), document.getContentStreamMimeType(), stream);
			boolean isMajorVersion = version.name().equals(Version.MAJOR.name());
			objectId = pwc.checkIn(isMajorVersion, null, contentStream, version.name() + " changes");
			LOGGER.info("Document: "+filePath+" has been updated succesfully. New Object ID is: "+objectId.getId());
			
		}
		return objectId.getId();
	}
	
	public void increaseCounter(){
		threadCounter = threadCounter + 1 ;
	}
	
	public void decreaseCounter(){
		threadCounter = threadCounter - 1 ;
	}
	
	public int getCounter(){
		return threadCounter;
	}


}
