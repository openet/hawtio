package com.openet.ext.hawtio.registration;

import com.openet.appSelector.appRegistry.AppRegistryMBean;

public class AppRegistration implements AppRegistrationMBean {

	private static final String APP_ID = "hawtio";
	private AppRegistryMBean appRegistry;

	@Override
	public void start() throws Exception {
		System.out.println("Startig HAWTIO AppRegistration");
		
		appRegistry.registerApplication(APP_ID, "Interaction Gateway", "The web console for monitoring orchestration flows", "/hawtio", "/hawtio/img/ig-logo.png", null);

	}

	@Override
	public void stop() throws Exception {
		appRegistry.deregisterApplication(APP_ID);
	}

	@Override
	public void setAppRegistryService(AppRegistryMBean appRegService) {
		this.appRegistry = appRegService;
		
	}

}
