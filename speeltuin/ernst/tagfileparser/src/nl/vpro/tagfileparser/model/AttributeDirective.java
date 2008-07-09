package nl.vpro.tagfileparser.model;

/**
 * This class represents a attribute tag directive. it contains all relevant data
 * @author Ernst Bunders
 *
 */
public class AttributeDirective {
	private String name;
	private Boolean isRequired = null;
	private Boolean isFragment = null;
	private Boolean isRtexprvalue = null;
	private Class type = null;
	private String description = null;
	
	public Boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}


	public Class getType() {
		return type;
	}

	public void setType(Class type) {
		this.type = type;
	}

	public boolean isFragment() {
		return isFragment;
	}

	public void setFragment(boolean isFragment) {
		this.isFragment = isFragment;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public Boolean getIsFragment() {
		return isFragment;
	}

	public void setIsFragment(Boolean isFragment) {
		this.isFragment = isFragment;
	}

	public Boolean getIsRtexprvalue() {
		return isRtexprvalue;
	}

	public void setIsRtexprvalue(Boolean isRtexprvalue) {
		this.isRtexprvalue = isRtexprvalue;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
