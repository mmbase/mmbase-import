/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

/* 
	$Id: HtmlBase.java,v 1.40 2001-05-11 12:10:38 vpro Exp $

	$Log: not supported by cvs2svn $
	Revision 1.39  2001/03/12 09:00:12  pierre
	pierre: added SEARCH attribute to the LIST MULTILEVEL tag. Values are BOTH, DESTINATION, SOURCE, ALL, and EITHER.
	Default value of this attribute is EITHER, which simulates the old list behavior.
	
	Revision 1.38  2001/03/09 10:10:47  pierre
	pierre: adapted mmeditor classes to new relations system and added logging
	
	Revision 1.37  2000/12/10 16:00:55  daniel
	moved a error msg
	
	Revision 1.36  2000/11/29 12:05:22  vpro
	Rico: Probably fixed getReload problem
	
	Revision 1.35  2000/11/19 00:24:48  daniel
	turned cachedebug off
	
	Revision 1.34  2000/11/08 16:48:12  pierre
	pierre: removeFunctions now recognizes underscores in tablenames
	
	Revision 1.33  2000/11/07 12:37:56  vpro
	Rico: fixed reload bug
	
	Revision 1.32  2000/11/07 10:48:19  vpro
	Rico: added seperate cachedebug switch
	
	Revision 1.31  2000/11/06 12:47:01  vpro
	Rico: fixed speling error
	
	Revision 1.30  2000/08/29 15:04:37  wwwtech
	Rob: Added TYPENAME tag. usage $MOD-MMBASE-TYPENAME-400 gives person
	
	Revision 1.29  2000/08/07 22:25:14  daniel
	changed LIST RELATIONS to support aliases
	
	Revision 1.28  2000/07/22 10:48:45  daniel
	Removed some debug
	
	Revision 1.27  2000/07/15 23:58:32  daniel
	added option to turn html caching off
	
	Revision 1.26  2000/07/15 14:10:13  daniel
	Removed from debug
	
	Revision 1.25  2000/07/15 10:11:19  daniel
	Changed getDBType to int
	
	Revision 1.24  2000/07/14 05:41:07  daniel
	removed fieldDef dep. for getField
	
	Revision 1.23  2000/06/28 10:48:07  daniel
	Daniel.. removed ref to FieldDef
	
	Revision 1.22  2000/06/20 14:24:52  install
	Rob: turned debug off
	
	Revision 1.21  2000/06/20 14:20:02  install
	Rob: turned debug off
	
	Revision 1.20  2000/06/06 20:54:08  wwwtech
	small updates
	
	Revision 1.19  2000/04/14 12:12:15  wwwtech
	- (marcel) Made nicer output when LIST RELATIONS fails in logfile
	
	Revision 1.18  2000/04/03 09:03:38  wwwtech
	Rico: added tag for multilevel "MEMCACHE=NO" to bypass memory cache
	
	Revision 1.17  2000/03/31 13:21:28  wwwtech
	Wilbert: Introduction of ParseException for method getList
	
	Revision 1.16  2000/03/31 13:18:19  wwwtech
	Wilbert: Introduction of ParseException for method getList
	
	Revision 1.15  2000/03/30 13:11:27  wwwtech
	Rico: added license
	
	Revision 1.14  2000/03/29 11:06:00  wwwtech
	Wilbert Removed TSEARCH from replace
	
	Revision 1.13  2000/03/29 10:42:19  wwwtech
	Rob: Licenses changed
	
	Revision 1.12  2000/03/29 10:07:49  wwwtech
	Wilbert Removed TSEARCH from getList
	
	Revision 1.11  2000/03/21 15:42:55  wwwtech
	Wilbert changed use of teaser builder to use of typedef builder where possible to get nodes
	
	Revision 1.10  2000/03/20 10:40:59  wwwtech
	Rico: added GETVALUE replace command
	
	Revision 1.9  2000/03/17 12:19:45  wwwtech
	Rico: added better support for functions in getValue
	
	Revision 1.8  2000/03/15 10:18:42  wwwtech
	Rico added which url caused the exception to doRelations
	
	Revision 1.7  2000/03/10 12:09:57  wwwtech
	Rico: added circular part detection to scanparser, it is also now possilbe to subclass ParseException and throw that in scanparser for those unholdable situations.
	
	Revision 1.6  2000/03/09 15:57:47  wwwtech
	Rico: fixed bugs in the detection of the reload state, also detects CACHE PAGE pages as a reload situation now
	
	Revision 1.5  2000/03/09 13:10:40  wwwtech
	Rico: added cache passthrough for multilevel
	
	Revision 1.4  2000/03/08 14:53:30  wwwtech
	Rico: added caching for Multilevel (128 entries) this should increase performance for HTML pages a bit
	
*/
package org.mmbase.module.gui.html;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.util.Date;
import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.*;
import org.mmbase.module.database.support.*;


