package com.openet.ext.hawtio.registration;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.annotation.WebListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import com.openet.appSelector.appRegistry.AppRegistrySingleton;
import com.openet.appSelector.appRegistry.ApplicationDetails;
import com.openet.util.BeanLookup;

/**
 * <p>
 * 	This Servle tListener handles registration and deregistration of a web app
 * 	with the AppRegistrySingleton EJB. This is the mechanism used to get the web app
 * 	displayed in the application selector page that the user sees when they log in.
 * </p>
 * <p>
 * 	The web application's details can be set in two ways:
 * 	<ol>
 * 		<li>Set servlet's context parameters.</li>
 * 		<li>Create a subclass of this listener and override {@link #getApplicationDetails(ServletContextEvent)}.</li>
 * 	</ol>
 * </p>
 * <p>
 * 	<h3>Servlet context parameters</h3>
 * 	The following parameters can be set:
 * 	<table border=1>
 * 		<tr>
 * 			<th>Name</th>
 * 			<th>Description</th>
 * 			<th>Default Value</th>
 * 		</tr>
 * 		<tr>
 * 			<td>APP_REG_KEY</td>
 * 			<td>The key to be used to reference this web app. Must be unique across all registered apps.</td>
 * 			<td>Servlet's context path.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>APP_REG_NAME</td>
 * 			<td>The name to display for this web app. Visible to the end user.</td>
 * 			<td>Empty string.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>APP_REG_DESC</td>
 * 			<td>The description to display for this web app. Visible to the end user.</td>
 * 			<td>Empty string.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>APP_REG_ICON</td>
 * 			<td>The url for the icon to display for this web app. Visible to the end user.</td>
 * 			<td>Empty string.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>APP_REG_SEC_OBJ</td>
 * 			<td>The name of the security object associated with this web app.</td>
 * 			<td>Empty string.</td>
 * 		</tr>
 * 	</table>
 * The servlet parameters can be set in the web.xml. For example:
<pre>
...
&lt;context-param&gt;
	&lt;param-name&gt;APP_REG_NAME&lt;/param-name&gt;
	&lt;param-value&gt;Fancy New Openet GUI&lt;/param-value&gt;
&lt;/context-param&gt;
...
&lt;listener&gt;        
	&lt;listener-class&gt;com.openet.cwg.servlet.listener.AppRegistryServletListener&lt;/listener-class&gt;
&lt;/listener&gt;
...
</pre>
 * </p>
 * @author gary.madden@openet.com
 *
 */
@WebListener
public class AppRegistryServletListener implements ServletContextListener {
	
	private static final Logger LOGGER = Logger.getLogger(AppRegistryServletListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//mytodo remove
		System.out.println("--------------------- ServletContextListener started -------------------------");	
		ApplicationDetails appDetails = getApplicationDetails(sce);		
		
		try {
			register(appDetails);
		} catch (Exception e) {
			LOGGER.error("Error registering " + appDetails.getKey(), e);
		}
	}
	
	private void register(ApplicationDetails appDetails) throws NotSupportedException, SystemException, NamingException {		
		UserTransaction transaction = getUserTransaction();
		
		transaction.begin();
		
		try {
			getAppRegistry().registerApplication(appDetails);
		
			transaction.commit();
		} catch (Exception e) {
			LOGGER.error("Error registering " + appDetails.getKey(), e);
			
			transaction.rollback();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ApplicationDetails appDetails = getApplicationDetails(sce);
		
		try {
			deRegister(appDetails);
		} catch (Exception e) {
			LOGGER.error("Error de-registering " + appDetails.getKey(), e);
		}
	}
	
	private void deRegister(ApplicationDetails appDetails) throws NotSupportedException, SystemException, NamingException {		
		UserTransaction transaction = getUserTransaction();
		
		transaction.begin();
		
		try {
			getAppRegistry().deregisterApplication(appDetails.getKey());
		
			transaction.commit();
		} catch (Exception e) {
			LOGGER.error("Error de-registering " + appDetails.getKey(), e);
			
			transaction.rollback();
		}
	}
	
	private AppRegistrySingleton getAppRegistry() {
		return BeanLookup.getBean(AppRegistrySingleton.NAME, AppRegistrySingleton.class);
	}
	
	private UserTransaction getUserTransaction() throws NamingException {
		InitialContext context = new InitialContext();
		return (UserTransaction) context.lookup("java:comp/UserTransaction");
	}
	
	/**
	 * See the {@link #AppRegistryServletListener()} for the names of the ServletContext parameters.
	 * @param sce The {@link javax.servlet.ServletContext} used to retrieve the parameters.
	 * @return A new ApplicationDetails containing values read from the {@link javax.servlet.ServletContext}'s parameters.
	 */
	protected ApplicationDetails getApplicationDetails(ServletContextEvent sce) {
		String key = sce.getServletContext().getInitParameter("APP_REG_KEY");
		String name = getNonNullInitParam(sce, "APP_REG_NAME");
		String description = getNonNullInitParam(sce, "APP_REG_DESC");
//		String context = sce.getServletContext().getContextPath();
		String context = "hawtio";
//		String context = getNonNullInitParam(sce, "APP_CTXT_PATH");
		//mytodo remove
		System.out.println("------------------------- context:" + context);
		String iconUrl = getNonNullInitParam(sce, "APP_REG_ICON");
		String securityObjectName = getNonNullInitParam(sce, "APP_REG_SEC_OBJ");
		
		key = key == null ? context : key;
		
		return new ApplicationDetails(key, name, description, context, iconUrl, securityObjectName);
	}
	
	private String getNonNullInitParam(ServletContextEvent sce, String name) {
		String param = sce.getServletContext().getInitParameter(name);
		return param == null ? "" : param;
	}
}
