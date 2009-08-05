package nl.vpro.tagfileparser.model;

import java.util.ArrayList;
import java.util.List;


/**
 * This class represents a namespace. It actually is a directory
 * that dous or dous not contain tagfiles or child 'namespace' directories.
 * 
 * @author Ernst Bunders
 *
 */
public final class NameSpace {
	private NameSpace parent;
	private List<NameSpace> chlidren = new ArrayList<NameSpace>();
	private List<TagInfo> tags;
	private String name;
	
	public NameSpace(NameSpace parent, String name){
		this.parent = parent;
		this.name = name;
	}
	
	/**
	 * use this constructor for the root namespace
	 * @param string
	 */
	public NameSpace(String name) {
		this.name = name;
		this.parent = null;
	}

	public NameSpace addChildNamespace(NameSpace nameSpace){
		chlidren.add(nameSpace);
		return nameSpace;
	}
	
	public void addTagInfo(TagInfo tagInfo){
		tags.add(tagInfo);
	}

	public NameSpace getParent() {
		return parent;
	}

	public List<NameSpace> getChlidren() {
		return chlidren;
	}

	public List<TagInfo> getTags() {
		return tags;
	}

	public String getName() {
		return name;
	}
	
}
