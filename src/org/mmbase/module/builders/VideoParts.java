/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */

public class VideoParts extends MMObjectBuilder {

	private static String classname = "VideoParts"; // getClass().getName();

	String diskid;
	int playtime;

	public VideoParts() {
	}


	public static long calcTime( String time )
	{
		long result = -1;

		long r 		= 0;

		int calcList[] 	= new int[5];
		calcList[0]		= 0;
		calcList[1] 	= 100; 	// secs 
		calcList[2] 	= 60;	// min 
		calcList[3] 	= 60;	// hour 
		calcList[4] 	= 24;	// day

		if (time.indexOf(".")!=-1 || time.indexOf(":") != -1)
		{
			int day 	= -1;
			int hour 	= -1;
			int min		= -1;
			int	sec		= -1;
			StringTokenizer tok = new StringTokenizer( time, ":" );
			if (tok.hasMoreTokens())
			{	
				int i 		= 0;
				int	total 	= tok.countTokens();
				int mulfac, t;

				String tt	= null;
				try
				{
					int ttt = 0;
					int tttt = 0;

					while(tok.hasMoreTokens())
					{
						tt 		= tok.nextToken();
						tttt	= 0;

						if (tt.indexOf(".")==-1)
						{
							tttt	= total - i;
							t 		= Integer.parseInt( tt );
							int tot		= t;
	
							while (tttt != 0)
							{
								mulfac 	 = calcList[ tttt ];
								tot 	 = mulfac * tot;	
								tttt--;
								//debug("calctime("+time+"): ["+mulfac+"*"+ttt+"] = "+tot);
							}
							r += tot;
							//debug("calctime("+time+"): adding("+tot+"), making total of("+r+")");
							i++;
						}
					}
				}
				catch( NumberFormatException e )
				{
					debug("calcTime("+time+"): ERROR: Cannot convert pos("+(total-i)+") to a number("+tt+")!" + e.toString());
				}
			}

			if (time.indexOf(".") != -1)
			{
				// time is secs.msecs

				int index = time.indexOf(":");	
				while(index != -1)
				{
					time = time.substring( index+1 );
					index = time.indexOf(":");
				}
	
				index = time.indexOf(".");
				String 	s1 = time.substring( 0, index );
				String	s2 = time.substring( index +1 );

				try
				{
					int t1 = Integer.parseInt( s1 );
					int t2 = Integer.parseInt( s2 );
		
					r += (t1*100) + t2; 
					//debug("calctime("+time+"): adding("+(t1*100 + t2)+"), making total("+r+")");	
				}
				catch( NumberFormatException e )
				{
					debug("calctime("+time+"): ERROR: Cannot convert s1("+s1+") or s2("+s2+")!");
				} 
			}

			result = r;
		}	
		else
		{
			// time is secs
			try
			{
				r = Integer.parseInt( time );
				result = r * 100;
			}
			catch( NumberFormatException e )
			{
				debug("calctime("+time+"): ERROR: Cannot convert time("+time+")!");
			}
		}

		//debug("calctime("+time+"): result("+result+")");
		return result;
	}

	/**
	 * checktime( time )
	 *
	 * time = dd:hh:mm:ss.ss
	 * 
	 * Checks whether part is valid, each part (dd/hh/mm/ss/ss) are numbers, higher than 0, lower than 100
	 * If true, time can be inserted in DB.
	 *
	 */
	private boolean checktime( String time )
	{
		boolean result = true;
		
		if (time!=null && !time.equals(""))
		{
			StringTokenizer tok = new StringTokenizer( time, ":." );
			while( tok.hasMoreTokens() )
				if (!checktimeint(tok.nextToken()))
				{
					result = false;
					break;
				}
		}
		else
			debug("checktime("+time+"): ERROR: Time is not valid!");
		
		//debug("checktime("+time+"): simpleTimeCheck(" + result+")");
		return result;
	}

