/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import java.util.*;
import java.text.DateFormat;
import java.io.*;

import javax.servlet.http.*;
import javax.servlet.*;

import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;
import org.mmbase.jumpers.Jumpers;
import org.mmbase.util.*;
import org.mmbase.bridge.*;
import org.mmbase.core.CoreField;

import org.mmbase.util.logging.*;

/**
 * The servdb servlet handles binary requests.
 * This includes images (img.db), realaudio (realaudio.db) but also xml (xml.db) and dtd's (dtd.db).
 * With servscan it provides the communication between the clients browser and the mmbase space.
 *
 * @rename Servdb
 * @deprecation-used
 * @deprecated use {@link org.mmbase.servlet.ImageServlet} or {@link org.mmbase.servlet.AttachmentServlet} instead
 * @version $Id: servdb.java,v 1.66 2008-03-25 21:00:24 nklasens Exp $
 * @author Daniel Ockeloen
 */
public class servdb extends JamesServlet {
    private static Logger log;
    /**
     * when set to true you can use yourhost/xml.db?objectnumber to get the XML representation of that object
     */
    private boolean provideXML = false;
    private		cacheInterface 		cache;
    private		filebuffer 			buffer;
    private		Hashtable 			Roots 		= new Hashtable();
    private 	sessionsInterface 	sessions;
    private String dtdbase = "http://www.mmbase.org";

    /**
     * Construct a servfile worker, it should be places in a worker
     * pool (by the admin thread).
     */
    public servdb() {
        super();
    }

    /**
     * @javadoc
     */
    public void init() throws ServletException {
        super.init();
        // Initializing log here because log4j has to be initialized first.
        log = Logging.getLoggerInstance(servdb.class);

        // associate explicit mapping
        // Needed because servdb explicitly tests on these maps
        associateMapping("images","/img.db",new Integer(10));
        associateMapping("attachments","/attachment.db",new Integer(10));
    }


    public void setMMBase(MMBase mmb) {
        super.setMMBase(mmb);

        String tmp = getInitParameter("DTDBASE");
        if (tmp != null && !tmp.equals("")) {
            dtdbase = tmp;
        }

        cache = (cacheInterface) getModule("cache");
        if (cache == null) {
            log.debug("Could not find cache module, proceeding without cache");
        }

        sessions = (sessionsInterface) getModule("SESSION");
        if (sessions == null) {
            log.debug("Could not find session module, proceeding without sessions");
        }
    }

    // utility method for converting strings to bytes
    private byte[] stringToBytes(String value) {
        try {
            return value.getBytes(mmbase.getEncoding());
        } catch (UnsupportedEncodingException e) {
            return value.getBytes();
        }
    }
    /**
     * @vpro
     * @javadoc
     */

