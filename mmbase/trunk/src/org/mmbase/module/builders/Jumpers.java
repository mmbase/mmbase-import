/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.util.logging.*;
import org.mmbase.cache.Cache;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.*;

/**
 * Maintains jumpers for redirecting urls.
 * The data stored in this builder is used to redirect urls based ons a specific key.
 * The jumpers builder is called from the {@link org.mmbase.servlet.servjumpers} servlet.
 * <br />
 * The jumpers builder can be configured using two properties:<br />
 * <ul>
 * <li><code>JumperCacheSize</code> determines the size of the jumper cache (in nr of items).
 *                                 The default size is 1000.</li>
 * <li><code>JumperNotFoundURL</code> Determines the default url (such as a home page or error page)
 *              when no jumper is found. If not specified nothing will be done if no jumper is found.</li>
 * </ul>
 * <br />
 * XXX:Note that this builder is called directly from a servlet, and may therefor
 * be bound to the cloud context rather than a cloud.
 * This would mean that in a multi-cloud environment, this builder will be shared.
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadocs)
 * @version $Id: Jumpers.java,v 1.24 2004-02-03 15:39:16 michiel Exp $
 */
public class Jumpers extends MMObjectBuilder {

    /**
     * Default Jump Cache Size.
     * Customization can be done through the central caches.xml
     * Make an entry under the name "JumpersCache" with the size you want.
     */
    private static final int DEFAULT_JUMP_CACHE_SIZE = 1000;

    private static final Logger log = Logging.getLoggerInstance(Jumpers.class);

    /**
     * Cache for URL jumpers.
     */
    protected JumpersCache jumpCache = new JumpersCache(DEFAULT_JUMP_CACHE_SIZE);

    /**
     * Default redirect if no jumper can be found.
     * If this field is <code>null</code>, a url will not be 'redirected' if the
     * search for a jumper failed. This may cause a 404 error on your server if
     * the path specified is unavailable.
     * However, you may need it if other servlets rely on specific paths
     * that would otherwise be caught by the jumper servlet.
     * The value fo this field is set using the <code>JumperNotFoundURL</code>
     * property in the builder configuration file.
     */
    protected static String jumperNotFoundURL;

    /**
     * Initializes the builder.
     * Determines the jumper cache size, and initializes it.
     * Also determines the default jumper url.
     * @return always <code>true</code>
     */
    public boolean init() {
        super.init();

        String tmp;
        jumperNotFoundURL = getInitParameter("JumperNotFoundURL");
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
            if (url == null) {
                // Search jumpers with name;
                NodeSearchQuery query = new NodeSearchQuery(this);
                FieldDefs fieldDefs = getField("name");
                StepField field = query.getField(fieldDefs);
                FieldDefs numberFieldDefs = getField("number");
                StepField numberField = query.getField(numberFieldDefs);
                BasicSortOrder sortOrder = query.addSortOrder(numberField); // use 'oldest' jumper
                BasicFieldValueConstraint cons = new BasicFieldValueConstraint(field, key);
                query.setConstraint(cons);
                query.setMaxNumber(1);

                try {
                    List resultList = getNodes(query);
                    if (resultList.size() > 0) {
                        node = (MMObjectNode) resultList.get(0);
                        url = node.getStringValue("url");                 
                    }
                } catch (SearchQueryException sqe) {
                    log.error(sqe.getMessage());
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
                if (url.equalsIgnoreCase("NOREDIRECT")) {  // return null if the url specified is NOREDIRECT
                    url=null;
                }
            } else {
                log.debug("No jumper found for key '"+key+"'");
                url=jumperNotFoundURL;
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

class JumpersCache extends Cache {
    public String getName() {
        return "JumpersCache";
    }

    public String getDescription() {
        return "Cache for Jumpers";
    }

    JumpersCache(int size) {
        super(size);
        putCache(this);
    }
}
