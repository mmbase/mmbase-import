/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.util;

import java.util.*;

public class HTMLElementRadio  extends HTMLElement 
{
	public HTMLElementRadio()
	{
	}	

	protected String generate()
	{		
		String val = null;
		String html = "";

	 	if (selected != null && selected.equals("null"))
        {
            sel = false;
        }
        if (exclude != null && exclude.equals("null"))
        {
            ex = false;
        }
	
		if (moreValues)
		{
			Enumeration e = valuesList.elements();
			String basic = "<INPUT TYPE=RADIO NAME=" + name + " " + "VALUE=\"";
			
			Vector list = new Vector();
			
			while (e.hasMoreElements())
			{	
				val = (String) e.nextElement();
				if (sel)
				{	
					if (selected.equalsIgnoreCase(val))
					{  
						if (vertical) list.addElement(basic + val + "\" CHECKED" + "> " + val + "<BR>\n");
						else list.addElement(basic + val + "\" CHECKED" + "> " + val);
					}
					else
					{
						if (ex)
						{
							if (!exclude.equalsIgnoreCase(val))
							{		
								if (vertical) list.addElement(basic + val + "\" > " + val + "<BR>\n");
								else list.addElement(basic + val + "\" > " + val);
							}
						}
						else
						{
							if (vertical) list.addElement(basic + val + "\" > " + val + "<BR>\n");
                            else list.addElement(basic + val + "\" > " + val);
	
						}
					}
				}
				else
				{
					if (ex)
					{
						if (!exclude.equalsIgnoreCase(val))
						{		
							if (vertical) list.addElement( basic + val + "\" > " + val	+ "<BR>\n");
							else list.addElement(basic + val + "\" > " + val);
						}
					}
					else
					{
						if (vertical) list.addElement( basic + val + "\" > " + val	+ "<BR>\n");
						else list.addElement(basic + val + "\" > " + val);
					}
				}
			}
		
			Enumeration le = list.elements();
			int i=0;
			String h = "";
			while(le.hasMoreElements())
			{	
				while( i < 22 && le.hasMoreElements() )
				{
					h += (String) le.nextElement();
					i++;
				}	
				html += h;
				h = "";
				i = 0;
			}
		}
		else
		if (moredouble)
		{
			Enumeration e = valuesList.elements();
			String basic = "<INPUT TYPE=RADIO NAME=" + name + " " + "VALUE=\"";
			
			Vector list = new Vector();
			
			while (e.hasMoreElements())
			{	
				val = (String) e.nextElement();
				String val2 ;
				if (e.hasMoreElements())
				{
					val2= (String) e.nextElement();
				}
				else
				{
					 System.out.println("HTMLElementRadio.generate: Expecting a double list (the DOUBLE key word was selected");
					 return html;
				}
				if (sel)
				{	
					if (selected.equalsIgnoreCase(val))
					{  
						if (vertical) list.addElement(basic + val2 + "\" CHECKED" + "> " + val + "<BR>\n");
						else list.addElement(basic + val2 + "\" CHECKED" + "> " + val);
					}
					else
					{
						if (ex)
						{
							if (!exclude.equalsIgnoreCase(val))
							{		
								if (vertical) list.addElement(basic + val2 + "\" > " + val + "<BR>\n");
								else list.addElement(basic + val2 + "\" > " + val);
							}
						}
						else
						{
							if (vertical) list.addElement(basic + val2 + "\" > " + val + "<BR>\n");
                            else list.addElement(basic + val2 + "\" > " + val);
	
						}
					}
				}
				else
				{
					if (ex)
					{
						if (!exclude.equalsIgnoreCase(val))
						{		
							if (vertical) list.addElement( basic + val2 + "\" > " + val	+ "<BR>\n");
							else list.addElement(basic + val2 + "\" > " + val);
						}
					}
					else
					{
						if (vertical) list.addElement( basic + val2 + "\" > " + val	+ "<BR>\n");
						else list.addElement(basic + val2 + "\" > " + val);
					}
				}
			}
		
			Enumeration le = list.elements();
			int i=0;
			String h = "";
			while(le.hasMoreElements())
			{	
				while( i < 22 && le.hasMoreElements() )
				{
					h += (String) le.nextElement();
					i++;
				}	
				html += h;
				h = "";
				i = 0;
			}
		}
		else
		{
			html += "<INPUT ";
			html += "TYPE=RADIO NAME=" + name + " "; 		
			html +=	"VALUE=\"" + values + "\"";
			if (sel) html += "CHECKED";
 			html += "> " + values ;
		}
		return html;
	}
}
