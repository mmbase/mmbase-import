package org.mmbase.util;

import java.util.*;

public class HTMLElementPassword  extends HTMLElement 
{
	public HTMLElementPassword()
	{
	}	

	protected String generate()
	{		
		String html = "";
		if (moreValues)
		{
			Enumeration e = valuesList.elements();
			if (e.hasMoreElements())
			{
				String val = (String) e.nextElement();
				html += name + " <INPUT ";
				html += "TYPE=PASSWORD NAME=" + name + " "; 		
				html +=	"VALUE=\"" + val + "\"";
				if (size != null) html += "SIZE= " +  size;	
 				html += ">" ;
			}
		}
		else
		{
			html += name + " <INPUT ";
			html += "TYPE=PASSWORD NAME=" + name + " "; 		
			if (values != null) html +=	"VALUE=\"" + values + "\"";
			if (size != null) html += "SIZE= " +  size;	
 			html += ">";
		}
		return html;
	}
}
