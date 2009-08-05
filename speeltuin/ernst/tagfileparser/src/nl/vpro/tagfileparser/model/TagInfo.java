package nl.vpro.tagfileparser.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * This bean contains all the information we want to know about a tag file.
 * 
 * @author Ernst Bunders
 *
 */
public final class TagInfo {
	private String name;
	private String description;
	private List<AttributeDirective> attributes = new ArrayList<AttributeDirective>();
	private TagDirective tagDirective = null;
	private List<VariableDirective> variables = new ArrayList<VariableDirective>();
	
	
	
	public TagInfo(String name) {
		super();
		this.name = name;
	}
	/**
	 * returns either the name of the tag or the 'display-name of the
	 * tag directive if it was set.
	 * @return
	 */
	public String getName() {
		if(tagDirective != null && !StringUtils.isEmpty(tagDirective.getDisplayName())){
			return tagDirective.getDisplayName();
		}
		return name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<AttributeDirective> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<AttributeDirective> attributes) {
		this.attributes = attributes;
	}
	
	public void addAttribute(AttributeDirective attribute){
		attributes.add(attribute);
	}
	public TagDirective getTagDirective() {
		return tagDirective;
	}
	public void setTagDirective(TagDirective tagDirective) {
		this.tagDirective = tagDirective;
	}
	public void addVariableDirective(VariableDirective variable) {
		this.variables.add(variable);
	}
	
	public void setVariables(List<VariableDirective> variables){
		this.variables = variables;
	}
	
	public List<VariableDirective> getVariables(){
		return variables;
	}
	
	
}
