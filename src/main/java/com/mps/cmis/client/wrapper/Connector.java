package com.mps.cmis.client.wrapper;

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
				cmisUploadResponse.setErrorMessage("Cann't connect to CMIS repositiry, because of following exception: "+ ex);
				return cmisUploadResponse;
			}
		}		
		try {
			UploadDocument uploadDocument = new UploadDocument(cmisSession);
			cmisUploadResponse  = uploadDocument.uploadDoc(cmisRequest.getFolderpath(), cmisRequest.getFileName(), cmisRequest.getContent(), cmisRequest.getVersion());
		} catch (Exception ex) {
			cmisUploadResponse = new CMISUploadResponse();
			cmisUploadResponse.setErrorMessage("Cann't upload the document because of following exception: "+ ex);
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
				cmisDownloadResponse.setErrorMessage("Cann't connect to CMIS repositiry, because of following exception: "+ ex);
				return cmisDownloadResponse;
			}
		}
		try {
			DownloadDocument downloadDocument = new DownloadDocument(cmisSession);
			cmisDownloadResponse = downloadDocument.downloadDoc(cmisDownloadRequest.getFolderPath(), cmisDownloadRequest.getFileName(), cmisDownloadRequest.getVersion());
		} catch (Exception ex) {
			cmisDownloadResponse = new CMISDownloadResponse();
			cmisDownloadResponse.setErrorMessage("Cann't download the document, because of following exception: "+ ex);
			return cmisDownloadResponse;
		}
		return cmisDownloadResponse;
	}

}
