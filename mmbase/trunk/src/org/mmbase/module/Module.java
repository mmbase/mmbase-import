/* 

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;

/**
 * Module , the wrapper for the modules.
 *
 * @author Rico Jansen
 * @author Rob Vermeulen (securitypart)
 *
 * @version $Revision: 1.7 $ $Date: 2000-04-11 14:33:13 $
 */
public abstract class Module {

	private static String   classname   = "org.mmbase.module.Module"; // getClass().getName();
	private static void     debug( String msg ) { System.out.println( classname +":"+ msg ); }

	private Object SecurityObj;
	private String moduleName=null;
	protected final Hashtable state=new Hashtable();
	// org.mmbase private UsersInterface users;
	private Hashtable mimetypes;

	private static Hashtable modules;
	private static String mmbaseconfig;
	
	private Hashtable properties;
	private static ModuleProbe mprobe;
	private static boolean debug=false;

		
	/**
 	 * variable to synchronize SecurityObj
 	 */
	private Object synobj=new Object();

	public Module() {
		String StartedAt=(new Date(DateSupport.currentTimeMillis())).toString();
		//String StartedAt=(new Date()).toString();
		state.put("Start Time",StartedAt);
		// org.mmbase mimetypes=Environment.getProperties(this,"mimetypes");
	}
	
	public final void setName(String name) {
		if (moduleName==null) {
			moduleName=name;
		}
	}

	/**
	 * Inits the module (startup final step 2).
	 * This is called second on startup, the module is expected
	 * to read the environment variables it needs. Startup threads,
	 * open connections etc.
	 */
	public abstract void init();

	public abstract void onload();

	/**
	 * state, returns the state hashtable that is/can be used to debug. Should
	 * be overridden when live state should be done.
	 */
	 public Hashtable state() {
		return(state);
	 }

	/**
	 * Gets his init-parameters
 	 */
    protected String getInitParameter(String key) {
		if (properties!=null) {
			String value=(String)properties.get(key);
			return(value);
		} else {
			debug("getInitParameters("+key+"): No properties found, called before they where loaded");
		}
		return(null);
	}


	/**
	 * Returns the properties to the subclass. 
	 */
   	protected Hashtable getProperties(String propertytable) {
		//String filename="/usr/local/vpro/james/adminopen/modules/";
		//return(results);		
		return(null);
		//return(Environment.getProperties(this,propertytable));
	}
	
	/**
	 * Returns one propertyvalue to the subclass.
	 */
	/* daniel, org.mmbase needs fix
	*/
	protected String getProperty(String name, String var) {
		//return(Environment.getProperty(this,name,var));
		return("");
	} 

	/* daniel, org.mmbase
   	protected Object removeProperty(String propertytable, String key) {
		return(Environment.removeProperty(this,propertytable,key));
	}
	*/
	
	/**
	 * Adds a property to the propertytabel 
	 */
	/* daniel, org.mmbase
	protected String putProperty(String propertytable, String key, String value) {
		return(String)(Environment.putProperty(this,propertytable,key,value));
	}
	*/
 
	/**
	 * Adds a property to the propertytabel 
	 */
	/* daniel, org.mmbase
	protected String putInitProperty(String key, String value) {
		return(String)(Environment.putProperty(this,"module/"+moduleName,key,value));
	} 
	*/

	/**
	 * Gets own modules properties
	 */
   	protected Hashtable getInitParameters() {
		return(null);
		//org.mmabse return(Environment.getProperties(this,"module/"+moduleName));
	}



	/**
 	 * Gets all the modules of the Environment that this worker may access. If allowed.
 	 */
	protected final Object getModules() {
		return(null);
	}


	/**
	 *	Get user Module property
	 */
	/* daniel, org.mmbase
	protected final String getUserModuleProperty(String userName,String name,int type) {
		if (users!=null && userName!=null) {
			return(users.getModuleProperty(moduleName,userName,name,type));
		} else {
			return(null);
		}
	}
	*/

	/**
	 *	Get user Module property
	 */
	/* daniel, org.mmbase
	protected final String getUserModuleProperty(String userName,String name) {
		return(getUserModuleProperty(userName,name,1));
	}
	*/

	/**
	 *	Get user Module property
	 */
	/* daniel, org.mmbase
	protected final boolean setUserModuleProperty(String userName,String name,String value, int type) {
		if (users!=null && userName!=null) {
			return(users.setModuleProperty(moduleName,userName,name,value,type));
		} else {
			return(false);
		}
	}
	*/

	/**
	 *	Get user Module property
	 */
	/* daniel, org.mmbase
	protected final boolean setUserModuleProperty(String userName,String name,String value) {
		return(setUserModuleProperty(userName,name,value,1));
	}
	*/
	
	/** 
	 * getName
 	 */
	/* daniel, org.mmbase
	protected final String getName(Object asker) {
		return Environment.getName(asker);
	}
	*/


	public final String getName() {
		return(null); // org.mmbase
	}

