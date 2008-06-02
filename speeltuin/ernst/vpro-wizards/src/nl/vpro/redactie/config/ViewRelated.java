package nl.vpro.redactie.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ViewRelated implements TagConfig {

    private String title;
    private String relationRole;
    private String nodeType;
    private String wizardFile;
    private Boolean edit;
    private Boolean delete;
    private Boolean sortable;
    private Boolean multipart;
    private List<Field> fields = new ArrayList<Field>();

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public Boolean isEdit() {
        return edit;
    }

    public void setEdit(Boolean edit) {
        this.edit = edit;
    }

    public Boolean isMultipart() {
        return multipart;
    }

    public void setMultipart(Boolean multipart) {
        this.multipart = multipart;
    }

    public String getNodetype() {
        return nodeType;
    }

    public void setNodetype(String nodetype) {
        this.nodeType = nodetype;
    }

    public String getRelationRole() {
        return relationRole;
    }

    public void setRelationRole(String relationRole) {
        this.relationRole = relationRole;
    }

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getWizardFile() {
        return wizardFile;
    }

    public void setWizardFile(String wizardFile) {
        this.wizardFile = wizardFile;
    }

    @Override
    public String toString() {
        return new ToStringBuilder("ViewRelated").append(delete).append(edit).append(multipart).append(sortable).append(nodeType).append(
                relationRole).append(fields).toString();
    }

}
