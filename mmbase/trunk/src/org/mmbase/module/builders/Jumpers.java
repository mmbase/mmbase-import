/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
//import java.sql.*;
import org.mmbase.module.core.*;
//import org.mmbase.module.database.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * Maintains jumpers for redirecting urls.
 * The data stored in this builder is used to redirect urls based ons a specific key.
 * The jumpers builder is called from the {@link org.mmbase.servlet.servjumpers} servlet.
 * <br>
 * The jumpers builder can be configured using two properties:<br>
 * <ul>
 * <li><code>JumperCacheSize</code> determines the size of the jumper cache (in nr of items).
 *                                 The default size is 1000.</li>
 * <li><code>JumperNotFoundURL</code> Determines the default url (such as a home page or error page)
 *              when no jumper is found. The default is <code>/index.html</code>
 *              Specifying <code>NOREDIRECT</code> means an unfound jumper is not
 *              redirected. Note that you can also specify <code>NOREDIRECT</code>
 *              as a redirecty value of a key in the jumpers builder, excluding specific
 *              urls from being redirected, rather than all of them.
 * </ul>
 * <br>
 * XXX:Note that this builder is called directly from a servlet, and may therefor
 * be bound to the cloud context rather than a cloud.
 * This would mean that in a multi-cloud environment, this builder will be shared.
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadocs)
 * @version 24 Apr 2001
 */
public class Jumpers extends MMObjectBuilder {

    /**
     * Jump Cache Size.
     * Should be made non-final if we want to customize, see example code in init.
     */
    private static final int JUMP_CACHE_SIZE = 1000;

    // logger
    private static Logger log = Logging.getLoggerInstance(Jumpers.class.getName());

    /**
     * Cache for URL jumpers.
     * (better to initialize in init, as it will allow for customizing
     * cache size, see example code)
     */
    LRUHashtable jumpCache = new LRUHashtable(JUMP_CACHE_SIZE);

    /**
     * Default redirect if no jumper can be found.
     * If this field is <code>null</code>, a url will not be 'redirected' if the
     * search for a jumper failed. This may cause a 404 error on your server if
     * the path specified is unavailable.
     * However, you may need it if other servlets rely on specific paths
     * that would otherwise be caught by the jumper servlet.
     * The value fo this field is set using the <code>JumperNotFoundURL</code>
     * property in the builder configuration file. If this property is
     * set to <code>NOREDIRECT</code> the field will be set to <code>null</code>.
     */
    private static String jumperNotFoundURL = "/index.html";

    /**
     * Initializes the builder.
     * Determines the jumper cache size, and initializes it.
     * Also determines the default jumper url.
     * @return always <code>true</code>
     */
    public boolean init() {
        super.init();

        String tmp;
/*  example code for specifying JumperCacheSize

        tmp=getInitParameter("JumperCacheSize");
        if (tmp!=null) {
            try {
                JUMP_CACHE_SIZE = Integer.parseInt(tmp);
            } catch (Exception e) {}
        }
        jumpCache = new LRUHashtable(JUMP_CACHE_SIZE);
*/
        tmp=getInitParameter("JumperNotFoundURL");
        if (tmp!=null) {
            if (tmp.equalsIgnoreCase("NOREDIRECT")) {
                jumperNotFoundURL = null;
            } else {
                jumperNotFoundURL = tmp;
            }
        }
        return true;
    }

    public String getGUIIndicator(String field,MMObjectNode node) {
        if (field.equals("url")) {
            String url=node.getStringValue("url");
            return("<A HREF=\""+url+"\" TARGET=\"extern\">"+url+"</A>");
        } else if (field.equals("id")) {
            return(""+node.getIntValue(field));
        } else if (field.equals("name")) {
            return(""+node.getStringValue(field));
        }
        return(null);
    }

    /**
     * Retrieves a jumper for a specified key.
     * @param tok teh tokenizer, in which the first token is the key to search for.
     * @return the found alternate url.
     */
    public String getJump(StringTokenizer tok) {
        String key = tok.nextToken();
        return getJump(key);
    }