    // perhaps this method can simply be doGet
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException {
        if (!checkInited(res)) {
            return;
        }

        Date lastmod;
        String templine,templine2;
        int filesize;

        incRefCount(req); // this is already done in service of MMBaseServlet,

        try {
            scanpage sp = new scanpage(this, req, res, sessions );

            boolean cacheReq=true;

            if (log.isDebugEnabled()) {
                String msg = "["+sp.getAddress();

                msg = msg + "]"+req.getRequestURI()+"?"+req.getQueryString();
                log.debug("service("+msg+")");
            }

            int len;
            boolean done=false;
            cacheline cline=null;
            long nowdate=0;
            // org.mmbase String mimetype=getContentType();
            String mimetype="image/jpeg";

            String req_line=req.getRequestURI();
            boolean	audio = (req_line.indexOf("rastream") != -1);

            //		debug("REQ_LINE="+req_line);
            // org.mmbase res.setKeepAlive(true);

            BufferedOutputStream out=null;
            try {
                out=new BufferedOutputStream(res.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }

            HttpPost poster=new HttpPost(req);

            // added to do enable Referer logging
            // ----------------------------------

            String ref=req.getHeader("Referer");
            //if (ref!=null && ref.indexOf("vpro.nl")==-1 && ref.indexOf(".58.169.")==-1) {
            if (ref!=null && ref.indexOf("vpro.nl")==-1 && ref.indexOf("vpro.omroep.nl")==-1 && ref.indexOf(".58.169.")==-1) {
                // second layer to make sure its valid/clean
                int pos=ref.indexOf("?");
                if (pos!=-1) {
                    // probably a search engine remove the keywords need to be
                    // counted in the future
                    ref=ref.substring(0,pos);
                }
                //debug("service(): Referer="+ref);
                if (ref.length()>70) ref=ref.substring(0,70);
                // org.mmbase if (stats!=null) stats.countSimpleEvent("Linked="+ref);
            }

            if (req_line.indexOf('#')!=-1) {
                req_line=req_line.substring(0,req_line.indexOf('#')-1);
            }
            templine2 = req.getHeader("Pragma");

            if (cache!=null && cache.get("www"+req.getRequestURI()+req.getQueryString())!=null && (templine2==null || templine2.indexOf("no-cache")==-1) && !audio ) {
                //			debug("service(): GRRR1");
                cline=cache.get("www"+req.getRequestURI()+req.getQueryString());
                filesize=cline.filesize;
                lastmod=cline.lastmod;
                mimetype=cline.mimetype;

                // are we sure we want to send the whole file ?

                templine = req.getHeader("If-Modified-Since");
                if (templine!=null) templine+=";"; // added for a netscape bug ?
                if (templine2==null) templine2=" ";
                try {
                    nowdate=DateFormat.getInstance().parse(templine.substring(0,templine.indexOf(';'))).getTime();
                } catch(Exception e) {
                    nowdate=(new Date(0)).getTime();
                }
                if (1==2 && templine!=null  && templine2.indexOf("no-cache")==-1 && !(lastmod.getTime()>nowdate)) {

                    // logAccess(304,""+cline.filesize);
                    res.setStatus(HttpServletResponse.SC_NOT_MODIFIED); // 304, "Not Modified"
                    res.setContentType(mimetype);
                    res.setContentLength(cline.filesize);
                    res.setHeader("Date",RFC1123.makeDate(new Date()));
                    res.setHeader("Last-modified",RFC1123.makeDate(lastmod));
                } else {
                    // logAccess(200,""+cline.filesize);
                    try {
                        if (cline.mimetype==null) {
                            res.setContentType("text/html");
                        } else {
                            res.setContentType(cline.mimetype);
                        }
                        res.setContentLength(cline.filesize);
                        res.setHeader("Date",RFC1123.makeDate(new Date()));
                        res.setHeader("Last-modified",RFC1123.makeDate(cline.lastmod));
                        out.write(cline.buffer,0,cline.filesize);
                        out.flush();
                        out.close();
                    } catch(Exception e) {
                        log.error("service(): ERROR: Error writing to socket() : " + e.toString());
                    }
                }
            } else {

                if (req_line.indexOf("..")!=-1) {
                    req_line="index.html";
                }

                // 2hoog hack
                if (req_line.indexOf("/www/")==0) {
                    req_line=req_line.substring(4);
                }
                if (req_line.indexOf("/htbin/scan/www/")==0) {
                    req_line=req_line.substring(15);
                }

                if (done==false) {
                    lastmod = new Date();
                    cline = new cacheline(0);
                    cline.lastmod=lastmod;
                    cline.mimetype="image/jpeg";
                    mimetype=cline.mimetype;
                    // try {
                    // hack for db  len=scan.read(cline.buffer,0,filesize);

                    if (req.getRequestURI().indexOf("img")!=-1) {
                        // ---
                        // img
                        // ---

                        boolean notANumber=false;
                        Vector params = getParamVector(req);
                        // Catch alias only images without parameters.
                        if (params.size()==1) {
                            try {
                                Integer.parseInt((String)params.elementAt(0));
                            } catch (NumberFormatException e) {
                                notANumber=true;
                            }
                        }

                        if (params.size() > 1 || notANumber) {
                            // template was included on URL
                            log.debug("Using a template, precaching this image");
                            // this is an image number + template, cache the image, and go ahead
                            // with the number of the cached image.
                            Images bul = (Images) mmbase.getMMObject("images");
                            String imageId = null;
                            StringBuffer template = new StringBuffer();
                            if (req.getQueryString()!=null) {
                                StringTokenizer tok=new StringTokenizer(req.getQueryString(),"+\n\r");
                                // rico
                                if(tok.hasMoreTokens()) {
                                    imageId = tok.nextToken();
                                    params.addElement(tok.nextToken());
                                }
                                while(tok.hasMoreTokens()) {
                                    template.append(tok.nextToken());
                                    if (tok.hasMoreTokens()) {
                                        template.append("+");
                                    }
                                }

                            }
                            int imageNumber = bul.getCachedNode(bul.getNode(imageId), template.toString()).getNumber();
                            if (imageNumber > 0) {
                                params.clear();
                                params.add(new Integer(imageNumber));
                            } else {
                                // Conversion failure.
                                log.error("Image not found " + params);
                                cacheReq=false;
                                params.clear();
                            }
                            if (log.isDebugEnabled()) log.debug("found image " + imageNumber);
                        }

                        if (params.size()>0) {
                            // good image
                            ImageCaches icaches = (ImageCaches) mmbase.getMMObject("icaches");
                            MMObjectNode node = icaches.getNode("" + params.get(0));
                            if (node == null) {
                                cline.buffer = null;
                            } else {
                                cline.buffer = node.getByteValue("handle");
                            }
                            cline.mimetype = icaches.getMimeType(node);
                            mimetype=cline.mimetype;
                        } else {
                            // return a broken image
                            cline.buffer=null;
                            cline.mimetype="image/gif";
                            mimetype=cline.mimetype;
                        }

                        // bugfix #6558 - fix for broken IE images in scan
                        if (mimetype.equals("image/jpeg") || mimetype.equals("image/jpg"))
                            cline.buffer = IECompatibleJpegInputStream.process(cline.buffer);

                        if (log.isDebugEnabled()) log.debug("servdb::service(img): The contenttype for this image is: "+mimetype);

                        // check point, plugin needed for mirror system
                        checkImgMirror(sp);
                    } else if (req.getRequestURI().indexOf("xml")!=-1) {
                        // ---
                        // xml
                        // ---
                        cline.buffer=getXML(getParamVector(req));
                        cline.mimetype="text/plain";
                        mimetype=cline.mimetype;
                    } else if (req.getRequestURI().indexOf("dtd")!=-1) {
                        // ---
                        // dtd
                        // ---
                        cline.buffer=getDTD(getParamVector(req));
                        cline.mimetype="text/html";
                        mimetype=cline.mimetype;
                    } else if (req.getRequestURI().indexOf("jump")!=-1) {
                        // do jumper
                        long begin=System.currentTimeMillis();
                        Jumpers bul=(Jumpers)mmbase.getMMObject("jumpers");
                        String key=(String)(getParamVector(req)).elementAt(0);
                        String url = bul.getJump(key);
                        log.debug("jump.db Url="+url);
                        if (url!=null) {
                            // jhash.put(key,url);
                            res.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);  // 302, "OK" ??
                            res.setContentType("text/html");
                            res.setHeader("Location",url);
                            Date d=new Date(0);
                            String dt=RFC1123.makeDate(d);
                            res.setHeader("Expires",dt);
                            res.setHeader("Last-Modified",dt);
                            res.setHeader("Date",dt);
                        }
                        long end=System.currentTimeMillis();
                        //debug("getUrl="+(end-begin)+" ms");

                    } else if (req.getRequestURI().indexOf("attachment")!=-1) {

                        // ---
                        // downloading attachment
                        //   cjr@dds.nl, July 27th 2000
                        // ---
                        cline.buffer=getAttachment(getParamVector(req));
                        cline.mimetype=getAttachmentMimeType(getParamVector(req));
                        mimetype=cline.mimetype;
                        String savefilename=getAttachmentFileName(getParamVector(req));
                        if (savefilename!=null) {
                            res.setHeader("Content-Disposition","attachment; filename=\""+savefilename+"\"");
                        }
                    } else  if (req.getRequestURI().indexOf("flash")!=-1) {
                        // flash
                        cline.buffer=getFlash(getParamVector(req));
                        cline.mimetype="application/x-shockwave-flash";
                        mimetype=cline.mimetype;
                    }

                    if (cline.buffer!=null) {
                        len=cline.buffer.length;
                        filesize=len;
                    } else {
                        len=0;
                        filesize=0;
                    }

                    if (len!=-1)  {
                        try {
                            res.setContentType(mimetype);
                            //res.setContentLength(filesize);
                            cline.filesize=filesize;
                            res.setHeader("Last-modified",RFC1123.makeDate(lastmod));
                            res.setHeader("Date",RFC1123.makeDate(new Date()));
                            res.setContentLength(cline.filesize);
                            out.write(cline.buffer,0,filesize);
                            out.flush();
                            out.close();

                            if(len>0 && cacheReq && (cache!=null))
                                cache.put("www"+req.getRequestURI()+req.getQueryString(),cline);
                        } catch(Exception e) {
                            log.error("Servfile : Error writing to socket:");
                            log.error(Logging.stackTrace(e));
                            len=-1;
                        }
                    }
                }
            }
        }
        finally {
            decRefCount(req);
        }
    }

