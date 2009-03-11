package com.finalist.cmsc.beans;

import java.io.Serializable;

/**
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class NodeBean implements Serializable {

    private static final long serialVersionUID = -8280773380954246333L;

    private int id; // mmbase number

	private String nodeType; // mmbase otype

	private String owner;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String type) {
		this.nodeType = type;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return id;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final NodeBean other = (NodeBean) obj;
        if (id != other.id) return false;
        return true;
    }
    
}
