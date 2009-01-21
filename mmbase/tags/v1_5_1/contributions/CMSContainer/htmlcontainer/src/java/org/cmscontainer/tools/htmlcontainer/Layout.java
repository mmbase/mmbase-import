package org.cmscontainer.tools.htmlcontainer;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Layout {

	private String name;
	private String html;
	private Properties properties;
	private Set<String> positions;
	
	public Layout(String name, String html, Properties properties) {
		this.name = name;
		this.html = html;
		this.properties = properties;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public Properties getProperties() {
		return properties;
	}
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<String> getPositions() {
		if(positions == null) {
			positions = new HashSet<String>();
			for(Object oName:properties.keySet()) {
				String name = (String)oName;
				if(name.indexOf(".") != -1) {
					positions.add(name.substring(0, name.indexOf(".")));
				}
			}
		}
		return positions;
	}
	public boolean isRequired(String position) {
		String value = properties.getProperty(position+".required");
		return (value != null && value.equals("true"));
	}
	public String getFirstView(String position) {
		String[] views = getViews(position);
		return (views == null || views.length == 0)?null:views[0];
	}
	public String[] getViews(String position) {
		// TODO: handle duplicate names for this position
		String value = properties.getProperty(position+".view");
		return value.split(",");
	}
}
