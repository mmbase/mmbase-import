/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.module.gui.html;

import java.util.*;
import java.io.*;

import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.Jumpers;
import org.mmbase.module.builders.MultiRelations;
import org.mmbase.storage.search.RelationStep;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * The module which provides access to the multimedia database
 * it creates, deletes and gives you methods to keep track of
 * multimedia objects. It does not give you direct methods for
 * inserting and reading them thats done by other objects
 *
 * @author Daniel Ockeloen
 * @version $Id: HtmlBase.java,v 1.49 2004-02-23 19:01:01 pierre Exp $
 */
public class HtmlBase extends ProcessorModule {
    /**
     * Logging instance
     */
    private static Logger log = Logging.getLoggerInstance(HtmlBase.class.getName());

    sessionsInterface sessions;
    boolean scancache=false;
    MMBase mmb=null;

    // should use org.mmbase.cache!
    private int multilevel_cachesize=300;
    private MultilevelCacheHandler multilevel_cache;

    public void init() {
        scancache tmp=(scancache)getModule("SCANCACHE");

        if (tmp!=null && tmp.getStatus()) scancache=true;

        mmb=(MMBase)getModule("MMBASEROOT");
        sessions=(sessionsInterface)getModule("SESSION");

        // get size from properties
        multilevel_cache=new MultilevelCacheHandler(mmb,multilevel_cachesize);
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
            if (cmd.equals("OBJECTS")) return doObjects(sp,tagger);
            if (cmd.equals("RELATIONS")) return doRelations(sp,tagger);
            if (cmd.equals("MULTILEVEL")) return doMultiLevel(sp,tagger);
            if (cmd.equals("MULTI")) return doMultiLevel(sp,tagger);
            if (cmd.equals("BUILDER")) return doBuilder(sp,tagger,tok);
        }
        return null;
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
                result=node.getStringValue(fieldname);

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
        return results;
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
        return results;
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
                return "wrong source node";
            }
        } else {
            return "missing source node";
        }


        //obtain destination number
        if (tok.hasMoreTokens()) {
            try {
                dnumber=Integer.parseInt(tok.nextToken());
            } catch (Exception e) {
                return "wrong destination node";
            }
        } else {
            return "missing destination node";
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
                            result=dnode.getStringValue(fieldname);
                            if (result!=null && !result.equals("null")) {
                                return result;
                            } else {
                                return "";
                            }
                        }
                    }
                }
            } else {
                return "wrong source node";
            }
        } else {
            return "missing fieldname";
        }
        return "";
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
                return "wrong source node";
            }
        } else {
            return "missing source node";
        }

        // obtain possible builder if not defined it will return the total count
        if (tok.hasMoreTokens()) {
            bulname=tok.nextToken();
        }

        MMObjectNode snode=bul.getNode(""+snumber);
        if (snode!=null) {
            if (bulname==null) {
                return ""+snode.getRelationCount();
            } else {
                return ""+snode.getRelationCount(bulname);
            }
        } else {
            return "0";
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
                return "wrong source node";
            }
        } else {
            return "missing source node";
        }

        //obtain destination number
        if (tok.hasMoreTokens()) {
            try {
                dnumber=Integer.parseInt(tok.nextToken());
            } catch (Exception e) {
                return "wrong destination node";
            }
        } else {
            return "missing destination node";
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
                            result=dnode.getStringValue(fieldname);
                            if (result!=null && !result.equals("null")) {
                                return result;
                            } else {
                                return "";
                            }
                        }
                    }
                }
            } else {
                return "wrong source node";
            }
        } else {
            return "missing fieldname";
        }
        return "";
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
                // use StringValue instead?
                tmp=node.getValue(tok.nextToken());
                if (tmp!=null && !tmp.equals("null")) {
                    results.addElement(""+tmp);
                } else {
                    results.addElement("");
                }
            }
        } catch(Exception g) {
            return null;
        }
        if (results.size()>0) {
            return results;
        } else {
            return null;
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
                //                InsRel.deleteNodeCache();
                //                InsRel.deleteRelationCache();

            }
        }
        return false;
    }

    /**
     *	Handle a $MOD command
     */
    public String replace(scanpage sp, String cmds) {
        StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("FIELD")) {
                return getObjectField(sp,tok);
            } else if (cmd.equals("GETVALUE")) {
                return getBuilderValue(sp,tok);
            } else if (cmd.equals("PROPERTY")) {
                return getObjectProperty(sp,tok);
            } else if (cmd.equals("OTYPE")) {
                return getObjectType(sp,tok);
            } else if (cmd.equals("TYPENAME")) {
                return getObjectTypeName(sp,tok);
            } else if (cmd.equals("GUIINDICATOR")) {
                return getGuiIndicator(sp,tok);
            } else if (cmd.equals("RELATION")) {
                Vector result=doRelations_replace(sp,tok);
                if (result!=null) return (String)result.elementAt(0);
                return "";
            } else if (cmd.equals("GETRELATIONCOUNT")) {
                return doGetRelationCount(sp,tok);
            } else if (cmd.equals("GETRELATIONVALUE")) {
                return doGetRelationValue(sp,tok);
            } else if (cmd.equals("SETRELATIONVALUE")) {
                return doSetRelationValue(sp,tok);
            } else if (cmd.equals("GETAUTHTYPE")) {
                return mmb.getAuthType();
            } else if (cmd.equals("GETSEARCHAGE")) {
                return getSearchAge(tok);
            } else if (cmd.equals("CACHE")) {
                return ""+doCache(sp,tok);
            } else if (cmd.equals("GETDAYMARKER")) {
                return mmb.doGetAgeMarker(tok);
                // org.mmbase } else if (cmd.equals("FILEINFO")) {
                // org.mmbase		return (doFile(rq, tok));
            } else if (cmd.equals("BUILDER")) {
                return doBuilderReplace(sp, tok);
            } else if (cmd.equals("BUILDERACTIVE")) {
                return isBuilderActive(tok);
            } else if (cmd.equals("GETJUMP")) {
                Jumpers bul=(Jumpers)mmb.getMMObject("jumpers");
                String url=bul.getJump(tok);
                if (url.startsWith("http://")) {
                    return url;
                } else {
                    return "";
                }
            } else if (cmd.equals("GETNUMBER")) {
                // Get the number for a alias
                return ""+mmb.getOAlias().getNumber(tok.nextToken());
            } else if (cmd.equals("FIELDLENGTH")) {
                String s = getObjectField(sp,tok);
                if (s==null)
                    return "0";
                else
                    return ""+s.length();
            }
        }
        return "No command defined";
    }

    String doCache(scanpage sp, StringTokenizer tok) {
        String result="";
        String cmd=tok.nextToken();
        if (cmd.equals("SIZE")) {
            if (tok.hasMoreTokens()) {
                String type=tok.nextToken();
                MMObjectBuilder bul=mmb.getMMObject("fielddef");
                result=""+bul.getCacheSize(type);
            } else {
                MMObjectBuilder bul=mmb.getMMObject("fielddef");
                result=""+bul.getCacheSize();
            }
        }
        return result;
    }

    String getObjectType(scanpage sp, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String number=tok.nextToken();
            MMObjectNode node=mmb.getTypeDef().getNode(number);
            return mmb.getTypeDef().getValue(node.getIntValue("otype"));
        }
        return "unknown";
    }

    String getObjectTypeName(scanpage sp, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String number=tok.nextToken();
            MMObjectNode node=mmb.getTypeDef().getNode(number);
            return node.getName();
        }
        return "unknown";
    }

    String getGuiIndicator(scanpage sp, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String number=tok.nextToken();
            MMObjectNode node=mmb.getTypeDef().getNode(number);
            return node.getGUIIndicator();
        }
        return "unknown";
    }

    String getBuilderValue(scanpage sp, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String number=tok.nextToken();
            String field="number";
            if (tok.hasMoreTokens()) field=tok.nextToken();
            MMObjectNode node=mmb.getTypeDef().getNode(number);
            return ""+node.getValue(field);
        }
        return "";
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
                    return result;
                } else  {
                    if (node!=null) {
                        result=node.getStringValue(fieldname);
                    }
                    if (result!=null && !result.equals("null")) {
                        return result;
                    } else {
                        return "";
                    }
                }
            } else log.error("getObjectField(): no token fieldname found, nodenr("+nodeNr+"), url("+sp.getUrl()+")");
        } else log.error("getObjectField(): no token nodenr found, url("+sp.getUrl()+")");
        return "no command defined";
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
                return pnode.getStringValue("value");
            } else {
                return "";
            }
        }
        return "no command defined";
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
        return results;
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
        return inlist.toString();
    }

    private String getFile(String file) {
        String results=null;
        byte[] buffer=getFileBytes(file);
        if (buffer!=null) {
            results=new String(buffer);
        }
        return results;
    }

    private byte[] getFileBytes(String file) {
        File scanfile;
        int filesize,len=0;
        byte[] buffer;
        FileInputStream scan;

        scanfile = new File(file);
        filesize = (int)scanfile.length();
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
        int searchdir = RelationStep.DIRECTIONS_EITHER;
        if (searchdirs!=null) {
            searchdirs = searchdirs.toUpperCase();
            if ("DESTINATION".equals(searchdirs)) {
                log.debug("DESTINATION");
                searchdir = RelationStep.DIRECTIONS_DESTINATION;
            } else if ("SOURCE".equals(searchdirs)) {
                log.debug("SOURCE");
                searchdir = RelationStep.DIRECTIONS_SOURCE;
            } else if ("BOTH".equals(searchdirs)) {
                log.debug("BOTH");
                searchdir = RelationStep.DIRECTIONS_BOTH;
            } else if ("ALL".equals(searchdirs)) {
                log.debug("ALL");
                searchdir = RelationStep.DIRECTIONS_ALL;
            }
        }

        tagger.setValue("ITEMS",""+fields.size());

        hash=calcHashMultiLevel(tagger);
        results=(Vector)multilevel_cache.get(hash);

        //if (results==null || reload) {
        if (results==null) {

            if (reload) {
                log.debug("doMultiLevel cache RELOAD "+hash);
            } else {
                log.debug("doMultiLevel cache MISS "+hash);
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
            if(nodes==null) {
                nodes = new Vector();
            }
            results=new Vector();
            for (e=nodes.elements();e.hasMoreElements();) {
                node=(MMObjectNode)e.nextElement();
                for (f=fields.elements();f.hasMoreElements();) {
                    // hack hack this is way silly, StringTagger needs to be fixed
                    fieldname=Strip.DoubleQuote((String)f.nextElement(),Strip.BOTH);
                    if (fieldname.indexOf('(')>=0) {
                        result=""+node.getValue(fieldname);
                    } else {
                        result=node.getStringValue(fieldname);
                    }
                    if (result!=null && !result.equals("null")) {
                        results.addElement(result);
                    } else {
                        results.addElement("");
                    }
                }
            }

            multilevel_cache.put(hash,results,type,tagger);
            long end=(long)System.currentTimeMillis();
            len=(end-begin);
            if (len>200) {
                log.debug("doMultilevel("+type+")="+(len)+" ms URI for page("+sp.req_line+")");
            }
        } else {
            log.debug("doMultiLevel cache HIT  "+hash);
        }
        return results;
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

        return new Integer(hash);
    }

    public String doBuilderReplace(scanpage sp,StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String type=tok.nextToken();
            MMObjectBuilder bul=mmb.getMMObject(type);
            if (bul!=null) {
                return bul.replace(sp,tok);
            }
        }
        return null;
    }

    /**
     * Returns whether a builder is active.
     * @param tok tokenized command, should contain the builder name
     * @return <code>TRUE</code> if the builder is active, <code>FALSE</code> otherwise
     */
    public String isBuilderActive(StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String type=tok.nextToken();
            MMObjectBuilder bul=mmb.getMMObject(type);
            if (bul!=null) {
                return "TRUE";
            }
        }
        return "FALSE";
    }

    public Vector doBuilder(scanpage sp,StringTagger tagger, StringTokenizer tok) throws ParseException {
        if (tok.hasMoreTokens()) {
            String type=tok.nextToken();
            MMObjectBuilder bul=mmb.getMMObject(type);
            if (bul!=null) {
                return bul.getList(sp,tagger,tok);
            }
        }
        return null;
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

    /**
     * @vpro refers to 'James'
     * @deprecated always returns null, do not use.
     */
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
                // return session;
            }
        }
        return null;
    }

    public void stop() {}

    String getObjectField(StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String result=null;
            String nodeNr=tok.nextToken();
            String fieldname=tok.nextToken();
            MMObjectBuilder bul=mmb.getMMObject("fielddef");
            MMObjectNode node=bul.getAliasedNode(nodeNr);
            if (node!=null) {
                result=node.getStringValue(fieldname);
            }
            if (result!=null && !result.equals("null")) {
                return result;
            } else {
                return "";
            }
        }
        return "no command defined";
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
                result=node.getStringValue(fieldname);
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
        return results;
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
                String fieldname2 = fieldname.substring(posarc+1,pos);
                if (fieldname2 != null && fieldname2.length() > 0) {
                    results.addElement(fieldname2);
                }
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
        return results;
    }

    public String getSearchAge(StringTokenizer tok) {
        String builder=tok.nextToken();
        log.debug("getSearchAge(): BUILDER="+builder);
        MMObjectBuilder bul=(MMObjectBuilder)mmb.getMMObject(builder);
        if (bul!=null) {
            return bul.getSearchAge();
        } else {
            return "30"; // ???
        }
    }

    public MultilevelCacheHandler getMultilevelCacheHandler() {
        return multilevel_cache;
    }

}