    /**
     * @javadoc
     */
    boolean Show_Directory(String pathname,File dirfile, PrintWriter out) {
        String body,bfiles,bdirs,header;
        int i;

        String files[] = dirfile.list();
        bfiles="";
        bdirs="";

        body="<TITLE>James : Index of "+pathname+"</TITLE>";
        body+="<BODY BGCOLOR=\"#FFFFFF\">";
        body+="<h1>James : Index of "+pathname+"</h1>";
        if (pathname.lastIndexOf('/')!=-1) {
            bdirs+="<IMG SRC=\"/jamesdoc/images/back.gif\"><A HREF=\""+pathname.substring(0,pathname.lastIndexOf('/'))+"\" TARGET=\""+pathname+"\">Parent Directory</A>\n";

        }
        bdirs+="<HR>";

        // Luke Gorrie's fix, avoid URL's with '//' in them
        // So if the URL ends with '/' then strip that.
        i=pathname.length();
        if (pathname.charAt(i-1)=='/') pathname=pathname.substring(0,i-1);

        for (i=0;i<files.length;i++) {
            File theFile = new File(dirfile,files[i]);
            if (theFile.isDirectory()) {
                bdirs+="<TR><TD><IMG SRC=\"/jamesdoc/images/dir.gif\"></TD><TD><A HREF=\""+pathname+"/"+files[i]+"\">"+files[i]+"/</A></TD>";
                bdirs+="<TD>"+(new Date(theFile.lastModified())).toString();
                bdirs+="</TD><TD>-</TD></TR>\n";
            } else {
                bfiles+="<TR><TD><IMG SRC=\"/jamesdoc/images/text.gif\"></TD><TD><A HREF=\""+pathname+"/"+files[i]+"\">"+files[i]+"</A></TD><TD>"+(new Date(theFile.lastModified())).toString()+"</TD><TD>"+(theFile.length()/1024)+" Kb</TD></TR>";
            }
        }

        body+="<TABLE WIDH=100%>";
        if (files.length!=0) {
            body+="<TR><TD><B>Type</B></TD><TD><B>Name</B></TD><TD><B>Last-modified</B></TD><TD><B>Size</B></TD>";
        } else {
            body+="<TR><TD>No Files Found</TD>";
        }
        body+=bdirs+bfiles;
        body+="</TABLE>";
        header="HTTP/1.0 200 OK\nMIME-Version: 1.0\nServer: James/utils\nContent-type: text/html\nContent-Length: "+body.length()+"\n\n";

        out.print(header+body);
        return true;
    }

