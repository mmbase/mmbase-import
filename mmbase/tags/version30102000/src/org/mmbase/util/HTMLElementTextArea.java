/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;

public class HTMLElementTextArea  extends HTMLElement 
{
	public HTMLElementTextArea()
	{
	}	

	protected String generate()
	{		
		String html = "";
		if (moreValues)
		{
			if (valuesList != null)
			{
            	String val = null; 
        		Enumeration e = valuesList.elements();
        		if (e.hasMoreElements())
       			{ 
            		val = (String) e.nextElement();
				}
				html += "<TEXTAREA NAME=" + name ; 		
				if (cols != null) html += " COLS=" + cols;
				if (rows != null) html += " ROWS=" + rows;
				html += ">";
				if (val != null) html += val;
				html += "</TEXTAREA>";
				
			}
		}
		else
		{	
			html += "<TEXTAREA NAME=" + name ; 		
			if (cols != null) html += " COLS=" + cols;
			if (rows != null) html += " ROWS=" + rows;
			html += ">";
			if (values != null) 
			{
				if (values.charAt(0) == '\"')
				{
					html += values.substring(1,values.length()-1);
				}
				else
				{
					html +=	values;
				}
			}
			html += "</TEXTAREA>";
		}
		return html;
	}
}
