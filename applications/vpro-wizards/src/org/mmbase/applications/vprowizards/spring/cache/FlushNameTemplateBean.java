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
 * 
 * <pre>
 * This bean is used in the templates of the vpro-wizards. it is used to add node numbers to templates inside flush names.
 * It also has a method for cleaning the templates out of the flush names. For this reason it implements the Modifier 
 * interface.
 * 
 * As arguments it takes a template string, a 
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
public class FlushNameTemplateBean implements Modifier {
    private static final String TEMPLATE_REGEXP = "^.*\\[[a-zA-Z0-9\\.:]+\\].*$";

    private String template;

    private String nodeType;

    private String nodeNumber;

    private Cloud cloud;

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


    public String processAndGetTemplate() {
        if (StringUtils.isEmpty(template)) {
            throw new IllegalStateException("template not set");
        }

        if (StringUtils.isEmpty(nodeNumber)) {
            throw new IllegalStateException("nodenr not set");
        }
        if (StringUtils.isEmpty(nodeType)) {
            throw new IllegalStateException("type not set");
        }
        
        if (cloud == null) {
            throw new IllegalStateException("cloud not set");
        }
        
        //we create a processor that will first strip the old node numbers, and then insert new ones.
        processTemplate(new SubTemplateProcessor() {
            @Override
            void process() {
                // Perhaps the template has been used before, and 'old' node numbers are present
                // if this is so, they must be removed.
                _subTemplate = stripNodenumberFromSubtemplate(_subTemplate);
                

                String templateNodeType = deriveTemplateType(_subTemplate);
                if (nodeType.equals(templateNodeType)) {
                    _subTemplate = appendNodenumberToTemplate(_subTemplate);
                }
                
                //even if the node type did not match, perhaps the template was cleaned from previous uses.
                //reinsert it anyway.
                reinsertSubTemplateIntoTemplate(_subTemplate, _begin, _end);
            }
        });
        return template;
    }

    
    /**
     * This method is from the Modifier interface, and allows this class to play as a Modifier instance.
     */
    public String modify(String input) {
            setTemplate(input);
            processTemplate(new SubTemplateProcessor(){
                @Override
                void process() {
                    _subTemplate = stripNodenumberFromSubtemplate(_subTemplate);
                    reinsertSubTemplateIntoTemplate(_subTemplate, _begin, _end);
                }});
        return template;
    }

    private void processTemplate(SubTemplateProcessor processor) {
        int offset = 0;
        while (offset < template.length()  && template.substring(offset).matches(TEMPLATE_REGEXP) ) {
            log.debug("evaluating: " + template.substring(offset) + ", from: " + offset);
            int begin = template.indexOf("[", offset) + 1;
            int end = template.indexOf("]", offset);

            String subTemplate = template.substring(begin, end);
            log.debug("begin: " + begin + ", end: " + end + ", template: " + subTemplate);

            processor.setSubTemplate(subTemplate);
            processor.setBegin(begin);
            processor.setEnd(end);
            processor.process();
            offset = offset + begin + processor.getSubTemplate().length() + 1;
        }
    }

    
    
    private void reinsertSubTemplateIntoTemplate(String subTemplate, int begin, int end){
        String subTemplatePrefix = template.substring(0, begin);
        String subTemplateSuffix = template.substring(end);
        template = subTemplatePrefix + subTemplate + subTemplateSuffix;
    }

    private String stripNodenumberFromSubtemplate(String subTemplate) {
        //pattern: aaa:89 or aaa.aaa.aaa:00
        if (subTemplate.matches("(\\w+:\\d+)|((\\w+)(\\.\\w+){2}:\\d+)")) {
            subTemplate = subTemplate.substring(0, subTemplate.indexOf(":"));
            log.debug("template reuse. after cleaning: " + subTemplate);
        }
        return subTemplate;
    }

    private boolean subtemplateIsQuery(String subTemplate) {
        return subTemplate.split("\\.").length == 3;
    }

    private String resolveNodeNumber(String subTemplate) {
        if (subtemplateIsQuery(subTemplate)) {
            return resolveNodeNumberForQuery(subTemplate);
        } else {
            return nodeNumber;
        }
    }
    
    private String appendNodenumberToTemplate(String subTemplate) {
        return subTemplate + ":" + resolveNodeNumber(subTemplate);
    }

    private String resolveNodeNumberForQuery(String subTemplate) {
        QueryTemplate queryTemplate = parseQueryTemplate(subTemplate);
        Node node = cloud.getNode(nodeNumber);
        NodeList nl = node.getRelatedNodes(queryTemplate.getDestinationType(), queryTemplate.getRelationRole(), "both");
        if (nl.size() > 0) {
            return "" + nl.getNode(0).getNumber();
        } else {
            log.error("could not find 'parent' node with path " + subTemplate + " and root node " + nodeNumber);
            return "!notfound!";
        }
    }

    private String deriveTemplateType(String subTemplate) {
        if (subtemplateIsQuery(subTemplate)) {
            return parseQueryTemplate(subTemplate).getSourceType();
        } else {
            return subTemplate;
        }
    }

    private QueryTemplate parseQueryTemplate(String subTemplate) {
        String[] q = subTemplate.split("\\.");
        if (q.length == 3) {
            return new QueryTemplate(q[0], q[1], q[2]);
        } else {
            throw new IllegalStateException(String.format(
                    "template '%s' is not a query template, can not create QueryTemplate instance", subTemplate));
        }
    }

    public Modifier copy() {
        // as a Modifier this thing is completely stateless, so just return
        // this instance
        return this;
    }

    private static class QueryTemplate {
        private String sourceType, relationRole, destinationType;

        public QueryTemplate(String sourceType, String relationRole, String destinationType) {
            this.sourceType = sourceType;
            this.relationRole = relationRole;
            this.destinationType = destinationType;
        }

        public String getSourceType() {
            return sourceType;
        }

        public String getRelationRole() {
            return relationRole;
        }

        public String getDestinationType() {
            return destinationType;
        }
    }

    private abstract class SubTemplateProcessor {
        protected int _begin;
        protected int _end;
        protected String _subTemplate;

        abstract void process();

        public void setSubTemplate(String subTemplate) {
            this._subTemplate = subTemplate;
        }
        
        public String getSubTemplate(){
            return _subTemplate;
        }

        public void setBegin(int begin) {
            this._begin = begin;
        }

        public void setEnd(int end) {
            this._end = end;
        }

    }
}