    /**
     * @javadoc
     */
    public String getServletInfo()  {
        return "ServFile handles normal file requests, Daniel Ockeloen";
    }

    /**
     * @javadoc
     */
    Hashtable getRoots() {
        int pos;
        String tmp,tmp2;
        Hashtable result=new Hashtable();
        //result.put("www",DocumentRoot);

        for (Enumeration e=getInitParameters().keys();e.hasMoreElements();) {
            tmp=(String)e.nextElement();
            tmp2=getInitParameter(tmp);
            pos=tmp.indexOf("Root.");
            if (pos==0) {
                result.put(tmp.substring(5),tmp2);
            }
        }
        return result;
    }

    /**
     * @javadoc
     */
    private sessionInfo getSession(scanpage sp) {
        if (sessions==null)
            return null;
        else
            return sessions.getSession(sp,sp.sname);
    }

    /**
     * @javadoc
     */
    public Vector filterSessionMods(scanpage sp,Vector params,HttpServletResponse res) {
        sessionInfo session=getSession(sp);
        if (session!=null) {
            int pos1;
            String line;
            Enumeration e=params.elements();
            if (e.hasMoreElements()) {
                line=(String)e.nextElement();
                log.debug("filterSessionMods(): line("+line+")");
                pos1=line.indexOf("(SESSION-");

                if (pos1!=-1) {
                    int pos2=line.indexOf(")");
                    String part1=line.substring(0,pos1);
                    String part2=line.substring(pos1+9,pos2);
                    log.debug("servdb -> REPLACE="+part1+" "+part2);

                    String value=sessions.replace(sp,part2);

                    //String value=null;
                    log.debug("servdb -> REPLACE2="+value);
                    if (value==null) {
                        value="";
                    }
                    params.removeElement(line);
                    params.addElement(part1+"("+value+")");
                }
            }
        } else
            log.error("filterSessionMods(): ERROR: session is null!");

        return params;
    }