	/** 
	 * provide some info on the module
 	 */
	 public String getModuleInfo() {
		return("No module info provided");
	 }

	/**
	* maintainance call called by the admin module every x seconds.
	*/
	public void maintainance() {
	}

	/**
	* getMimeType: Returns the mimetype using ServletContext.getServletContext which returns the servlet context
	* which is set when servscan is loaded.
	* Fixed on 22 December 1999 by daniel & davzev.
	* @param ext A String containing the extension.
	* @return The mimetype.
	*/
	public String getMimeType(String ext) {
		// org.mmbase return((String)mimetypes.get(ext));

		ServletContext sx=MMBaseContext.getServletContext();
		// Since sx.getMimeType wants a file.ext String, we call it as "dummy."+ext
		String mimetype=sx.getMimeType("dummy."+ext);
		// System.out.println("Module::getMimeType: The mimetype= "+mimetype);
		if (mimetype==null) {
			debug("getMimeType("+ext+"): WARNING: Can't find mimetype retval=null -> setting mimetype to default text/html");
			mimetype="text/html";
		}
		return(mimetype);
	}


	public static synchronized final Hashtable loadModulesFromDisk() {
		Class newclass;

		debug("loadModulesFromDisk(): mmbase.config="+System.getProperty("mmbase.config"));
		mmbaseconfig=System.getProperty("mmbase.config");
		MMBaseContext.setConfigPath(mmbaseconfig);

		// the container for the started modules
		Hashtable results=new Hashtable();
	
		// get us a propertie reader	
		ExtendedProperties Reader=new ExtendedProperties();

		// load the properties file of this server
		String filename=mmbaseconfig+"/modules.properties";
		filename=filename.replace('/',(System.getProperty("file.separator")).charAt(0));
		filename=filename.replace('\\',(System.getProperty("file.separator")).charAt(0));


		Hashtable mods = Reader.readProperties(filename);

		// oke try loading all these modules and start em up
		for (Enumeration e=mods.keys();e.hasMoreElements();) {
			String key=(String)e.nextElement();
			String value=(String)mods.get(key);
			if( debug ) debug("loadModulesFromDisk(): MODULE="+key+" VAL="+value);

			// try starting the module and give it its properties
			try {
				newclass=Class.forName(value);
				if( debug ) debug("loadModulesFromDisk(): Loaded load class : "+newclass);
				Object mod = newclass.newInstance();
				if (mod!=null) {
					results.put(key,mod);
	
					// try to load the properties that are defined for this module	
					filename=mmbaseconfig+"/modules/"+key+".properties";
					filename=filename.replace('/',(System.getProperty("file.separator")).charAt(0));
					filename=filename.replace('\\',(System.getProperty("file.separator")).charAt(0));

					// extra check to load propertie files from weird places (security reasons for example)
					String tmp=System.getProperty("mmbase.mod_"+key);
					if (tmp!=null) {
						debug("Reading "+key+" mod file from : "+tmp);
						filename=tmp;
					}	
					Hashtable modprops = Reader.readProperties(filename);
					//debug("loadModulesFromDisk(): MOD "+key+" "+modprops);
					if (modprops!=null) {
						((Module)mod).properties=modprops;	
					}
				}
			} catch(Exception f) {
				f.printStackTrace();
			}
		}
		return(results);
	}

	public static synchronized final void startModules() {
		// call the onload to get properties
		if( debug ) debug("startModules(): onloading modules("+modules+")");
		for (Enumeration e=modules.elements();e.hasMoreElements();) {
			Module mod=(Module)e.nextElement();
			if( debug ) debug("startModules(): modules.onload("+mod+")");
			try {
				mod.onload();		
			} catch (Exception f) {
				debug("startModules(): Warning: modules("+mod+") not found to 'onload'!");
				f.printStackTrace();
			}
		}

		// so now really give em their init
		debug("startModules(): init the modules("+modules+")");
		for (Enumeration e=modules.elements();e.hasMoreElements();) {
			Module mod=(Module)e.nextElement();
			if (debug) debug("startModules(): mod.init("+mod+")");
			try {
				mod.init();		
			} catch (Exception f) {
				debug("startModules(): module("+mod+") not found to 'init'!");
				f.printStackTrace();
			}
		}
	}

	public static final Object getModule(String name) {
		// are the modules loaded yet ? if not load them
		if (modules==null) {
			debug("getModule("+name+"): Modules not loaded, loading them..");
			modules=loadModulesFromDisk();
			startModules();
			// also start the maintaince thread that calls all modules every x seconds
			mprobe = new ModuleProbe(modules);
		}

		// try to obtain the ref to the wanted module
		Object obj=modules.get(name);	
		if (obj!=null) {
			return(obj);
		} else {
			// Ugly and should be removed ROB
 			if(!name.equals("PLAYLISTS")) {
				debug("getModule("+name+"): ERROR: No module loaded with this name!");
			}
			return(null);
		}
	}	

}
