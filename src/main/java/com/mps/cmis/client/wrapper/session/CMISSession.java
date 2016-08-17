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
import org.apache.log4j.Logger;

public class CMISSession {
	static Logger LOGGER = Logger.getLogger(CMISSession.class);
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
		LOGGER.info("Creating the session for Cmis Repository");
		session = createSession();
	}
	
	private Session createSession() throws Exception {

		Map<String, String> parameter = new HashMap<String, String>();
		LOGGER.info("********Create Session***********");
		Properties cmisProperies=loadResources("cmisResources.properties");
		parameter.put(SessionParameter.USER, cmisProperies.getProperty("cmis.session.User"));
		parameter.put(SessionParameter.PASSWORD,cmisProperies.getProperty("cmis.session.Password"));
		parameter.put(SessionParameter.BROWSER_URL,cmisProperies.getProperty("cmis.session.Browser_Url"));
		parameter.put(SessionParameter.BINDING_TYPE,  cmisProperies.getProperty("cmis.session.Binding_Type"));
		parameter.put(SessionParameter.REPOSITORY_ID,cmisProperies.getProperty("cmis.session.Repository_ID"));

		try {
			SessionFactory factory = SessionFactoryImpl.newInstance();
			session = factory.createSession(parameter);
			LOGGER.info("*******Session successfully created*******");
			session.getDefaultContext().setCacheEnabled(false);
		} catch (Exception ex) {
			LOGGER.error("-----Error in creating session-----:"+ex);
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
		LOGGER.info("Retrieving the properties from file:"+propertyFile);
		return properties;
	}

	public String getFileLocationFromClassPath(String propertyFileName)
			throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(propertyFileName).getFile());
		return file.getCanonicalPath();
	}

}
