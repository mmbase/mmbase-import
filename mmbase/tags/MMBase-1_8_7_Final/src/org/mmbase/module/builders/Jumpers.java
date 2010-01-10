/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package org.mmbase.module.builders;

import java.util.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Field;
import org.mmbase.cache.Cache;
import org.mmbase.core.CoreField;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.functions.*;

/**
 * Maintains jumpers for redirecting urls. The data stored in this builder is
 * used to redirect urls based ons a specific key. The jumpers builder is called
 * from the {@link org.mmbase.servlet.JumpersFilter}. <br />
 * The jumpers builder can be configured using two properties: <br />
 * <ul>
 * <li><code>JumperCacheSize</code> determines the size of the jumper cache
 * (in nr of items). The default size is 1000.</li>
 * <li><code>JumperNotFoundURL</code> Determines the default url (such as a
 * home page or error page) when no jumper is found. If not specified nothing
 * will be done if no jumper is found.</li>
 * </ul>
 * <br />
 * XXX:Note that this builder is called directly from a servlet, and may
 * therefor be bound to the cloud context rather than a cloud. This would mean
 * that in a multi-cloud environment, this builder will be shared.
 *
 * @application Tools, Jumpers
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadocs)
 * @version $Id$
 */
public class Jumpers extends MMObjectBuilder {

    /**
     * Default Jump Cache Size. Customization can be done through the central
     * caches.xml Make an entry under the name "JumpersCache" with the size you
     * want.
     */
    private static final int DEFAULT_JUMP_CACHE_SIZE = 1000;

    private static final Logger log = Logging.getLoggerInstance(Jumpers.class);

    /**
     * Cache for URL jumpers.
     */
    protected JumpersCache jumpCache = new JumpersCache(DEFAULT_JUMP_CACHE_SIZE);

    /**
     * Default redirect if no jumper can be found. If this field is
     * <code>null</code>, a url will not be 'redirected' if the search for a
     * jumper failed. This may cause a 404 error on your server if the path
     * specified is unavailable. However, you may need it if other servlets rely
     * on specific paths that would otherwise be caught by the jumper servlet.
     * The value fo this field is set using the <code>JumperNotFoundURL</code>
     * property in the builder configuration file.
     */
    protected static String jumperNotFoundURL;

    /**
     * Initializes the builder. Determines the jumper cache size, and
     * initializes it. Also determines the default jumper url.
     *
     * @return always <code>true</code>
     */
    public boolean init() {
        super.init();
        jumperNotFoundURL = getInitParameter("JumperNotFoundURL");
        return true;
    }

    /**
     * @since MMBase-1.7.1
     */
    public String getGUIIndicator(MMObjectNode node, Parameters args) {
        String field = (String) args.get("field");
        if (field == null || field.equals("url")) {
            String url = node.getStringValue("url");
            HttpServletRequest req = (HttpServletRequest) args.get(Parameter.REQUEST);
            HttpServletResponse res = (HttpServletResponse) args.get(Parameter.RESPONSE);
            String link;
            if (url.startsWith("http:") || url.startsWith("https:") || url.startsWith("ftp:")) {
                link = url;
            } else if (!url.startsWith("/")) { // requested relative to context
                // path
                String context = req == null ? MMBaseContext.getHtmlRootUrlPath() : req.getContextPath();
                String u = context + "/" + url;
                link = res == null ? u : res.encodeURL(u);
            } else {
                String context = req == null ? MMBaseContext.getHtmlRootUrlPath() : req.getContextPath();
                // request relative to host's root
                if (url.startsWith(context + "/")) { // in this context!
                    String u = url.substring(context.length() + 1);
                    link = res == null ? u : res.encodeURL(u);
                } else { // in other context
                    link = url;
                }
            }
            return "<a href=\"" + link + "\" target=\"extern\">" + url + "</a>";
        } else {
            return super.getGUIIndicator(node, args);
        }

    }

    /**
     * Retrieves a jumper for a specified key.
     *
     * @param tok teh tokenizer, in which the first token is the key to search
     * for.
     * @return the found alternate url.
     */
    public String getJump(StringTokenizer tok) {
        String key = tok.nextToken();
        return getJump(key);
    }