/**
 * The module which provides access to the multimedia database
 * it creates, deletes and gives you methods to keep track of
 * multimedia objects. It does not give you direct methods for
 * inserting and reading them thats done by other objects
 *
 * @author Daniel Ockeloen
 * @version $Id: HtmlBase.java,v 1.40 2001-05-11 12:10:38 vpro Exp $
 */
public class HtmlBase extends ProcessorModule {

	/**
    * Logging instance
    */
	private static Logger log = Logging.getLoggerInstance(HtmlBase.class.getName());
 	
 	public MMBaseMultiCast mmc;
	public String baseName="def1";
	
	public Hashtable mmobjs=new Hashtable();
	
	String machineName="unknown";
	sessionsInterface sessions;
	Vector onlineVector=null;
	String SyncNodes="NO";
	boolean scancache=false;
	MMBase mmb=null;
	int delay;
	boolean	nodecachesdone=false;
	MMBaseProbe probe;
	JDBCInterface jdbc;
	RelDef RelDef;
	MMObjectBuilder MMObjectBuilder;
	OAlias OAlias;
	InsRel InsRel;
	TypeRel TypeRel;
	MMJdbc2NodeInterface database;
	String databasename;
	
	private int multilevel_cachesize=300;
	private LRUHashtable multilevel_cache;
	private boolean cachedebug=false;
	private SendMailInterface sendmail;
	private String dtdbase="http://www.mmbase.org";


	public void init() {
		scancache tmp=(scancache)getModule("SCANCACHE");		
		
		if (tmp!=null && tmp.getStatus()) scancache=true;

		mmb=(MMBase)getModule("MMBASEROOT");		
		// is there a basename defined in MMBASE.properties ?
		sessions=(sessionsInterface)getModule("SESSION");		

		// get size from properties
		multilevel_cache=new LRUHashtable(multilevel_cachesize);
	}


	/**
	 */
	public HtmlBase() {
	}