    /**
     * @javadoc
     */
    public Vector checkSessionJingle(scanpage sp,Vector params,HttpServletResponse res) {
        sessionInfo session=getSession(sp);
        boolean havesession=false,havesbj=false;
        String str="";
        int i=0;

        for (Enumeration e=params.elements();e.hasMoreElements();) {
            str=(String)e.nextElement();
            if (str.startsWith("sbj(")) {
                havesbj=true;
                break;
            }
            i++;
        }

        if (havesbj) {
            int t;
            Vector v=new Vector();
            // str is param
            StringTokenizer tok=new StringTokenizer(str,",()");
            if (tok.hasMoreTokens()) tok.nextToken();
            while(tok.hasMoreTokens()) v.addElement(tok.nextToken());

            if (session!=null) {
                // If we have session AND speed
                if ((str=sessions.getValue(session,"SETTING_RASPEED"))!=null) {
                    try {
                        t=Integer.parseInt(str);
                    } catch(Exception e) {
                        t=-1;
                    }
                    if (t>0) havesession=true;
                }
            }
            // is index in param list
            if (havesession) {
                str=(String)v.elementAt(1);
            } else {
                str=(String)v.elementAt(0);
            }
            log.debug("checkSessionJingle(): "+havesession+" : "+str);
            params.setElementAt("bj("+str+")",i);
            log.debug("checkSessionJingle(): "+params.elementAt(i));
        }
        return params;
    }

