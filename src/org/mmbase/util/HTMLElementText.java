/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
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
