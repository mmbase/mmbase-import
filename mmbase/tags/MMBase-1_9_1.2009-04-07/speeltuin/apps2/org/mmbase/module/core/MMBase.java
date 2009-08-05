/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.util.Date;
import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.util.*;
import org.mmbase.util.platform.*;
import org.mmbase.module.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.*;
import org.mmbase.module.database.support.*;

import org.mmbase.security.MMBaseCop;
import org.mmbase.application.ApplicationManager;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * The module which provides access to the MMBase database defined
 * by the provided name/setup.
 * It holds the overal object cloud made up of builders, objects and relations and
 * all the needed tools to use them.
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @author Rob Vermeulen
 * @author Johan Verelst
 */
public class MMBase extends ProcessorModule  {

	/**
	*  our securityManager (MMBaseCop)
	*/
	private MMBaseCop mmbaseCop = null;

	/**
	*  our application manager 
	*/
	// ROB added variable
	static private ApplicationManager applicationManager = null;

	/**
	*  Defines what 'channel' we are talking to when using multicast.
	*/
	public static String multicasthost=null;

	/**
	*  Determines on what port does this multicast talking between nodes take place.
	*  This can be set to any port but check if something else on
	*  your network is allready using multicast when you have problems.
	*/
	public static int multicastport=-1;

	/**
	*  Builds a MultiCast Thread to receive and send
	*  changes from other MMBase Servers.
	*/
	public MMBaseChangeInterface mmc;

	/**
	*  Base name for the database to be accessed using this instance of MMBase.
	*  Retrieved from the mmbaseroot module configuration file.
	*  If not specified the default is "def1"
	*  Should be made private and accessed instead using getBaseName()
	*/
	public String baseName="def1";

	/**
	* Reference to the TypeDef builder.
	* Should be made private and accessed instead using getTypeDef()
	*/
	public TypeDef TypeDef;
	/**
	* Reference to the RelDef builder.
	* Should be made private and accessed instead using getRelDef()
	*/
	public RelDef RelDef;
	/**
	* Reference to the OALias builder.
	* Should be made private and accessed instead using getOAlias()
	*/
	public OAlias OAlias;
	/**
	* Reference to the InsRel builder.
	* Should be made private and accessed instead using getInsRel()
	*/
	public InsRel InsRel;
	/**
	* Reference to the TypeRel builder.
	* Should be made private and accessed instead using getTypeRel()
	*/
	public TypeRel TypeRel;

	/**
	* The table that contains all loaded builders. Includes virtual builders such as "multirelations".
	* Should be made private and accessed using getMMObjects()
	*/
	public Hashtable mmobjs=new Hashtable();

    /**
    *  The (base)path to the builder configuration files
    */
	String builderpath = "";

	/** Unused
    */
	int delay;
	/** Unused
    */
	boolean	nodecachesdone=false;

    /**
    * A thread object that gets activated by MMbase.
    * It activates every X seconds and takes this signal to call all the
    * builders probeCalls, using the callback {@link #doProbeRun} method in MMBase.
    */
    MMBaseProbe probe;

    /**
    * A reference to the jdbc driver to use for the current database system.
    * JDBC drivers differ by sytsem (various database systems provide their own drivers).
    * The configuration for the jdbc class to use for your datanse system is set in the configuration files.
    */
    JDBCInterface jdbc;

    /**
     * MultiRelations virtual builder, used for performing multilevel searches.
     */
    MultiRelations MultiRelations;

    /**
	* The database to use. Retrieve using getDatabase();
	*/
    MMJdbc2NodeInterface database;

    /*
	* Name of the machine used in the mmbase cluster.
	* it is used for the mmservers objects. Make sure that this is different
	* for each node in your cluster. This is not the machines dns name
	* (as defined by host as name or ip number).
	*/
    String machineName="unknown";

    /*
	* The host or ip number of the machine this module is
	* running on. Its important that this name is set correctly because it is
	* used for communication between mmbase nodes and external devices
	*/
    String host="unknown";

    /*
	* Authorisation type. Access using getAuthType()
	*/
    String authtype="none";

