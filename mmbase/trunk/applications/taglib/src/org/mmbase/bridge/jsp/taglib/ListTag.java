/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.NodeList;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ListTag, provides functionality for listing cluster nodes
 * ('multilevel' search) in MMBase
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 */
public class ListTag extends AbstractNodeListTag {
    private static Logger log = Logging.getLoggerInstance(ListTag.class.getName());

    protected String nodesString=null;
    protected String pathString=null;
    protected String distinctString=null;
    protected String searchString=null;

    /**
     * Sets the nodes to start the search with.
     * The value '-1' interpreted as <code>null</code>, whcih indicates no
     * predefined startnodes are sued (a more general search is conducted
     * instead).
     * @param nodes a node or a comma separated list of nodes.
     */
    public void setNodes(String nodes) throws JspTagException {
        // parse/map the nodes they can be params, sessions or aliases
        // instead of just numbers
        nodesString=parseNodes(nodes);
    }

    protected String parseNodes(String nodes) throws JspTagException {
        // should be a StringTokenizer ?
        return getAttributeValue(nodes);
    }

    /**
     * @param type a comma separated list of nodeManagers
     */
    public void setPath(String path) throws JspTagException {
        this.pathString = getAttributeValue(path);
    }

    /**
     * @param type a comma separated list of nodeManagers
     * @depercated use setPath instead
     */
    public void setType(String type) throws JspTagException {
        this.pathString = getAttributeValue(type);
    }

    /**
     * The search parameter, determines how directionality affects the search.
     * Possible values are <code>both</code>, <code>destination</code>,
     * <code>source</code>, and <code>all</code>
     * @param search the swerach value
     */
    public void setSearch(String search) throws JspTagException {
        searchString = getAttributeValue(search).toUpperCase().trim();
        if (searchString.length()==0) {
            searchString="BOTH";
        } else if ( !searchString.equals("BOTH") &&
                    !searchString.equals("SOURCE") &&
                    !searchString.equals("DESTINATION") &&
                    !searchString.equals("ALL"))  {
            throw new JspTagException("Search should be one of BOTH, SOURCE, "+
                        "DESTINATION, or ALL (value found was "+search+")");
        }
    }

    /**
     * @param distinct the selection query for the object we are looking for
     */
    public void setDistinct(String distinct){
        this.distinctString = distinct;
    }

    /**
     * Performs the search
     */
    public int doStartTag() throws JspTagException{
        String searchNodes= (nodesString == null)? "-1" : nodesString;
        boolean searchDistinct= (distinctString != null);
        NodeList nodes = getCloudProviderVar().getList(searchNodes,
                                              pathString,
                                              fields,
                                              whereString,
                                              sortedString,
                                              directionString,
                                              searchString,
                                              searchDistinct);
        return setReturnValues(nodes,true);
    }

}

