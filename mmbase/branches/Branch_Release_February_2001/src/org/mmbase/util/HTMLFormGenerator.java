/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.module.*;

/**
* <b>input</b> Macro Vector of Strings, Proc Processor.<BR>
* <b>output</b> HTML FORM Element(String).<BR>
*  <BR> 
* generates from the MACRO Strings a HTML FORM. <BR>
*<P> 
* How should a macro look like in your HTML file?<BR>
* &lt;MACRO ELEMENT NAME VALUE [TAG(S)]&gt;<BR>
* The "MACRO" word will be stripped in de servscan Servlet and does not enters this object.<BR>
* ELEMENT: (if you are fimiliar with HTML FORMs this should be known stuff   <BR>
* TEXTAREA, SELECT, PASSWORD, RADIO, CHECKBOX  <BR>
* NAME:<BR>
* The name of this HTML Element <BR>
* VALUE:<BR> 
* This can do 2 things: <BR>
* 1. if the PROC TAG is present, this value will be sent to the processor <BR>
* 2. else ( no PROC TAG) this will be the VALUE of the HTML Element <BR>
* TAG(S) <BR> 
* there can be more TAGS (some TYPES does support them and some don't ...)<BR>
* some need an argument some don't (some can handle both :-)<BR> 
* <PRE>
* TAG                        WHAT DOES IT?                                                         SUPPORTED BY WHICH ELEMENT?
<hr SIZE=1>
* ROWS=                      The number of ROWS                                                    TEXTAREA
* COLS=                      The number of COLS                                                    TEXTAREA
* SIZE=                      The SIZE of this ELEMENT                                              SELECT TEXT PASSWORD
* MULTIPLE                   We want MULTIPLE selection                                            SELECT     
* CHECKED(=) or SELECTED(=)  The SELECTED element (if there is no PROC tag     
                             you don't need an argument)                                         SELECT RADIO CHECKBOX
* EXCLUDE=                   The EXCLUDED element (only use with PROC)                             SELECT RADIO CHECKBOX
* VERTICAL                   Add a &lt;BR&gt; after a item                                               SELECT RADIO CHECKBOX
* HORIZONTAL                 Don't add a &lt;BR&gt; after a item                                         SELECT RADIO CHECKBOX
* EMPTY                      Add an EMPTY element                                                  SELECT
* PROC                       Use the processor to get a list (Vector) of values (Strings)          TEXT and PASSWORD (only the first of the Vector will be used) SELECT RADIO CHECKBOX 
* DOUBLE                     Tell the processor to get a paired list of values                     SELECT RADIO CHECKBOX
</PRE>
*   
* <b>example</b>: SELECT name ProcessorTag SELECTED="selected_item" MULTIPLE SIZE=10 PROC VERTICAL.<BR>
* This generates a SELECT HTML FORM with NAME=name the OPTIONs are filled with<BR>
* the list which is received from the Processor.getList("ProcessorTag") call<BR>
* If the "selected_item" is presend in the list it is "&lt;OPTION SELECTED&gt;" item" <BR>
* The SIZE is the number of displayed items and MULTIPLE tells that multiple <BR>
* selections are posible. <BR> 
* VERTICAL tells that you want a &lt;BR&gt; after every &lt;OPTION&gt;<BR>
* <BR> <b> Generated</b>  from <b>example</b>:<BR>
* &lt;SELECT NAME=name SIZE=10 MULTIPLE&gt; <BR>
* &lt;OPTION&gt; item1 &lt;BR&gt;<BR>
* &lt;OPTION SELECTED&gt; sleceted_item &lt;BR&gt;<BR>
* &lt;OPTION&gt; item3 &lt;BR&gt;<BR>
* &lt;/SELECT&gt; <BR>
*<P> 
* 
*
* @author Jan van Oosterom
* @version 23-Sep-1996 
*/
public class HTMLFormGenerator 
{
	/**
	*  TEXTAREA Element
	*/
	protected HTMLElementTextArea textArea;	
	
	/**
	* RADIO Element
	*/
	protected HTMLElementRadio 	radio;	
	/**
	* SELECT Element
	*/
	protected HTMLElementSelect 	select;	
	/**
 	* CHECKBOX Element
	*/
	protected HTMLElementCheckBox checkBox;	
	/**
	* TEXT
	*/
	protected HTMLElementText 	text;	
	/**
	* PASSWORD
	*/
	protected HTMLElementPassword password;	
	
	/**
	*	Contructs the HTMLElements 
	*/
	public HTMLFormGenerator()
	{
		textArea = new HTMLElementTextArea();
		radio 	 = new HTMLElementRadio();
		select 	 = new HTMLElementSelect();
		checkBox = new HTMLElementCheckBox();
		text     = new HTMLElementText();
		password = new HTMLElementPassword();
	}

	/**	
	* Gets the first element of the Vector and selects the corespondending HTMLElement,<BR>
	* to handle this elements and passes the tail elements to that Element.<BR>
	* output String: HTML FORM Element (TEXTAREA, RADIO, SELECT, CHECKBOX, TEXT or PASSWORD).<BR>
	* @param proc The Processor to handle the getList (2nd Element from the Vector marco)
	* @param macro The Vector with Strings  
	*/
	public String getHTMLElement (scanpage sp,ProcessorInterface proc, Vector macro)
	{
		String type = getFirstElement(macro);
		Vector params = getTailElements(macro);
		
		if (type.equalsIgnoreCase("TEXTAREA"))
		{		// we want a TEXTAREA .....

			return textArea.generateHTML(sp,proc,params);
		}

		if (type.equalsIgnoreCase("RADIO"))
		{		//We want a RADIO ...... 
			String radioHTML = radio.generateHTML(sp,proc,params) ; 
			return radioHTML;
		}	
		

		if (type.equalsIgnoreCase("SELECT"))
		{		//We want a SELECT ...... 
			String selectHTML = select.generateHTML(sp,proc,params) ; 
			return selectHTML;
		}	
		
		if (type.equalsIgnoreCase("CHECKBOX"))
		{		//We want a CHECKBOX...... 
			String checkBoxHTML = checkBox.generateHTML(sp,proc,params) ; 
			return checkBoxHTML;
		}	

		if (type.equalsIgnoreCase("TEXT"))
		{		//We want a TEXT input...... 
			String textHTML = text.generateHTML(sp,proc,params) ; 
			return textHTML;
		}	
		
		if (type.equalsIgnoreCase("PASSWORD"))
		{		//We want a PASSWORD input...... 
		//	System.out.println("password ....:");
			String passwordHTML = password.generateHTML(sp,proc,params) ; 
			return passwordHTML;
		}	
		System.out.println("HTMLFormGenerator: Unknown HTML type re quested: " + type);
		return null;
	}

	/*
	* returns the tail elements from the vector. (All but the first).
	*/	
	protected Vector getTailElements(Vector vector)
	{ 	
		Enumeration e = vector.elements();
		if (e.hasMoreElements())
		{		
			//We don't want the first one ....	
			Object dummy = e.nextElement();	 
				
			Vector tailToReturn = new Vector();
			//We only want the tail

			while(e.hasMoreElements())
			{
				tailToReturn.addElement(e.nextElement());		
			}
			return tailToReturn;
		}
		else
		{
			System.out.println("Empty Vector in HTMLFormGenerator");
			return null;
		}
		
	}
	/*
	* returns the first element of the vector 
	*/	
	protected String getFirstElement(Vector vector)
	{
		return (String) vector.elementAt(0); 
	}
}