    /*
	* Cookie domain (?). Access using getCookieDomain()
	*/
    String cookieDomain=null;

    /**
     * Base url for the location of the DTDs. obtained using getDTDBase()
     */
    private String dtdbase="http://www.mmbase.org";

    // debug routines
    private static Logger log = Logging.getLoggerInstance(MMBase.class.getName());

    /**
     * Reference to the cluster builder.
     * The cluster builder is a version of the multirelations builder
     * that is used by the MMCI.
     */
    private ClusterBuilder clusterBuilder;

    /**
     * Reference to the sendmail module. Accessible using getSendMail();
     */
    private SendMailInterface sendmail;

    /**
     * Currently used language. Access using getLanguage()
     */
    private String language="us";

    /**
     * MMbase 'up state. Access using getState()
     */
    private boolean mmbasestate=false;

    /**
    * Constructor to create the MMBase root module.
    */
    public MMBase() {
        log.debug("MMBase constructed");
        // first thing to do is load our security system....
        try {
            mmbaseCop = new
                MMBaseCop(MMBaseContext.getConfigPath() +
                          File.separator + "security" + File.separator + "security.xml");
        } catch(Exception e) {
            log.fatal("error loading the mmbase cop: " + e.toString());
            log.error(Logging.stackTrace(e));
            log.error("MMBase will continue without security.");
            log.error("All future security invocations will fail.");
        }
    }

    /**
     * Initializes the MMBase module. Evaluates the parameters loaded from the configuration file.
     * Sets parameters (authorisation, langauge), loads the builders, and starts MultiCasting.
     */
    public void init() {

        log.info("\n---\nInit of MMBase");
        // is there a basename defined in MMBASE.properties ?
        String tmp=getInitParameter("BASENAME");
        if (tmp!=null) {
            // yes then replace the default name (def1)
            baseName=tmp;
        } else {
            log.info("init(): No name defined for mmbase using default (def1)");
        }

        tmp=getInitParameter("AUTHTYPE");
        if (tmp!=null && !tmp.equals("")) {
            authtype=tmp;
        }

        tmp=getInitParameter("LANGUAGE");
        if (tmp!=null && !tmp.equals("")) {
            language=tmp;
        }

        tmp=getInitParameter("AUTH401URL");
        if (tmp!=null && !tmp.equals("")) {
            HttpAuth.setLocalCheckUrl(tmp);
        }
        tmp=getInitParameter("DTDBASE");
        if (tmp!=null && !tmp.equals("")) {
            dtdbase=tmp;
        }

        tmp=getInitParameter("HOST");
        if (tmp!=null && !tmp.equals("")) {
            host=tmp;
        }

        tmp=getInitParameter("MULTICASTPORT");
        if (tmp!=null && !tmp.equals("")) {
            try {
                multicastport=Integer.parseInt(tmp);
            } catch(Exception e) {}
        }

        tmp=getInitParameter("MULTICASTHOST");
        if (tmp!=null && !tmp.equals("")) {
            multicasthost=tmp;
        }

        tmp=getInitParameter("COOKIEDOMAIN");
        if (tmp!=null && !tmp.equals("")) {
            cookieDomain=tmp;
        }

        sendmail=(SendMailInterface)getModule("sendmail");
        machineName=getInitParameter("MACHINENAME");

        jdbc=(JDBCInterface)getModule("JDBC");

        if (multicasthost!=null) {
            mmc=new MMBaseMultiCast(this);
        } else {
            mmc=new MMBaseChangeDummy(this);
        }

        if (!checkMMBase()) {
            // there is no base defined yet, create the core objects
            createMMBase();
        }

        builderpath = getInitParameter("BUILDERFILE");
        if (builderpath==null || builderpath.equals("")) {
            builderpath=MMBaseContext.getConfigPath() + File.separator + "builders" + File.separator;
        }

        log.debug("Init builders:");
        initBuilders();

        log.debug("Objects started");

        log.debug("Starting MultiRelations Builder");
        MultiRelations = new MultiRelations(this);

        log.debug("Starting Cluster Builder");
        clusterBuilder = new ClusterBuilder(this);

		// weird place needs to rethink (daniel).
        Vwms bul=(Vwms)getMMObject("vwms");
        if (bul!=null) {
            bul.startVwms();
        }
        Vwmtasks bul2=(Vwmtasks)getMMObject("vwmtasks");
        if (bul2!=null) {
            bul2.start();
        }

        String writerpath=getInitParameter("XMLBUILDERWRITERDIR");
        if (writerpath!=null && !writerpath.equals("")) {
            Enumeration t = mmobjs.elements();
            while (t.hasMoreElements()) {
                MMObjectBuilder fbul=(MMObjectBuilder)t.nextElement();
                if (!fbul.isVirtual()) {
                    String name=fbul.getTableName();
                    log.debug("WRITING BUILDER FILE ="+writerpath+File.separator+name);
                    XMLBuilderWriter.writeXMLFile(writerpath+File.separator+fbul.getTableName()+".xml",fbul);
                }
            }
        }

	// Start the Application Manager
	applicationManager = new ApplicationManager(this);
	applicationManager.init();

        // signal that MMBase is up and running
        mmbasestate=true;
        log.info("MMBase is up and running");
        checkUserLevel();
    }