      /**
     * Converts a node to XML.
     * This routine does not take into account invalid charaters (such as &ft;, &lt;, &amp;) in a datafield.
     * @param node the node to convert
     * @return the XML <code>String</code>
     * @todo   This generates ad-hoc system id's and public id's.  Don't know what, why or how this is used.
     */
    private String toXML(MMObjectNode node) {
        String tableName = node.getBuilder().getTableName();
        StringBuffer body = new StringBuffer("<?xml version=\"" + node.getBuilder().getVersion()+ "\"?>\n");
        body.append("<!DOCTYPE mmnode.").append(tableName).append(" SYSTEM \"").append(dtdbase).append("/mmnode/").append(tableName).append(".dtd\">\n");
        body.append("<" + tableName + ">\n");
        body.append("<number>" + node.getNumber() + "</number>\n");
        for (Object element : node.getBuilder().getFields(NodeManager.ORDER_CREATE)) {
            CoreField field = (CoreField)element;
            int type = field.getType();
            String name = field.getName();
            body.append('<').append(name).append('>');
            if ((type == Field.TYPE_INTEGER)|| (type == Field.TYPE_NODE)) {
                body.append(node.getIntValue(name));
            } else if (type == Field.TYPE_BINARY) {
                body.append(node.getByteValue(name));
            } else {
                body.append(node.getStringValue(name));
            }
            body.append("</").append(name).append(">\n");
        }
        body.append("</").append(tableName).append(">\n");
        return body.toString();
    }

    /**
     * @javadoc
     */
    public byte[] getXML(Vector params) {
        log.debug("getXML(): param="+params);
        String result="";
        if (params.size()==0) return null;
        MMObjectBuilder bul=mmbase.getTypeDef();
        if (params.size()==1) {
            MMObjectNode node=null;
            try {
                node=bul.getNode((String)params.elementAt(0));
            } catch(Exception e) { }


            if (node!=null) {
                result=toXML(node);
            } else {
                result="Sorry no valid mmnode so no xml can be given";
            }
        } else if (params.size()==2) {
            try {
                int start=Integer.parseInt((String)params.elementAt(0));
                int end=Integer.parseInt((String)params.elementAt(1));
                for (int i=start;i<(end+1);i++) {
                    MMObjectNode node=null;
                    try {
                        node=bul.getNode(i);
                    } catch(Exception e) { }


                    if (node!=null) {
                        result+=toXML(node)+"\n\n";
                    }
                }
            } catch(Exception f) {
                result="Sorry no valid mmnode so no xml can be given";
            }
        }
        if(!provideXML) {
            result="Turn provideXML to true in servdb.java";
            log.warn("warning: provideXML in servdb.java is turned off");
        }
        return stringToBytes(result);
    }

    /**
     * Downloading Attachment
     * cjr@dds.nl, July 27th 2000
     *
     * @return Byte array with contents of 'handle' field of attachment builder
     * @deprecated moved to AttachmentServlet
     */
    public String getAttachmentFileName(Vector params) {
        log.debug("getAttachment(): param="+params);
        if (params.size()==1) {
            MMObjectBuilder bul=mmbase.getTypeDef();
            MMObjectNode node=null;
            try {
                node=bul.getNode((String)params.elementAt(0));
                if (node!=null) {
                    String filename=node.getStringValue("filename");
                    if (filename!=null && !filename.equals("")) {
                        return filename;
                    }
                }
            } catch(Exception e) {
                log.error("Failed to get attachment node for objectnumber "+(String)params.elementAt(0));
                return null;
            }
        }
        return null;
    }

    /**
     * Downloading Attachment
     * cjr@dds.nl, July 27th 2000
     *
     * @return Byte array with contents of 'handle' field of attachment builder
     * @deprecated moved to AttachmentServlet
     */
    public byte[] getAttachment(Vector params) {
        log.debug("getAttachment(): param="+params);
        if (params.size()==1) {
            MMObjectBuilder bul=mmbase.getTypeDef();
            MMObjectNode node=null;
            try {
                node=bul.getNode((String)params.elementAt(0));
            } catch(Exception e) {
                log.error("Failed to get attachment node for objectnumber "+(String)params.elementAt(0)+" :"+e);
                return null;
            }

            if (node!=null) {
                byte[] data = node.getByteValue("handle");
                return data;
            } else {
                return stringToBytes("Sorry, no valid mmnode, so no attachment can be given");
            }
        } else {
            log.debug("getAttachment called with "+params.size()+" arguments, instead of exactly 1");
            return null;
        }
    }

