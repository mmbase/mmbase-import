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


/**
 * The module which provides access to the MMBase database defined
 * by the provided name/setup.
 * It holds the overal object 'world' madeup of builders, objects and relations and
 * all the needed tools to use them.
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @author Johan Verelst
 * @version 13 november 2000
 */
public class MMBase extends ProcessorModule  {

        /**
        *  The path to the builder configuration files
        */
	String builderpath = "";
	
	private String	_classname = getClass().getName();
	private boolean debug=false;
	private void 	debug( String msg ) { System.out.println( _classname +":"+ msg ); }

	private SendMailInterface sendmail;
	public MMBaseChangeInterface mmc;
	int delay;
	boolean	nodecachesdone=false;
	MMBaseProbe probe;

	JDBCInterface jdbc;
	public String baseName="def1";

	public TypeDef TypeDef;
	public RelDef RelDef;
	public OAlias OAlias;
	public InsRel InsRel;
	public TypeRel TypeRel;

	MultiRelations MultiRelations;
	private String dtdbase="http://www.mmbase.org";
	MMJdbc2NodeInterface database;

	public Hashtable mmobjs=new Hashtable();
	String machineName="unknown";
	String host="unknown";
	String authtype="none";
	public static String multicasthost=null;
	public static int multicastport=-1;
	private String language="us";
	private boolean mmbasestate=false;
	String cookieDomain=null;

