/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/

/* 
	$Id: HtmlBase.java,v 1.5 2000-03-09 13:10:40 wwwtech Exp $

	$Log: not supported by cvs2svn $
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
 * @version $Id: HtmlBase.java,v 1.5 2000-03-09 13:10:40 wwwtech Exp $
 */
public class HtmlBase extends ProcessorModule {

	private String classname = getClass().getName();
	private boolean debug = true;
	private void debug( String msg ) { System.out.println( classname +":"+ msg ); } 
	private int multilevel_cachesize=128;
	private LRUHashtable multilevel_cache;

	MMBase mmb=null;

	private SendMailInterface sendmail;
	public MMBaseMultiCast mmc;
	int delay;
	boolean	nodecachesdone=false;
	MMBaseProbe probe;

	JDBCInterface jdbc;
	public String baseName="def1";
	RelDef RelDef;
	MMObjectBuilder MMObjectBuilder;
	OAlias OAlias;
	FieldDef FieldDef;
	InsRel InsRel;
	TypeRel TypeRel;
	private String dtdbase="http://www.mmbase.org";
	MMJdbc2NodeInterface database;
	String databasename;

	public Hashtable mmobjs=new Hashtable();
	String machineName="unknown";
	sessionsInterface sessions;

	Vector onlineVector=null;

	String SyncNodes="NO";

	public void init() {
		mmb=(MMBase)getModule("MMBASEROOT");		
		debug("init(): mmbase="+mmb);
		// is there a basename defined in MMBASE.properties ?
		sessions=(sessionsInterface)getModule("SESSION");		

		// get size from properties
		multilevel_cache=new LRUHashtable(multilevel_cachesize);
	}


	/**
	 */
	public HtmlBase() {
		debug("HtmlBase(): constructed");
	}

	/**
	 * Generate a list of values from a command to the processor
	 */
	 public Vector  getList(scanpage sp,StringTagger tagger, String value) {
    	String line = Strip.DoubleQuote(value,Strip.BOTH);
		StringTokenizer tok = new StringTokenizer(line,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("OBJECTS")) return(doObjects(sp,tagger));
			if (cmd.equals("RELATIONS")) return(doRelations(sp,tagger));
			if (cmd.equals("MULTILEVEL")) return(doMultiLevel(sp,tagger));
			if (cmd.equals("MULTI")) return(doMultiLevel(sp,tagger));

			if (cmd.equals("TSEARCH")) {
				// should be removed soon !
				Teasers bul=(Teasers)mmb.getMMObject("teasers");
				if (bul!=null) {
					return(bul.doTSearch(sp,tagger));
				} else {
					debug("getList(): can't use teasers");
					return(null);
				}
			}
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
				// hack hack this is way silly Strip needs to be fixed

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
		if (debug) debug("doObjects("+type+")="+(end-begin)+" ms");
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
		int snode=-1;
		int onode=-1;
		Vector results=new Vector(); 
		Vector wherevector=null;
		String type=tagger.Value("TYPE");
		String dbsort=tagger.Value("DBSORT");
		String where=tagger.Value("WHERE");


		long begin=(long)System.currentTimeMillis();

		MMObjectBuilder bul=null;
		try {

			snode=Integer.parseInt(tagger.Value("NODE"));
			int otype=mmb.TypeDef.getIntValue(type);
			bul=mmb.getMMObject(type);
//			debug("2="+type+" "+bul);
			Enumeration e=null;


			// how do we smartly sort this
			if (dbsort==null) {
				MMObjectNode node2=bul.getNode(snode);
				e=node2.getRelations();
			} else {
				// e=InsRel.getRelations(snode);
				MMObjectNode node2=bul.getNode(snode);
				e=node2.getRelations();
			}
			if (where!=null) {
				wherevector=bul.searchNumbers(where);
			}

			for (;e.hasMoreElements();) {
				node=(MMObjectNode)e.nextElement();
	
				// oke find out who is the other
				onode=node.getIntValue("snumber");	
				if (snode==onode) {
					onode=node.getIntValue("dnumber");
				}
				// so we found the other one get that node
				node=bul.getNode(onode);
				// if node not null than we have a match
				if (node!=null && node.getIntValue("otype")==otype && (where==null || wherevector.contains(new Integer(node.getIntValue("number"))))) {
					Enumeration f=tagger.Values("FIELDS").elements();
					for (;f.hasMoreElements();) {
						// hack hack this is way silly Strip needs to be fixed
						tmp=node.getValue(Strip.DoubleQuote((String)f.nextElement(),Strip.BOTH));
						if (tmp!=null && !tmp.equals("null")) {
   		 				results.addElement(""+tmp); 
						} else {
   		 				results.addElement(""); 
						}
					}
				}
			}
			tagger.setValue("ITEMS",""+tagger.Values("FIELDS").size());
		} catch(Exception g) {
			if (bul==null) {
				debug("doRealations(): ERROR: asking relations on a unkown builder : "+type);
			} else {
				g.printStackTrace();
			}
		}
		long end=(long)System.currentTimeMillis();
		return(results);
	}


