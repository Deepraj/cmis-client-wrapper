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

import com.mps.cmis.client.wrapper.CMISUploadResponse;
import com.mps.cmis.client.wrapper.enums.Version;
import com.mps.cmis.client.wrapper.session.CMISSession;

public class UploadDocument {

	private Session session;

	public UploadDocument(CMISSession cmisSession) throws Exception {
		this.session = cmisSession.retrieveSession();
	}

	public CMISUploadResponse uploadDoc(String folderpath, String fileName, byte[] content, Version version) {
		String latestObjectID = null;

		String fileAbsolutePath = folderpath + "/" + fileName; //put check if folder ends with slash or not, file name should end with some extension
		Folder documentParentFolder = getDocumentParentFolder(folderpath);
		if (isCMISObjectExist(fileAbsolutePath)) {
			latestObjectID = uploadNewVersion(documentParentFolder, fileName, content, version);
		} else {
			latestObjectID = createDocument(documentParentFolder, fileName, content, version);
		}
		
		CMISUploadResponse cmisUploadResponse = new CMISUploadResponse();
		cmisUploadResponse.setSuccess(true);
		cmisUploadResponse.setObjectID(latestObjectID);
		
		return cmisUploadResponse;
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
	
	private boolean isCMISObjectExist(String path) {
		
		try {
			session.getObjectByPath(path);
			return true;
		} catch (CmisObjectNotFoundException onfe) {
			return false;
		}
	}

	private String createDocument(Folder folder, String fileName, byte[] content, Version version) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		properties.put(PropertyIds.NAME, fileName);
		InputStream stream = new ByteArrayInputStream(content);
		ContentStream contentStream = new ContentStreamImpl(fileName, BigInteger.valueOf(content.length), "text/plain",
				stream);
		Document newDoc = null;
		try {
			VersioningState versioningState = version.equals(Version.MAJOR) ? VersioningState.MAJOR : VersioningState.MINOR ;
			newDoc = folder.createDocument(properties, contentStream, versioningState);
		} catch (CmisContentAlreadyExistsException ccaee) {
			newDoc = (Document) this.session.getObjectByPath(folder.getPath() + "/" + fileName);
		}
		return newDoc.getId();

	}
	
	private String uploadNewVersion(Folder folder, String fileName, byte[] content, Version version) {
		
		String objectId = null;
		String filePath = folder.getPath() + "/" + fileName;
		Document document = (Document) session.getObjectByPath(filePath);
		objectId = upload(document, content, version);
		return objectId;
	}
	
	private String upload(Document document, byte[] content, Version version ) {
		
		ObjectId objectId = null;
		
		if (document.getAllowableActions().getAllowableActions()
				.contains(org.apache.chemistry.opencmis.commons.enums.Action.CAN_CHECK_OUT)) {
			ObjectId idOfCheckedOutDocument = document.checkOut();
			Document pwc = (Document) session.getObject(idOfCheckedOutDocument);
			ByteArrayInputStream stream = new ByteArrayInputStream(content);
			ContentStream contentStream = session.getObjectFactory().createContentStream(document.getName(),
					Long.valueOf(content.length), "text/plain", stream);
			boolean isMajorVersion = version.name().equals(Version.MAJOR.name());
			objectId = pwc.checkIn(isMajorVersion, null, contentStream, version.name() + " changes");
			
		}
		return objectId.getId();
	}	
}