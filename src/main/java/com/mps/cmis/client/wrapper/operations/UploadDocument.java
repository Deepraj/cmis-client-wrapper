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
	private Map<String,UploadNewVersion> filePathMap = new HashMap<String,UploadNewVersion>();

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
		String fileAbsolutePath =  getFilePath(folderpath, fileName);
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
				Folder folder = getFolder(availableFolder, folderName);
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
			LOGGER.error("Following folderpath i.e " + folderpath + " does not exist. " , onfe);
			Folder subFolder = null;
			int endIndex = folderpath.lastIndexOf("/");
			if (endIndex != -1) {
				String newfolderpath = folderpath.substring(0, endIndex);
				subFolder = getDocumentParentFolder(newfolderpath);
			}
			return subFolder;
		}
	}

	private synchronized Folder getFolder(Folder parentFolder, String folderName) {

		Folder subFolder = null;
		try {
			subFolder = (Folder) session.getObjectByPath(parentFolder.getPath() + "/" + folderName);
		} catch (CmisObjectNotFoundException onfe) {
			Map<String, Object> props = new HashMap<String, Object>();
			props.put("cmis:objectTypeId", "cmis:folder");
			props.put("cmis:name", folderName);
			subFolder = parentFolder.createFolder(props);
			LOGGER.info(parentFolder.getPath() + "/" + folderName+ " Folder created successfully");
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
			LOGGER.info("New document: "+newDoc.getName()+" has been created at location: "+folder.getPath()+" having object ID: "+ newDoc.getId());
		} catch (CmisContentAlreadyExistsException ccaee) {
			LOGGER.error("Error in creating document: "+fileName+" at location: "+folder.getPath() ,ccaee);
			return uploadNewVersion(folder, fileName, content, version);
		}
		return newDoc.getId();

	}
	
	private String uploadNewVersion(Folder folder, String fileName, byte[] content, Version version) {
		
		UploadNewVersion upnv;
		String filePath = getFilePath(folder.getPath(), fileName);
		synchronized (this){			
			upnv = filePathMap.get(filePath);
			if (upnv == null) {
				upnv = new UploadNewVersion(session);
				filePathMap.put(filePath, upnv);
			}
			upnv.increaseCounter();
		}
		String objectId = null;
		synchronized (upnv) {			
			objectId = upnv.upload(folder, fileName, content, version);
			upnv.decreaseCounter();
			if(upnv.getCounter() == 0){
				synchronized (this) {
					if(upnv.getCounter() == 0){
						filePathMap.remove(filePath);
					}				
				}				
			}
		}		
		return objectId;
	}
	
	private String getFileMimeType(String fileName) throws IOException {
		String mimeType = null;
		Path path = Paths.get(fileName);
		mimeType = Files.probeContentType(path);
		return mimeType;
	}
	
	private String getFilePath(String folderpath, String fileName){
		
		if(!folderpath.endsWith("/")){
			folderpath = folderpath + "/";
		}		
		return folderpath + fileName;
		
	}
}