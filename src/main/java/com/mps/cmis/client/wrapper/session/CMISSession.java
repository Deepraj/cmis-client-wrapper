package com.mps.cmis.client.wrapper.session;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;

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
		Properties cmisProperies=loadResources("cmisResources.properties");
		parameter.put(SessionParameter.USER, cmisProperies.getProperty("cmis.session.User"));
		parameter.put(SessionParameter.PASSWORD,cmisProperies.getProperty("cmis.session.Password"));
		parameter.put(SessionParameter.BROWSER_URL,cmisProperies.getProperty("cmis.session.Browser_Url"));
		parameter.put(SessionParameter.BINDING_TYPE,  cmisProperies.getProperty("cmis.session.Binding_Type"));
		parameter.put(SessionParameter.REPOSITORY_ID,cmisProperies.getProperty("cmis.session.Repository_ID"));

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
	
	
	public Properties loadResources(String propertyFile) throws Exception {

		String fileAbsoultePath = getFileLocationFromClassPath(propertyFile);
		FileInputStream input = new FileInputStream(fileAbsoultePath);
		Properties properties = new Properties();
		properties.load(input);
		return properties;
	}

	public String getFileLocationFromClassPath(String propertyFileName)
			throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(propertyFileName).getFile());
		return file.getCanonicalPath();
	}

}
