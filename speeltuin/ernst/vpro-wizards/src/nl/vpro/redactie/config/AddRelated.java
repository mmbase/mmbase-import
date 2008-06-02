package nl.vpro.redactie.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class AddRelated implements TagConfig {
    private String relationRole;
    private String nodeType;
    private List<Field> fields = new ArrayList<Field>();
    public List<Field> getFields() {
        return fields;
    }
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
    public void setField(Field field) {
        fields.add(field);
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
    
    @Override
    public String toString(){
        return new ToStringBuilder("AddRelation").append(nodeType).append(relationRole).append(fields).toString();
    }
}
