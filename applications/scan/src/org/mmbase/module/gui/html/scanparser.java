/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.gui.html;

import java.util.*;
import java.io.*;
import java.util.zip.*;
import javax.servlet.*;

import org.mmbase.module.*;
import org.mmbase.util.*;
import org.mmbase.module.core.*;

import org.mmbase.util.logging.*;

/**
 * scanpage is a container class it holds all objects needed per scan page
 * it was introduced to make servscan threadsafe but will probably in the future
 * hold all page related info instead of HttpServletR equest and HttpServletResponse
 * because we want extend the model of offline page generation.
 *
 * @javadoc
 * @application SCAN
 * @rename SCANParser
 * @author Daniel Ockeloen
 * @version $Id: scanparser.java,v 1.79 2008-09-03 15:23:39 michiel Exp $
 */
public class scanparser extends ProcessorModule {

    public static scancacheInterface scancache=null;

    private static Logger log =  Logging.getLoggerInstance(scanparser.class.getName());
    private static HTMLFormGenerator htmlgen=new HTMLFormGenerator();
    private CounterInterface counter = null;
    private static sessionsInterface sessions=null;
    private static idInterface id=null;
    private static MMBase mmbase=null;
    private static TransactionHandlerInterface transactionhandler;
    private static Hashtable<String, ProcessorModule> processors = new Hashtable<String, ProcessorModule>();
    private static RandomPlus rnd;
    private static int crcseed;

    private Hashtable<String, PageProcess> pagesprocessing=new Hashtable<String, PageProcess>();

    private static String htmlroot;
    Hashtable Roots;

    public scanparser() {
        htmlroot = MMBaseContext.getHtmlRoot();
    }

    /**
     * Init the servscan, this is needed because it was created using
     * a newInstanceOf().
     */
    public void init() {
        Roots=getRoots();
        // org.mmbase start();
        sessions=(sessionsInterface)getModule("SESSION");
        scancache=(scancacheInterface)getModule("SCANCACHE");
        mmbase=(MMBase)getModule("MMBASEROOT");
        transactionhandler=(TransactionHandlerInterface)getModule("TRANSACTIONHANDLER");
        // org.mmbase stats=(StatisticsInterface)getModule("STATS");
        rnd=new RandomPlus();
        crcseed=rnd.nextInt();
    }

     /**
      * getMimeType: Returns the mimetype using ServletContext.getServletContext which returns the servlet context
      * which is set when servscan is loaded.
      * Fixed on 22 December 1999 by daniel & davzev.
      * @param ext A String containing the extension.
      * @return The mimetype.
      */
     public String getMimeType(String ext) {
         return getMimeTypeFile("dummy."+ext);
     }

     public String getMimeTypeFile(String fileName) {
         ServletContext sx = MMBaseContext.getServletContext();
         String mimeType = sx.getMimeType(fileName);
         if (mimeType == null) {
             log.warn("getMimeType(" + fileName + "): Can't find mimetype retval=null -> setting mimetype to default text/html");
             mimeType = "text/html";
         }
         return mimeType;
     }


    public String doPrePart(String template, int last, int rpos, int numitems,int epos) {
        int precmd=0,postcmd=0,prepostcmd=0,index;
        StringBuffer dst=new StringBuffer();
        String cmd="$ITEM";
        if (template==null) return "No Template";
        while ((precmd=template.indexOf(cmd,postcmd))!=-1) {
            dst.append(template.substring(postcmd,precmd));
            prepostcmd=precmd+cmd.length();
            postcmd=precmd+6;
            try {
                index=Integer.parseInt(template.substring(prepostcmd,postcmd));
                try {
                    index=Integer.parseInt(template.substring(prepostcmd,postcmd+1));
                    postcmd++;
                } catch (Exception g) {}
                index--;
                try {
                    dst.append("DUMMY");
                } catch (ArrayIndexOutOfBoundsException e) {}
            } catch (NumberFormatException e) {
                //index=1;
                try {
                    if (template.charAt(prepostcmd)=='L') {
                        dst.append(""+last);
                    } else if (template.charAt(prepostcmd)=='R') {
                        dst.append(""+(rpos+1));
                    } else if (template.charAt(prepostcmd)=='E') {
                        dst.append(""+epos);
                    } else if (template.charAt(prepostcmd)=='S') {
                        dst.append(""+numitems);
                    }
                } catch (ArrayIndexOutOfBoundsException f) {}
            }
        }
        dst.append(template.substring(postcmd));
        return dst.toString();
    }

    public String doEndPart(String template, int last, int rpos, int numitems,int epos) {
        int precmd=0,postcmd=0,prepostcmd=0,index;
        StringBuffer dst=new StringBuffer();
        String cmd="$ITEM";
        if (template==null) return "No Template";
        while ((precmd=template.indexOf(cmd,postcmd))!=-1) {
            dst.append(template.substring(postcmd,precmd));
            prepostcmd=precmd+cmd.length();
            postcmd=precmd+6;
            try {
                index=Integer.parseInt(template.substring(prepostcmd,postcmd));
                try {
                    index=Integer.parseInt(template.substring(prepostcmd,postcmd+1));
                    postcmd++;
                } catch (Exception g) {}
                index--;
                try {
                    dst.append("DUMMY");
                } catch (ArrayIndexOutOfBoundsException e) {}
            } catch (NumberFormatException e) {
                //index=1;
                try {
                    if (template.charAt(prepostcmd)=='L') {
                        dst.append(""+last);
                    } else if (template.charAt(prepostcmd)=='R') {
                        dst.append(""+(rpos+1));
                    } else if (template.charAt(prepostcmd)=='E') {
                        dst.append(""+epos);
                    } else if (template.charAt(prepostcmd)=='S') {
                        dst.append(""+numitems);
                    }
                } catch (ArrayIndexOutOfBoundsException f) {}
            }
        }
        dst.append(template.substring(postcmd));
        return dst.toString();
    }

    public Vector reverse(List input,int num) {
        Vector results=new Vector();
        for (Enumeration e = Collections.enumeration(input);e.hasMoreElements();) {
            for (int i=0;i<num;i++) {
                String val=(String)e.nextElement();
                results.insertElementAt(val,i);
            }
        }
        return results;
    }

    boolean do_vals(String cmd,sessionInfo session,scanpage sp) throws ParseException {
        int andpos=cmd.indexOf(" AND ");
        int orpos=cmd.indexOf(" OR ");
        if (andpos!=-1 || orpos!=-1) {
            boolean result=false;
            boolean done=false;
            String part;
            do {
                if (andpos!=-1) {
                    part=cmd.substring(0,andpos);
                    cmd=cmd.substring(andpos+5);
                    result=do_val(part,session,sp);
                    done=!result;
                } else if (orpos!=-1) {
                    part=cmd.substring(0,orpos);
                    cmd=cmd.substring(orpos+4);
                    result=do_val(part,session,sp);
                    done=result;
                } else if (andpos<orpos) {
                    part=cmd.substring(0,andpos);
                    cmd=cmd.substring(andpos+5);
                    result=do_val(part,session,sp);
                    done=!result;
                } else {
                    part=cmd.substring(0,orpos);
                    cmd=cmd.substring(orpos+4);
                    result=do_val(part,session,sp);
                    done=result;
                }
                andpos=cmd.indexOf(" AND ");
                orpos=cmd.indexOf(" OR ");
            } while (!done && (andpos!=-1 || orpos!=-1));
            if (done) return result;
            result=do_val(cmd,session,sp);
            return result;
        } else {
            return do_val(cmd,session,sp);
        }
    }

