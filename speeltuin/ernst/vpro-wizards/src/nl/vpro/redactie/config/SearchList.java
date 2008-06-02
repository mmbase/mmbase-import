package nl.vpro.redactie.config;

import org.apache.commons.lang.builder.ToStringBuilder;

public abstract class SearchList implements TagConfig {
    private String nodeType;
    private Boolean collapesd;
    private String wizardFile;
    private String searchFields;
    private String resultFields;
    private Boolean relate;
    private Boolean delete;
    private Boolean edit;
    private Boolean hardDelete;
    private Boolean confirmDelete;
    private Integer defaultMinAge;
    private Integer defaultMaxAge;

    public Boolean getCollapesd() {
        return collapesd;
    }

    public void setCollapesd(Boolean collapesd) {
        this.collapesd = collapesd;
    }

    public Boolean getConfirmDelete() {
        return confirmDelete;
    }

    public void setConfirmDelete(Boolean confirmDelete) {
        this.confirmDelete = confirmDelete;
    }

    public Integer getDefaultMaxAge() {
        return defaultMaxAge;
    }

    public void setDefaultMaxAge(Integer defaultMaxAge) {
        this.defaultMaxAge = defaultMaxAge;
    }

    public Integer getDefaultMinAge() {
        return defaultMinAge;
    }

    public void setDefaultMinAge(Integer defaultMinAge) {
        this.defaultMinAge = defaultMinAge;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public Boolean getHardDelete() {
        return hardDelete;
    }

    public void setHardDelete(Boolean hardDelete) {
        this.hardDelete = hardDelete;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Boolean getRelate() {
        return relate;
    }

    public void setRelate(Boolean relate) {
        this.relate = relate;
    }

    public String getResultFields() {
        return resultFields;
    }

    public void setResultFields(String resultFields) {
        this.resultFields = resultFields;
    }

    public String getSearchFields() {
        return searchFields;
    }

    public void setSearchFields(String searchFields) {
        this.searchFields = searchFields;
    }

    public String getWizardFile() {
        return wizardFile;
    }

    public void setWizardFile(String wizardFile) {
        this.wizardFile = wizardFile;
    }

    public String toString() {
        return new ToStringBuilder("").append(collapesd).append(delete).append(hardDelete).append(edit).append(confirmDelete).append(
                defaultMaxAge).append(defaultMinAge).append(nodeType).append(relate).append(wizardFile).append(searchFields).append(
                resultFields).toString();
    }

    public Boolean getEdit() {
        return edit;
    }

    public void setEdit(Boolean edit) {
        this.edit = edit;
    }

}
