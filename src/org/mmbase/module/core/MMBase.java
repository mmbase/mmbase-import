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
import org.mmbase.module.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.*;
import org.mmbase.module.database.support.*;


/**
 * The module which provides access to the MMbase defined
 * by the provided name/setup it holds the overal object
 * 'world' madeup of builders, objects and relations and
 * all the needed tools to use them.
 *
 * @author Daniel Ockeloen
 */
public class MMBase extends ProcessorModule  {

	private String	_classname = getClass().getName();
	private boolean debug=false;
	private void 	debug( String msg ) { System.out.println( _classname +":"+ msg ); }

	private SendMailInterface sendmail;
	public MMBaseMultiCast mmc;
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

		mmc=new MMBaseMultiCast(this);

		if (!checkMMBase()) {
			// there is no base defined yet, create the core objects
			createMMBase();
		}

		// new loader system for now only checks the defines
		String builderfile=getInitParameter("BUILDERFILE");
		if (builderfile==null || builderfile.equals("")) {
			builderfile=MMBaseContext.getConfigPath()+("/builders/");
		}
		initBuilders(builderfile);


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
				System.out.println("WRITING BUILDER FILE ="+writerpath+"/"+name);
				if (!name.equals("multirelations")) {
					XMLBuilderWriter.writeXMLFile(writerpath+"/"+fbul.getTableName()+".xml",fbul);
				}
			}
		}
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
		debug("MMBase constructed");
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
	 * Get a direct database connection, should only be used
	 * if you want to do database specific things that use non-jdbc
	 * interface calls. Use very sparingly
	 */
	public Connection getDirectConnection() {
		debug(" getDirectConnection()");
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
			if( debug) debug("maintenance(): in="+mmc.incount+" out="+mmc.outcount+" spawn="+mmc.spawncount);
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

		//debug("MMBase -> Opening file:"+file); 
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

	boolean initBuilders(String path) {

		initBuilder("typedef",path);
		TypeDef=(TypeDef)getMMObject("typedef");
		TypeDef.init();


		initBuilder("reldef",path);
		RelDef=(RelDef)getMMObject("reldef");
		RelDef.init();
		
		initBuilder("typerel",path);
		TypeRel=(TypeRel)getMMObject("typerel");
		TypeRel.init();

		initBuilder("insrel",path);
		InsRel=(InsRel)getMMObject("insrel");
		InsRel.init();

		initBuilder("oalias",path);
		OAlias=(OAlias)getMMObject("oalias");

		// new code checks all the *.xml files in builder dir
        	File bdir = new File(path);
		if (bdir.isDirectory()) {
			String files[] = bdir.list();		
			for (int i=0;i<files.length;i++) {
				String bname=files[i];
				if (bname.endsWith(".xml")) {
					bname=bname.substring(0,bname.length()-4);
					initBuilder(bname,path);
				}
			}
		}

		Enumeration t = mmobjs.elements(); 
		while (t.hasMoreElements()) {
			MMObjectBuilder fbul=(MMObjectBuilder)t.nextElement();
			fbul.init();
		}
		return(true);
	}



	boolean initBuilder(String builder,String path) {
		if (!(new File(path+builder+".xml")).exists()) return(false);
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
			debug(" Starting builder XML : "+objectname);
			try {
				// is it a full name or inside the org.mmase.* path
				int pos=classname.indexOf('.');
				Class newclass=null;
				if	(pos==-1) {
					newclass=Class.forName("org.mmbase.module.builders."+classname);
				} else {
					newclass=Class.forName(classname);
				}
				//debug("Vwms -> Loaded load class : "+newclass);

				MMObjectBuilder bul = (MMObjectBuilder)newclass.newInstance();
				// debug("MMBase -> started : "+newclass);
				
				bul.setXmlConfig(true);
				bul.setMMBase(this);
				bul.setTableName(objectname);
				bul.setDescription(description);
				bul.setDescriptions(descriptions);
				bul.setDutchSName(dutchsname);
				bul.setSingularNames(parser.getSingularNames());
				bul.setPluralNames(parser.getPluralNames());
				bul.setClassName(classname);
				bul.setSearchAge(""+searchage);
				bul.setInitParameters(properties);
				bul.setXMLValues(parser.getFieldDefs()); // temp  ?
				//bul.init();
				// bul.getEditFields();
				mmobjs.put(objectname,bul);

				// oke set the huge hack for insert layout 
				//bul.setDBLayout(fields);

				} catch (Exception e) {
					e.printStackTrace();
					return(false);
				}
		}
		return(true);
	}


	public String getDTDBase() {	
		return(dtdbase);
	}

	public MMJdbc2NodeInterface getDatabase() {
		if (database==null) {
			try {
				String databasename=getInitParameter("DATABASE");
				String path=MMBaseContext.getConfigPath()+("/databases/"+databasename+".xml");
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
}
