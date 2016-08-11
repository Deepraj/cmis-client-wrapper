package com.mps.cmis.client.wrapper.operations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;

import com.mps.cmis.client.wrapper.enums.Version;

public class UploadDocument {

	private Session session;

	public UploadDocument(Session session) {
		this.session = session;

	}

	public String uploadDoc(String folderpath, String fileName, byte[] content, Version version) {
		String latestObjectID = null;

		String fileAbsolutePath = folderpath + "/" + fileName; //put check if folder ends with slash or not, file name should end with some extension
		Folder documentParentFolder = getDocumentParentFolder(folderpath);
		if (isCMISObjectExist(fileAbsolutePath)) {
			latestObjectID = uploadNewVersion(documentParentFolder, fileName, content, version);
			System.out.println("Document has been updated having name:" + fileName);
		} else {
			latestObjectID = createDocument(documentParentFolder, fileName, content);
			System.out.println("New document has been created having name:" + fileName);
		}
		return latestObjectID;
	}

	private Folder getDocumentParentFolder(String folderpath) {
		Folder availableFolder = checkDirectoryExistence(folderpath);
		String availablePath = availableFolder.getPath();

		if (!folderpath.equals(availablePath)) {
			String remainingPath = folderpath.substring(availablePath.length()+1);
			String[] folders = remainingPath.split("/");
			for (String folderName : folders) {
				Folder folder = getFolder(availableFolder.getId(), folderName);
				availableFolder = folder;
			}
		}
		return availableFolder;
	}

	private Folder checkDirectoryExistence(String folderpath) {
		try {
			Folder folder = (Folder) session.getObjectByPath(folderpath);
			return folder;
		} catch (CmisObjectNotFoundException onfe) {
			Folder subFolder = null;
			int endIndex = folderpath.lastIndexOf("/");
			if (endIndex != -1) {
				String newfolderpath = folderpath.substring(0, endIndex);
				subFolder = getDocumentParentFolder(newfolderpath);
			}
			return subFolder;
		}
	}

	private Folder getFolder(String parentFolderId, String folderName) {
		Session cmisSession = session;
		Folder folder = (Folder) cmisSession.getObject(parentFolderId);

		Folder subFolder = null;
		try {
			subFolder = (Folder) cmisSession.getObjectByPath(folder.getPath() + "/" + folderName);
			System.out.println("Folder already existed!");
		} catch (CmisObjectNotFoundException onfe) {
			Map<String, Object> props = new HashMap<String, Object>();
			props.put("cmis:objectTypeId", "cmis:folder");
			props.put("cmis:name", folderName);
			subFolder = folder.createFolder(props);
			String subFolderId = subFolder.getId();
			System.out.println("Created new folder: " + subFolderId);
		}

		return subFolder;
	}
	
	boolean isCMISObjectExist(String path) {
		
		try {
			session.getObjectByPath(path);
			return true;
		} catch (CmisObjectNotFoundException onfe) {
			return false;
		}
	}

	private String createDocument(Folder folder, String fileName, byte[] content) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		properties.put(PropertyIds.NAME, fileName);
		InputStream stream = new ByteArrayInputStream(content);
		ContentStream contentStream = new ContentStreamImpl(fileName, BigInteger.valueOf(content.length), "text/plain",
				stream);
		Document newDoc = null;
		try {
			newDoc = folder.createDocument(properties, contentStream, VersioningState.MAJOR);
			System.out.println("Created new document: " + newDoc.getId());
		} catch (CmisContentAlreadyExistsException ccaee) {
			newDoc = (Document) this.session.getObjectByPath(folder.getPath() + "/" + fileName);
			System.out.println("Document already exists: " + fileName);
		}
		return newDoc.getId();

	}
	
	public String uploadNewVersion(Folder folder, String fileName, byte[] content, Version version) {
		
		String objectId = null;
		String filePath = folder.getPath() + "/" + fileName;
		Document document = (Document) session.getObjectByPath(filePath);
		objectId = upload(document, content, version);
		return objectId;
	}
	
	public String uploadNewVersion(String objectID, byte[] content, Version version) {
		
		String latestObjectID = null;
		try {
			Document document = (Document) session.getObject(objectID);
			latestObjectID = upload(document, content, version).toString();
		} catch (CmisObjectNotFoundException onfe) {
			System.out.println("No document found" + onfe);	
		}
		return latestObjectID;

	}

	private String upload(Document document, byte[] content, Version version ) {
		
		ObjectId objectId = null;
		String newobjectId=null;
		
		if (document.getAllowableActions().getAllowableActions()
				.contains(org.apache.chemistry.opencmis.commons.enums.Action.CAN_CHECK_OUT)) {
			ObjectId idOfCheckedOutDocument = document.checkOut();
			Document pwc = (Document) session.getObject(idOfCheckedOutDocument);
			ByteArrayInputStream stream = new ByteArrayInputStream(content);
			ContentStream contentStream = session.getObjectFactory().createContentStream(document.getName(),
					Long.valueOf(content.length), "text/plain", stream);
			boolean isMajorVersion = version.name().equals(Version.MAJOR.name());
			objectId = pwc.checkIn(isMajorVersion, null, contentStream, version.name() + " changes");
		    newobjectId=splitObjectID(objectId.toString());
			
		}
		return newobjectId.toString();
	}
	private String splitObjectID (String objectId)
	{
		String[] newObjectId=objectId.split(":");
		return newObjectId[1];
	}
	
/*	public String uploadDocumentFromFileSystem() throws IOException
	{
		 // creating a document 
		  String filePath =  "C:/Documents and Settings/a.kushik/Desktop/Test.txt"; 
		  File file = new File(filePath); 
		  String content = readFile(filePath,StandardCharsets.UTF_8);
		  System.out.println(content);
		  String filename =file.getName(); 
		  Folder folder = (Folder)session.getObjectByPath("/Sites/"); 
		  byte[] buf =content.getBytes("UTF-8"); 
		 createDocument(folder, filename, buf);
		 return null;
	}
	
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}*/
	
}