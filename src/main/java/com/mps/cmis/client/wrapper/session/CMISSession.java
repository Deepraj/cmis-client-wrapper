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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CMISSession {
	static Logger LOGGER = LoggerFactory.getLogger(CMISSession.class);
	private static CMISSession cmisSessionSingletonInstance;

	public static CMISSession getInstance() throws Exception{
		
		if(cmisSessionSingletonInstance == null){
	        synchronized (CMISSession.class) {
	            if(cmisSessionSingletonInstance == null){
	            	cmisSessionSingletonInstance = new CMISSession();
	            }
	        }
	    }
	    return cmisSessionSingletonInstance;		
	}
	
	private Session session;
	
	private CMISSession() throws Exception{
		
		session = createSession();
	}
	
	private Session createSession() throws Exception {

		Map<String, String> parameter = new HashMap<String, String>();
		LOGGER.info("Creating the session for CMIS Repository");
		LOGGER.info("********Create Session***********");
		
		Properties cmisProperies = loadResources("cmis-resources.properties");
		parameter.put(SessionParameter.USER, cmisProperies.getProperty("cmis.session.user"));
		parameter.put(SessionParameter.PASSWORD,cmisProperies.getProperty("cmis.session.password"));
		parameter.put(SessionParameter.BROWSER_URL,cmisProperies.getProperty("cmis.session.browser.url"));
		parameter.put(SessionParameter.BINDING_TYPE,  cmisProperies.getProperty("cmis.session.binding.type"));
		parameter.put(SessionParameter.REPOSITORY_ID,cmisProperies.getProperty("cmis.session.repository.id"));
		parameter.put(SessionParameter.CONNECT_TIMEOUT,cmisProperies.getProperty("cmis.session.connect.timeout"));
		parameter.put(SessionParameter.READ_TIMEOUT, cmisProperies.getProperty("cmis.session.read.timeout"));

		try {
			SessionFactory factory = SessionFactoryImpl.newInstance();
			session = factory.createSession(parameter);
			LOGGER.info("******* CMIS Session created successfully *******");
			session.getDefaultContext().setCacheEnabled(false);
		} catch (Exception ex) {
			LOGGER.error("*****Error in creating CMIS session *****: ",ex);
			session = null;
			throw new Exception(ex);
		}
		return session;
	}
	
	public Session retrieveSession() throws Exception {
		
		if(session == null){
			synchronized (cmisSessionSingletonInstance) {
				if(session == null){
					session = createSession();
				}				
			}
		}
		return session;	
	}
	
	
	private Properties loadResources(String propertyFile) throws Exception {
		
		LOGGER.info("Retrieving the connection properties from file: "+propertyFile);
		String fileAbsoultePath = getFileLocationFromClassPath(propertyFile);
		FileInputStream input = new FileInputStream(fileAbsoultePath);
		Properties properties = new Properties();
		properties.load(input);	
		LOGGER.info("Properties file: "+propertyFile+ " loaded successfully.");
		return properties;
	}

	private String getFileLocationFromClassPath(String propertyFileName)
			throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(propertyFileName).getFile());
		return file.getCanonicalPath();
	}

}
