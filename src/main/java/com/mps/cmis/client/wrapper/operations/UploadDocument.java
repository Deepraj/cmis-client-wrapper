package com.mps.cmis.client.wrapper.operations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.cmis.client.wrapper.CMISUploadResponse;
import com.mps.cmis.client.wrapper.enums.Version;
import com.mps.cmis.client.wrapper.session.CMISSession;

public class UploadDocument {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(UploadDocument.class);
	private static UploadDocument uploadDocumentSingletonInstance;
	private Session session;

	public static UploadDocument getInstance(CMISSession cmisSession) throws Exception {

		if (uploadDocumentSingletonInstance == null) {
			synchronized (UploadDocument.class) {
				if (uploadDocumentSingletonInstance == null) {
					uploadDocumentSingletonInstance = new UploadDocument(cmisSession);
				}
			}
		}
		return uploadDocumentSingletonInstance;
	}

	private UploadDocument(CMISSession cmisSession) throws Exception {
		this.session = cmisSession.retrieveSession();
	}


	public CMISUploadResponse uploadDoc(String folderpath, String fileName, byte[] content, Version version) throws IOException {
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
			LOGGER.error("Following folderpath i.e" + folderpath + " does not exist: " +onfe);
			Folder subFolder = null;
			int endIndex = folderpath.lastIndexOf("/");
			if (endIndex != -1) {
				String newfolderpath = folderpath.substring(0, endIndex);
				subFolder = getDocumentParentFolder(newfolderpath);
			}
			return subFolder;
		}
	}

	private synchronized Folder getFolder(String parentFolderId, String folderName) {
		Session cmisSession = session;
		Folder folder = (Folder) cmisSession.getObject(parentFolderId);

		Folder subFolder = null;
		try {
			subFolder = (Folder) cmisSession.getObjectByPath(folder.getPath() + "/" + folderName);
		} catch (CmisObjectNotFoundException onfe) {
			LOGGER.error("Error in getting the required destination folder :" +onfe);
			Map<String, Object> props = new HashMap<String, Object>();
			props.put("cmis:objectTypeId", "cmis:folder");
			props.put("cmis:name", folderName);
			subFolder = folder.createFolder(props);
			LOGGER.info("Folder created successfully: ");
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

	private String createDocument(Folder folder, String fileName, byte[] content, Version version) throws IOException {
		
		String fileMimeType = getFileMimeType(fileName);
		
		Map<String, Object> properties = new HashMap<String, Object>();		
		properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		properties.put(PropertyIds.NAME, fileName);
		properties.put(PropertyIds.CONTENT_STREAM_MIME_TYPE,fileMimeType);
		
		InputStream stream = new ByteArrayInputStream(content);
		ContentStream contentStream = new ContentStreamImpl(fileName, BigInteger.valueOf(content.length), fileMimeType,
				stream);
		Document newDoc = null;
		try {
			VersioningState versioningState = version.equals(Version.MAJOR) ? VersioningState.MAJOR : VersioningState.MINOR ;
			newDoc = folder.createDocument(properties, contentStream, versioningState);
			LOGGER.info("New document has been created with name: "+newDoc.getName()+"with version"+newDoc.getVersionLabel()+"at location:"+folder+"having object ID"+ newDoc.getId());
		} catch (CmisContentAlreadyExistsException ccaee) {
			LOGGER.error("Error in creating document with name: "+newDoc.getName()+"with version"+newDoc.getVersionLabel()+"at location:"+folder +ccaee);
			return uploadNewVersion(folder, fileName, content, version);
		}
		return newDoc.getId();

	}
	
	private String uploadNewVersion(Folder folder, String fileName, byte[] content, Version version) {
		
		String filePath = folder.getPath() + "/" + fileName;
	
		synchronized (this) {
			String objectId = null;
			objectId = upload(folder, fileName, content, version);
			return objectId;
		}
	}
	
	private String upload(Folder folder, String fileName, byte[] content, Version version ) {
		
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
			LOGGER.info("Document has been updated with name:"+fileName+"at location"+folder +"New Object ID is:"+objectId.getId());
			
		}
		return objectId.getId();
	}
	

	public String getFileMimeType(String fileName) throws IOException {
		String mimeType = null;
		Path path = Paths.get(fileName);
		mimeType = Files.probeContentType(path);
		return mimeType;
	}
}