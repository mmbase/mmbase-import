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