	public void init() {
		// is there a basename defined in MMBASE.properties ?
		String tmp=getInitParameter("BASENAME");
		if (tmp!=null) {
			// yes then replace the default name (def1)
			baseName=tmp;
		} else {
			debug("init(): No name defined for mmbase using default (def1)");
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
 		
		initBuilders();


		if (debug) debug("Objects started");


		MultiRelations = new MultiRelations(this);

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
				String name=fbul.getTableName();
				if (debug) debug("WRITING BUILDER FILE ="+writerpath+File.separator+name);
				if (!name.equals("multirelations")) {
					XMLBuilderWriter.writeXMLFile(writerpath+File.separator+fbul.getTableName()+".xml",fbul);
				}
			}
		}
		// signal that MMBase is up and running
		mmbasestate=true;
		System.out.println("MMBase is up and running");
		checkUserLevel();
	}

	public void onload() {
	}

	public void unload() {
	}

	public void shutdown() {
	}


	/**
	 * INFO, a support module for servscan. provides const and
	 * general info like dates, times, browser info etc etc
	 */
	public MMBase() {
		if (debug) debug("MMBase constructed");
	}

	/**
	* is this mmbase as defined in baseName created ?
	*/
	boolean checkMMBase() {
		if (database==null) database=getDatabase();
		try {
			MultiConnection con=getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select count(*) from "+baseName+"_object");
			stmt.close();
			con.close();	
			return(true);	
		} catch (SQLException e) {
			//e.printStackTrace();
			return(false);	
		}
	}

	/**
	* create a new mmbase under the name defined in baseName
	*/
	boolean createMMBase() {
		debug(" creating new multimedia base : "+baseName);
		Vector v;
		database=getDatabase();
		database.createObjectTable(baseName);
		return(true);
	}


	public MMObjectBuilder getMMObject(String name) {
		Object o=mmobjs.get(name);
  		return((MMObjectBuilder)o);
	}

	public InsRel getInsRel() {
		return(InsRel);
	}

	public RelDef getRelDef() {
		return(RelDef);
	}

	public TypeDef getTypeDef() {
		return(TypeDef);
	}

	public TypeRel getTypeRel() {
		return(TypeRel);
	}

	/**
	 * Get a database connection that is multiplexed and checked.
	 */
	public MultiConnection getConnection() {
		try {
			//MultiConnection con=jdbc.getConnection(jdbc.makeUrl());
			//MultiConnection con=jdbc.getConnection("jdbc:HypersonicSQL:.","sa","");
			//MultiConnection con=jdbc.getConnection("jdbc:HypersonicSQL:mmbase","sa","");
			MultiConnection con=database.getConnection(jdbc);
			return(con);
		} catch (SQLException e) {
			debug("Can't get a JDBC connection (database error)"+e);
			e.printStackTrace();
			return(null);
		} catch (Exception e) {
			debug("Can't get a JDBC connection (JDBC module error)"+e);
			e.printStackTrace();
			return(null);
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
	 * Get a direct database connection, should only be used
	 * if you want to do database specific things that use non-jdbc
	 * interface calls. Use very sparingly
	 */
	public Connection getDirectConnection() {
		try {
			Connection con=jdbc.getDirectConnection(jdbc.makeUrl());
			return(con);
		} catch (SQLException e) {
			e.printStackTrace();
			return(null);
		} catch (Exception e) {
			debug("Can't get a JDBC connection (JDBC module error)"+e);
			e.printStackTrace();
			return(null);
		}
	}

	public String getBaseName() {
		return(baseName);
	}

	public void doProbeRun() {
		DayMarkers bul=(DayMarkers)getMMObject("daymarks");
		if (bul!=null) {
			bul.probe();
		} else {
			debug("doProbeRun(): ERROR: Can't access builder : daymarks");
		}
	}

	public void maintainance() {
		if (probe==null) probe=new MMBaseProbe(this);

		//LStreams.checkBroadcastState(LStreams.getNode(573949));
		if (2==1 && mmc!=null) {
			// debug for in/out multicast check
			// if( debug) debug("maintenance(): in="+mmc.incount+" out="+mmc.outcount+" spawn="+mmc.spawncount);
		}
	}


	public Hashtable getSearchHash(Vector se,String mapper) {
		Hashtable results=new Hashtable();
		Enumeration t = se.elements(); 
		MMObjectNode node;
		while (t.hasMoreElements()) {
			node=(MMObjectNode)t.nextElement();
			results.put(new Integer(node.getIntValue(mapper)),node);
		}
		return(results);
	}

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
        return(rtn);
	}
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
        	return(buffer);
        }
        return(null);
	}

	public SendMailInterface getSendMail() {
		return(sendmail);
	}

	public String getMachineName() {
		return(machineName);
	}

	public String getHost() {
		return(host);
	}

	public String getCookieDomain() {
		return(cookieDomain);
	}

	public boolean addRemoteObserver(String type,MMBaseObserver obs) {
		MMObjectBuilder bul=getMMObject(type);
		if (bul!=null) {
			return(bul.addRemoteObserver(obs));
		} else {
			debug("addRemoteObserver(): ERROR: Can't find builder : "+type);
			return(false);
		}
	}


	public boolean addLocalObserver(String type,MMBaseObserver obs) {
		MMObjectBuilder bul=getMMObject(type);
		if (bul!=null) {
			return(bul.addLocalObserver(obs));
		} else {
			debug("addLocalObserver(): ERROR: Can't find builder : "+type);
			return(false);
		}
	}

	public String doGetAgeMarker(StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String age=tok.nextToken();
			try {
				int agenr=Integer.parseInt(age);
				int agecount=((DayMarkers)getMMObject("daymarks")).getDayCountAge(agenr);
				return(""+agecount);
			} catch (Exception e) {
				debug(" Not a valid AGE");
				return("No valid age given");
			}
		} else {
			return("No age given");
		}
	}

	public synchronized int getDBKey() {
		return(database.getDBKey());
	}


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
		return(results);
	}
	
	public Object getBaseModule(String name) {
		return(getModule(name));
	}

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

		Enumeration t = mmobjs.elements();
		while (t.hasMoreElements()) {
			MMObjectBuilder fbul=(MMObjectBuilder)t.nextElement();
			fbul.init();
		}

		Enumeration t2 = mmobjs.keys(); 
		while (t2.hasMoreElements()) {
			TypeDef.loadTypeDef(""+t2.nextElement());
		}

		// check and update versions if needed	
		Versions ver=(Versions)getMMObject("versions");
		if (ver!=null) {
			t2 = mmobjs.keys(); 
			while (t2.hasMoreElements()) {
				checkBuilderVersion((String)t2.nextElement(),ver);
			}
		}

		return(true);
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
            }
        }
		
    }
     	     	
     	/**
	*  Locate one specific builder within a given path, relative to the main builder config path, including sub-paths.
	*  If the builder already exists, the existing object is returned instead.
	*  @param builder name of the builder to initialize
	*  @param ipath the path to start searching. The path need be closed with a File.seperator character.
	*  @return the initialized builder object, or null if no builder could be created..
	*/
 	MMObjectBuilder loadBuilder(String builder, String ipath) {
		MMObjectBuilder bul=getMMObject(builder);
		if (bul!=null) {
			if (debug) debug("Builder '"+builder+"' is already loaded");
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
 			}
 			return(null);
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
			if (debug) debug("Builder '"+builder+"' is already loaded");
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
			    debug(" Starting builder : "+objectname);
				// is it a full name or inside the org.mmase.* path
				int pos=classname.indexOf('.');
				Class newclass=null;
				if	(pos==-1) {
					newclass=Class.forName("org.mmbase.module.builders."+classname);
				} else {
					newclass=Class.forName(classname);
				}
				//debug("Vwms -> Loaded load class : "+newclass);

				bul = (MMObjectBuilder)newclass.newInstance();
				// debug("MMBase -> started : "+newclass);
				
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
			return(null);
		}
		return(bul);
	}


	public String getDTDBase() {	
		return(dtdbase);
	}

	public MMJdbc2NodeInterface getDatabase() {
		if (database==null) {
			try {
				String databasename=getInitParameter("DATABASE");
 				String path=MMBaseContext.getConfigPath()+ File.separator + "databases" + File.separator + databasename+".xml";
				XMLDatabaseReader dbdriver=new XMLDatabaseReader(path);
				Class newclass=Class.forName(dbdriver.getMMBaseDatabaseDriver());
				if (debug) debug("Loaded load class : "+newclass);
				database=(MMJdbc2NodeInterface)newclass.newInstance();
				database.init(this,dbdriver);
			} catch(Exception e) {
				e.printStackTrace();	
			}
		}
		return(database);
	}


  	public MMObjectNode castNode(MMObjectNode node) {
                /* fake because solved
                */
                int otype=node.getIntValue("otype");
                String ename=TypeDef.getValue(otype);
                if (ename==null) return(null);
                MMObjectBuilder res=getMMObject(ename);
                MMObjectNode node2=res.getNode(node.getIntValue("number"));
                return(node2);
                //return(node);
        }

	public String getAuthType() {
		return(authtype);
	}
	
	public String getLanguage() {
		return(language);
	}
	
	public boolean getState() {
		return(mmbasestate);
	}


	public void checkUserLevel() {
		String level=System.getProperty("mmbase.userlevel");
		if (level!=null) {
			System.out.println("CheckUserLevel ->  mmmbase.userlevel="+System.getProperty("mmbase.userlevel"));
			int pos=level.indexOf(':');
			if (pos!=-1) {
				String user=level.substring(0,pos);
				String group=level.substring(pos+1);
	 			setUser setuser=new setUser();
				setuser.setUserGroup(user,group);
			} else {
				System.out.println("CheckUserLevel ->  mmmbase.userlevel= not defined as user:group");
			}
		}
	}
	
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
		return(true);
	}
}
