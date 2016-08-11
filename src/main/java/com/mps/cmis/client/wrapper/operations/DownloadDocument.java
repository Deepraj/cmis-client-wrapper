package com.mps.cmis.client.wrapper.operations;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;

public class DownloadDocument {

	private Session session;

	public DownloadDocument(Session session) {
		this.session = session;
	}

	public String downloadDoc(String folderPath, String fileName, String version) {
		String path = folderPath + "/"+ fileName;
		try {
			Document doc = (Document) session.getObjectByPath(path);
			String ObjectID = doc.getId();
			ObjectID=createObjectId(ObjectID, version);
			String document = download(ObjectID);
			return document;
		} catch (CmisObjectNotFoundException onfe) {
			System.out.println("Document does not exist:" + onfe);
			return null;
		}
	}
	
	public String createObjectId(String previousId, String version) {
		String newObjectID = null;
		String[] splittedObjectID = previousId.split(";");
		newObjectID = splittedObjectID[0] + ";" + version;
		return newObjectID;
	}

	public String download(String objectID){
	
		Document doc = (Document) session.getObject(objectID);

		System.out.println("*********************************");
		List<Property<?>> props = doc.getProperties();
		for (Property<?> p : props) {
			System.out.println(p.getDefinition().getDisplayName() + "=" + p.getValuesAsString());
		}
		System.out.println("*********************************");
		
		ContentStream contentStream = doc.getContentStream();
		String content = null;// returns null if the document has no content
		if (contentStream != null) {
			try {
				content = getContentAsString(contentStream);
			} catch (IOException e) {
			}
		} else {
			System.out.println("No content.");
		}
		return content;
	}	

	private String getContentAsString(ContentStream stream) throws IOException {
		StringBuilder sb = new StringBuilder();
		Reader reader = new InputStreamReader(stream.getStream(), "UTF-8");

		try {
			final char[] buffer = new char[4 * 1024];
			int b;
			while (true) {
				b = reader.read(buffer, 0, buffer.length);
				if (b > 0) {
					sb.append(buffer, 0, b);
				} else if (b == -1) {
					break;
				}
			}
		} finally {
			reader.close();
		}

		return sb.toString();
	}
	
}


