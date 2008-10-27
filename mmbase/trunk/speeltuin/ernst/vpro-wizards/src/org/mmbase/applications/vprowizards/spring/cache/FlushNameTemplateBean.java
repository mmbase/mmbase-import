/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.cache;

import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This bean is used in the templates of the vpro-wizards. it is used to add node numbers to templates of flushnames. It
 * also has a method for cleaning the templates out of the flushnames. this is used by
 * {@link TokenizerCacheNameResolver}. For this reason it implements the Modifier interface.<br>
 * So, what are templates?<br>
 * Templates are a way to create dynamic cacheGroup names, where the dynamic bit is dependend on the node you are
 * currently editing, and are used by the list tag. You can use a flushname like, 'locations_[location]', where the
 * 'location' between brackets is the builder name. Inside the list tag the [buildername] part is then changed into [buildername:nodenumber]
 * where nodenumber is the number of each row (where the nodetype of the list matches the nodetype set in this bean).
 * This way the flushname parameter wil be differen for each row in the list.<br>
 * There is one variety, that you can use if you want to create a dynamic cache flush name for the parent node of the nodes in a certain list.
 * To do this you create a template like [thistype,role,parenttype]. Then the number of the first node found with this path (where thistype matches the
 * type set in this bean) will be inserted in the template.<br>
 * So why insert and not replace (the filtering out of the template is done just before using the flush names?<br>
 * 
 * 
 * TODO:/to test the 'extended' template (where the parent node is found we need either a cloud or a cloud mock object
 * 
 * @author ebunders
 * 
 */
public class FlushNameTemplateBean implements Modifier {
    private String template;

    private String type;

    private String nodenr;

    private Cloud cloud;

    private static final Logger log = Logging.getLoggerInstance(FlushNameTemplateBean.class);

    /**
     * this method processes the template and appends the nodenumber to all matching placeholders in the templates it
     * also removes preveously added nodenumbers. (templates can be reused)
     * 
     * @return
     */
    public String getProcessedTemplate() {
        if(StringUtils.isEmpty(template)) {
            throw new IllegalStateException("template not set");
        }
        
        if(StringUtils.isEmpty(nodenr)) {
            throw new IllegalStateException("nodenr not set");
        }
        if(StringUtils.isEmpty(type)) {
            throw new IllegalStateException("type   not set");
        }
        int from = 0;
        while (template.substring(from).matches("^.*\\[[a-zA-Z0-9\\.:]+\\].*$")) {
            // \\[[a-zA-Z0-9]+\\]
            log.debug("evaluating: " + template.substring(from) + ", from: " + from);
            int begin = template.indexOf("[", from);
            int end = template.indexOf("]", from);
            String t = template.substring(begin + 1, end);
            log.debug("begin: " + begin + ", end: " + end + ", template: " + t);

            // maybe this template was used before, in that case we have to clean the old value out of it
            if (t.indexOf(":") > -1) {
                t = t.substring(0, t.indexOf(":"));
                log.debug("template reuse. after cleaning: " + t);
            }

            // is the template a path?
            String thistype = null, relation = null, destination = null;
            boolean isQuery = false;
            if (t.indexOf(".") > -1) {
                StringTokenizer st = new StringTokenizer(t, ".");
                thistype = st.nextToken();
                relation = st.nextToken();
                destination = st.nextToken();
                isQuery = true;
            } else {
                thistype = t;
            }

            // if the template matches the given type, add the node number
            if (thistype.equals(type)) {

                // we copy the nodenumber field becouse if there are more templates then one we need the original again.
                String copyNodeNumber = nodenr;
                if (isQuery) {
                    Node node = cloud.getNode(nodenr);
                    NodeList nl = node.getRelatedNodes(destination, relation, "both");
                    if (nl.size() > 0) {
                        copyNodeNumber = "" + nl.getNode(0).getNumber();
                    } else {
                        log.error("could not find 'parent' node with path " + t + " and root node " + copyNodeNumber);
                        copyNodeNumber = "!notfound!";
                    }
                }
                template = template.substring(0, begin + 1) + t + ":" + copyNodeNumber + template.substring(end);
                // adjust the end index
                end = template.indexOf("]", from);
            }
            from = end + 1;
        }
        return template;
    }

    /**
     * This method is from the Modifier interface, and allows this class to 
     * play as a Modifier instance.
     */
    public String modify(String input) {
        try {
            return stripTemplates(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // TODO: What to do when an exception occurs?
        return input;
    }

    /**
     * this method strips the templates away from the flushnames and just leaves the nodenumbers. This should yield the
     * actual flushnames.
     * 
     * @param flushname
     * @return
     * @throws Exception
     *             when there is a problem with parsing the template
     */
    public static String stripTemplates(String flushname) throws Exception {
        log.debug("before. template: " + flushname);
        // decode the templates out of the flushname
        int from = 0;
        while (flushname.substring(from).matches("^.*\\[[a-zA-Z0-9\\.]+:[a-zA-Z0-9]+\\].*$")) {
            log.debug("evaluating: " + flushname.substring(from) + ", from: " + from);
            int begin = flushname.indexOf("[", from);
            int end = flushname.indexOf("]", from);
            String t = flushname.substring(begin + 1, end);
            log.debug("begin: " + begin + ", end: " + end + ", flushname: " + t);

            if (t.indexOf(":") == -1) {
                // when this happens there is a template in the flushname that has not been suffixed
                // with an actual nodenumber. this is an application error!
                throw new Exception("flushname '" + flushname
                        + "' illegal. some temlates have not been suffixed with ':<nodenr>'");
            }
            String nodenr = t.substring(t.indexOf(":") + 1);

            flushname = flushname.substring(0, begin) + nodenr + flushname.substring(end + 1);
            from = begin + 1;
        }
        log.debug("after. tempate: " + flushname);
        return flushname;
    }

    public void setCloud(Cloud cloud) {
        this.cloud = cloud;
    }

    /**
     * @param type
     *            the nodetype to add the nodenumber to.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @param template
     *            flushname with placeholders to be replaced
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * @param nodenr
     *            the number of the node appended to the tempate for matches with 'type'
     */
    public void setNodenr(String nodenr) {
        this.nodenr = nodenr;
    }

    public Modifier copy() {
        //as a Modifier this thing is completely stateless, so just return
        //this instance
        return this;
    }
}