    boolean do_val(String cmd,sessionInfo session,scanpage sp) throws ParseException {
        int il=-1;
        int ir=-1;
        int pos=cmd.indexOf('=');
        if (pos!=-1) {
            String l=cmd.substring(0,cmd.indexOf("="));
            String r=cmd.substring(cmd.indexOf("=")+2);
            char e=cmd.charAt(pos+1);
            l=handle_line(l,session,sp);
            r=handle_line(r,session,sp);
            try {
                il=Integer.parseInt(l);
                ir=Integer.parseInt(r);
            } catch (Exception f){ }
            if (il!=-1) {
                if ((e=='=' || e=='E') && (il==ir)) return true;
                if (e=='N' && (il!=ir)) return true;
                if (e=='H' && (il>ir)) return true;
                if (e=='L' && (il<ir)) return true;
                if (e=='M' && (il<=ir)) return true;
                if (e=='A' && (il>=ir)) return true;
                return false;
            } else {
                if ((e=='=' || e=='E') && (l.equals(r))) return true;
                if (e=='N' && (!l.equals(r))) return true;
                if (e=='C' && (l.equalsIgnoreCase(r))) return true;
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Process the HTML for our own extensions
     *
     */
    public final String handle_line(String body,sessionInfo session,scanpage sp) throws ParseException {

        String part=null;
        int end_pos2;
        int precmd=0,postcmd=-1,prepostcmd=0;
        StringBuffer newbody=new StringBuffer();

        // old pos for  IFs moved to past LIST
        if (body.indexOf("<IF")!=-1) {
            body=do_conditions(body,session,sp);
        }

        // First find the processor (for the MACRO commands)
        part=finddocmd(body,"<PROCESSOR ",'>',8,session,sp);
        body=part;

        // Include other HTML
        part=finddocmd(body,"<INCLUDE ",'>',5,session,sp);
        body=part;


        // <LIST text1> text2 </LIST>
        // The code below will hand text1 and text2 to the method do_list(text1, text2, session, sp)
        while ((precmd=body.indexOf("<LIST ",postcmd))!=-1) {
            newbody.append(body.substring(postcmd+1,precmd));
            prepostcmd=precmd+6;
            if ((postcmd=body.indexOf('>',precmd))!=-1) {
                end_pos2=body.indexOf("</LIST>",prepostcmd);
                if (end_pos2!=-1) {
                    try {
                        newbody.append(do_list(body.substring(prepostcmd,postcmd),body.substring(postcmd+1,end_pos2),session,sp));
                    } catch(Exception e) {
                        String errorMsg = "Error in list: "+e.getMessage()+" in page "+sp.getUrl();
                        newbody.append(errorMsg);
                        log.debug("handle_line(): ERROR: do_list(): "+prepostcmd+","+postcmd+","+end_pos2+" in page("+sp.getUrl()+") : "+e);
                        e.printStackTrace();
                    }
                    postcmd=end_pos2+7;
                }
            } else {
                postcmd=prepostcmd;
            }
        }
        if (postcmd < body.length()) {
            newbody.append(body.substring(postcmd+1));
        }
        body=newbody.toString();

        // detect a <IF> page (daniel)

        // Unmap (special commands)
        part=finddocmd(body,"<UNMAP ",'>',7,session,sp);
        body=part;

        // Macro's (special commands)
        part=finddocmd(body,"<MACRO ",'>',1,session,sp);
        body=part;

        // Include other HTML
        part=finddocmd(body,"<INCLUDE ",'>',5,session,sp);
        body=part;

        // Include other HTML in the LIST command
        part=finddocmd(body,"<LISTINCLUDE ",'>',5,session,sp);
        body=part;

        // Unmap (special commands)
        part=finddocmd(body,"<UNMAP ",'>',7,session,sp);
        body=part;

        // Macro's (special commands)
        part=finddocmd(body,"<MACRO ",'>',1,session,sp);
        body=part;
        // Do the dollar commands
        body=dodollar(body,session,sp);

        // TESTING 1 2 3  of the ifs after the include RICO
        if (body.indexOf("<IF")!=-1) {
            body=do_conditions(body,session,sp);
        }

        // TESTING 1 2 3  of the ifs after the _ONLY_ after the list Daniel
        if (body.indexOf("<LIF")!=-1) {
            //  debug("handle_line(): LIF detected on servscan");
            body=do_conditions_lif(body,session,sp);
        }

        // <NORELOAD>, make it possible to jump pages
        part=finddocmd(body,"<NORELOAD ",'>',13,session,sp);
        body=part;

        // DO set variables
        part=finddocmd(body,"<DO ",'>',6,session,sp);
        body=part;

        // <SAVE, make it possible to save
        part=finddocmd(body,"<SAVE ",'>',19,session,sp);
        body=part;

        // <CRC HREF=", make it possible to save
        part=finddocmd(body,"<CRC HREF=",'>',24,session,sp);
        body=part;

        // <TRANSACTION text1> text2 </TRANSACTION>
        // The code below will hand text1 and text2 to the method do_transaction(text1, text2, session, sp)
        newbody=new StringBuffer();
        postcmd=0;

        while ((precmd=body.indexOf("<transactions",postcmd))!=-1) {
            newbody.append(body.substring(postcmd,precmd));
            prepostcmd=precmd+13;
            if ((postcmd=body.indexOf('>',precmd))!=-1) {
                end_pos2=body.indexOf("</transactions>",prepostcmd);
                if (end_pos2!=-1) {
                    postcmd=end_pos2+15;
                    try {
                        newbody.append(do_transaction(body.substring(precmd,postcmd),session,sp));
                    } catch(Exception e) {
                        log.error("handle_line(): ERROR: do_transaction(): "+prepostcmd+","+postcmd+","+end_pos2+" in page("+sp.getUrl()+") : "+e);
                        e.printStackTrace();
                    }
                }
            } else {
                postcmd=prepostcmd;
            }
        }

        newbody.append(body.substring(postcmd));
        body=newbody.toString();

        // <GOTO, make it possible to jump pages
        part=finddocmd(body,"<GOTO ",'>',10,session,sp);
        body=part;

        // <HOST, demand a host for this page
        part=finddocmd(body,"<HOST ",'>',11,session,sp);
        body=part;

        // <NEWPAGE, make it possible to  'jump' by loading new pages
        part=finddocmd(body,"<NEWPAGE ",'>',12,session,sp);
        body=part;

        // <GRAB, make it possible to get information of other html-pages
        part=finddocmd(body,"<GRAB ",'>',14,session,sp);
        body=part;

        // <PART, make it possible to include other parsed ! and cached pages
        part=finddocmd(body,"<PART ",'>',20,session,sp);
        body=part;

        // Counter tag
        part=finddocmd(body,"<COUNTER",'>',21,session,sp);
        body=part;

        // <TREEPART, TREEFILE
        part=finddocmd(body,"<TREE",'>',22,session,sp);
        body=part;

        // <LEAFPART, LEAFFILE
        part=finddocmd(body,"<LEAF",'>',23,session,sp);
        body=part;

        // Last one always
        part=finddocmd(body,"$LBJ-",'^',4,session,sp);
        body=part;

        return body;
    }

    /**
     * Main function, calls the several handlers for the diffenrent TAGS
     */
    private final String finddocmd(String body,String cmd,int endc,int docmd,sessionInfo session,scanpage sp) throws ParseException {
        return finddocmd(body,cmd,""+(char)endc,docmd,session,sp);
    }

    /**
     * Main function, calls the several handlers for the diffenrent TAGS
     */
    private final String finddocmd(String body,String cmd,String endc,int docmd,sessionInfo session,scanpage sp) throws ParseException {
        int precmd=0,postcmd=0,prepostcmd=0;
        StringBuffer newbody=new StringBuffer();
        int pos,pos2;
        boolean removeToken=false;

        while ((precmd=body.indexOf(cmd,postcmd))!=-1) {
            if (removeToken) {
                newbody.append(body.substring(postcmd+1,precmd));
            } else {
                newbody.append(body.substring(postcmd,precmd));
            }

            removeToken=false;
            prepostcmd=precmd+cmd.length();

            //if ((postcmd=body.indexOf(endc,precmd))!=-1) {
            pos=-1;
            for (int i=0;i<endc.length();i++) {
                pos2=body.indexOf(endc.charAt(i),precmd);
                if (pos2!=-1 && (pos==-1 || pos2<pos)) {
                    pos=pos2;
                    if (i==0) {
                        removeToken=true;
                    } else {
                        removeToken=false;
                    }
                }
            }

            if (pos!=-1) {
                postcmd=pos;

                //debug("newpart "+newpart);

                String partbody;
                switch(docmd) {
                    case 1: // '<MACRO '
                        newbody.append(do_macro(body.substring(prepostcmd,postcmd),session,sp));
                        break;
                    case 2: // '$SQL-'
                        //org.mmbase newbody.append(do_sql(body.substring(prepostcmd,postcmd)));
                        break;
                    case 3: // '$ID-'
                        newbody.append(do_id(body.substring(prepostcmd,postcmd),sp));
                        break;
                    case 4: // '$OBJ-' , '$VAR-' , '$COLOR-' '$TEXT-' and '$LBJ-'
                        newbody.append(body.substring(prepostcmd,postcmd));
                        break;
                    case 5: // '<INCLUDE '
                        newbody.append(do_include(body.substring(prepostcmd,postcmd),session,sp));
                        break;
                    case 6: // '<DO '
                        newbody.append(do_do(body.substring(prepostcmd,postcmd),session,sp));
                        break;
                    case 7: // '<UNMAP '
                        newbody.append(do_unmap(body.substring(prepostcmd,postcmd),session,sp));
                        break;
                    case 8: // '<PROCESSOR '
                        sp.processor=getProcessor(body.substring(prepostcmd,postcmd));
                        break;
                    //case 9: // '$DOC- '
                    //  newbody.append(do_doc(body.substring(prepostcmd,postcmd)));
                    //  break;
                    case 9: // '$MOD-'
                        newbody.append(do_mod(sp,body.substring(prepostcmd,postcmd)));
                        break;
                    case 10: // '<GOTO'
//                      newbody=new StringBuffer();
//                      newbody.append(do_goto(sp,body.substring(prepostcmd,postcmd)));
                        return do_goto(sp,body.substring(prepostcmd,postcmd));
//                      break;
                    case 11: // '<HOST'
                        String url = do_host(sp, body.substring(prepostcmd,postcmd));
                        if (sp.rstatus==1) // if redir return url else eat the tag
                            return url;
                        break;
                    case 12: // '<NEWPAGE'
                        newbody=new StringBuffer();
                        newbody.append(do_newpage(sp,body.substring(prepostcmd,postcmd)));
                        break;
                    case 13: // '<RELOAD'
                        // org.mmbase newbody.append(do_reload(body.substring(prepostcmd,postcmd)));
                        break;
                    case 14: // '<GRAB'
                        // org.mmbase newbody.append(do_grab(body.substring(prepostcmd,postcmd)));
                        break;
                    case 15: // '$PAGE-'
                        //  newbody.append(do_page(body.substring(prepostcmd,postcmd)));
                        break;
                    case 16: // '$AREA-'
                        newbody.append(body.substring(prepostcmd,postcmd));
                        break;
                    case 17: // '$SESSION-'
                        newbody.append(do_session(body.substring(prepostcmd,postcmd),session));
                        break;
                    case 18: // '$LASTLIST'
                        // org.mmbase newbody.append(""+lastlistitem);
                        break;
                    case 19: // '<SAVE'
                        newbody.append(do_save(session,body.substring(prepostcmd,postcmd)));
                        break;
                    case 20: // '<PART '
                        partbody=do_part(body.substring(prepostcmd,postcmd),session,sp,0);
                        if ((sp.rstatus==1) || (sp.rstatus==2)) {
                            return partbody;
                        };
                        newbody.append(partbody);
                        break;
                    case 21: // '<COUNTER'
                        newbody.append(do_counter(body.substring(prepostcmd,postcmd),session,sp));
                        break;
                    case 22: // '<TREEPART, TREEFILE'
                    case 23: // '<LEAFPART, LEAFFILE'
                        partbody=do_smart(body.substring(prepostcmd,postcmd),session,sp, docmd==23);
                        if ((sp.rstatus==1) || (sp.rstatus==2)) {
                            return partbody;
                        };
                        newbody.append(partbody);
                        break;
                    case 24: // '<CRC CHECK'
                        newbody.append(do_crc(session,body.substring(prepostcmd,postcmd)));
                        break;
                    default:
                        log.fatal("Woops broken case in method finddocmd");
                        break;
                }
            } else {
                postcmd=prepostcmd;
            }
        }
        if (removeToken) {
            newbody.append(body.substring(postcmd+1));
        } else {
            newbody.append(body.substring(postcmd));
        }
        return newbody.toString();
    }

    /**
     * The &lt;COUNTER x y&gt; tag is only used by the VPRO, it returns information in regard to nedstat.
     *
     * @param part A string containing the remaining COUNTER part.
     * @param session The sessionInfo object.
     * @param sp The current scanpage object.
     * @return A String containing the counter value.
     */
    private String do_counter( String part, sessionInfo session, scanpage sp ) throws ParseException {
        String result = null;

        if(counter==null) {
            counter=(CounterInterface)getModule("COUNTER");
            if(counter==null) {
                log.error("<COUNTER tag is used but counter module is not loaded.");
                return "";
            }
        }

        // Scan & Parse all $ attributes used in the tag.
        part = dodollar(part,session,sp);

        long time = System.currentTimeMillis();
        part = part.trim();
        log.debug("Using part ["+part+"]");
        int i=part.indexOf(' ');
        String params="";
        if (i!=-1) {
            params=part.substring(i+1);
        } else {
            log.debug("["+part +"] no arguments are given.");
        }

        result = counter.getTag(params, session, sp);

        log.debug("processing counter took "+ (System.currentTimeMillis() - time ) + " ms.");
        return result;
    }

    private final String do_part(String part2,sessionInfo session,scanpage sp,int markPart) throws ParseException {

        String part="",filename,paramline=null;;
        Vector<String> oldparams=sp.getParamsVector();
        String oldQueryString = sp.querystring;

        sp.partlevel++;

        part2=dodollar(part2,session,sp);
        // we now may have a a param setup like part.shtml?1212+1212+1212
        // split it so we can load the file and get the params
        int pos=part2.indexOf('?');
        if (pos!=-1) {
            filename=part2.substring(0,pos);
            paramline=part2.substring(pos+1);
            if (sp.req_line==null) sp.req_line=filename;
        } else {
            filename = part2;
            paramline = "";
        }

        if (filename.indexOf("..")>=0) {
            sp.partlevel--;
            log.error("do_part: Usage of '..' in filepath not allowed!");
            return "Usage of '..' in filepath not allowed!";
        }

        // Set new params for part
        sp.querystring = paramline;
        sp.setParamsLine(paramline);

        if ((filename.length()>0) && (filename.charAt(0)!='/')) {
            String servletPath = sp.req_line;
            if (sp.req!=null) {
                servletPath=sp.req.getServletPath();
            }
            filename = servletPath.substring(0,servletPath.lastIndexOf("/")+1)+filename;
            if (log.isDebugEnabled()) log.debug("do_part: filename:"+filename);
        }

        // Test if we are going circular
        if (sp.partlevel>8) {
            log.warn("Warning more then "+sp.partlevel+" nested parts "+sp.req_line);
            if (sp.partlevel>14) throw new CircularParseException("Too many parts, level="+sp.partlevel+" URI "+sp.getUrl());
        }

        // debug("do_part(): filename="+filename);
        // debug("do_part(): paramline="+paramline);
        part=getfile(filename);
        if (part!=null) {

            // unlike include we need to map this ourselfs before including it
            // in this page !!
            try {
                String cachedPart = handlePartCache(filename + "?" + paramline, part, session, sp);
                if (cachedPart == null) part = handle_line(part,session,sp); else part = cachedPart;
            } catch (Exception e) {
                String errorMsg = "Error in part "+filename;
                if (paramline!=null) errorMsg += "?" + paramline;
                errorMsg += "\n" + e.getMessage() + "\n Parted by "+sp.getUrl();
                part = errorMsg;
                log.error("do_part(): "+errorMsg);
                e.printStackTrace();
            }

            if (markPart>0) {
                // Add start and end comments to part
                String marker = "part "+filename;
                if (paramline!=null) marker += "?"+paramline;
                String startComment, endComment;
                if (markPart==1) { startComment = "<!--"; endComment = "-->"; }
                else  { startComment = "/*"; endComment = "*/"; }
                marker += "\n"+endComment;
                part = startComment+"\nStart "+ marker + part + startComment+"\nEnd of " + marker;
            }

            sp.setParamsVector(oldparams);
            sp.querystring = oldQueryString;
            sp.partlevel--;
            return part;
        } else {
            sp.setParamsVector(oldparams);
            sp.querystring = oldQueryString;
            sp.partlevel--;
            return "";
        }
    }

    /**
     * Returns the cached part when the part contains the tag <CACHE HENK> and the page is not exprired and reload is off
     * else a null wil be returned.
     */
    private String handlePartCache(String filename, String part, sessionInfo session, scanpage sp) throws ParseException {
        if (part == null)
            return null;

        /* Test if cache HENK is used in this page or not.
         */
        int start = part.indexOf("<CACHE HENK");
        int end;
        if (start>=0) {
            start+=11;
            end = part.indexOf(">", start);
        } else
            return null;

        /* Ok it's used. Now look for the specified filename in the cache by (poolname, key, <CACHE HENK expire_time>).
         */
        // if (debug) debug("handlePartCache(): lookup " + filename);

        String result = null;
        if (!sp.reload) {
            result = scancache.get("HENK", filename, part.substring(start,end+1),sp);
            if (result != null) {
                if(log.isDebugEnabled())
                    log.debug("Got " + filename + "out of cache HENK.");
                return result;
            }
        }

        /* The page couldn't be retrieved out of the cache.
         * Parse it and put it in the cache.
         */
        try {
            // Parse everything before the cache henk tag
            result = "";
            if (start>11) {
                result = handle_line(part.substring(0,start-11),session,sp);
            }
            // Parse everything after the cache henk tag
            result += handle_line(part.substring(end + 1),session,sp);
        } catch (Exception e) {
            String errorMsg = "Error in part "+filename;
            errorMsg += "\n" + e.getMessage() + "\n Parted by "+sp.getUrl();
            part = errorMsg;
            log.error("handlePartCache(): "+errorMsg);
            log.error(e.getMessage());
            log.error(Logging.stackTrace(e));
        }
        scancache.put("HENK", filename, result);
        return result;
    }

    /**
     * Support method for do_smart
     * Add version to path if version defined
     */
    private static String getVersion(String name, sessionInfo session) {
        String version = session.getValue(name+"version");
        if (version!=null) {
            version=version.trim();
            if (version.equals("")) version = null;
        }
        return version;
    }

    /**
     * Support method for do_smart
     * Return the path for the file to part
     */
    private String getSmartFileName( String path, // Path currently investigated
                                     String builderPath, // path to add between path and filename for LEAVE version
                                     String fileName, // File name of part we are looking for
                                     String bestFile, // Last found file which is ok
                                     Enumeration<MMObjectNode> nodes, // The passed object nodes
                                     sessionInfo session, // The session for version control
                                     boolean leaf, // TREE or LEAF version
                                     boolean byALias // NAME version
                                    ) throws ParseException {
        // Get node from args
        MMObjectNode node = nodes.nextElement();
        String nodeNumber;
        if (byALias) {
            nodeNumber = ""+mmbase.getOAlias().getAlias(node.getIntValue("number"));
        } else {
            nodeNumber = ""+node.getValue("number");
        }

        // Ask the builder of the node to create the path to search for the part
        // If null returned we're done and return bestFile
        path = node.getBuilder().getSmartPath(htmlroot, path, nodeNumber, getVersion(node.getName(), session));
        if (path==null) {
            if (log.isDebugEnabled()) log.debug("getSmartFile: no dir found for node "+nodeNumber+". Returning "+bestFile);
            return bestFile;
        }

        String newFileName;
        if (leaf) {
            // Remove one builder name from the builder path
            int i = builderPath.indexOf(File.separatorChar);
            if (i<0) newFileName = path+fileName;
            else {
                builderPath = builderPath.substring(i+1);
                newFileName = path + builderPath + File.separator + fileName;
            }
        } else newFileName = path + fileName;

        // Check if file present if so, select it as the new bestFile to use
        String fileToCheck = htmlroot+newFileName;
        File f = new File(fileToCheck);
        if (f.exists()) {
            bestFile = newFileName;
            if (log.isDebugEnabled()) log.debug("Found and selecting " + newFileName + " as new best file");
        } else if (log.isDebugEnabled()) log.debug(fileToCheck + " not found, continuing search");

        // If no more object numbers then return the bestFile so far else continue the travel
        if (!nodes.hasMoreElements())
            return bestFile;

        return getSmartFileName( path, builderPath, fileName, bestFile, nodes, session, leaf, byALias);
    }

    /**
     * TREEPART, TREEFILE, LEAFPART or LEAFFILE
     * @param args action+objectnumbers+filepath
     * action: PATH or FILE
     * objectnumbers: + seperated list of objectnumbers, a ( will start skipping args, a ) will stop skipping args
     * filepath: (optional) file to part
     * @param leaf false for TREE and true for LEAF version
     */
    private String do_smart(String args, sessionInfo session, scanpage sp, boolean leaf) throws ParseException {
        // Get action: PART or FILE
        String cmdName;
        if (leaf) cmdName = "LEAF"; else cmdName = "TREE";
        int pos = args.indexOf(" ");
        if (pos<0) throw new ParseException("Blank expected after <"+cmdName+"PART or FILE");
        String action = args.substring(0, pos);
        if (!(action.equals("PART") || action.equals("FILE")))
            throw new ParseException("PART or FILE expected after <"+cmdName);
        args = args.substring(pos+1);
        args = dodollar(args, session, sp);
        if (log.isDebugEnabled()) log.debug(cmdName+action+" "+args);

        boolean byALias=false;
        if ((args.length()>=6) && args.substring(0,6).equals("ALIAS ")) {
            byALias=true;
            args = args.substring(6); // Returns empty if length 6, no exception
        }

        int addMarkers = 0;
        if ((args.length()>=6) && args.substring(0,6).equals("DEBUG ")) {
            addMarkers = 1;
            args = args.substring(6); // Returns empty if length 6, no exception
        }
        if ((args.length()>=12) && args.substring(0,12).equals("DEBUGCSTYLE ")) {
            addMarkers = 2;
            args = args.substring(12); // Returns empty if length 12, no exception
        }

        // Set root path
        String path = File.separator;

        // Use the last argument or the builder name of last arg to compose the filename to find
        // Add the buildernames of the passed nodes to builderPath for leafpart and leaffile
        String filename = "";
        String builderPath = "";
        Vector<MMObjectNode> nodes = new Vector<MMObjectNode>();
        String arg = "";
        boolean skip = false;
        StringTokenizer tokens = new StringTokenizer(args, "+");
        while (tokens.hasMoreTokens()) {
            arg = tokens.nextToken().trim();
            if ((arg==null) || arg.equals(""))
                throw new ParseException(cmdName+action+" "+args+": no or empty object number specified");
            if (skip) { // Skip all args until closing ) found
                if (arg.equals(")")) skip = false;
            }
            else if (arg.equals("(")) skip = true;
            else {
                boolean isNumber = true;
                try { Integer.parseInt(arg); } catch (NumberFormatException n) { isNumber = false; }
                if (isNumber) {
                    MMObjectNode node = mmbase.getTypeDef().getNode(arg);
                    if (node==null) throw new ParseException(cmdName+action+" node "+arg+" not found");
                    nodes.addElement(node);
                    if (leaf) builderPath += File.separator + node.getName();
                }
                else {
                    // Select the non number as filename to part, first add the remaining tokens
                    while (tokens.hasMoreTokens()) arg+="+"+tokens.nextToken();
                    filename = arg; // Use it as filename
                    args = "";      // Clear args to pass to part and split filename on ? for new args
                    pos = filename.indexOf('?');
                    if (pos>=0) {
                        if (pos<filename.length()-1) args = filename.substring(pos+1);
                        filename = filename.substring(0, pos);
                    }
                    break; // Save one test, we're done
                }//else
            }//else
        }//while

        // If no part name passed as arg, use parts/buildername.shtml?args
        if (filename.equals("")) {
            MMObjectNode node = mmbase.getTypeDef().getNode(arg);
            if (node==null) throw new ParseException(cmdName+action + " node " + arg + " not found");
            filename = "parts"+File.separator+node.getName()+".shtml";
        }

        String bestFile;
        if (leaf) {
            // Remove leading slash from builderPath
            if (builderPath.length()>0) builderPath = builderPath.substring(1);
            // If nothing better found part bestFile
            bestFile = path + builderPath + File.separator +filename;
        } else bestFile = path + filename; // If nothing better found part bestFile

        // Travel the smart object tree to find an override of the default part
        Enumeration<MMObjectNode> e = nodes.elements();
        if (e.hasMoreElements())
            bestFile = getSmartFileName( path, builderPath, filename, bestFile, e, session, leaf, byALias);

        if (!args.equals("")) bestFile += "?"+args;
        if (log.isDebugEnabled()) log.debug(cmdName+action+" using "+bestFile);

        if (action.equals("FILE"))
            return bestFile;

        return do_part(bestFile, session, sp, addMarkers);
    }

    /**
     * @param where A string with the location of the to be parsed file
     * @return A html-page
     */
    public final String getfile(String where) {
        File scanfile=null;
        int filesize,len=-1;
        cacheline cline=null;
        FileInputStream scan=null;
        Date lastmod;
        String rtn=null;

        String filename=htmlroot.trim()+where.trim();

        filename=filename.replace('/',(System.getProperty("file.separator")).charAt(0));
        filename=filename.replace('\\',(System.getProperty("file.separator")).charAt(0));
        scanfile = new File(filename);
        filesize = (int)scanfile.length();
        lastmod=new Date(scanfile.lastModified());
        cline=new cacheline(filesize);
        cline.lastmod=lastmod;
        try {
            scan = new FileInputStream(scanfile);
            len=scan.read(cline.buffer,0,filesize);
            scan.close();
        } catch(FileNotFoundException e) {
            // give_404_error("getfile"); // does not seem to exist.
            // not nice but, at least it does _something_ now:
            rtn = "File " + where + " not found";
            // perhaps better do not show, since it looks ugly in the editors, of which the help files normally are missing...
            // but at least log:
            log.error("file not found: "+where);
        } catch(IOException e) {
            log.error("IOException: " + Logging.stackTrace(e));
        }
        if (len!=-1) {
            rtn=new String(cline.buffer,0);
        }
        log.trace(rtn); // perhaps a little overkill
        return rtn;
    }


    /**
     * Main function, replaces most of the $ commands
     */
    private final String dodollar(String newbody,sessionInfo session,scanpage sp) throws ParseException {

        if ( newbody.indexOf('$') == -1) {
            return newbody;
        }

        long oldtime = System.currentTimeMillis();

        String part;
        int qw_pos,qw_pos2;

        // Parameter fill in
        while (newbody.indexOf("$PARAM")!=-1) {
            qw_pos=newbody.indexOf("$PARAM");
            qw_pos2=qw_pos+7;
            part=newbody.substring(0,qw_pos);
            part+=do_param(sp,newbody.substring(qw_pos+6,qw_pos2));
            part+=newbody.substring(qw_pos2);
            newbody=part;
        }

        // locals fill in
        while (newbody.indexOf("$LOCAL")!=-1) {
            qw_pos=newbody.indexOf("$LOCAL");
            qw_pos2=qw_pos+7;
            part=newbody.substring(0,qw_pos);
            part+=do_local(newbody.substring(qw_pos+6,qw_pos2));
            part+=newbody.substring(qw_pos2);
            newbody=part;
        }

        //debug("dodollar(): 2 " + (System.currentTimeMillis() - oldtime) + " ms." + cookie);

        // Personal obj's
        part=finddocmd(newbody,"$ID-","^\n\r\"=<> ,",3,session,sp);
        newbody=part;

        part=finddocmd(newbody,"$LASTLIST","^\n\r\"=<> ,",18,session,sp);
        newbody=part;

        // OBJects new VERSION
        part=finddocmd(newbody,"$AREA-","^\n\r\"=<> ,",16,session,sp);
        newbody=part;

        // find pages
        part=finddocmd(newbody,"$PAGE-","^\n\r\"=<> ,",15,session,sp);
        newbody=part;

        // find sessions
        part=finddocmd(newbody,"$SESSION-","^\n\r\"=<> ,",17,session,sp);
        newbody=part;

        // OBJects
        part=finddocmd(newbody,"$OBJ-",'^',4,session,sp);
        newbody=part;
        // clone of OBJ
        part=finddocmd(newbody,"$VAR-",'^',4,session,sp);
        newbody=part;
        // clone of OBJ
        part=finddocmd(newbody,"$COLOR-",'^',4,session,sp);
        newbody=part;
        // clone of OBJ
        part=finddocmd(newbody,"$TEXT-",'^',4,session,sp);
        newbody=part;
        // SQL queries
        part=finddocmd(newbody,"$SQL-",'^',2,session,sp);
        newbody=part;
        // CONST , not recognized in finddocmd , so must be build and added there
        // part=finddocmd(newbody,"$CONST-",'^',7,session,sp);
        // newbody=part;
        // Cineserv
        // part=finddocmd(newbody,"$DOC-",'^',9,);
        // newbody=part;

        // Modules
        part=finddocmd(newbody,"$MOD-","^\n\r\"=< ,",9,session,sp);
        newbody=part;

        //debug("dodollar(): 3 " + (System.currentTimeMillis() - oldtime) + " ms." + cookie);

        return newbody;
    }

    // local fill in routine
    //private final String do_local(HttpPost poster,String part2) {
    private final String do_local(String part2) {
        // daniel
        String rtn=null;

        //rtn=poster.getPostParameter("LOCAL"+part2);
        rtn=null;
        if (rtn==null) {
            rtn="";
        }
        return rtn;
    }

    // Parameter fill in routine
    private final String do_param(scanpage sp,String part2) {
        // rico
        int i;
        String rtn=null;

        if (part2!=null) {
            if (part2.equals("L")) {
                // Eval $PARAML: the number of params
                if (sp.params==null) {
                    sp.getParam(0); // Force build of params
                    if (sp.params==null) // No params
                        return "0";
                }
                return ""+sp.params.size();
            }
            if (part2.equals("A")) {
                // Eval $PARAMA
                if (sp.querystring == null)
                    return "";
                return sp.querystring;
            }
            if (part2.equals("T")) {
                // Eval $PARAMT: Returns value of the tail parameter.
                if (sp.params==null) {
                    sp.getParam(0); // Force build of params
                    if (sp.params==null) // No params
                        return "";
                }
                return sp.getParam(sp.params.size()-1);
            }
            if (part2.equals("Y")) {
                // Eval $PARAMY: Returns value of the tail-1 parameter.
                if (sp.params==null) {
                    sp.getParam(0); // Force build of params
                    if (sp.params==null) // No params
                        return "";
                }
                if (sp.params.size()<2)
                    return "";
                return sp.getParam(sp.params.size()-2);
            }

        }
        // Handle $PARAMn
        i=Integer.parseInt(part2);
        rtn=sp.getParam(i-1);
        if (rtn==null) rtn="";
        return rtn;
    }

    /**
     * Special commands, this will grow
     */
    private final String do_save(sessionInfo session,String part) {
        StringTokenizer tok= new StringTokenizer(part," -",true);
        if (tok.hasMoreTokens()) {
            String type=tok.nextToken();
            if (type.equals("SESSION")) {
                sessions.saveValue(session,part.substring(8));
            }
        }
        return "";
    }

    private final String do_crc(sessionInfo session,String part) {
        part=part.substring(1,part.length()-2);

        int crckey=calccrc32(part);
        return "<A HREF=\""+part+"+CRC"+crckey+"\">";
    }

    private final String do_session(String part2,sessionInfo session) {
        if (sessions!=null) {
            String value=sessions.getValue(session,part2);
            if (value!=null) {
                return value;
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    private final String do_newpage(scanpage sp,String part) {
        //debug( "do_newpage("+sp.getUrl()+")");
        sp.rstatus=2;
        return part;
    }

    private final String do_goto(scanpage sp,String part) {
        sp.rstatus=1;
        return part;
    }

    /**
     * Check request host against wanted host and optional backend host
     * Handles &lt;HOST [host[,backendhost]]&gt; tag
     * @return null and sp.rstatus unchanged if hosts match or in case of an unspecified host
     *         returns url and sp.rstatus 1, if redir to wantedhost requested
     */
    private final String do_host(scanpage sp, String hosts) {
        String hostDelimiters = ",";
        if (hosts==null) return null;

        //Get the requested host from the request
        if (sp.req==null) return null;
        String requestHost = sp.req.getHeader("Host");
        if (requestHost==null) return null; // No such header
        requestHost = requestHost.trim();
        if (requestHost.length() < 1) return null;

        // remove port
        int i = requestHost.indexOf(':');
        if (i==0) return null; // First char :, no host only port....
        if (i>0) requestHost = requestHost.substring(0, i);

        //try to match requested host with allowed host
        StringTokenizer allowedHosts = new StringTokenizer(hosts.trim(),hostDelimiters);

        //allowed to use this server? Take the first if no
        while (allowedHosts.hasMoreTokens()) {
            String aHost = allowedHosts.nextToken().trim();
            if (aHost.equalsIgnoreCase(requestHost)) {
                log.debug("requested host " + requestHost + " found in allowed host list");
                return null;
            }
        }
        String firstHost = null;
        int delimitIndex = hosts.indexOf(hostDelimiters);
        if (delimitIndex > -1) {
            firstHost = hosts.substring(0,delimitIndex).trim();
        } else {
            firstHost = hosts.trim();
        }

        if (log.isDebugEnabled()) log.debug("Request host: " + requestHost + "not found, first host: " + firstHost);

        // Redirect the request to the wanted host
        sp.rstatus = 1;
        String url = "http://" + firstHost + sp.getUrl();
        if (log.isDebugEnabled()) log.debug("Redirecting to "+url);
        return url;
    }

    /**
     *  try to acces a module (Must be a ProcessorModule)
     *  and replace "part" by whatever the processor returns.
     */
    private final String do_mod(scanpage sp,String part) {
        int index = part.indexOf('-');
        if (index == -1) {
            log.error("do_mod(): part (no '-'): '" + part+"' ("+sp.getUrl()+")");
            return "";
        } else {
            String moduleName = part.substring(0,index);
            String moduleCommand = part.substring(index+1,part.length());

            ProcessorModule proc = getProcessor(moduleName);
            if (proc == null) {
                log.error("do_mod(): no Processor(" + moduleName +") found for page("+sp.getUrl()+")");
                return "";
            } else {
                return proc.replace(sp, moduleCommand);
            }
        }
    }

    /**
     * give a name an you get the  processor (Interface), if procName does not exists then null is returned.
     */
    private final ProcessorModule getProcessor(String procName) {
        if (processors.containsKey(procName)) {
            return processors.get(procName);
        } else {
            Object obj = getModule (procName);
            if (obj == null) {
                log.error("getProcessor(): Not authorized or not a valid class name: " + procName);
                return null;
            } else {
                // debug(obj);
            }

            if (obj instanceof ProcessorModule) {
                //debug("servscan.getProcessor: we have a new Processor("+procName+")");
                ProcessorModule pr = (ProcessorModule) obj;
                processors.put(procName,pr);
                return pr;
            } else {
                log.error("getProcessor(): not a valid Processor("+ procName+")");
                return null;
            }
        }
    }

     private final String do_unmap(String part,sessionInfo session,scanpage sp) throws ParseException {
        part=dodollar(part,session,sp);
        return "";
    }

    private final String do_do(String part,sessionInfo session,scanpage sp) throws ParseException {
        String part1,part2;
        int pnt;
        part=dodollar(part,session,sp);

        // Chop
        pnt=part.indexOf('=');
        part2=part.substring(pnt+1);
        part1=part.substring(0,pnt);
        if (part1.indexOf("SESSION-")==0) {
            if (sessions!=null) {
                // do some sec. checks
                sessions.setValue(session,part1.substring(8),part2);
            }
        } else if (part1.indexOf("ID-")==0) {
            String name=HttpAuth.getRemoteUser(sp);
            if (name!=null && name.length()>1) {
                //setUserServletProperty(part1.substring(3),part2,0);
                id.setValue(name,part1.substring(3),part2);
            }
        }
        return "";
    }

    private final String do_include(String part2,sessionInfo session,scanpage sp) throws ParseException {
        String part="";
        part2=dodollar(part2,session,sp);
        //time(" - cookie = " + cookie + ", doinclude - dodollar", false);
        part=getfile(part2);
        //time(" - cookie = " + cookie + ", doinclude - doinclude", false);
        if (part!=null) {
            return part;
        } else {
            return "";
        }
    }

    private final String do_id(String part2,scanpage sp) {
        String name=HttpAuth.getRemoteUser(sp);
        String part="";
        if (name!=null && name.length()>1) {
            //part=getUserServletProperty(part2,0);
            part=id.getValue(name,part2);
            if (part==null) return "";
        } else {
            part="Unknown";
        }
        return part;
    }

    /**
     * This is the generic macro processor.
     * It processes the tokens after the &lt;MACRO&gt; tag and gives them in a
     * Vector to the processor specified in the page
     * Tokens supported of the form :
     * tokje=blah
     * tokje="blah"
     * tokje
     * "tokje"
     * All seperated by spaces
     * To hook into to this create a Processor (@see vpro.james.coreserver.Module
     * and @see vpro.james.modules.Processor)
     * load it in the server, give servscan permission and put the tag with
     * the module name in the html page (&lt;PROCESSOR YourProcessor&gt;, and
     * for forms &lt;INPUT TYPE="HIDDEN" NAME="PRC-VAR-PROCESSOR" VALUE="YourProcessor"&gt;)
     */
    private final String do_macro(String part,sessionInfo session,scanpage sp) throws ParseException {
        String tokje;

        Vector cmds;
        ProcessorModule tmpprocessor=null;

        part=dodollar(part,session,sp);

        int pos=part.indexOf("PROCESSOR=");
        if (pos!=-1) {
            String str=part.substring(pos+10);
            if (str.indexOf(' ')!=-1) str=str.substring(0,str.indexOf(' '));
            if (str!=null) {
                if (str.charAt(0)=='"') str=str.substring(1,str.length()-1);
                tmpprocessor=getProcessor(str);

                if( tmpprocessor==null ) {
                    if (sp.processor!=null)
                        log.warn("do_macro(): No processor("+str+") found for page("+sp.getUrl()+"), but scanpage has one.");
                    else
                        log.error("do_macro(): No processor("+str+") found for page("+sp.getUrl()+")");
                }
            }
        }

        cmds=tokenizestring(part);

        if (tmpprocessor!=null) {
            tokje=htmlgen.getHTMLElement(sp, tmpprocessor,cmds);
        } else if (sp.processor!=null) {
            tokje=htmlgen.getHTMLElement(sp, sp.processor,cmds);
        } else {
            log.error("do_macro(): No processor() specified in page("+sp.getUrl()+")");
            tokje="<b> No Processor specified in page </b><br />";
        }
        return tokje;
    }

    /**
     * Method that tokenizes a string into pieces
     * Tokens supported of the form :
     * tokje=blah
     * tokje="blah"
     * tokje
     * "tokje"
     * All seperated by spaces
     */
    private Vector tokenizestring(String part) {
        String current="",tokje;
        boolean inDQuote=false;
        Vector cmds=new Vector();
        StringTokenizer tok;

        // OK lets chop the parts
        tok= new StringTokenizer(part," \"=",true);
        while(tok.hasMoreTokens()) {
            tokje=tok.nextToken();
            if (inDQuote) {
                if (tokje.equals("\"")) {
                    inDQuote=false;
                    current+=tokje;
                } else {
                    current+=tokje;
                }
            } else {
                if (tokje.equals("\"")) {
                    inDQuote=true;
                    current+=tokje;
                } else if (tokje.equals(" ")) {
                    cmds.addElement(current);
                    current="";
                } else if (tokje.equals("=")) {
                    current+=tokje;
                } else {
                    current+=tokje;
                }
            }
        }
        if (!current.equals("")) {
            cmds.addElement(current);
        }
        return cmds;
    }

    /**
     * handle if/then/elseif/\/if
     */
    String do_conditions_lif(String body,sessionInfo session,scanpage sp) throws ParseException {
        StringBuffer buffer = new StringBuffer();

        int ifpos=0;
        buffer = new StringBuffer();
        ifpos=body.indexOf("<LIF",ifpos);
        while (ifpos!=-1) {
            // append the part before the <IF
            buffer.append(body.substring(0,ifpos));

            // unmap this one IF, result will be the correct part+rest
            buffer.append(do_if(body.substring(ifpos+4),session,sp));

            // convert buffer back to body and clear buffer
            body=buffer.toString();
            buffer=new StringBuffer();

            // try to get a new </IF pos
            ifpos=body.indexOf("<LIF");
        }
        return body;
    }

    String do_if(String body,sessionInfo session,scanpage sp) throws ParseException {
        // first hunt down the command
        int pos = body.indexOf('>');
        if (pos!=-1) {
            String cmd=body.substring(1,pos);
            boolean state=do_vals(cmd,session,sp);
            body=body.substring(pos+1);

            boolean found=false;
            int depth=0;
            pos=0;
            int beg=0;
            int end=0;
            int els=-1;
            while (!found) {
                beg=body.indexOf("<IF",pos);
                end=body.indexOf("</IF>",pos);
                if (beg==-1 || beg>end) {
                    if (depth==0) {
                        found=true;
                        if (els==-1) {
                            els=body.indexOf("<ELSE>",pos);
                            if (els>end) els=-1;
                        }
                    } else {
                        depth--;
                        pos=end+2;
                    }
                } else {
                    if (depth==0 && els==-1) {
                        els=body.indexOf("<ELSE>",pos);
                        if (els>end && els<beg) els=-1;
                    }
                    pos=beg+2;
                    depth++;
                }
            }
            if (els==-1) {
                if (state) {
                    body=body.substring(0,end)+body.substring(end+5);
                } else {
                    body=body.substring(end+5);
                }
            } else {
                if (state) {
                    body=body.substring(0,els)+body.substring(end+5);
                } else {
                    body=body.substring(els+6,end)+body.substring(end+5);
                }
            }
        } else {
            log.error("do_if(): no end on if command");
        }
        return body;
    }


    /**
     * handle if/then/elseif/\/if
     */
    String do_conditions(String body,sessionInfo session,scanpage sp) throws ParseException {
        StringBuffer buffer = new StringBuffer();

        int ifpos=0;
        buffer = new StringBuffer();
        ifpos=body.indexOf("<IF",ifpos);
        while (ifpos!=-1) {
            // append the part before the <IF
            buffer.append(body.substring(0,ifpos));

            // unmap this one IF, result will be the correct part+rest
            buffer.append(do_if(body.substring(ifpos+3),session,sp));

            // convert buffer back to body and clear buffer
            body=buffer.toString();
            buffer=new StringBuffer();

            // try to get a new </IF pos
            ifpos=body.indexOf("<IF");
        }
        return body;
    }

    private String do_list(String cmd,String template, sessionInfo session,scanpage sp) throws ParseException {
        long ll1,ll2;
        StringBuffer rtn=new StringBuffer();
        ProcessorModule tmpprocessor=null;
        String command=null;
        String sorted=null;
        String sortedpos=null;
        String str,key=null;
        StringTagger tagger=null;
        Object obj;
        List t,cmds,result;
        int numitems=1,maxitems=-1,curitem=0,offset=0;
        int maxtotal=-1;

        ll1=System.currentTimeMillis();
        cmd=dodollar(cmd,session,sp);
        cmd=Strip.whitespace(cmd,Strip.BOTH);
        String oldcmd=cmd;

        if (sp.reload) {
            if (cmd.indexOf(" CACHE=")!=-1) {
                String rst=scancache.get("HENK","/LISTS/"+cmd+template,sp);
                if (rst!=null) {
                    return rst;
                }
            }
        }

        cmds=tokenizestring(cmd);

        t=new Vector();
        int pos=cmd.indexOf('"');
        if (pos==0) {
            pos=cmd.indexOf('"',pos+1);
            if (pos!=-1) {
                command=cmd.substring(0,pos+1);
                if ((pos+1)!=cmd.length()) {
                    cmd=cmd.substring(pos+2);
                } else {
                    cmd="";
                }
            }
        } else {
            pos=cmd.indexOf(' ');
            if (pos!=-1) {
                command=cmd.substring(0,pos);
                cmd=cmd.substring(pos+1);
            }
        }

        // Process commands to list if any parse them but they can be overiden
        // by the processor
        if (cmds.size()>1) {
            tagger=new StringTagger(cmd,' ','=',',','"');

            /*
            str=tagger.Value("ITEMS");
            try {
                numitems=Integer.parseInt(str);
            } catch (NumberFormatException x) {
                numitems=1;
            }
            */

            str=tagger.Value("MAX");
            try {
                maxitems=Integer.parseInt(str);
            } catch (NumberFormatException x) {
                maxitems=-1;
            }
            maxtotal=maxitems;

            str=tagger.Value("OFFSET");
            try {
                offset=Integer.parseInt(str);
            } catch (NumberFormatException x) {
                offset=0;
            }

            str=tagger.Value("AUTORANGE");
            if (str!=null && maxitems!=-1) {
                int u=str.indexOf('/');
                if (u!=-1) {
                    key=str.substring(0,u);
                    String acmd=str.substring(u+1);
                    String value=sessions.getValue(session,key);
                    try {
                        offset=Integer.parseInt(value);
                    } catch (NumberFormatException x) {
                        offset=0;
                    }
                    if (acmd.equals("NEXT")) {
                        //sessions.setValue(session,key,""+(offset+maxitems));
                        offset=offset+maxitems;
                    } else
                    if (acmd.equals("PREV")) {
                        //sessions.setValue(session,key,""+(offset-maxitems));
                        offset=offset-maxitems;
                    } else {
                        offset=0;
                        sessions.setValue(session,key,"0");
                    }
                }
            }

            sorted=tagger.Value("SORTED");
            sortedpos=tagger.Value("SORTPOS");

            str=tagger.Value("PROCESSOR");
            if (str!=null) {
                if (str.charAt(0)=='"') str=str.substring(1,str.length()-1);
                tmpprocessor=getProcessor(str);
            }

        } else {
            tagger=new StringTagger("",' ','=',',','"');
        }

        if (sessions!=null && session!=null && key!=null) {
            sessions.setValue(session,key,""+offset);
        }

        String version=tagger.Value("VERSION");
        if (version==null || version.equals("1.0")) {
            // is there a proccesor defined ?
            if (sp.processor!=null || tmpprocessor!=null) {
                // Call the processor to get the real info this might override
                // some of the layout calls like numitems and sort etc etc
                if (tmpprocessor==null) {
                    result=sp.processor.getList(sp,tagger,command);
                } else {
                    result=tmpprocessor.getList(sp,tagger,command);
                }
                // do we have a result ifso do the layout stuff
                if (result!=null) {
                    // added reverse, should be changed soon
                    if (sorted!=null && sorted.equals("ALPHA")) result=SortedVector.SortVector(result);

                    // get the number of items set by the user or by the processor
                    str=tagger.Value("ITEMS");
                    if (str!=null) {
                        try {
                            numitems=Integer.parseInt(str);
                        } catch (NumberFormatException x) {
                            numitems=1;
                        }
                    } else {
                        numitems=1;
                    }

                    if (sorted!=null && sorted.equals("MALPHA")) {
                        if (sortedpos!=null) {
                            result=doMAlphaSort(result,sortedpos,numitems);
                        } else {
                            result=doMAlphaSort(result,"1",numitems);
                        }
                    }

                    if (sorted!=null && sorted.equals("REVERSE")) result=reverse(result,numitems);

                    curitem=1;
                    int jsize=result.size();
                    if (maxtotal==-1 || maxtotal>(jsize/numitems)) maxtotal=jsize/numitems;
                    if (maxitems==-1) {
                        maxitems=jsize;
                    } else {
                        maxitems=(maxitems*numitems)+(offset*numitems);
                        if (maxitems>jsize) {
                            maxitems=jsize;
                            if (key!=null) {
                                //sessions.setValue(session,key,"-1");
                            }
                        }
                    }
                    offset*=numitems;
                    for (int j=offset;j<maxitems;j+=numitems) {
                        t.clear();
                        for (int i=0;i<numitems;i++) {
                            try {
                                obj=result.get(j+i);
                                if (obj==null) obj="NULL";
                            } catch (ArrayIndexOutOfBoundsException r) {
                                obj="No Such Element";
                            }
                            t.add(obj);
                        }
                        rtn.append(processtemplate(t,template,curitem+(offset/numitems),jsize/numitems,curitem,numitems));
                        //rtn.append(processtemplate(t,template,curitem+(offset/numitems),maxtotal,curitem));
                        curitem++;
                    }
                    // org.mmbase lastlistitem=curitem;
                } else {
                    rtn.append(" Processor failed to process command <br />");
                    log.error("do_list(): Processor failed to process command : "+cmd+" ("+sp.processor+") ("+tmpprocessor+")");
                }
            } else {
                rtn.append(" No Processor specified in page <br />");
            }
        } else if (version.equals("2.0")) {
            // is there a proccesor defined ?
            //debug("do_list(): LIST 2.0 -> Start");
            long ltime1=System.currentTimeMillis();
            if (sp.processor!=null || tmpprocessor!=null) {
                // Call the processor to get the real info this might override
                // some of the layout calls like numitems and sort etc etc
                if (tmpprocessor==null) {
                    result=sp.processor.getList(sp,tagger,command);
                } else {
                    result=tmpprocessor.getList(sp,tagger,command);
                }
                long ltime2=System.currentTimeMillis();
                //debug("do_list(): LIST 2.0 -> GETLIST="+(ltime2-ltime1));
                ltime1=ltime2;

                // do we have a result ifso do the layout stuff
                if (result!=null) {
                    // added reverse, should be changed soon
                    if (sorted!=null && sorted.equals("ALPHA")) result=SortedVector.SortVector(result);

                    // get the number of items set by the user or by the processor
                    str=tagger.Value("ITEMS");
                    if (str!=null) {
                        try {
                            numitems=Integer.parseInt(str);
                        } catch (NumberFormatException x) {
                            numitems=1;
                        }
                    } else {
                        numitems=1;
                    }

                    if (sorted!=null && sorted.equals("REVERSE")) result=reverse(result,numitems);

                    //debug("PART="+template);

                    String listprepart=null;
                    String listendpart=null;
                    // if there is a LISTLOOP split it in 3 parts
                    int listprepos=template.indexOf("<LISTLOOP>");
                    int listendpos=template.indexOf("</LISTLOOP>");
                    if (listprepos!=-1) {
                        // oke found a listloop
                        listprepart=template.substring(0,listprepos);
                        listendpart=template.substring(listendpos+11);
                        template=template.substring(listprepos+10,listendpos);
                    }

                    //debug("do_list(): PRE="+listprepart+"*");
                    //debug("do_list(): PART="+template+"*");
                    //debug("do_list(): END="+listendpart+"*");

                    // new part handles the 'prepart' of LISTLOOP
                    // ends new part handles the 'prepart' of LISTLOOP

                    curitem=1;
                    int jsize=result.size();
                    if (maxtotal==-1 || maxtotal>(jsize/numitems)) maxtotal=jsize/numitems;
                    if (maxitems==-1) {
                        maxitems=jsize;
                    } else {
                        maxitems=(maxitems*numitems)+(offset*numitems);
                        if (maxitems>jsize) {
                            maxitems=jsize;
                            if (key!=null) {
                                //sessions.setValue(session,key,"-1");
                            }
                        }
                    }
                    offset*=numitems;

                    // added for prelist/endlist
                    if (listprepart!=null && !listprepart.equals("")) {
                        rtn.append(doPrePart(listprepart,jsize/numitems,offset/numitems,maxitems/numitems,numitems));
                    }

                    for (int j=offset;j<maxitems;j+=numitems) {
                        t.clear();
                        for (int i=0;i<numitems;i++) {
                            try {
                                obj=result.get(j+i);
                                if (obj==null) obj="NULL";
                            } catch (ArrayIndexOutOfBoundsException r) {
                                obj="No Such Element";
                            }
                            t.add(obj);
                        }
                        rtn.append(processtemplate(t,template,curitem+(offset/numitems),jsize/numitems,curitem,numitems));
                        //rtn.append(processtemplate(t,template,curitem+(offset/numitems),maxtotal,curitem));
                        curitem++;
                    }
                    // org.mmbase lastlistitem=curitem;

                    // added for prelist/endlist
                    if (listendpart!=null && !listendpart.equals("")) {
                        rtn.append(doEndPart(listendpart,jsize/numitems,offset/numitems,maxitems/numitems,numitems));
                    }

                    ltime2=System.currentTimeMillis();
                    //debug("do_list(): LIST 2.0 -> PROCESSTEMPLATE="+(ltime2-ltime1));
                    ltime1=ltime2;
                } else {
                    rtn.append(" Processor failed to process command <br />");
                    log.error("do_list(): Processor failed to process command : "+cmd+" ("+sp.processor+") ("+tmpprocessor+")");
                }
            } else {
                rtn.append(" No Processor specified in page <br />");
            }
            //debug("do_list(): LIST 2.0 -> End");
        }

        // -----------------------------------

        if (cmd.indexOf(" CACHE=")!=-1) {
            scancache.put("HENK","/LISTS/"+oldcmd+template,rtn.toString());
        }
        ll2=System.currentTimeMillis();
        if (log.isDebugEnabled() && (ll2-ll1)>300) {
            log.debug("do_list(): time("+(ll2-ll1)+" ms)");
        }
        return rtn.toString();
    }

    private String processtemplate(List v,String template, int pos, int last, int rpos, int numitems) {
        int precmd=0,postcmd=0,prepostcmd=0,index;
        StringBuffer dst=new StringBuffer();
        String cmd="$ITEM";
        if (template==null) return "No Template";
        if (v==null) return "Vector is null";
        while ((precmd=template.indexOf(cmd,postcmd))!=-1) {
            dst.append(template.substring(postcmd,precmd));
            prepostcmd=precmd+cmd.length();
            postcmd=precmd+6;
            try {
                index=Integer.parseInt(template.substring(prepostcmd,postcmd));
                try {
                    index=Integer.parseInt(template.substring(prepostcmd,postcmd+1));
                    postcmd++;
                } catch (Exception g) {}
                index--;
                try {
                    dst.append((v.get(index)).toString());
                } catch (ArrayIndexOutOfBoundsException e) {}
            } catch (NumberFormatException e) {
                //index=1;
                try {
                    if (template.charAt(prepostcmd)=='P') {
                        dst.append(""+pos);
                    } else if (template.charAt(prepostcmd)=='L') {
                        dst.append(""+last);
                    } else if (template.charAt(prepostcmd)=='R') {
                        dst.append(""+rpos);
                    } else if (template.charAt(prepostcmd)=='S') {
                        dst.append(""+numitems);
                    } else if (template.charAt(prepostcmd)=='F') {
                        if (pos==last) {
                            dst.append("YES");
                        } else {
                            dst.append("NO");
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException f) {}
            }
        }
        dst.append(template.substring(postcmd));
        return dst.toString();
    }

    // how do we handle multihosts in servlet API now ?
    Hashtable getRoots() {
        /*
        int pos;
        String tmp,tmp2;
        Hashtable result=new Hashtable();
        result.put("www",DocumentRoot);

        for (Enumeration e=getInitParameters().keys();e.hasMoreElements();) {
            tmp=(String)e.nextElement();
            tmp2=(String)getInitParameter(tmp);
            pos=tmp.indexOf("Root.");
            if (pos==0) {
                result.put(tmp.substring(5),tmp2);
            }
        }
        */
        Hashtable result=new Hashtable();
        return result;
    }

    /**
     * This does the handling of the Form inputs.
     * It calls the processor to handle the PRC-CMD's it finds after
     * it has done the PRC-VAR's fill in.
     */
    public final void do_proc_input(String rq_line,HttpPost poster,Hashtable proc_var,Hashtable proc_cmd,scanpage sp) {
        String part;
        // First find the processor of this page
        if ((part=(String)proc_var.get("PROCESSOR"))!=null) {
            //processor=(Processor)getModule(part);
            sp.processor=getProcessor(part);
            if (sp.processor!=null) {
                sp.processor.process(sp,proc_cmd,proc_var);
            } else {
                log.error("do_proc_input(): Processor("+part+") is not loaded in server for page("+sp.getUrl()+")");
            }
        } else {
            log.warn("do_proc_input(): No Processor specified : "+rq_line);
            log.info("do_proc_input(): proc_var="+proc_var);
            log.info("do_proc_input(): proc_cmd="+proc_cmd);
            return;
        }
        return;
    }

    public String calcPage(String part2,scanpage sp,int cachetype) {

        if (log.isDebugEnabled()) log.debug("calcPage("+part2+","+sp.getUrl()+","+cachetype+")");

        try {
            String filename,paramline=null;

            // we now may have a a param setup like part.shtml?1212+1212+1212
            // split it so we can load the file and get the params
            int pos=part2.indexOf('?');
            if (pos!=-1) {
                filename=part2.substring(0,pos);
                paramline=part2.substring(pos+1);
                //((worker)req).setParamLine(paramline);
                sp.setParamsLine(paramline);
                if (sp.req_line==null) sp.req_line=filename;
                if (log.isDebugEnabled()) log.debug("calcPage(): setting paramline="+paramline);
            } else {
                filename=part2;
            }
            if ((sp.mimetype==null) || sp.mimetype.equals("")) {
                sp.mimetype=getMimeTypeFile(filename);
            }

            if (log.isDebugEnabled()) {
                log.debug("calcPage(): filename="+filename);
                log.debug("calcPage(): paramline="+paramline);
                log.debug("calcPage(): mimetype="+sp.mimetype);
            }
            sp.body=getfile(filename);

            if (sp.body!=null) {

                String wantCache=null;

                if (sp.body.indexOf("<CACHE PAGE")!=-1) {
                    wantCache="PAGE";
                }
                if (sp.body.indexOf("<CACHE HENK")!=-1) {
                    wantCache="HENK";
                }
                sp.wantCache=wantCache;
                // end cache
                // unlike include we need to map this ourselfs before including it
                // in this page !!
                //part=handle_line(part,req);
                sp.body=handle_line(sp.body,sp.session,sp);
                if (wantCache!=null) {
                    scancache.newput2(wantCache,part2,sp.body,cachetype, sp.mimetype);
                }

                return sp.body;
            } else {
                return "";
            }
        } catch (Exception r) {
            log.error("calcPage("+part2+","+sp.getUrl()+","+cachetype+"): "+r);
            r.printStackTrace();
            return "";
        }
    }

    /**
     * Start a process to calculate a page in the background
     * used by scancache to generate new pages for expired ones
     * @param sp The request of the page (duplicated from the original)
     * @param uri The uri of the request
     */
    public synchronized void processPage(scanpage sp,String uri) {
        if (!pagesprocessing.containsKey(uri)) {
            log.debug("processPage : creating process for: "+uri);
            PageProcess pp=new PageProcess(this,sp,uri);
            pagesprocessing.put(uri,pp);
            log.debug("processPage : currently running "+pagesprocessing);
        } else {
            log.debug("processPage : page already in progress: "+uri);
        }
    }

    /**
     * Remove a PageProcess from the list of pages currently being calculated
     * @param uri Uri of the request being calculated
     */
    public void removeProcess(String uri) {
        pagesprocessing.remove(uri);
    }

    /**
     * davzev changed method syntax from int sortonnumber to String sortonnumbers.
     * Sort the lines in the vector, to do this we need to first group
     * them and then sort them by item number defined
     * @param input - the result Vector unsorted.
     * @param sortonnumbers - a String with colpositions eg. "6:4" denoting the
     * order and colpositions for the sort.
     * @param numberofitems - integer with the amount of items.
     * @return a Vector with sorted items.
     */
    Vector doMAlphaSort(List input,String sortonnumbers,int numberofitems) {

        //SortedVector output = new SortedVector(new RowVectorCompare(sortonnumber));
        if (log.isDebugEnabled()) log.debug("doMAlphaSort: Sorting using MultiColCompare("+sortonnumbers+ ")");
        SortedVector output = new SortedVector(new MultiColCompare(sortonnumbers));
        // first create vectors with numberofitems per vector
        Enumeration einput = Collections.enumeration(input);
        while (einput.hasMoreElements()) {
            Vector row=new Vector();
            for (int i=0;i<numberofitems;i++) {
                row.add(einput.nextElement());
            }
            output.addSorted(row);
        }
        Vector result=new Vector();
        Enumeration eoutput=output.elements();
        while (eoutput.hasMoreElements()) {
            Vector row=(Vector)eoutput.nextElement();
            Enumeration erow=row.elements();
            while (erow.hasMoreElements()) {
                result.add(erow.nextElement());
            }
        }
        return result;
    }

    private String do_transaction(String template, sessionInfo session,scanpage sp) throws ParseException {
        transactionhandler.handleTransaction(template,session,sp);
        return "";
    }


    public static int calccrc32(String str) {
        CRC32 crc=new CRC32();
        str=""+crcseed+str+crcseed;
        byte dst[]=new byte[str.length()];

        str.getBytes(0,str.length(),dst,0);
        crc.update(dst);
        return (int)crc.getValue();
    }
}
