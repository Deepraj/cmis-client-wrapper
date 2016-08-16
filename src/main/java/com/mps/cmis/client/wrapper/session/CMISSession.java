package com.mps.cmis.client.wrapper.session;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

public class CMISSession {
	
	private static CMISSession singletonInstance;

	public static CMISSession getInstance() throws Exception{
		
		if(singletonInstance == null){
	        synchronized (CMISSession.class) {
	            if(singletonInstance == null){
	            	singletonInstance = new CMISSession();
	            }
	        }
	    }
	    return singletonInstance;		
	}
	
	private Session session;
	
	private CMISSession() throws Exception{
		session = createSession();
	}
	
	private Session createSession() throws Exception {

		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put(SessionParameter.USER, "admin");
		parameter.put(SessionParameter.PASSWORD, "qwer1234$");
		parameter.put(SessionParameter.BROWSER_URL,
				"http://192.168.232.31:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser");
		parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
		parameter.put(SessionParameter.REPOSITORY_ID, "-default-");

		try {
			SessionFactory factory = SessionFactoryImpl.newInstance();
			session = factory.createSession(parameter);
			session.getDefaultContext().setCacheEnabled(false);
		} catch (Exception ex) {
			session = null;
			throw new Exception(ex);
		}
		return session;
	}
	
	public Session retrieveSession() throws Exception {
		
		if(session == null){
			synchronized (singletonInstance) {
				if(session == null){
					session = createSession();
				}				
			}
		}
		return session;	
	}
	
	

}