	private boolean checktimeint( String time )
	{
		boolean result = false;

		try
		{
			int t = Integer.parseInt( time );
			if (t >= 0)
			{
				if( t < 100 )
					result = true;
				else
				{
					debug("checktimeint("+time+"): ERROR: this part is higher than 100!"); 
					result = false;
				}
			}
			else
			{
				debug("checktimeint("+time+"): ERROR: Time is negative!");
				result = false;
			}
		}
		catch( NumberFormatException e )
		{
			debug("checktimeint("+time+"): ERROR: Time is not a number!");
			result = false;
		}

		//debug("checktimeint("+time+"): " + result);	
		return result;
	}

	private String getProperty( MMObjectNode node, String key )
	{
		String result = null;

		//debug("getProperty("+key+"): start");

		int id = -1;
		if( node != null )
		{
			id = node.getIntValue("number");
			
			MMObjectNode pnode = node.getProperty( key );
			if( pnode != null )
			{
				result = pnode.getStringValue( "value" );
			}
			else
			{
				debug("getProperty("+node.getName()+","+key+"): ERROR: No prop found for this item("+id+")!");
			}
		}
		else
			debug("getProperty("+"null"+","+key+"): ERROR: Node is null!");

		//debug("getProperty("+key+"): end("+result+")");
		return result;
	}

	private void putProperty( MMObjectNode node, String key, String value )
	{
		//debug("putProperty("+key+","+value+"): start");
		int id = -1;
		if ( node != null )
		{
			id = node.getIntValue("number");

        	MMObjectNode pnode=node.getProperty(key);
            if (pnode!=null) 
			{
				if (value.equals("") || value.equals("null") || value.equals("-1"))
				{
					// remove
					pnode.parent.removeNode( pnode );

				} else {
					// insert
            		pnode.setValue("value",value);
                	pnode.commit();
				}
            } 
			else 
			{
				if ( value.equals("") || value.equals("null") || value.equals("-1") )
				{
					// do nothing
				}
				else
				{
					// insert
					MMObjectBuilder properties = mmb.getMMObject("properties");
					MMObjectNode snode = properties.getNewNode ("videoparts");
					 //snode.setValue ("otype", 9712);
   		             snode.setValue ("ptype","string");
   		             snode.setValue ("parent",id);
   		             snode.setValue ("key",key);
   		             snode.setValue ("value",value);
   		             int id2=properties.insert("videoparts", snode); // insert db
   		             snode.setValue("number",id2);
   		             node.putProperty(snode); // insert hash
				}
			}
		}
		else
			debug("putProperty("+"null"+","+key+","+value+"): ERROR: Node is null!");

		//debug("putProperty("+key+","+value+"): end");
	}

	private void removeProperty( MMObjectNode node, String key )
	{
		//debug("removeProperty("+key+","+value+"): start");
		if ( node != null )
		{
        	MMObjectNode pnode=node.getProperty(key);
            if (pnode!=null) 
				pnode.parent.removeNode( pnode );
			else
				debug("removeNode("+node+","+key+"): ERROR: Property not found( and cannot remove )");
		}
	}



	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("storage")) {
			int val=node.getIntValue("storage");
			switch(val) {
				case 1: return("Stereo");
				case 2: return("Stereo geen backup");
				case 3: return("Mono");
				case 4: return("Mono geen backup");
				default: return("Onbepaald");
			}
		}
		return(null);
	}

	/**
	* setDefaults for a node
	*/
	public void setDefaults(MMObjectNode node) {
		node.setValue("storage",2);
	}

	public static void debug( String msg )
	{
		System.out.println( classname +":"+ msg );
	}

	public static void main( String args[] )
	{
		String time = "05:04:03:02.01";
		System.out.println("calcTime("+time+") = " + VideoParts.calcTime( time ));	
		time = "04:03:02.01";
		System.out.println("calcTime("+time+") = " + VideoParts.calcTime( time ));	
		time = "03:02";
		System.out.println("calcTime("+time+") = " + VideoParts.calcTime( time ));	
		time = "02.01";
		System.out.println("calcTime("+time+") = " + VideoParts.calcTime( time ));	
		time = "02";
		System.out.println("calcTime("+time+") = " + VideoParts.calcTime( time ));	
	}	
}