    /**
     * Removes a specified key from the cache.
     *
     * @param key the key to remove
     */
    public void delJumpCache(String key) {
        if (key != null) {
            log.debug("Jumper builder - Removing " + key + " from jumper cache");
            jumpCache.remove(key);
        }
    }

    // jump on content of 'name' or 'id' field
    private String getJumpByField(String fieldName, String key) {
        NodeSearchQuery query = new NodeSearchQuery(this);
        CoreField field = getField(fieldName); // "name");
        StepField queryField = query.getField(field);
        StepField numberField = query.getField(getField(FIELD_NUMBER));
        BasicSortOrder sortOrder = query.addSortOrder(numberField); // use 'oldest' jumper
        BasicFieldValueConstraint cons = null;
        if (field.getType() == Field.TYPE_STRING) {
            cons = new BasicFieldValueConstraint(queryField, key);
        } else if (field.getType() == Field.TYPE_INTEGER) {
            try {
                cons = new BasicFieldValueConstraint(queryField, new Integer(key));
            } catch(NumberFormatException e) { log.error("this key("+key+") should be a number because field("+fieldName+") is of type int!");
                cons = null;
            }
        }
        query.setConstraint(cons);
        query.setMaxNumber(1);
        try {
            List resultList = getNodes(query);
            if (resultList.size() > 0) {
                MMObjectNode node = (MMObjectNode) resultList.get(0);
                return node.getStringValue("url");
            }
        } catch (SearchQueryException sqe) {
            log.error(sqe.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a jumper for a specified key.
     *
     * @param key the key to search for.
     * @return the found alternate url.
     */
    public String getJump(String key) {
        String url = null;

        if (key.equals("")) {
            url = jumperNotFoundURL;
        } else {
            url = (String) jumpCache.get(key);
            if (log.isDebugEnabled()) {
                if (url != null) {
                    log.debug("Jumper - Cache hit on " + key);
                } else {
                    log.debug("Jumper - Cache miss on " + key);
                }
            }
            if (url == null) {
                // Search jumpers with name;
                url = getJumpByField("name", key);
            }
            int ikey = -1;
            if (url == null) {
                try {
                    ikey = Integer.parseInt(key);
                } catch (NumberFormatException e) {}
                // Search jumpers with number (parent);
                if (ikey >= 0) {
                    url = getJumpByField("id", key);
                }
            }
            if (url == null) {
                // no direct url call its builder
                if (ikey >= 0) {
                    MMObjectNode node = getNode(ikey);
                    if (node != null) {
                        String buln = mmb.getTypeDef().getValue(node.getIntValue("otype"));
                        MMObjectBuilder bul = mmb.getMMObject(buln);
                        if (log.isDebugEnabled()) {
                            log.debug("getUrl through builder with name=" + buln + " and id " + ikey);
                        }
                        if (bul != null) {
                            url = bul.getDefaultUrl(ikey);
                        }
                    }
                }
            }
            if (url != null && url.length() > 0 && !url.equals("null")) {
                jumpCache.put(key, url);
                if (url.equalsIgnoreCase("NOREDIRECT")) { // return null if the
                    // url specified is
                    // NOREDIRECT
                    url = null;
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("No jumper found for key '" + key + "'");
                }
                url = jumperNotFoundURL;
            }
        }
        return url;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mmbase.module.core.MMObjectBuilder#notify(org.mmbase.core.event.NodeEvent)
     */
    public void notify(NodeEvent event) {
        log.debug("Jumpers=" + event.getMachine() + " " + event.getBuilderName() + " no="
            + event.getNodeNumber()+ " " + NodeEvent.newTypeToOldType(event.getType()));
        jumpCache.clear();
        super.notify(event);
    }

    protected Object executeFunction(MMObjectNode node, String function, List arguments) {
        if (function.equals("gui")) {
            String rtn;
            if (arguments == null || arguments.size() == 0) {
                rtn = getGUIIndicator(node);
            } else {
                rtn = getGUIIndicator(node, Functions.buildParameters(GUI_PARAMETERS, arguments));
            }
            if (rtn != null) return rtn;
        }
        return super.executeFunction(node, function, arguments);
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
