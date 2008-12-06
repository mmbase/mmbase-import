package org.mmbase.applications.vprowizards.spring.cache.template;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class Template {
    protected String template;
    protected String nodenr = "";
    static final String TEMPLATE_NODENR_SEPARATOR=":";
    
    
    
    public Template(String template) {
        this.template = template;
        splitNodenrFromTemplate();
    }
    
    public void removeNodenr(){
        nodenr = "";
    }
    
    String getNodeNumber(){
        return nodenr;
    }
    
    public boolean hasNodenr(){
        return !"".equals(nodenr);
    }
    
    public void setNodenr(String nodenr){
        this.nodenr = (StringUtils.isBlank(nodenr) ? "" : nodenr);
    }
    
    public String getTemplate(){
        return template + (hasNodenr() ? TEMPLATE_NODENR_SEPARATOR + nodenr : "");
    }
    
    private void splitNodenrFromTemplate() {
        String _template = template;
        Pattern p = Pattern.compile(":[0-9]+$");
        Matcher m = p.matcher(_template);
        if (m.find()) {
            template = _template.substring(0, m.start());
            nodenr = _template.substring(m.start() + 1);
        }
    }
}
