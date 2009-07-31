package org.mmbase.applications.vprowizards.spring.cache.template;

import org.mmbase.bridge.*;

public class MMBaseTemplateQueryRunner implements TemplateQueryRunner {
    private Cloud cloud;
    

    public MMBaseTemplateQueryRunner(Cloud cloud) {
        this.cloud = cloud;
    }
    
    public String runQuery(String nodeNumber, QueryTemplate queryTemplate) {
        Node node = cloud.getNode(nodeNumber);
        NodeList nl = node.getRelatedNodes(queryTemplate.getDestinationType(), queryTemplate.getRelationRole(), "both");
        if (nl.size() > 0) {
            return "" + nl.getNode(0).getNumber();
        } else {
            return "!notfound!";
        }
    }
}