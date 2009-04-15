package org.cmscontainer.tools.htmlcontainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Page {

	private String layoutHtml;
	private HtmlContainer container;
	private HashMap<String,ArrayList<String>> viewNames = new HashMap<String,ArrayList<String>>();
	
	public Page(String html, HtmlContainer container) {
		layoutHtml = html;
		this.container = container;
	}

	public void addView(String position, String viewName) {
		ArrayList<String> views = viewNames.get(position); 
		if(views == null) {
			views = new ArrayList<String>();
			viewNames.put(position, views);
		}
		views.add(viewName);
	}
	
	
	public String build() {
		String output = layoutHtml;
		for(String position:viewNames.keySet()) {
			ArrayList<String> views = viewNames.get(position);
			StringBuffer html = new StringBuffer();
			for(String viewName:views) {
				html.append(container.getViewHtml(viewName));
			}

			Pattern p = Pattern.compile("<div.*?id=\""+position+"\".*?>");
			
			Matcher m = p.matcher(output);
			
			 if(!m.find()) {
				 // TODO show layout name
				 System.out.println("No div found for position "+position);
				 System.exit(1);
			 }
			 
			 int index = m.end(0);
			 output = output.substring(0, index) + html.toString() + output.substring(index);

		}
		
		return output;
	}
	

}
