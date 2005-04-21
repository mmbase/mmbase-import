/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;

/**
 * Module , the wrapper for the modules.
 *
 * @author Rico Jansen
 * @author Rob Vermeulen (securitypart)
 *
 * @version 26 Nov 1996
 */
public abstract class Module {

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
			System.out.println("Module -> getInitParameters called before they where loaded");	
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


	public String getMimeType(String ext) {
		// org.mmbase return((String)mimetypes.get(ext));
		return("image/jpeg");
	}


	public static synchronized final Hashtable loadModulesFromDisk() {
		Class newclass;


		System.out.println("MMBASE -> mmbase.config="+System.getProperty("mmbase.config"));
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
			//System.out.println("MODULE="+key+" VAL="+value);

			// try starting the module and give it its properties
			try {
				newclass=Class.forName(value);
				//System.out.println("Module -> Loaded load class : "+newclass);
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
						System.out.println("Reading "+key+" mod file from : "+tmp);
						filename=tmp;
					}	
					Hashtable modprops = Reader.readProperties(filename);
					//System.out.println("MOD "+key+" "+modprops);
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
		for (Enumeration e=modules.elements();e.hasMoreElements();) {
			Module mod=(Module)e.nextElement();
			//System.out.println("ONLOAD THE MOD="+mod);
			try {
				mod.onload();		
			} catch (Exception f) {
				System.out.println("mod not found");
				f.printStackTrace();
			}
		}

		// so now really give em their init
		for (Enumeration e=modules.elements();e.hasMoreElements();) {
			Module mod=(Module)e.nextElement();
			if (debug) System.out.println("INIT THE MOD="+mod);
			try {
				mod.init();		
			} catch (Exception f) {
				System.out.println("mod not found");
				f.printStackTrace();
			}
		}
	}

	public static final Object getModule(String name) {

		// are the modules loaded yet ? if not load them
		if (modules==null) {
			//System.out.println("Modules still NULL calling to startup");
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
			System.out.println("Module-> no module loaded with name : "+name);
			return(null);
		}
	}	

}