    /**
     * Mimetype of attachment
     * cjr@dds.nl, July 27th 2000
     *
     * @return Mimetype of attachment
     * @deprecated moved to AttachmentServlet
     */
    public String getAttachmentMimeType(Vector params) {
        if (params.size()==1) {
            MMObjectBuilder bul=mmbase.getTypeDef();
            MMObjectNode node=null;
            try {
                node=bul.getNode((String)params.elementAt(0));
            } catch(Exception e) {
                log.error("Failed to get attachment node for objectnumber "+(String)params.elementAt(0));
                return null;
            }

            if (node!=null && !node.getStringValue("mimetype").equals("")) {
                log.debug("servdb mimetype = "+node.getStringValue("mimetype"));
                return node.getStringValue("mimetype");
            } else {
                //result="Sorry no valid mmnode so no attachment can be given";
                log.debug("servdb mimetype = application/x-binary");
                return "application/x-binary";
            }
        } else {
            log.debug("getAttachmentMimeType called with "+params.size()+" arguments, instead of exactly 1");
            return null;
        }
    }


    /**
     * Return Flash movie
     * @return Byte array with Flash movie
     */
    public byte[] getFlash(Vector params) {
        if (log.isDebugEnabled()) log.debug("getFlash: param="+params);
        if (params.size()!=1) {
            if (log.isDebugEnabled()) log.debug("getFlash called with "+params.size()+" arguments, instead of exactly 1");
            return null;
        }
        MMObjectBuilder bul=mmbase.getMMObject("flash");
        MMObjectNode node=null;
        try {
            node=bul.getNode((String)params.elementAt(0));
        } catch(Exception e) {};
        if (node!=null) {
            byte[] data = node.getByteValue("handle");
            return data;
        }

        if (log.isDebugEnabled()) log.debug("Failed to get node number "+(String)params.elementAt(0));
        return null;
    }

    /**
     * @javadoc
     */
    public byte[] getDTD(Vector params) {
        return stringToBytes("Test DTD");
    }

    /**
     * try to obtain a decoded param string from the input Vector
     * format in : s(11212)
     * format out 11212
     * on a get with 's'
     */
    public String getParamValue(String wanted,Vector params) {
        String val=null;
        int pos=-1;
        Enumeration e=params.elements();
        while (e.hasMoreElements()) {
            val=(String)e.nextElement();
            pos=val.indexOf((wanted+"("));
            if (pos!=-1) {
                pos=val.indexOf('(');
                int pos2=val.indexOf(')');
                return val.substring(pos+1,pos2);
            }
        }
        return null;
    }

    /**
     * @javadoc
     */
    Vector checkPostPlaylist(HttpPost poster,scanpage sp, Vector vec) {
        if (sp.req.getMethod().equals("POST")) {
            if (poster.checkPostMultiParameter("only")) {
                String line="";
                Vector<Object> only=poster.getPostMultiParameter("only");
                for (Enumeration<Object> e=only.elements();e.hasMoreElements();) {
                    if (!line.equals("")) {
                        line+=","+(String)e.nextElement();
                    } else {
                        line+=(String)e.nextElement();
                    }
                }
                vec.addElement("o("+line+")");
            }
        }
        return vec;
    }

    /**
     * @vpro
     * @javadoc
     */
    private void checkImgMirror(scanpage sp) {
        String host=sp.getAddress();
        if (host!=null && (host.equals("sneezy.omroep.nl") || host.equals("images.vpro.nl")) && mmbase!=null) {
            log.debug("checkImgMirror ->"+sp.req.getQueryString());
            NetFileSrv bul=(NetFileSrv)mmbase.getMMObject("netfilesrv");
            if (bul!=null) {
                bul.fileChange("images","main","/img.db:"+sp.req.getQueryString()+".asis");
            }
        }
    }
}
