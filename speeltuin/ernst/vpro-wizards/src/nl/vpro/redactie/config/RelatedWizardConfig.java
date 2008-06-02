package nl.vpro.redactie.config;

import org.apache.commons.lang.builder.ToStringBuilder;

public class RelatedWizardConfig extends WizardConfig {
    String nodenr;
    String nodeType;
    String relationRole;
    Boolean showBack;
    
    
    
    public String getNodenr() {
        return nodenr;
    }



    public void setNodenr(String nodenr) {
        this.nodenr = nodenr;
    }



    public String getNodeType() {
        return nodeType;
    }



    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }



    public String getRelationRole() {
        return relationRole;
    }



    public void setRelationRole(String relationRole) {
        this.relationRole = relationRole;
    }



    public Boolean isShowBack() {
        return showBack;
    }



    public void setShowBack(Boolean showBack) {
        this.showBack = showBack;
    }



    public String toString(){
        return super.toString() +  new ToStringBuilder(this).append(nodenr).append(nodeType).append(relationRole).append(showBack).toString();
    }
}
