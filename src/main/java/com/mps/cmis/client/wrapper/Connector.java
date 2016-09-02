package com.mps.cmis.client.wrapper;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.cmis.client.wrapper.enums.Version;
import com.mps.cmis.client.wrapper.exception.CMISException;
import com.mps.cmis.client.wrapper.operations.DownloadDocument;
import com.mps.cmis.client.wrapper.operations.UploadDocument;
import com.mps.cmis.client.wrapper.session.CMISSession;

public class Connector {
	private final static Logger LOGGER = LoggerFactory.getLogger(Connector.class);
	private static CMISSession cmisSession;

	
	private Connector(){}
	
	public static  CMISUploadResponse uploadDocument(CMISUploadRequest cmisUploadRequest) {
		
		String folderPath=null;
		String fileName=null;
		Version version=null;
		CMISUploadResponse cmisUploadResponse = null;
		
		if(cmisSession == null){
			try {
				cmisSession = CMISSession.getInstance();
			} catch (Exception ex) {
				LOGGER.error("Cann't connect to CMIS repository, because of following exception: ", ex);
				cmisUploadResponse = new CMISUploadResponse();
				CMISException connectionException = new CMISException("Cann't connect to CMIS repository, because of following exception: ", ex);
				cmisUploadResponse.setCmisException(connectionException);
				return cmisUploadResponse;
			}
		}
		
		try{
		 folderPath = cmisUploadRequest.getFolderpath();
		 fileName = cmisUploadRequest.getFileName();
		 version = cmisUploadRequest.getVersion();
		}
		catch(NullPointerException exception)
		{
			LOGGER.error("The request cannot be  processed as cmisUploadRequest object is null :", exception);
			cmisUploadResponse = new CMISUploadResponse();
			CMISException contentUploadRequestException=new CMISException("The request cannot be  processed as cmisUploadRequest object is null :", exception);
			cmisUploadResponse.setCmisException(contentUploadRequestException);
			return cmisUploadResponse;
		}
		try {
			UploadDocument uploadDocument = UploadDocument.getInstance(cmisSession);
			cmisUploadResponse  = uploadDocument.uploadDoc(folderPath, fileName, cmisUploadRequest.getContent(), version);
		} catch (Exception ex) {
			LOGGER.error("Error in uploading file: "+fileName+" at path: "+folderPath+" with version: "+version , ex);
			cmisUploadResponse = new CMISUploadResponse();
			CMISException contentUploadException=new CMISException("Error in uploading file: "+fileName+" at path: "+folderPath+" with version: "+version, ex);
			cmisUploadResponse.setCmisException(contentUploadException);
			return cmisUploadResponse;
		}
		return cmisUploadResponse;
	}
	
	public static  CMISDownloadResponse downloadDocument(CMISDownloadRequest cmisDownloadRequest) {
		
		String folderPath=null;
		String fileName=null;
		String version=null;
		CMISDownloadResponse cmisDownloadResponse = null;
		
		if(cmisSession == null){
			try {
				cmisSession = CMISSession.getInstance();
			} catch (Exception ex) {
				LOGGER.error("Cann't connect to CMIS repository, because of following exception: ", ex);
				cmisDownloadResponse = new CMISDownloadResponse();
				CMISException connectionException=new CMISException("Cann't connect to CMIS repository, because of following exception: ",ex);
				cmisDownloadResponse.setCmisException(connectionException);
				return cmisDownloadResponse;
			}
		}
		
		try{
		 folderPath = cmisDownloadRequest.getFolderPath();
		 fileName = cmisDownloadRequest.getFileName();
		 version = cmisDownloadRequest.getVersion();
		}
		catch(NullPointerException exception)
		{
			LOGGER.error("The request cannot be  processed as cmisDownloadRequest object is null :",exception);
			cmisDownloadResponse = new CMISDownloadResponse();
			CMISException contentUploadRequestException=new CMISException("The request cannot be  processed as cmisDownloadRequest object is null :", exception);
			cmisDownloadResponse.setCmisException(contentUploadRequestException);
			return cmisDownloadResponse;
		}
		
		
		try {
			DownloadDocument downloadDocument = DownloadDocument.getInstance(cmisSession);
			cmisDownloadResponse = downloadDocument.downloadDoc(folderPath, fileName, version);
		} catch (Exception ex) {
			LOGGER.error("Error in downloading the file: "+fileName+" from path: "+folderPath+" with version: "+version, ex);
			cmisDownloadResponse = new CMISDownloadResponse();
			CMISException contentDownloadException=new CMISException("Error in downloading the file: "+fileName+" from path: "+folderPath+" with version: "+version, ex);
			cmisDownloadResponse.setCmisException(contentDownloadException);
			return cmisDownloadResponse;
		}
		return cmisDownloadResponse;
	}

}
