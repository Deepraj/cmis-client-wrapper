package com.mps.cmis.client.wrapper;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mps.cmis.client.wrapper.exception.CMISException;
import com.mps.cmis.client.wrapper.operations.DownloadDocument;
import com.mps.cmis.client.wrapper.operations.UploadDocument;
import com.mps.cmis.client.wrapper.session.CMISSession;

public class Connector {
	private final static Logger LOGGER = LoggerFactory.getLogger(Connector.class);
	private static CMISSession cmisSession;
	
	private Connector(){}
	
	public static  CMISUploadResponse uploadDocument(CMISUploadRequest cmisRequest) {
		
		CMISUploadResponse cmisUploadResponse = null;
		if(cmisSession == null){
			try {
				cmisSession = CMISSession.getInstance();
			} catch (Exception ex) {
				LOGGER.error("Connection to repository refused: " +ex);
				cmisUploadResponse = new CMISUploadResponse();
				CMISException connectionException = new CMISException("Cann't connect to CMIS repository, because of following exception: ",ex);
				cmisUploadResponse.setException(connectionException);
				return cmisUploadResponse;
			}
		}		
		try {
			UploadDocument uploadDocument = UploadDocument.getInstance(cmisSession);
			cmisUploadResponse  = uploadDocument.uploadDoc(cmisRequest.getFolderpath(), cmisRequest.getFileName(), cmisRequest.getContent(), cmisRequest.getVersion());
		} catch (Exception ex) {
			LOGGER.error("Error in uploading "+cmisRequest.getFileName()+"at path "+cmisRequest.getFolderpath()+"with version "+cmisRequest.getVersion() +ex);
			cmisUploadResponse = new CMISUploadResponse();
			CMISException contentUploadException=new CMISException("Error in uploading "+cmisRequest.getFileName()+"at path "+cmisRequest.getFolderpath()+"with version "+cmisRequest.getVersion(),ex);
			cmisUploadResponse.setException(contentUploadException);
			return cmisUploadResponse;
		}
		return cmisUploadResponse;
	}
	
	public static  CMISDownloadResponse downloadDocument(CMISDownloadRequest cmisDownloadRequest) {

		CMISDownloadResponse cmisDownloadResponse = null;
		if(cmisSession == null){
			try {
				cmisSession = CMISSession.getInstance();
			} catch (Exception ex) {
				LOGGER.error("Connection to repoitory is refused on downloading the document:" +ex);
				cmisDownloadResponse = new CMISDownloadResponse();
				CMISException connectionException=new CMISException("Cann't connect to CMIS repository, because of following exception:"+"",ex);
				cmisDownloadResponse.setException(connectionException);
				return cmisDownloadResponse;
			}
		}
		try {
			DownloadDocument downloadDocument = DownloadDocument.getInstance(cmisSession);
			cmisDownloadResponse = downloadDocument.downloadDoc(cmisDownloadRequest.getFolderPath(), cmisDownloadRequest.getFileName(), cmisDownloadRequest.getVersion());
		} catch (Exception ex) {
			LOGGER.error("Error in downloading the content, name: "+cmisDownloadRequest.getFileName()+" from path"+cmisDownloadRequest.getFolderPath()+"with version"+cmisDownloadRequest.getVersion()+ex);
			cmisDownloadResponse = new CMISDownloadResponse();
			CMISException contentDownloadException=new CMISException("Error in downloading the content, name: "+cmisDownloadRequest.getFileName()+" from path"+cmisDownloadRequest.getFolderPath()+"with version"+cmisDownloadRequest.getVersion(),ex);
			cmisDownloadResponse.setException(contentDownloadException);
			return cmisDownloadResponse;
		}
		return cmisDownloadResponse;
	}

}
