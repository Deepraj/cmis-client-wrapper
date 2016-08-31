package com.mps.cmis.client.wrapper.operations;

import java.io.ByteArrayInputStream;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;

import com.mps.cmis.client.wrapper.enums.Version;

public class UploadNewVersion {
	
	private Session session;
	
	public UploadNewVersion(Session session){
		this.session = session;
	}
	
	public String upload(Folder folder, String fileName, byte[] content, Version version ) {
		
		System.out.println("start uploading document: "+fileName+" for thread: "+ Thread.currentThread());
		
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
			//LOGGER.info("Document has been updated with name: " + fileName + " at location " + folder +" New Object ID is: "+objectId.getId());
			//System.out.println("Document has been updated with name: " + fileName + " at location " + folder +" New Object ID is: "+objectId.getId());
			
		}
		System.out.println("uploading document: "+fileName+" for thread: "+ Thread.currentThread()+" has been completed");
		System.out.println();
		return objectId.getId();
	}

}
