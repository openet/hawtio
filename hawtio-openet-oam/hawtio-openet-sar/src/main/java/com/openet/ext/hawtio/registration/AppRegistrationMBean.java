package com.openet.ext.hawtio.registration;

import com.openet.appSelector.appRegistry.AppRegistryMBean;

public interface AppRegistrationMBean {
	
	public void start() throws Exception;

	public void stop() throws Exception;
	
	public void setAppRegistryService(AppRegistryMBean appRegService);

}
