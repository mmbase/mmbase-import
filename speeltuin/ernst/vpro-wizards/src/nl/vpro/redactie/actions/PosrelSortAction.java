package nl.vpro.redactie.actions;

public class PosrelSortAction extends Action {
    private String containerNode = null;

    private String direction = null;

    /**
     * When the relation you want to sort is a reldef that uses the posrel builder
     * you have to set the role.
     */
    private String role = "posrel";

    public static String DIRECTION_UP = "up";

    public static String DIRECTION_DOWN = "down";

    public String getContainerNode() {
        return containerNode;
    }

    public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setContainerNode(String containerNode) {
        this.containerNode = containerNode;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public boolean directionValid(){
        return direction != null && !"".equals(direction) && (DIRECTION_DOWN.equals(direction) || DIRECTION_UP.equals(direction));
    }

}
