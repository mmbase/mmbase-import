package nl.vpro.redactie.config;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ParentSearchList extends SearchList {

    private Integer numberOfItems;
    private Integer parentNodeNumber;
    private String relationRole;
    private Boolean showSearchall;
    private Boolean searchAll;
    private String orderBy;
    private String direction;

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Integer getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(Integer numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getParentNodeNumber() {
        return parentNodeNumber;
    }

    public void setParentNodeNumber(Integer parentNodeNumber) {
        this.parentNodeNumber = parentNodeNumber;
    }

    public String getRelationRole() {
        return relationRole;
    }

    public void setRelationRole(String relationRole) {
        this.relationRole = relationRole;
    }

    public Boolean getSearchAll() {
        return searchAll;
    }

    public void setSearchAll(Boolean searchAll) {
        this.searchAll = searchAll;
    }

    public Boolean getShowSearchall() {
        return showSearchall;
    }

    public void setShowSearchall(Boolean showSearchall) {
        this.showSearchall = showSearchall;
    }

    public String toString() {
        return new ToStringBuilder("ParentSearchList").append(relationRole).append(parentNodeNumber).append(numberOfItems).append(
                showSearchall).append(searchAll).append(orderBy).append(direction).toString()
                + super.toString();
    }
}
