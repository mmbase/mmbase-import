/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.util.Locale;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.CloudContext;
import org.mmbase.bridge.ContextProvider;

import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;


import javax.servlet.jsp.JspTagException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Tags which are meant to live as a child of the CloudTag, could extend this
 * class.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public abstract class CloudReferrerTag extends ContextReferrerTag {

    private static final Logger log = Logging.getLoggerInstance(CloudReferrerTag.class);


    private static CloudContext cloudContext;


    protected Attribute cloudId = Attribute.NULL;
    // the id of the cloud to which we refer
    // not yet supported by CloudTag


    /**
     * If there are more clouds to choose from, you can have a 'cloud'
     * attribute in your tag, in wich you can indicate the id of the
     * cloud you mean.
     */
    public void setCloud(String c) throws JspTagException {
        cloudId = getAttribute(c);
    }


    /**
    * This method tries to find an ancestor object of type CloudProvider.
    *
    * @return the CloudTag if found, else an exception.
    */

    protected CloudProvider findCloudProvider() throws JspTagException {
        return findParentTag(CloudProvider.class, (String) cloudId.getValue(this));
    }

    /**
     * This method tries to find an ancestor object of type CloudProvider.
     *
     * @return the CloudProvider or null.
     *
    */
    public CloudProvider findCloudProvider(boolean throwexception) throws JspTagException {
        return findParentTag(CloudProvider.class, (String) cloudId.getValue(this), throwexception);
    }


    /**
     * Find the CloudProvider and return its cloud variable in one
     * step. And the result of findCloudProvider is stored, so
     * invoking this function more often is better then invoking
     * findCloudProvider every time.
     *
     * @return a Cloud
     */
    public Cloud getCloudVar() throws JspTagException {
        CloudProvider provider = findCloudProvider(false);
        if (provider != null) return provider.getCloudVar();
        Cloud c = (Cloud) pageContext.getAttribute(CloudTag.KEY, CloudTag.SCOPE);
        if (c != null) return c;
        return ContextProvider.getDefaultCloudContext().getCloud("mmbase");
        //throw new JspTagException("Could not find parent cloud provider");
    }



    /**
    * @return the cloud context
    */
    protected CloudContext getCloudContext(){
        if (cloudContext == null){
            cloudContext = ContextProvider.getDefaultCloudContext();
        }
        return cloudContext;
    }

    protected Node getNode(String key) throws JspTagException {
        Node n = getNodeOrNull(key);
        if (n == null) getCloudVar().getNode(org.mmbase.util.Casting.toString(getObject(key))); // cause exception
        return n;
    }
    /**
     * Gets a node from the context.
     */

    protected Node getNodeOrNull(String key) throws JspTagException {
        Object n = getObject(key);
        if (n == null) {
            return null;
        } else if (n instanceof Node) {
            log.debug("found a Node in Context");
            return (Node) n;
        } else if ((n instanceof String) || (n instanceof Number)) {
            Cloud cloud = getCloudVar();
            if (! getCloudVar().hasNode(n.toString())) return null;
            log.debug("found a Node Number in Context");
            return getCloudVar().getNode(n.toString());
        } else {
            return org.mmbase.util.Casting.toNode(n, getCloudVar());
        }
    }


    public void fillStandardParameters(Parameters p) throws JspTagException {
        super.fillStandardParameters(p);
        Cloud cloud = null;
        CloudProvider provider = findCloudProvider(false);
        if (provider != null) {
            cloud = provider.getCloudVar();
        }
        if (cloud  == null) {
            cloud = (Cloud) pageContext.getAttribute(CloudTag.KEY, CloudTag.SCOPE);
        }
        if (cloud != null) {
            p.setIfDefined(Parameter.CLOUD, cloud);
            p.setIfDefined(Parameter.USER, cloud.getUser());
        }
    }


    /**
     * @since MMBase-1.8
     */
    public Locale getLocale() throws JspTagException {
        LocaleTag localeTag = findParentTag(LocaleTag.class, null, false);
        if (localeTag != null) {
            Locale locale = localeTag.getLocale();
            if (locale != null) {
                return locale;
            }
        }
        CloudProvider cloudProvider = findCloudProvider(false);
        return cloudProvider == null ? null : cloudProvider.getCloudVar().getLocale();
    }

}
