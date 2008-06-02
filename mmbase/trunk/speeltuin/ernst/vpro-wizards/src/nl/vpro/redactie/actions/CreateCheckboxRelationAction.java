package nl.vpro.redactie.actions;

import java.util.HashSet;
import java.util.Set;

public class CreateCheckboxRelationAction extends Action {
	private Set<String> source = new HashSet<String>();
	private Set<String> destination = new HashSet<String>();
	private Set<String> referSource = new HashSet<String>();
	private Set<String> referDestination = new HashSet<String>();
	private Set<String> role = new HashSet<String>();
	private Boolean relate = false;

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

	public Boolean getRelate() {
		return relate;
	}

	public void setRelate(Boolean relate) {
		this.relate = relate;
	}
}