    /**
     * Removes a specified key from the cache.
     * @param key the key to remove
     */
    public void delJumpCache(String key) {
        if (key!=null) {
            log.debug("Jumper builder - Removing "+key+" from jumper cache");
            jumpCache.remove(key);
        }
    }

    /**
     * Retrieves a jumper for a specified key.
     * @param key the key to search for.
     * @return the found alternate url.
     */
    public String getJump(String key) {
        String url = null;
        MMObjectNode node;
        int ikey;

        if (key.equals("")) {
            url=jumperNotFoundURL;
        } else {
            try {
                ikey=Integer.parseInt(key);
            } catch (NumberFormatException e) {
                ikey=-1;
            }
            url = (String)jumpCache.get(key);
            if (log.isDebugEnabled()) {
                if (url!=null) {
                    log.debug("Jumper - Cache hit on "+key);
                } else {
                    log.debug("Jumper - Cache miss on "+key);
                }
            }
            if (url==null) {
                // Search jumpers with name;
                log.debug("Search jumpers with name="+key);
                //Enumeration res=search("WHERE name='"+key+"'");
                Enumeration res=search("name=='"+key+"'");
                if (res.hasMoreElements()) {
                    node=(MMObjectNode)res.nextElement();
                    url=node.getStringValue("url");
                }
            }
            if (url==null) {
                // Search jumpers with number (parent);
                log.debug("Search jumpers with id="+ikey);
                if (ikey>=0) {
                    Enumeration res=search("WHERE id="+ikey);
                    if (res.hasMoreElements()) {
                        node=(MMObjectNode)res.nextElement();
                        url=node.getStringValue("url");
                    }
                }
            }
            if (url==null) {
                // no direct url call its builder
                if (ikey>=0) {
                    node=getNode(ikey);
                    if (node!=null) {
                        String buln=mmb.getTypeDef().getValue(node.getIntValue("otype"));
                        MMObjectBuilder bul=mmb.getMMObject(buln);
                        log.debug("getUrl through builder with name="+buln+" and id "+ikey);
                        if (bul!=null) url=bul.getDefaultUrl(ikey);
                    }
                }
            }
            if (url!=null && url.length()>0 && !url.equals("null")) {
                jumpCache.put(key,url);
            } else {
                log.debug("No jumper found for key '"+key+"'");
                url=jumperNotFoundURL;
            }
            if (url.equalsIgnoreCase("NOREDIRECT")) {  // return null if the url specified is NOTREDIRECT
                url=null;
            }
        }
        return url;
    }

    /**
     * Handles changes made to a node by a remote server.
	 * @param machine Name of the machine that changed the node.
     * @param number the number of the node that was added, removed, or changed.
     * @param builder the name of the builder of the changed node (should be 'jumpers')
     * @param ctype the type of change
     */
    public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
        super.nodeRemoteChanged(machine,number,builder,ctype);
        return nodeChanged(machine,number,builder,ctype);
    }

    /**
     * Handles changes made to a node by this server.
	 * @param machine Name of the machine that changed the node.
     * @param number the number of the node that was added, removed, or changed.
     * @param builder the name of the builder of the changed node (should be 'jumpers')
     * @param ctype the type of change
     */
    public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
        super.nodeLocalChanged(machine,number,builder,ctype);
        return nodeChanged(machine,number,builder,ctype);
    }

    /**
     * Clears the jump cache if a jumper was added, removed, or changed.
	 * @param machine Name of the machine that changed the node.
     * @param number the number of the node that was added, removed, or changed.
     * @param builder the name of the builder of the changed node (should be 'jumpers')
     * @param ctype the type of change
     */
    public boolean nodeChanged(String machine,String number,String builder,String ctype) {
        log.debug("Jumpers="+machine+" " +builder+" no="+number+" "+ctype);
        jumpCache.clear();
        return true;
    }
}
