/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.applications.vprowizards.spring.cache;

import org.apache.commons.lang.StringUtils;
import org.mmbase.applications.vprowizards.spring.cache.template.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * 
 * <pre>
 * This bean is used in the templates of the vpro-wizards. it is used to add node numbers to templates inside flush names.
 * It is actually a facade for the classes in {@link org.mmbase.applications.vprowizards.spring.cache.template}. 
 * 
 * So, what are templates?
 * 
 * Templates are a way to create dynamic cacheGroup names, where the dynamic bit is dependent on the node you are
 * currently editing, and are used by the list tag. 
 * You can use a flush name like, 'locations_[location]', where the 'location' between brackets is the builder name.
 *  
 * Inside the list tag the [builder name] part is then changed into [buildername:nodenumber] where node number is the 
 * number of each row (where the node type of the list matches the node type set in this bean). 
 * This way the flush name parameter will be different for each row in the list.
 * 
 * There is one variety, that you can use if you want to create a dynamic cache flush name for the parent node of the
 * nodes in a certain list. To do this you create a template like [this_type.relation_role.child_type]. Then the number of the
 * first node found with this path (where this_type matches the type set in this bean) will be inserted in the template.
 * 
 * The original template is not replaced with the node number, but the node number is appended to the template. This is
 * done because the template is handed to a (hierarchy of) jsp page(s), and is reused. with each reuse the previous node
 * numbers are stripped out of the template.
 * 
 * </pre>
 * 
 * TODO:/to test the 'extended' template (where the parent node is found we need either a cloud or a cloud mock object
 * 
 * @author ebunders
 * 
 */
public class FlushNameTemplateBean {

    private String template;

    private String nodeType;

    private String nodeNumber;

    private Cloud cloud;

    private TemplateQueryRunner templateQueryRunner = null;

    private static final Logger log = Logging.getLoggerInstance(FlushNameTemplateBean.class);

    public void setCloud(Cloud cloud) {
        this.cloud = cloud;
    }

    public void setNodeType(String type) {
        this.nodeType = type;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void setNodeNumber(String nodenr) {
        this.nodeNumber = nodenr;
    }

    public String getTemplate() {
        checkNull(template, "template");
        checkNull(nodeType, "nodeType");
        checkNull(nodeNumber, "nodeNumber");
        checkNull(cloud, "cloud");
        if (MultiTemplateParser.isTemplate(template)) {
            templateQueryRunner = new MMBaseTemplateQueryRunner(cloud);
            TemplateParser parser = new MultiTemplateParser(nodeType, nodeNumber, template, templateQueryRunner);
            parser.insertNodeNumber();
            return parser.getTemplate();
        }
        return template;
    }

    void setTemplateQueryRunner(TemplateQueryRunner templateQueryRunner) {
        this.templateQueryRunner = templateQueryRunner;
    }

    private void checkNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalStateException(String.format("property %s has not been set.", name));
        }
        if (String.class.isAssignableFrom(obj.getClass()) && StringUtils.isBlank((String) obj)) {
            throw new IllegalStateException(String.format("property %s is an empty string.", name));
        }
    }

}
