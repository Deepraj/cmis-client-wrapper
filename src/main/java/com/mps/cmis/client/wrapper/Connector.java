package com.mps.cmis.client.wrapper;

import com.mps.cmis.client.wrapper.exception.ContentException;
import com.mps.cmis.client.wrapper.operations.DownloadDocument;
import com.mps.cmis.client.wrapper.operations.UploadDocument;
import com.mps.cmis.client.wrapper.session.CMISSession;

public class Connector {
	
	private static CMISSession cmisSession;
	
	private Connector(){}
	
	public static CMISUploadResponse uploadDocument(CMISUploadRequest cmisRequest) {
		
		CMISUploadResponse cmisUploadResponse = null;
		if(cmisSession == null){
			try {
				cmisSession = CMISSession.getInstance();
			} catch (Exception ex) {
				cmisUploadResponse = new CMISUploadResponse();
				ContentException connectionException=new ContentException("Cann't connect to CMIS repository, because of following exception:"+"",ex);
				cmisUploadResponse.setException(connectionException);
				return cmisUploadResponse;
			}
		}		
		try {
			UploadDocument uploadDocument = new UploadDocument(cmisSession);
			cmisUploadResponse  = uploadDocument.uploadDoc(cmisRequest.getFolderpath(), cmisRequest.getFileName(), cmisRequest.getContent(), cmisRequest.getVersion());
		} catch (Exception ex) {
			cmisUploadResponse = new CMISUploadResponse();
			ContentException contentUploadException=new ContentException("Error in uploading the content to repository due to:"+"",ex);
			cmisUploadResponse.setException(contentUploadException);
			return cmisUploadResponse;
		}
		return cmisUploadResponse;
	}
	
	public static CMISDownloadResponse downloadDocumant(CMISDownloadRequest cmisDownloadRequest) {

		CMISDownloadResponse cmisDownloadResponse = null;
		if(cmisSession == null){
			try {
				cmisSession = CMISSession.getInstance();
			} catch (Exception ex) {
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
			cmisDownloadResponse = new CMISDownloadResponse();
			ContentException contentDownloadException=new ContentException("Cann't connect to CMIS repository, because of following exception:"+" ",ex);
			cmisDownloadResponse.setException(contentDownloadException);
			return cmisDownloadResponse;
		}
		return cmisDownloadResponse;
	}

}