    /**
	* Started when the module is loaded.
	* Doesn't do anything (so why bother?)
	*/
	public void onload() {
	}

	/**
	* unused (?)
	*/
	public void unload() {
	}

	/**
	* unused (?)
	*/
	public void shutdown() {
	}

	/**
	* Checks whether the database to be used exists.
	* The system determines whether an object tables was craeted for the baseName provided in the configuration file.
	* @return <code>true</code> if the database exists and is accessible, <code>false</code> otherwise.
	*/
	boolean checkMMBase() {
		if (database==null) database=getDatabase();
		try {
			MultiConnection con=getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select count(*) from "+baseName+"_object");
            closeConnection(con,stmt);
			return true;
		} catch (SQLException e) {
			//e.printStackTrace();
			return false;
		}
	}

	/**
	* Create a new database.
	* The database created is based on the baseName provided in the configuration file.
	* @return <code>true</code> if the database was succesfully created, otherwise a runtime exception is thrown
	*   (shouldn't it return <code>false</code> instead?)
	*/
	boolean createMMBase() {
		log.debug(" creating new multimedia base : "+baseName);
		Vector v;
		database=getDatabase();
		database.createObjectTable(baseName);
		return true;
	}


    /**
    * Retrieves a specified builder
    * @param name The name of the builder to retrieve
    * @return a <code>MMObjectBuilder</code> if found, <code>null</code> otherwise
    */
    public MMObjectBuilder getMMObject(String name) {
        Object o=mmobjs.get(name);
        if (o == null) {
            log.trace("MMObject " + name + " could not be found"); // can happen...
        }
        return (MMObjectBuilder)o;
    }

    /**
    * Retrieves the loaded security manager(MMBaseCop).
    * @return the loaded security manager(MMBaseCop)
    */
	public MMBaseCop getMMBaseCop() {
		return mmbaseCop;
	}

	/**
	 * Retrieves the application manager
	 * @return the loaded application manager
	 */
	// ROB added method
	static public ApplicationManager getApplicationManager() {
		return applicationManager;
	}


    /**
    * Retrieves the loaded builders.
    * @return an <code>Enumeration</code> listing the loaded builders
    */
	public Enumeration getMMObjects() {
		return mmobjs.elements();
	}

	/**
	* Returns a reference to the InsRel builder.
	* @return the <code>InsRel</code> builder if defined, <code>null</code> otherwise
	*/
	public InsRel getInsRel() {
		return InsRel;
	}

	/**
	* Returns a reference to the RelDef builder.
	* @return the <code>RelDef</code> builder if defined, <code>null</code> otherwise
	*/
	public RelDef getRelDef() {
		return RelDef;
	}

	/**
	* Returns a reference to the TypeDef builder.
	* @return the <code>TypeDef</code> builder if defined, <code>null</code> otherwise
	*/
	public TypeDef getTypeDef() {
		return TypeDef;
	}

	/**
	* Returns a reference to the TypeRel builder.
	* @return the <code>TypeRel</code> builder if defined, <code>null</code> otherwise
	*/
	public TypeRel getTypeRel() {
		return TypeRel;
	}