	public String doGetRelationValue(scanpage sp, StringTokenizer tok) {
		Teasers Teasers=(Teasers)mmb.getMMObject("teasers");

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
			debug("S="+snumber+" D="+dnumber+" F="+fieldname);
			MMObjectNode snode=Teasers.getNode(""+snumber);
			if (snode!=null) {
				for (Enumeration e=snode.getRelations();e.hasMoreElements();) {
					MMObjectNode inode=(MMObjectNode)e.nextElement();
					debug("I="+inode);
					int s=inode.getIntValue("snumber");
					int d=inode.getIntValue("dnumber");
					if (d==dnumber || s==dnumber) {
						String result="";
						int n=inode.getIntValue("number");
						MMObjectNode dnode=(MMObjectNode)Teasers.getNode(""+n);
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

		Teasers Teasers=(Teasers)mmb.getMMObject("teasers");
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

		MMObjectNode snode=Teasers.getNode(""+snumber);
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

		Teasers Teasers=(Teasers)mmb.getMMObject("teasers");
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
			debug("S="+snumber+" D="+dnumber+" F="+fieldname);
			MMObjectNode snode=Teasers.getNode(""+snumber);
			if (snode!=null) {
				for (Enumeration e=snode.getRelations();e.hasMoreElements();) {
					MMObjectNode inode=(MMObjectNode)e.nextElement();
					debug("I="+inode);
					int s=inode.getIntValue("snumber");
					int d=inode.getIntValue("dnumber");
					if (d==dnumber || s==dnumber) {
						String result="";
						int n=inode.getIntValue("number");
						MMObjectNode dnode=(MMObjectNode)Teasers.getNode(""+n);
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
		int snode=-1;
		int onode=-1;
		Vector results=new Vector(); 
		String type=tok.nextToken();

		long begin=(long)System.currentTimeMillis();

		try {
			snode=Integer.parseInt(tok.nextToken());
			int otype=mmb.TypeDef.getIntValue(type);
		MMObjectBuilder bul=mmb.getMMObject(type);
		Enumeration e=null;
		// how do we smartly sort this
		MMObjectNode node2=bul.getNode(snode);
		e=node2.getRelations();
		for (;e.hasMoreElements();) {
			node=(MMObjectNode)e.nextElement();

			// oke find out who is the other
			onode=node.getIntValue("snumber");	
			if (snode==onode) {
				onode=node.getIntValue("dnumber");
			}
			// so we found the other one get that node
			node=bul.getNode(onode);
			// if node not null than we have a match
			if (node!=null && node.getIntValue("otype")==otype) {
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
		}
		} catch(Exception g) {
		   return(null);
		}
		long end=(long)System.currentTimeMillis();
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
				debug("process(): DELETE ON CACHES");
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
			} else if (cmd.equals("PROPERTY")) { 
				return(getObjectProperty(sp,tok));
			} else if (cmd.equals("OTYPE")) { 
				return(getObjectType(sp,tok));
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
			} else if (cmd.equals("TSEARCH")) {
				// should be removed soon !
				Teasers bul=(Teasers)mmb.getMMObject("teasers");
				if (bul!=null) {
					return(""+bul.doTSearchSize(sp,tok));
				} else {
					debug("MMBASE -> can't use teasers");
					return(null);
				}
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


	String getGuiIndicator(scanpage sp, StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String number=tok.nextToken();
			MMObjectNode node=mmb.getTypeDef().getNode(number);
			return(node.getGUIIndicator());
		}
		return("unknown");
	}

	String getObjectField(scanpage sp, StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String nodeNr=tok.nextToken();
			String fieldname=tok.nextToken();
			String result=null;
			MMObjectBuilder bul=mmb.getMMObject("fielddef");
			MMObjectNode node=bul.getNode(nodeNr);
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
		}
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

	public Vector doMultiLevel(scanpage sp, StringTagger tagger) {
		String result=null,fieldname;
		Object tmp;
		MMObjectNode node;
		int snode=-1,onode=-1;
		Integer hash;
		Vector results=null,nodes,wherevector=null;
		Enumeration e,f;
		boolean reload=getReload(sp);

		Vector type=tagger.Values("TYPE");
		Vector dbsort=tagger.Values("DBSORT");
		Vector dbdir=tagger.Values("DBDIR");
		String where=tagger.Value("WHERE");
		Vector fields=tagger.Values("FIELDS");
		Vector snodes=tagger.Values("NODE");
		String distinct=tagger.Value("DISTINCT");

		tagger.setValue("ITEMS",""+fields.size());

		hash=calcHashMultiLevel(tagger);
		results=(Vector)multilevel_cache.get(hash);
	
		if (results==null || reload) {
			if (debug) {
				if (reload) {
					debug("doMultiLevel cache RELOAD "+hash);
				} else {
					debug("doMultiLevel cache MISS "+hash);
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
	
			nodes=bul.searchMultiLevelVector(snodes,cleanfields,distinct,type,where,dbsort,dbdir);
			results=new Vector();
			for (e=nodes.elements();e.hasMoreElements();) {
				node=(MMObjectNode)e.nextElement();
				for (f=fields.elements();f.hasMoreElements();) {
					// hack hack this is way silly, StringTagger needs to be fixed
					fieldname=Strip.DoubleQuote((String)f.nextElement(),Strip.BOTH);
					result=getNodeStringValue(node,fieldname);
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
				debug("doMultilevel("+type+")="+(len)+" ms URI for page("+sp.req_line+")");
			}
		} else {
			if (debug) debug("doMultiLevel cache HIT  "+hash);
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


	public Vector doBuilder(scanpage sp,StringTagger tagger, StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String type=tok.nextToken();
			MMObjectBuilder bul=mmb.getMMObject(type);
			if (bul!=null) {
				return(bul.getList(sp,tagger,tok));
			}
		}	
		return(null);
	}

	private boolean getReload(scanpage sp) {
		boolean rtn=false;
		if (sessions!=null) {
			if( sp.sname == null || sp.sname.equals("")) {
				sp.sname = "james/1234";
			}
			sessionInfo session=sessions.getSession(sp,sp.sname);
			String cachetype=session.getValue("CACHE");
			if (cachetype!=null && cachetype.equals("PAGE")) {
				rtn=true;
			}
			String reloadtype=session.getValue("RELOAD");
			if (reloadtype!=null && reloadtype.equals("R")) {
				rtn=true;
			}
		} else {
			debug("getReload no session module loaded ? ");
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
		//debug("TYPE="+type);
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
		//debug("MMbase -> doObject ("+type+")="+(end-begin)+" ms");
		return(results);
	}

	private Vector removeFunctions(Vector fields) {
		Vector results=new Vector();
		Enumeration f=fields.elements();
		for (;f.hasMoreElements();) {
			// hack hack this is way silly Strip needs to be fixed
			String fieldname=Strip.DoubleQuote((String)f.nextElement(),Strip.BOTH);
			// get the first part (Example : episodes.);
			String prefix="";
			int pos1=fieldname.indexOf('.');
			if (pos1!=-1) {
				prefix=fieldname.substring(0,pos1+1);	
			}
			pos1=fieldname.indexOf('(');
			if (pos1!=-1) {
				int pos2=fieldname.indexOf(')');
				results.addElement(fieldname.substring(pos1+1,pos2));
			} else {
				pos1=fieldname.indexOf('_');
				if (pos1!=-1) {
					results.addElement(prefix+fieldname.substring(pos1+1));
				} else {
					results.addElement(fieldname);
				}
			}
		}
//		debug("FIELDS="+results);
		return(results);
	}

	public String getSearchAge(StringTokenizer tok) {
		String builder=tok.nextToken();
		debug("getSeachAge(): BUILDER="+builder);
		MMObjectBuilder bul=(MMObjectBuilder)mmb.getMMObject(builder);
		if (bul!=null) {
			return(bul.getSearchAge());
		} else {
			return("30");
		}	
	}

}
