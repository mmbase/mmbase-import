package org.mmbase.util;

import java.util.*;

/**
* generates a HTML Element: SELECT, uses this variables which are set in the<BR>
* super class (HTMLElement) to generate HTML:
*<PRE>
* boolean sel        if true it checks if the String selected equals the 
*                    current value if equal the HTML tag SELECTED is added 
*                    after OPTION.
* String selected    see above   
* boolean ex         if true it check if the String exclude equals the current 
*                    value, if equal this value will be skipped (no HTML 
*                    generated for this item)
* String exclude     see above
* boolean moreValues if true it will make a list of items.
* boolean moredouble if true it will make a paired list of items.
*                    (first item = VALUE second item=NAME)
* Vector valuesList  The list of items. 
* String size        if not null the HTML tag SIZE=size is added 
* boolean multiple   if true the HTML tag MULTIPLE is added.
* </PRE>
*
* @version 26-Sep-1996
* @author Jan van Oosterom
*/
public class HTMLElementSelect  extends HTMLElement 
{
	/**
	* Creats a HTMLElementSelect.
	*/
	public HTMLElementSelect()
	{	
	}	
	/**
	* generates the HTML code ...
	*/
	protected String generate()
	{	//	System.out.println("generate");
		if (selected != null && selected.equals("null"))
		{	
			sel = false;
		}
		if (exclude != null && exclude.equals("null"))
		{	
			ex = false;
		}
		String html = "";
		if (moreValues)
		{
			html += "<SELECT NAME=" + name + " "; 		
			if (size != null) html += "SIZE=" + size;
			if (multiple) 
			{
				html += " MULTIPLE";
			}
			html += ">";
			if (empty) html += "<OPTION> "; 

			String val = null;
			Vector list = new Vector();
            Vector vec = valuesList;
			// System.out.println("ServScan->"+vec);
			if (sorted!=null && (sorted.equals("ALPHA") || sorted.equals("\"ALPHA\""))) {
				vec=SortedVector.SortVector(vec); 
			}
            Enumeration e = vec.elements();
			int j=0;
            while (e.hasMoreElements() && ((j++<max)||max==-1))
            {
            	val = (String) e.nextElement();
				if (sel ) 
				{
					if (selected.equalsIgnoreCase(val))
					{
						list.addElement ("<OPTION SELECTED>" + val + "\n");
					}
					else
					{
						if (ex)
						{
							if (!exclude.equalsIgnoreCase(val))
								list.addElement("<OPTION>" + val + "\n");
						}
						else
						{
								list.addElement("<OPTION>" + val + "\n");
						}
					}
				}
				else
				{
					if (ex) 
					{
						if (!exclude.equalsIgnoreCase(val))
							list.addElement("<OPTION>" + val +"\n");
					}
					else
					{
						list.addElement("<OPTION>" + val +"\n");
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
			html += "</SELECT>" ;
		}
		else if (moredouble)
		{
			//System.out.println("moredouble");
			html += "<SELECT NAME=" + name + " "; 		
			if (size != null) html += "SIZE=" + size;
			if (multiple) 
			{
				html += " MULTIPLE";
			}
			html += ">";
			if (empty) html += "<OPTION> "; 
			String val = null;
			String val2 = null;
			Vector list = new Vector();
            Enumeration e = valuesList.elements();
            while (e.hasMoreElements())
            {
            	val = (String) e.nextElement();
				if (e.hasMoreElements()) 
				{
					val2 = (String) e.nextElement();
				}
				else
				{
					System.out.println("HTMLElementSelect.generate: Expecting a double list (the DOUBLE key word was selected");
					return html;
				}
				if (sel) 
				{
					if (selected.equalsIgnoreCase(val2))
					{
						list.addElement ("<OPTION VALUE=\"" + val2 + "\" SELECTED>" + val + "\n");
					}
					else
					{
						if (ex)
						{
							if (!exclude.equalsIgnoreCase(val2))
								list.addElement("<OPTION VALUE=\"" + val2 + "\">" + val + "\n");
						}
						else
						{
								list.addElement("<OPTION VALUE=\"" + val2 + "\">" + val + "\n");
						}
					}
				}
				else
				{
					if (ex) 
					{
						if (!exclude.equalsIgnoreCase(val2))
							list.addElement("<OPTION VALUE=\"" + val2 + "\">" + val +"\n");
					}
					else
					{
						list.addElement("<OPTION VALUE=\"" + val2 + "\">" + val +"\n");
					}
				}

			}			
			//System.out.println("after");
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
			html += "</SELECT>" ;
		}
		else
		{
			html += "<SELECT NAME=" + name + " "; 		
			if (size != null) html += "SIZE=" + size;
			html += ">";
			html +=	"<OPTION ";
			if (sel) html += " SELECTED";
			html += ">" + values + "</SELECT>" ;
		}
		return html;
	}
}