	/**
	* Returns a reference to the OAlias builder.
	* @return the <code>OAlias</code> builder if defined, <code>null</code> otherwise
	*/
	public OAlias getOAlias() {
		return OAlias;
	}

    /**
     * Returns a reference to the cluster builder.
     * @return an instantiation of the <code>ClusterBuilder</code>
     */
    public ClusterBuilder getClusterBuilder() {
        return clusterBuilder;
    }

	/**
	 * Get a database connection that is multiplexed and checked.
	 * @return a <code>MultiConnection</code> if the connection succeeded, <code>null</code> if it failed.
	 */
	public MultiConnection getConnection() {
		try {
			//MultiConnection con=jdbc.getConnection(jdbc.makeUrl());
			//MultiConnection con=jdbc.getConnection("jdbc:HypersonicSQL:.","sa","");
			//MultiConnection con=jdbc.getConnection("jdbc:HypersonicSQL:mmbase","sa","");
			MultiConnection con=database.getConnection(jdbc);
			return con;
		} catch (SQLException e) {
			log.error("Can't get a JDBC connection (database error)"+e);
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			log.error("Can't get a JDBC connection (JDBC module error)"+e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	* Safely close a database connection and/or a database statement.
	* @param con The connection to close. Can be <code>null</code>.
	* @param stmt The statement to close, prior to closing the connection. Can be <code>null</code>.
	*/
	public void closeConnection(MultiConnection con, Statement stmt) {
            try {
                if (stmt!=null) stmt.close();
            } catch(Exception g) {}
            try {
                if (con!=null) con.close();
            } catch(Exception g) {}
	}

	/**
	 * Get a direct database connection.
	 * Should only be used if you want to do database specific things that use non-jdbc
	 * interface calls. Use very sparingly.
	 */
	public Connection getDirectConnection() {
		try {
			Connection con=jdbc.getDirectConnection(jdbc.makeUrl());
			return con;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			log.error("Can't get a JDBC connection (JDBC module error)"+e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	* Retrieves the database base name
	* @return the base name as a <code>String</code>
	*/
	public String getBaseName() {
		return baseName;
	}

	/**
	* Callback method, called from MMBaseProbe.
	* The probe is a seperate thread taht is created every 10 minutes by the module's {@link #maintainance} call.
	*/
	public void doProbeRun() {
		DayMarkers bul=(DayMarkers)getMMObject("daymarks");
		if (bul!=null) {
			bul.probe();
		} else {
			log.error("doProbeRun(): ERROR: Can't access builder : daymarks");
		}
	}

	/**
	* Performs periodic maintenance.
	* Starts a separate thread that probes the builders by calling {@link #doProbeRun}.
	* The reference to the thread is cleared when it dies (scehduled every 10 minutes), prompting
	* the system to start a new thread.
	* @see MMBaseProbe
	*/
	public void maintainance() {
		if (probe==null) probe=new MMBaseProbe(this);

		//LStreams.checkBroadcastState(LStreams.getNode(573949));
		if (2==1 && mmc!=null) {
			// debug for in/out multicast check
			// log.debug("maintenance(): in="+mmc.incount+" out="+mmc.outcount+" spawn="+mmc.spawncount);
		}
	}


	/**
	* Converts a vector containing nodes to a hashmap,
	* using a specified (unique) integer field as the hash key.
	* @param se The vector containing the nodes
	* @param mapper the name of the (integer) field that determines the hash key, i.e. "number"
	* @return the node list mapped to a <code>Hashtable</code>
	*/
	public Hashtable getSearchHash(Vector se,String mapper) {
		Hashtable results=new Hashtable();
		Enumeration t = se.elements();
		MMObjectNode node;
		while (t.hasMoreElements()) {
			node=(MMObjectNode)t.nextElement();
			results.put(new Integer(node.getIntValue(mapper)),node);
		}
		return results;
	}

	/**
	* Converts a vector containing nodes to a comma seperated list of values,
	* obtained from a specified integer field.
	* @param se The vector containing the nodes
	* @param mapper the name of the (integer) field whose value to include in the list
	* @return a parenthised, comma-seperated list of values, as a <code>String</code>
	*/
	public String getWhereList(Vector se,String mapper) {
		if (se==null) return null;
		StringBuffer inlist = new StringBuffer();
		inlist.append(" (");
		Enumeration t = se.elements();
		MMObjectNode node;
		while (t.hasMoreElements()) {
			node=(MMObjectNode)t.nextElement();
			inlist.append(node.getIntValue(mapper) + ",");
		}
		if (inlist.length() >= 1 ) inlist.setLength(inlist.length()-1);
		inlist.append( ") ");
		return (inlist.toString());
	}

	/*
	private String getFile(String file) {
        File scanfile;
        int filesize,len=0;
        byte[] buffer;
        FileInputStream scan;
        Date lastmod;
        String rtn=null;

        scanfile = new File(file);
        filesize = (int)scanfile.length();
        lastmod=new Date(scanfile.lastModified());
        buffer=new byte[filesize];
        try {
            scan = new FileInputStream(scanfile);
            len=scan.read(buffer,0,filesize);
            scan.close();
        } catch(FileNotFoundException e) {
			// oops we have a problem
        } catch(IOException e) {}
        if (len!=-1) {
            rtn=new String(buffer,0);
        }
        return rtn;
	}
	*/

    /**
    * Returns the contents of a file as a byte array.
    * Utility function, should probably not be in this module
    */
	private byte[] getFileBytes(String file) {
        File scanfile;
        int filesize,len=0;
        byte[] buffer;
        FileInputStream scan;
        Date lastmod;
        String rtn=null;

        scanfile = new File(file);
        filesize = (int)scanfile.length();
        lastmod=new Date(scanfile.lastModified());
        buffer=new byte[filesize];
        try {
            scan = new FileInputStream(scanfile);
            len=scan.read(buffer,0,filesize);
            scan.close();
        } catch(FileNotFoundException e) {
			// oops we have a problem
        } catch(IOException e) {}
        if (len!=-1) {
        	return buffer;
        }
        return null;
	}

	/**
	* Retrieves a reference to the sendmail module.
	* @return a <code>SendMailInterface</code> object if the module was loaded, <code>null</code> otherwise.
	*/
	public SendMailInterface getSendMail() {
		return sendmail;
	}

   /**
   * Retrieves the machine name.
   * This value is set using the configuration file.
   * @return the machine name as a <code>String</code>
   */
	public String getMachineName() {
		return machineName;
	}

   /**
   * Retrieves the host name or ip number
   * This value is set using the configuration file.
   * @return the host name as a <code>String</code>
   */
	public String getHost() {
		return host;
	}

    /**
    * Retrieves the cookiedomain (whatever that is)
    * This value is set using the configuration file.
    * @return the cookie domain as a <code>String</code>
    */
	public String getCookieDomain() {
		return cookieDomain;
	}

    /**
    * Adds a remote observer to a specified builder.
    * The observer is notified whenever an object of that builder is changed, added, or removed.
    * @return <code>true</code> if adding the observer succeeded, <code>false</code> otherwise.
    */
	public boolean addRemoteObserver(String type,MMBaseObserver obs) {
		MMObjectBuilder bul=getMMObject(type);
		if (bul!=null) {
			return bul.addRemoteObserver(obs);
		} else {
			log.error("addRemoteObserver(): ERROR: Can't find builder : "+type);
			return false;
		}
	}

    /**
    * Adds a local observer to a specified builder.
    * The observer is notified whenever an object of that builder is changed, added, or removed.
    * @return <code>true</code> if adding the observer succeeded, <code>false</code> otherwise.
    */
	public boolean addLocalObserver(String type,MMBaseObserver obs) {
		MMObjectBuilder bul=getMMObject(type);
		if (bul!=null) {
			return bul.addLocalObserver(obs);
		} else {
			log.error("addLocalObserver(): ERROR: Can't find builder : "+type);
			return false;
		}
	}

	/**
	* Returns the number of marked days from a specified daycount (?)
	* @deprecated SCAN related, should not be in this module.
	*/
	public String doGetAgeMarker(StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String age=tok.nextToken();
			try {
				int agenr=Integer.parseInt(age);
				int agecount=((DayMarkers)getMMObject("daymarks")).getDayCountAge(agenr);
				return ""+agecount;
			} catch (Exception e) {
				log.debug(" Not a valid AGE");
				return "No valid age given";
			}
		} else {
			return "No age given";
		}
	}

	/**
	* Retrieves an unique key to use for a new node's number.
	* Calls the database to request the key. <code>Sychronized</code> so the same number cannot be dealt out to different nodes.
	* Does possibly not work well with multiple mmbase systems that work on the same database.
	* @return the new unique key as an <code>int</code> value
	*/
	public synchronized int getDBKey() {
		return database.getDBKey();
	}

    /**
    * Removes functions from fieldnames.
    * private, never called. should be removed.
    */
	private Vector removeFunctions(Vector fields) {
		Vector results=new Vector();
		Enumeration f=fields.elements();
		for (;f.hasMoreElements();) {
			// hack hack this is way silly Strip needs to be fixed
			String fieldname=Strip.DoubleQuote((String)f.nextElement(),Strip.BOTH);
			int pos1=fieldname.indexOf('(');
			if (pos1!=-1) {
				int pos2=fieldname.indexOf(')');
				results.addElement(fieldname.substring(pos1+1,pos2));
			} else {
				results.addElement(fieldname);
			}
		}
		return results;
	}

	/**
	* Retrieves a (mmbase) module by name.
	# @return the module as an <code>Object</code> if it exists, <code>null</code> otherwise
	* @deprecated. Use {@link #getModule} instead
	*/
	public Object getBaseModule(String name) {
		return getModule(name);
	}

	/**
	* Unused.
	*/
	public void stop()
	{
	}


	/**
	* Initializes the builders, using the builder xml files in the config directory
	* @return Always <code>true</code>
	*/
	boolean initBuilders() {

		// first load the builders

 		String path = "";

 		TypeDef=(TypeDef)loadBuilder("typedef",path);
		TypeDef.init();

		RelDef=(RelDef)loadBuilder("reldef",path);
		RelDef.init();

		TypeRel=(TypeRel)loadBuilder("typerel",path);
		TypeRel.init();

		InsRel=(InsRel)loadBuilder("insrel",path);
		InsRel.init();

		OAlias=(OAlias)loadBuilder("oalias",path);

 		// new code checks all the *.xml files in builder dir, recursively
 		loadBuilders(path);

                log.debug("mmobjects, inits");
		Enumeration t = mmobjs.elements();
		while (t.hasMoreElements()) {
                    MMObjectBuilder fbul=(MMObjectBuilder)t.nextElement();
                    log.debug("init " + fbul);
                    fbul.init();
		}

		Enumeration t2 = mmobjs.keys();
		while (t2.hasMoreElements()) {
                    TypeDef.loadTypeDef(""+t2.nextElement());
		}

                log.debug("Versions:");
		// check and update versions if needed
		Versions ver=(Versions)getMMObject("versions");
		if (ver!=null) {
			t2 = mmobjs.keys();
			while (t2.hasMoreElements()) {
				checkBuilderVersion((String)t2.nextElement(),ver);
			}
		}

                log.debug("**** end of initBuilders");
		return true;
	}


	/**
	*  Loads all builders within a given path relative to the main builder config path, including builders in sub-paths
	*  @param ipath the path to start searching. The path need be closed with a File.seperator character.
	*/
 	void loadBuilders(String ipath) {
 		String path = builderpath + ipath;
 		// new code checks all the *.xml files in builder dir
 		File bdir = new File(path);
        if (bdir.isDirectory()) {
            String files[] = bdir.list();
            if (files!=null) {
                for (int i=0;i<files.length;i++) {
                    String bname=files[i];
                    if (bname.endsWith(".xml")) {
                         bname=bname.substring(0,bname.length()-4);
                         loadBuilderFromXML(bname,ipath);
                    } else {
 	    			 	 loadBuilders(ipath +  bname + File.separator);
 		    	    }
                }
            } else {
				log.error("Cannot find modules in "+path);
			}
        } else {
			//log.error(path+" is not a directory");
	}
    }

    /**
	*  Locate one specific builder within a given path, relative to the main builder config path, including sub-paths.
	*  If the builder already exists, the existing object is returned instead.
	*  @param builder name of the builder to initialize
	*  @param ipath the path to start searching. The path need be closed with a File.seperator character.
	*  @return the initialized builder object, or null if no builder could be created..
	*/
	// ROB changed scope to public
 	public MMObjectBuilder loadBuilder(String builder, String ipath) {
		MMObjectBuilder bul=getMMObject(builder);
		if (bul!=null) {
			log.info("Builder '"+builder+"' is already loaded");
			return bul;
		}
 	    String path = builderpath + ipath;
 		if ((new File(path+builder+".xml")).exists()) {
 			return loadBuilderFromXML(builder,ipath);
 		} else {
 			// not in the builders path, so we need to search recursively
 			File dirList = new File(path);
 			String[] files = dirList.list();
            if (files!=null) {
 			    for (int i=0; i<files.length;i++) {
 				    String lPath = ipath + files[i] + File.separator;
     				if ((new File(builderpath + lPath)).isDirectory()) {
 	    				bul = loadBuilder(builder, lPath);
 		    			if (bul!=null) {
 			    			return bul;
 				    	}
 				    }
 				}
 			} else {
				log.error("Cannot find builder files in "+path);
			}
 			return null;
 		}
    }


    /**
	*  Create a new builder object using a xml configfile located in a given path relative to the main builder config path,
	*  and return the builder object.
	*  If the builder already exists, the existing object is returned instead.
	*  Note that the builder's init() method is NOT called (since some builders need other builders in memory when their init() is called,
	*  this method is called seperately after all builders are loaded).
	*  @param builder name of the builder to initialize
	*  @param ipath the path to start searching. The path need be closed with a File.seperator character.
	*  @return the loaded builder object.
	*/
	MMObjectBuilder loadBuilderFromXML(String builder, String ipath) {

		MMObjectBuilder bul=getMMObject(builder);
		if (bul!=null) {
			log.info("Builder '"+builder+"' is already loaded");
			return bul;
		}

		String path = builderpath + ipath;
		try {
 		    XMLBuilderReader parser=new XMLBuilderReader(path+builder+".xml");
		    Hashtable descriptions=parser.getDescriptions();
		    String description=(String)descriptions.get(language);
		    String dutchsname="Default!";
		    String objectname=builder; // should this allow override in file ?
		    int searchage=parser.getSearchAge();
		    String classname=parser.getClassFile();
		    Hashtable properties=parser.getProperties();

		    String status=parser.getStatus();
		    if (status.equals("active")) {
			    log.info("Starting builder : "+objectname);
				// is it a full name or inside the org.mmase.* path
				int pos=classname.indexOf('.');
				Class newclass=null;
				if	(pos==-1) {
					newclass=Class.forName("org.mmbase.module.builders."+classname);
				} else {
					newclass=Class.forName(classname);
				}
				//log.debug("Vwms -> Loaded load class : "+newclass);

				bul = (MMObjectBuilder)newclass.newInstance();
				//log.debug("MMBase -> started : "+newclass);

 				bul.setXMLPath(ipath);
				bul.setXmlConfig(true);
				bul.setMMBase(this);
				bul.setTableName(objectname);
				bul.setDescription(description);
				bul.setDescriptions(descriptions);
				bul.setDutchSName(dutchsname);
				bul.setSingularNames(parser.getSingularNames());
				bul.setPluralNames(parser.getPluralNames());
				bul.setVersion(parser.getBuilderVersion());
				bul.setMaintainer(parser.getBuilderMaintainer());
				bul.setClassName(classname);
				bul.setSearchAge(""+searchage);
				bul.setInitParameters(properties);
				bul.setXMLValues(parser.getFieldDefs()); // temp  ?
				//bul.init();
				// bul.getEditFields();
				mmobjs.put(objectname,bul);

				// oke set the huge hack for insert layout
				//bul.setDBLayout(fields);

		    }
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return bul;
	}

   /**
   * Retrieves the DTD base url.
   * This value is set using the configuration file.
   * @return the dtd base as a <code>String</code>
   */
        public String getDTDBase() {
        return dtdbase;
	}

	/**
	* Returns a reference to the database used.
	* If needed, it loads the appropriate support class using the configuration parameters.
	* @return a <code>MMJdbc2NodeInterface</code> which holds the appropriate database class
	*/
	public MMJdbc2NodeInterface getDatabase() {
		if (database==null) {
			try {
				String databasename=getInitParameter("DATABASE");
 				String path=MMBaseContext.getConfigPath()+ File.separator + "databases" + File.separator + databasename+".xml";
				XMLDatabaseReader dbdriver=new XMLDatabaseReader(path);
				Class newclass=Class.forName(dbdriver.getMMBaseDatabaseDriver());
				log.info("Loaded load class : "+newclass);
				database=(MMJdbc2NodeInterface)newclass.newInstance();
				database.init(this,dbdriver);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return database;
	}


	/**
	* Loads a Node again, using its 'right' parent.
	* Reloading may retrieve extra fields if teh originalw a snot loaded accurately.
	* @deprecated Not necessary in most cases, with the possible exception of lists obtained from InsRel.
	*   However, in the later case using this method is probably too costly.
	*/
  	public MMObjectNode castNode(MMObjectNode node) {
                /* fake because solved
                */
                int otype=node.getOType();
                String ename=TypeDef.getValue(otype);
                if (ename==null) return null;
                 MMObjectBuilder res=getMMObject(ename);
                MMObjectNode node2=res.getNode(node.getNumber());
                return node2;
                //return node;
        }

   /**
   * Retrieves the autorisation type.
   * This value is set using the configuration file.
   * Examples are 'none' or 'basic'.
   * @return a <code>String</code> identifying the type
   */
    public String getAuthType() {
        return authtype;
    }

   /**
   * Retrieves the current language.
   * This value is set using the configuration file.
   * Examples are 'us' or 'nl'.
   * @return the language as a <code>String</code>
   */
    public String getLanguage() {
        return language;
    }

   /**
   * Retrieves whether this mmbase module is running.
   * @return <code>true</code> if the module has been initialized and all builders loaded, <code>false</code> otherwise.
   */
    public boolean getState() {
        return mmbasestate;
    }

    /**
    * Checks and switches the user/grouplevel in which MMBase runs.
    * The userlevel is set using the -Dmmbase:userlevel=user:group commandline parameter.
    * Should probably be changed to <code>private</code>.
    */
	public void checkUserLevel() {
		String level=System.getProperty("mmbase.userlevel");
		if (level!=null) {
			log.info("CheckUserLevel ->  mmmbase.userlevel="+System.getProperty("mmbase.userlevel"));
			int pos=level.indexOf(':');
			if (pos!=-1) {
				String user=level.substring(0,pos);
				String group=level.substring(pos+1);
    			setUser setuser=new setUser();
				setuser.setUserGroup(user,group);
			} else {
				log.info("CheckUserLevel ->  mmmbase.userlevel= not defined as user:group");
			}
		}
	}

	/**
	* Checks the builder version and, if needed, updates the version table.
	* Queries the xml files instead of the builder itself (?)
	* @return always <code>true</code>.
	*/
	private boolean checkBuilderVersion(String buildername,Versions ver) {

 		MMObjectBuilder tmp = (MMObjectBuilder)mmobjs.get(buildername);
 		String builderfile = builderpath + tmp.getXMLPath() + buildername + ".xml";
 		XMLBuilderReader bapp=new XMLBuilderReader(builderfile);
		if (bapp!=null) {
			int version=bapp.getBuilderVersion();
			String maintainer=bapp.getBuilderMaintainer();
			int installedversion=ver.getInstalledVersion(buildername,"builder");
			if (installedversion==-1 || version>installedversion) {
				if (installedversion==-1) {
					ver.setInstalledVersion(buildername,"builder",maintainer,version);
				} else {
					ver.updateInstalledVersion(buildername,"builder",maintainer,version);
				}
			}
		}
		return true;
	}

}
