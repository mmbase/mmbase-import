package org.mmbase.util;

import java.util.*;

public class HTMLElementText  extends HTMLElement 
{
	public HTMLElementText()
	{
	}	

	protected String generate()
	{		
		String html = "";
		if (moreValues)
		{
			System.out.println("valuesList: " + valuesList );
			Enumeration e = valuesList.elements();
			if (e.hasMoreElements())
			{
				String val = (String) e.nextElement();
				//System.out.println("val: " + val );

				if (val.equals("null"))
				{
					val = "";
				}
				html += " <INPUT TYPE=TEXT NAME=" + name + " VALUE=\"" + val + "\"";
				if (size != null) html += "SIZE= " +  size;	
 				html += ">" ;
				System.out.println("html: " + html);
			}
		}
		else
		{
			if (values.equals("null"))
			{
				values = "";
			}
			html += " <INPUT TYPE=TEXT NAME=" + name + " "; 		
			if (values != null) html +=	"VALUE=\"" + values + "\"";
			if (size != null) html += "SIZE= " +  size;	
 			html += ">";
			System.out.println("html2: " + html);
		}
		return html;
	}
}
