package nl.vpro.tagfileparser.model;

/**
 * @author Ernst Bunders
 *
 */
public class VariableDirective {
	private String nameGiven = null;
	private String nameFromAttribute = null;
	private String alias = null;
	private Boolean declare = null;
	private String scope = null;
	private String description = null;
	private Class variableClass = null;
	
	public Class getVariableClass() {
		return variableClass;
	}
	public void setVariableClass(Class variableClass) {
		this.variableClass = variableClass;
	}
	public String getNameGiven() {
		return nameGiven;
	}
	public void setNameGiven(String nameGiven) {
		this.nameGiven = nameGiven;
	}
	public String getNameFromAttribute() {
		return nameFromAttribute;
	}
	public void setNameFromAttribute(String nameFromAttribute) {
		this.nameFromAttribute = nameFromAttribute;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public Boolean getDeclare() {
		return declare;
	}
	public void setDeclare(Boolean declare) {
		this.declare = declare;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
