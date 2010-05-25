package org.mmbase.applications.vprowizards.spring.cache.template;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class Template {
    protected String template;
    protected String nodeNumber = "";
    static final String TEMPLATE_NODENR_SEPARATOR=":";
    
    
    
    public Template(String template) {
        this.template = template;
        splitNodenrFromTemplate();
    }
    
    public void removeNodenr(){
        nodeNumber = "";
    }
    
    String getNodeNumber(){
        return nodeNumber;
    }
    
    public boolean hasNodeNumber(){
        return !"".equals(nodeNumber);
    }
    
    public void setNodeNumber(String nodeNumber){
        this.nodeNumber = (StringUtils.isBlank(nodeNumber) ? "" : nodeNumber);
    }
    
    public String getTemplate(){
        return template + (hasTemplate() && hasNodeNumber()  ? TEMPLATE_NODENR_SEPARATOR  : "") + nodeNumber;
    }
    
    public boolean hasTemplate() {
        return ! StringUtils.isBlank(template);
    }

    private void splitNodenrFromTemplate() {
        String _template = template;
        Pattern p = Pattern.compile(":[0-9]+$");
        Matcher m = p.matcher(_template);
        if (m.find()) {
            template = _template.substring(0, m.start());
            nodeNumber = _template.substring(m.start() + 1);
        }
    }

    public void removeTemplate() {
        template = "";
    }
}