	/**
	 * Generate a list of values from a command to the processor
	 */
	 public Vector getList(scanpage sp,StringTagger tagger, String value) throws ParseException {
    	String line = Strip.DoubleQuote(value,Strip.BOTH);
		StringTokenizer tok = new StringTokenizer(line,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("OBJECTS")) return(doObjects(sp,tagger));
			if (cmd.equals("RELATIONS")) return(doRelations(sp,tagger));
			if (cmd.equals("MULTILEVEL")) return(doMultiLevel(sp,tagger));
			if (cmd.equals("MULTI")) return(doMultiLevel(sp,tagger));
			if (cmd.equals("BUILDER")) return(doBuilder(sp,tagger,tok));
		}
		return(null);
	}

	/**
	 * show Objects
	 */
	public Vector doObjects(scanpage sp, StringTagger tagger) {
		Object tmp;
		String result=null;
		MMObjectNode node;
		Vector results=new Vector(); 
		String type=tagger.Value("TYPE");
		String where=tagger.Value("WHERE");
		String dbsort=tagger.Value("DBSORT");
		String dbdir=tagger.Value("DBDIR");
		MMObjectBuilder bul=mmb.getMMObject(type);
		long begin=(long)System.currentTimeMillis(),len;
		Enumeration e=null;
		if (dbsort==null) {
			e=bul.search(where);
		} else {
			if (dbdir==null) {
				e=bul.search(where,dbsort);
			} else {
				if (dbdir.equals("DOWN")) {
					e=bul.search(where,dbsort,false);
				} else {
					e=bul.search(where,dbsort,true);
				}
			}
		}

		for (;e.hasMoreElements();) {
			node=(MMObjectNode)e.nextElement();
			Enumeration f=tagger.Values("FIELDS").elements();
			for (;f.hasMoreElements();) {

				String fieldname=Strip.DoubleQuote((String)f.nextElement(),Strip.BOTH);
				result=getNodeStringValue(node,fieldname);

				if (result!=null && !result.equals("null")) {
    				results.addElement(result); 
				} else {
    				results.addElement(""); 
				}
			}
		}
		tagger.setValue("ITEMS",""+tagger.Values("FIELDS").size());
		long end=(long)System.currentTimeMillis();
		log.debug("doObjects("+type+")="+(end-begin)+" ms");
		return(results);
	}

	private String getNodeStringValue(MMObjectNode node,String fieldname) {
		// extra hack to get functions working will be changed soon
		if (fieldname.indexOf('(')==-1)
			return node.getValueAsString(fieldname);
		else return "" + node.getValue(fieldname);
	}


	/**
	 * show Relations
	 */
	public Vector doRelations(scanpage sp, StringTagger tagger) {
		Object tmp;
		MMObjectNode node;
		MMObjectBuilder bul=null;
		int otype=-1;
		int snode=-1;
		int onode=-1;
		Vector results=new Vector(); 
		Vector wherevector=null;
		String type=tagger.Value("TYPE");
//		String dbsort=tagger.Value("DBSORT");   NOT in use!
		String where=tagger.Value("WHERE");

		try {
			String tm=tagger.Value("NODE");
			MMObjectNode srcnode = mmb.getTypeDef().getNode(tm);
			snode = srcnode.getIntValue("number");
			bul=srcnode.parent;
			
			if (type!=null) {
			    bul=mmb.getMMObject(type);
			    if (bul==null) {
			        throw new Exception("cannot find object type : "+type);
			    }
			    otype=bul.oType;
			}
			if ((where!=null) && (bul!=null)) {
				wherevector=bul.searchNumbers(where);
			}
			Iterator i = null;
			if (type==null) {
			 i=srcnode.getRelatedNodes().iterator();
			} else {
			 i=srcnode.getRelatedNodes(type).iterator();
			}
            while(i.hasNext()) {
				node=(MMObjectNode)i.next();
				if (where==null || wherevector.contains(new Integer(node.getIntValue("number")))) {
					for (Iterator f=tagger.Values("FIELDS").iterator(); f.hasNext();) {
						// hack hack this is way silly Strip needs to be fixed
						tmp=node.getValue(Strip.DoubleQuote((String)f.next(),Strip.BOTH));
						if (tmp!=null && !tmp.equals("null")) {
   		 				    results.addElement(""+tmp);
						} else {
   		 				    results.addElement("");
						}
					}
				}
			}
			tagger.setValue("ITEMS",""+tagger.Values("FIELDS").size());
		} catch(Exception e) {
			log.error("doRelations("+sp.getUrl()+"): ERROR: node("+snode+"), type("+type+"), where("+where+"):"+e);
			if (bul!=null) {
			    log.error(Logging.stackTrace(e));
			}
		}
		return(results);
	}


	public String doGetRelationValue(scanpage sp, StringTokenizer tok) {
		MMObjectBuilder bul = mmb.getMMObject("typedef");

		// reads $MOD-MMBASE-GETRELATIONVALUE-12-23-title where 12 is the source
		// number, 23 the target number and title the key of the relation
		// value you want.

		int snumber=-1;
		int dnumber=-1;


		//obtain source number
		if (tok.hasMoreTokens()) {
			try {
				snumber=Integer.parseInt(tok.nextToken());	
			} catch (Exception e) {
				return("wrong source node");
			}		
		} else {
			return("missing source node");
		}


		//obtain destination number
		if (tok.hasMoreTokens()) {
			try {
				dnumber=Integer.parseInt(tok.nextToken());	
			} catch (Exception e) {
				return("wrong destination node");
			}		
		} else {
			return("missing destination node");
		}

		//obtain field name
		if (tok.hasMoreTokens()) {
			String fieldname=tok.nextToken();
			MMObjectNode snode=bul.getNode(""+snumber);
			if (snode!=null) {
				for (Enumeration e=snode.getRelations();e.hasMoreElements();) {
					MMObjectNode inode=(MMObjectNode)e.nextElement();
					int s=inode.getIntValue("snumber");
					int d=inode.getIntValue("dnumber");
					if (d==dnumber || s==dnumber) {
						String result="";
						int n=inode.getIntValue("number");
						MMObjectNode dnode=(MMObjectNode)bul.getNode(""+n);
						if (dnode!=null) {
							result=getNodeStringValue(dnode,fieldname);
							if (result!=null && !result.equals("null")) {
								return(result);
							} else {
								return("");
							}

						}
					}
				}
			} else {
				return("wrong source node");
			}
		} else {
			return("missing fieldname");
		}

		return("");
	}


	public String doGetRelationCount(scanpage sp, StringTokenizer tok) {

		MMObjectBuilder bul=mmb.getMMObject("typedef");
		// reads $MOD-MMBASE-GETRELATIONCOUNT-12-images where 12 is the nodenumber
		// and images is optional (if not it will return the total number of
		// relations it has.
		

		int snumber=-1;
		int dnumber=-1;
		String bulname=null;


		//obtain source number
		if (tok.hasMoreTokens()) {
			try {
				snumber=Integer.parseInt(tok.nextToken());	
			} catch (Exception e) {
				return("wrong source node");
			}		
		} else {
			return("missing source node");
		}


		// obtain possible builder if not defined it will return the total count
		if (tok.hasMoreTokens()) {
			bulname=tok.nextToken();	
		}

		MMObjectNode snode=bul.getNode(""+snumber);
		if (snode!=null) {
			if (bulname==null) {
				return(""+snode.getRelationCount());
			} else {
				return(""+snode.getRelationCount(bulname));
			}
		} else {
			return("0");
		}
	}


	public String doSetRelationValue(scanpage sp, StringTokenizer tok) {

		MMObjectBuilder bul=mmb.getMMObject("typedef");
		// reads $MOD-MMBASE-GETRELATIONVALUE-12-23-title where 12 is the source
		// number, 23 the target number and title the key of the relation
		// value you want.

		int snumber=-1;
		int dnumber=-1;


		//obtain source number
		if (tok.hasMoreTokens()) {
			try {
				snumber=Integer.parseInt(tok.nextToken());	
			} catch (Exception e) {
				return("wrong source node");
			}		
		} else {
			return("missing source node");
		}


		//obtain destination number
		if (tok.hasMoreTokens()) {
			try {
				dnumber=Integer.parseInt(tok.nextToken());	
			} catch (Exception e) {
				return("wrong destination node");
			}		
		} else {
			return("missing destination node");
		}

		//obtain field name
		if (tok.hasMoreTokens()) {
			String fieldname=tok.nextToken();
			MMObjectNode snode=bul.getNode(""+snumber);
			if (snode!=null) {
				for (Enumeration e=snode.getRelations();e.hasMoreElements();) {
					MMObjectNode inode=(MMObjectNode)e.nextElement();
					int s=inode.getIntValue("snumber");
					int d=inode.getIntValue("dnumber");
					if (d==dnumber || s==dnumber) {
						String result="";
						int n=inode.getIntValue("number");
						MMObjectNode dnode=(MMObjectNode)bul.getNode(""+n);
						if (dnode!=null) {
							result=getNodeStringValue(dnode,fieldname);
							if (result!=null && !result.equals("null")) {
								return(result);
							} else {
								return("");
							}

						}
					}
				}
			} else {
				return("wrong source node");
			}
		} else {
			return("missing fieldname");
		}

		return("");
	}

    /**
     * show Relations
     */
    public Vector doRelations_replace(scanpage sp, StringTokenizer tok) {
        Object tmp;
        MMObjectNode node;
        MMObjectBuilder bul=null;
        int otype=-1;
        int snode=-1;
        int onode=-1;
        Vector results=new Vector();

//		long begin=(long)System.currentTimeMillis();
        try {
            String type=tok.nextToken();
            bul=mmb.getMMObject(type);
            otype=bul.oType;

            snode=Integer.parseInt(tok.nextToken());
            MMObjectNode node2=bul.getNode(snode);

            Iterator i=null;
            if (type==null) {
			 i=node2.getRelatedNodes().iterator();
			} else {
			 i=node2.getRelatedNodes(type).iterator();
			}
            while(i.hasNext()) {
                node=(MMObjectNode)i.next();
                // hack hack this is way silly Strip needs to be fixed
                tmp=node.getValue(tok.nextToken());
                if (tmp!=null && !tmp.equals("null")) {
                        /* org.mmbase
                        if (tmp!=null && (tmp instanceof String) && ((String)tmp).indexOf("I01")==0) {
                            byte[] text=getDatabase().getbinary((String)tmp);
                            tmp=new String(text,0);
                            if (tmp.equals("null")) {
                                tmp="";
                            }
                        }
                        */
                    results.addElement(""+tmp);
                } else {
                    results.addElement("");
                }
            }
		} catch(Exception g) {
		   return(null);
		}
//		long end=(long)System.currentTimeMillis();
		if (results.size()>0) {
			return(results);
		} else {
			return(null);
		}
	}

	/**
	 * Execute the commands provided in the form values
	 */
	public boolean process(scanpage sp, Hashtable cmds,Hashtable vars) {
		String cmdline,token;

		for (Enumeration h = cmds.keys();h.hasMoreElements();) {
			cmdline=(String)h.nextElement();	
			StringTokenizer tok = new StringTokenizer(cmdline,"-\n\r");
			token = tok.nextToken();
			if (token.equals("CACHEDELETE")) {
				log.debug("process(): DELETE ON CACHES");
				InsRel.deleteNodeCache();
				InsRel.deleteRelationCache();

			}
		}
		return(false);
	}

	/**
	*	Handle a $MOD command
	*/
	public String replace(scanpage sp, String cmds) {
		StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("FIELD")) { 
				return(getObjectField(sp,tok));
			} else if (cmd.equals("GETVALUE")) { 
				return(getBuilderValue(sp,tok));
			} else if (cmd.equals("PROPERTY")) { 
				return(getObjectProperty(sp,tok));
			} else if (cmd.equals("OTYPE")) { 
				return(getObjectType(sp,tok));
 			} else if (cmd.equals("TYPENAME")) {
                return(getObjectTypeName(sp,tok));
			} else if (cmd.equals("GUIINDICATOR")) { 
				return(getGuiIndicator(sp,tok));
			} else if (cmd.equals("RELATION")) {
					Vector result=doRelations_replace(sp,tok);
					if (result!=null) return((String)result.elementAt(0));
					return("");
			} else if (cmd.equals("GETRELATIONCOUNT")) {
					return(doGetRelationCount(sp,tok));
			} else if (cmd.equals("GETRELATIONVALUE")) {
					return(doGetRelationValue(sp,tok));
			} else if (cmd.equals("SETRELATIONVALUE")) {
					return(doSetRelationValue(sp,tok));
			} else if (cmd.equals("GETAUTHTYPE")) {
					return(mmb.getAuthType());
			} else if (cmd.equals("GETSEARCHAGE")) {
					return(getSearchAge(tok));
			} else if (cmd.equals("CACHE")) {
				return(""+doCache(sp,tok));
			} else if (cmd.equals("GETDAYMARKER")) {
				return(mmb.doGetAgeMarker(tok));
			// org.mmbase } else if (cmd.equals("FILEINFO")) {
			// org.mmbase		return (doFile(rq, tok));
			} else if (cmd.equals("BUILDER")) {
				return (doBuilderReplace(sp, tok));
			} else if (cmd.equals("GETJUMP")) {
				Jumpers bul=(Jumpers)mmb.getMMObject("jumpers");
				String url=bul.getJump(tok);
				if (url.startsWith("http://")) {
					return (url);
				} else {
					return("");
				}
			} else if (cmd.equals("GETNUMBER")) {
				// Get the number for a alias
				if (OAlias==null) OAlias=(OAlias)mmb.getMMObject("oalias");
				return(""+OAlias.getNumber(tok.nextToken()));
			} else if (cmd.equals("FIELDLENGTH")) {
				String s = getObjectField(sp,tok);
				if (s==null) 
					return "0"; 
				else 
					return ""+s.length();
			}
		}
		return("No command defined");
	}

	String doCache(scanpage sp, StringTokenizer tok) {
		String result="";
		String cmd=tok.nextToken();
		if (cmd.equals("SIZE")) {
			if (tok.hasMoreTokens()) {
				String type=tok.nextToken();
				MMObjectBuilder bul=mmb.getMMObject("fielddef");
				result=""+bul.getCacheSize(type);
				return(result);
			} else {
				MMObjectBuilder bul=mmb.getMMObject("fielddef");
				result=""+bul.getCacheSize();
				return(result);
			}
		}
		return(result);
	}


	String getObjectType(scanpage sp, StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String number=tok.nextToken();
			MMObjectNode node=mmb.getTypeDef().getNode(number);
			return(mmb.getTypeDef().getValue(node.getIntValue("otype")));
		}
		return("unknown");
	}

	String getObjectTypeName(scanpage sp, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String number=tok.nextToken();
            MMObjectNode node=mmb.getTypeDef().getNode(number);
            return(node.getName());
        }
        return("unknown");
    }

	String getGuiIndicator(scanpage sp, StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String number=tok.nextToken();
			MMObjectNode node=mmb.getTypeDef().getNode(number);
			return(node.getGUIIndicator());
		}
		return("unknown");
	}

	String getBuilderValue(scanpage sp, StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String number=tok.nextToken();
			String field="number";
			if (tok.hasMoreTokens()) field=tok.nextToken();
			MMObjectNode node=mmb.getTypeDef().getNode(number);
			return(""+node.getValue(field));
		}
		return("");
	}

	String getObjectField(scanpage sp, StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String nodeNr=tok.nextToken();
			if( tok.hasMoreTokens()){
				String fieldname=tok.nextToken();
				String result=null;
				MMObjectBuilder bul=mmb.getMMObject("typedef");
				MMObjectNode node=bul.getAliasedNode(nodeNr);
				sessionInfo pagesession=getPageSession(sp);
				if (pagesession!=null) {
					pagesession.addSetValue("PAGECACHENODES",""+nodeNr);
				}
				if (result!=null) {
					return(result);
				} else  {
					result=getNodeStringValue(node,fieldname);
					if (result!=null && !result.equals("null")) {
						return(result);
					} else {
						return("");
					}
				}
			} else log.error("getObjectField(): no token fieldname found, nodenr("+nodeNr+"), url("+sp.getUrl()+")");
		} else log.error("getObjectField(): no token nodenr found, url("+sp.getUrl()+")");
		return("no command defined");
	}


	String getObjectProperty(scanpage sp, StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String nodeNr=tok.nextToken();
			String fieldname=tok.nextToken();
			MMObjectBuilder bul=mmb.getMMObject("fielddef");
			MMObjectNode node=bul.getNode(nodeNr);
			sessionInfo pagesession=getPageSession(sp);
			if (pagesession!=null) {
				pagesession.addSetValue("PAGECACHENODES",""+nodeNr);
			}
			MMObjectNode pnode=node.getProperty(fieldname);
			if (pnode!=null) {
				String result=pnode.getStringValue("value");
				return(result);
			} else {
				return("");
			}
		}
		return("no command defined");
	}


	public String getBaseName() {
		return(baseName);
	}

	public void maintainance() {
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
            rtn=new String(buffer);
        }
        return(rtn);
	}


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

	public Vector doMultiLevel(scanpage sp, StringTagger tagger) throws MultiLevelParseException {
		String result=null,fieldname;
		Object tmp;
		MMObjectNode node;
		int snode=-1,onode=-1;
		Integer hash;
		Vector results=null,nodes,wherevector=null;
		Enumeration e,f;
		boolean reload=true;

		if (scancache) reload=getReload(sp,tagger);

		Vector type=tagger.Values("TYPE");
		if ((type==null) || (type.size()==0)) throw new MultiLevelParseException("No TYPE specified");
		Vector dbsort=tagger.Values("DBSORT");
		Vector dbdir=tagger.Values("DBDIR");
		String where=tagger.Value("WHERE");
		Vector fields=tagger.Values("FIELDS");
		if ((fields==null) || (fields.size()==0)) throw new MultiLevelParseException("No FIELDS specified");
		Vector snodes=tagger.Values("NODE");
		if ((snodes==null) || (snodes.size()==0)) throw new MultiLevelParseException("No NODE specified. Use NODE=\"-1\" to specify no node");
		String distinct=tagger.Value("DISTINCT");
		String searchdirs=tagger.Value("SEARCH");
		int searchdir = MultiRelations.SEARCH_EITHER;
		if (searchdirs!=null) {
		    searchdirs = searchdirs.toUpperCase();
    		if ("DESTINATION".equals(searchdirs)) {
    		    log.debug("DESTINATION");
                searchdir = MultiRelations.SEARCH_DESTINATION;
		    } else if ("SOURCE".equals(searchdirs)) {
    		    log.debug("SOURCE");
                searchdir = MultiRelations.SEARCH_SOURCE;
	    	} else if ("BOTH".equals(searchdirs)) {
    		    log.debug("BOTH");
                searchdir = MultiRelations.SEARCH_BOTH;
    		} else if ("ALL".equals(searchdirs)) {
    		    log.debug("ALL");
                searchdir = MultiRelations.SEARCH_ALL;
		    }
		}
		

		tagger.setValue("ITEMS",""+fields.size());				

		hash=calcHashMultiLevel(tagger);
		results=(Vector)multilevel_cache.get(hash);
	
		if (results==null || reload) {
			if (cachedebug) {
				if (reload) {
					log.debug("doMultiLevel cache RELOAD "+hash);
				} else {
					log.debug("doMultiLevel cache MISS "+hash);
				}
			}
	        MultiRelations bul=(MultiRelations)mmb.getMMObject("multirelations");
			long begin=(long)System.currentTimeMillis(),len;
			
	
			// strip the fields of their function codes so we can query the needed
			// fields (teasers.number,shorted(episodes.title)
			Vector cleanfields=removeFunctions(fields);
			// now we have (teasers.number,episodes.title);
	
			if (dbdir==null) {
				dbdir=new Vector();
				dbdir.addElement("UP"); // UP == ASC , DOWN =DESC
			}
			nodes=bul.searchMultiLevelVector(snodes,cleanfields,distinct,type,where,dbsort,dbdir,searchdir);
			results=new Vector();
			for (e=nodes.elements();e.hasMoreElements();) {
				node=(MMObjectNode)e.nextElement();
				for (f=fields.elements();f.hasMoreElements();) {
					// hack hack this is way silly, StringTagger needs to be fixed
					fieldname=Strip.DoubleQuote((String)f.nextElement(),Strip.BOTH);
					if (fieldname.indexOf('(')>=0) {
						result=""+node.getValue(fieldname);
					} else {
						result=getNodeStringValue(node,fieldname);
					}
					if (result!=null && !result.equals("null")) {
	    				results.addElement(result); 
					} else {
	    				results.addElement(""); 
					}
				}
			}
	
			multilevel_cache.put(hash,results);
			long end=(long)System.currentTimeMillis();
			len=(end-begin);
			if (len>200) {
				log.debug("doMultilevel("+type+")="+(len)+" ms URI for page("+sp.req_line+")");
			}
		} else {
			if (cachedebug) log.debug("doMultiLevel cache HIT  "+hash);
		}
		return(results);
	}

	/**
	 * Belongs to doMultiLevel
	 */
	private Integer calcHashMultiLevel(StringTagger tagger) {
		int hash=1;
		Object obj;

		obj=tagger.Values("TYPE");
	    hash = 31*hash + (obj==null ? 0 : obj.hashCode());
		obj=tagger.Values("DBSORT");
	    hash = 31*hash + (obj==null ? 0 : obj.hashCode());
		obj=tagger.Values("DBDIR");
	    hash = 31*hash + (obj==null ? 0 : obj.hashCode());
		obj=tagger.Value("WHERE");
	    hash = 31*hash + (obj==null ? 0 : obj.hashCode());
		obj=tagger.Values("FIELDS");
	    hash = 31*hash + (obj==null ? 0 : obj.hashCode());
		obj=tagger.Values("NODE");
	    hash = 31*hash + (obj==null ? 0 : obj.hashCode());
		obj=tagger.Value("DISTINCT");
	    hash = 31*hash + (obj==null ? 0 : obj.hashCode());
		obj=tagger.Value("SEARCH");
	    hash = 31*hash + (obj==null ? 0 : obj.hashCode());

		return(new Integer(hash));
	}


	public String doBuilderReplace(scanpage sp,StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String type=tok.nextToken();
			MMObjectBuilder bul=mmb.getMMObject(type);
			if (bul!=null) {
				return(bul.replace(sp,tok));
			}
		}	
		return(null);
	}


	public Vector doBuilder(scanpage sp,StringTagger tagger, StringTokenizer tok) throws ParseException {
		if (tok.hasMoreTokens()) {
			String type=tok.nextToken();
			MMObjectBuilder bul=mmb.getMMObject(type);
			if (bul!=null) {
				return(bul.getList(sp,tagger,tok));
			}
		}	
		return(null);
	}

	private boolean getReload(scanpage sp,StringTagger tagger) {
		boolean rtn=false;
		boolean done=false;
		String memcache;
		if (tagger!=null) {
			memcache=tagger.Value("MEMCACHE");
			if (memcache!=null && memcache.equals("NO")) {
				rtn=true;
				done=true;
			}
		}
		if (!done && sessions!=null) {
			sessionInfo session=sessions.getSession(sp,sp.sname);
			if (session!=null) {
				rtn=sp.reload;
			}
			// When pagemaster calls set the reload on true
			if (sp.wantCache!=null && sp.wantCache.equals("PAGE")) {
				rtn=true;
			}
		} else {
			log.debug("getReload no session module loaded ? ");
		}
		return rtn;
	}

	public sessionInfo getPageSession(scanpage sp) {
		if (sessions!=null) {
			// org.mmbase sessionInfo session=sessions.getSession(rq,rq.getSessionName());
			//sessionInfo session=sessions.getSession(sp.req,"james/1234");
			if( sp.sname == null || sp.sname.equals("")) {
				sp.sname = "james/1234";
			}
			sessionInfo session=sessions.getSession(sp,sp.sname);
			String cachetype=session.getValue("CACHE");
			if (cachetype!=null && cachetype.equals("PAGE")) {
				// return(session);
			}
		}
		return(null);
	}

	public void stop()
	{

	}

	String getObjectField(StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String result=null;
			String nodeNr=tok.nextToken();
			String fieldname=tok.nextToken();
			MMObjectBuilder bul=mmb.getMMObject("fielddef");
			MMObjectNode node=bul.getAliasedNode(nodeNr);

			result=getNodeStringValue(node,fieldname);
			if (result!=null && !result.equals("null")) {
				return(result);
			} else {
				return("");
			}
		}
		return("no command defined");
	}


	public String doObjects(StringTagger tagger) {
		Object tmp;
		String result=null;
		MMObjectNode node;
		String results=""; 
		String type=tagger.Value("TYPE");
		String where=tagger.Value("WHERE");
		String dbsort=tagger.Value("DBSORT");
		String dbdir=tagger.Value("DBDIR");
		//log.debug("TYPE="+type);
		MMObjectBuilder bul=mmb.getMMObject(type);
		long begin=(long)System.currentTimeMillis(),len;
		Enumeration e=null;
		if (dbsort==null) {
			e=bul.search(where);
		} else {
			if (dbdir==null) {
				e=bul.search(where,dbsort);
			} else {
				if (dbdir.equals("DOWN")) {
					e=bul.search(where,dbsort,false);
				} else {
					e=bul.search(where,dbsort,true);
				}
			}
		}

		for (;e.hasMoreElements();) {
			node=(MMObjectNode)e.nextElement();
			Enumeration f=tagger.Values("FIELDS").elements();
			for (;f.hasMoreElements();) {
				// hack hack this is way silly Strip needs to be fixed
				String fieldname=Strip.DoubleQuote((String)f.nextElement(),Strip.BOTH);
				result=getNodeStringValue(node,fieldname);
				if (result!=null && !result.equals("null")) {
    				results+=" "+result; 
				} else {
					// this is weird
				}
			}
			results+="\n";
		}
		long end=(long)System.currentTimeMillis();
		//log.debug("MMbase -> doObject ("+type+")="+(end-begin)+" ms");
		return(results);
	}

	private Vector removeFunctions(Vector fields) {
		Vector results=new Vector();
		String fieldname,prefix;
		int posdot,posarc,posunder,pos;
		Enumeration f=fields.elements();
		for (;f.hasMoreElements();) {
			fieldname=Strip.DoubleQuote((String)f.nextElement(),Strip.BOTH);
			// get the first part (Example : episodes.);
			// we got two styles:
			// episodes.html_body
			// html(episodes.body)
			prefix="";
			posarc=fieldname.indexOf('(');
			if (posarc!=-1) {
				pos=fieldname.indexOf(')');
				results.addElement(fieldname.substring(posarc+1,pos));
			} else {
				posdot=fieldname.indexOf('.');
				if (posdot!=-1) {
					prefix=fieldname.substring(0,posdot+1);	
					fieldname=fieldname.substring(posdot+1);
				}
				posunder=fieldname.indexOf('_');
				if (posunder!=-1) {
					results.addElement(prefix+fieldname.substring(posunder+1));
				} else {
					results.addElement(prefix+fieldname);
				}
			}
		}
		return(results);
	}

	public String getSearchAge(StringTokenizer tok) {
		String builder=tok.nextToken();
		log.debug("getSearchAge(): BUILDER="+builder);
		MMObjectBuilder bul=(MMObjectBuilder)mmb.getMMObject(builder);
		if (bul!=null) {
			return(bul.getSearchAge());
		} else {
			return("30");
		}	
	}

}
