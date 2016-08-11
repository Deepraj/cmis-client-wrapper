package com.mps.cmis.client.wrapper.session;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

public class CMISSession {
	
	public Session retrieveSession() {
		
		SessionFactory factory = SessionFactoryImpl.newInstance();
		
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put(SessionParameter.USER, "admin");
		parameter.put(SessionParameter.PASSWORD, "qwer1234$");
		parameter.put(SessionParameter.BROWSER_URL,"http://192.168.232.31:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser");
		parameter.put(SessionParameter.BINDING_TYPE,BindingType.BROWSER.value());
		parameter.put(SessionParameter.REPOSITORY_ID, "-default-");

		// create session
		Session session = factory.createSession(parameter);
		return session;
	}

}
