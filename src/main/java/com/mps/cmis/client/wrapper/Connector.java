package com.mps.cmis.client.wrapper;

import org.apache.log4j.Logger;

import com.mps.cmis.client.wrapper.exception.ContentException;
import com.mps.cmis.client.wrapper.operations.DownloadDocument;
import com.mps.cmis.client.wrapper.operations.UploadDocument;
import com.mps.cmis.client.wrapper.session.CMISSession;

public class Connector {
	static Logger LOGGER = Logger.getLogger(Connector.class);
	private static CMISSession cmisSession;
	
	private Connector(){}
	
	public static CMISUploadResponse uploadDocument(CMISUploadRequest cmisRequest) {
		
		CMISUploadResponse cmisUploadResponse = null;
		if(cmisSession == null){
			try {
				cmisSession = CMISSession.getInstance();
			} catch (Exception ex) {
				LOGGER.error("Connection to repository refused :" +ex);
				cmisUploadResponse = new CMISUploadResponse();
				ContentException connectionException=new ContentException("Cann't connect to CMIS repository, because of following exception:"+"",ex);
				LOGGER.error("Connection to repository is refused on Uploading a document:"+connectionException);
				cmisUploadResponse.setException(connectionException);
				return cmisUploadResponse;
			}
		}		
		try {
			UploadDocument uploadDocument = new UploadDocument(cmisSession);
			cmisUploadResponse  = uploadDocument.uploadDoc(cmisRequest.getFolderpath(), cmisRequest.getFileName(), cmisRequest.getContent(), cmisRequest.getVersion());
		} catch (Exception ex) {
			LOGGER.error("Error in uploading the content to repository :" +ex);
			cmisUploadResponse = new CMISUploadResponse();
			ContentException contentUploadException=new ContentException("Error in uploading the content to repository due to:"+"",ex);
			cmisUploadResponse.setException(contentUploadException);
			return cmisUploadResponse;
		}
		return cmisUploadResponse;
	}
	
	public static CMISDownloadResponse downloadDocument(CMISDownloadRequest cmisDownloadRequest) {

		CMISDownloadResponse cmisDownloadResponse = null;
		if(cmisSession == null){
			try {
				cmisSession = CMISSession.getInstance();
			} catch (Exception ex) {
				LOGGER.error("Connection to repoitory is refused on downloading the document:" +ex);
				cmisDownloadResponse = new CMISDownloadResponse();
				ContentException connectionException=new ContentException("Cann't connect to CMIS repository, because of following exception:"+"",ex);
				cmisDownloadResponse.setException(connectionException);
				return cmisDownloadResponse;
			}
		}
		try {
			DownloadDocument downloadDocument = new DownloadDocument(cmisSession);
			cmisDownloadResponse = downloadDocument.downloadDoc(cmisDownloadRequest.getFolderPath(), cmisDownloadRequest.getFileName(), cmisDownloadRequest.getVersion());
		} catch (Exception ex) {
			LOGGER.error("Error in downloading the content from repository:"+ex);
			cmisDownloadResponse = new CMISDownloadResponse();
			ContentException contentDownloadException=new ContentException("Error in downloading the content from repository beacause of following exception :"+" ",ex);
			cmisDownloadResponse.setException(contentDownloadException);
			return cmisDownloadResponse;
		}
		return cmisDownloadResponse;
	}

}
