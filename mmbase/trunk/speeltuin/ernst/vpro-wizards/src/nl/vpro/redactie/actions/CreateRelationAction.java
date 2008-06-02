package nl.vpro.redactie.actions;

import java.util.HashSet;
import java.util.Set;

public class CreateRelationAction extends Action {
    private Set<String> source = new HashSet<String>();

    private Set<String> destination = new HashSet<String>();

    private Set<String> referSource = new HashSet<String>();

    private Set<String> referDestination = new HashSet<String>();

    private Set<String> role = new HashSet<String>();

    /**
     * when this field is set. the new relation node will get some position value based on the sortdir field. When the role is posrel, this
     * field defaults to pos. It can be set explicitely for other sortable relation types (extensions of posrel).
     */
    private Set<String> sortField = new HashSet<String>();

    /**
     * This field determins wether the new relation wil be added to the top or the bottom of the sorted list. When the value is 'begin' the
     * new relation's sort field will be given a value one higher than the currently highest value of the sorted list. When the value is
     * 'end' the new relation's sort field will be given a value one lower than the currently lowest value of the sorted list.
     * This Field defaults to 'end'
     */
    private Set<String> sortPosition = new HashSet<String>();

    public CreateRelationAction(){
        sortPosition.add("end");
    }


    public Set<String> getDestination() {
        return destination;
    }

    public void setDestination(Set<String> destination) {
        this.destination = destination;
    }

    public Set<String> getRole() {
        return role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }

    public Set<String> getSource() {
        return source;
    }

    public void setSource(Set<String> source) {
        this.source = source;
    }

    public Set<String> getReferDestination() {
        return referDestination;
    }

    public void setReferDestination(Set<String> referdestination) {
        this.referDestination = referdestination;
    }

    public Set<String> getReferSource() {
        return referSource;
    }

    public void setReferSource(Set<String> refersource) {
        this.referSource = refersource;
    }

    public Set<String> getSortField() {
        return sortField;
    }

    public void setSortField(Set<String> sortField) {
        this.sortField = sortField;
    }

    public Set<String> getSortPosition() {
        return sortPosition;
    }

    /**
     * this field defaults to 'end'
     * @param sortPositio can be either 'begin' or 'end'
     */
    public void setSortPosition(Set<String> sortPosition) {
        if(sortPosition.toArray()[0].equals("begin") || sortPosition.toArray()[0].equals("end"))
        {
            this.sortPosition = sortPosition;
        }
    }
}
