/*

	(c) 2000 VPRO
	 
	@author 	Marcel Maatkamp, marmaa@vpro.nl
	@version	$version$
	
	$log$	
*/

package org.mmbase.util.media;

import java.net.*;
import java.util.*;

import org.mmbase.util.*;

public class MediaUtils
{
	// vars 
	// ----

	private static 	String		classname 		 = getClass().getName();
	private static 	boolean 	debug 			 = false;
	private static	void 		debug( String msg ) { System.out.println( classname +":"+ msg ); } 

	private static  boolean		isForVPRO		= true;
	/**
	* 
	*/
	public static String getBestMirrorUrl( scanpage sp, String url )
	{
		String 	result 	= null;

		// parameters ok?
		// --------------

		if( debug ) debug("getBestMirrorUrl("+url+")");

		//if( sp != null )
		{
			if( checkstring("getBestMirrorUrl","url",url) )
			{
				// start tagging the url, format is one of two formats:
				// 
				// 		- "http://station.vpro.nl/data/<nr>/<speed>_<channels>.ra" or			(old way)
				// 		- "F=/<nr>/<speed>_<channels>.ra H1=station.vpro.nl H2=streams.omroep.nl"  (new)
				// 		- "F=/<nr>/surestream.rm H1=station.vpro.nl H2=streams.omroep.nl"		(newest :)
				
				StringTagger 	tagger 	= new StringTagger( url );
				String			file	= tagger.Value("F");
				String 			u 		= url;

				// is it format "F=.."?
				// --------------------
				if( file != null && !file.equals("") )
				{
					// is this class used by VPRO or is it for others
					// ----------------------------------------------
					Hashtable 	urls 			= getUrls( url );
					boolean 	isInternalVPRO	= false;

					file = file.trim();

					if( isForVPRO )
					{
						// this is the exception 
						// ---------------------
						if( sp.isInternalVPROAddress() )
							// this user has to be redirected to station.vpro.nl 
							// if file is there, otherwise to alternative url
							// -------------------------------------------------
							isInternalVPRO = true;
					}

					u = filterBestUrl(sp, urls,isInternalVPRO);

					if( u.endsWith("/") )
					{
						if( file.startsWith("/") )
						// too many /'s
						// ------------
						file = file.substring(1);

					}
					else
					{
						if( !file.startsWith("/") )
						// too few /'s
						// -----------
						file = file + "/";
					}
					result = u + file;
				}
				else
				{
					// format = http://station.vpro.nl/audio/ra/<nr>/<speed>_<chan>.ra
					// output = station.vpro.nl/<nr>/<speed>_<chan>.ra

					u = url;
					int i = u.indexOf("//");	
					if( i > 0 )
					{
						u = u.substring( i+2 );
						
						i = u.indexOf("/");
						if( i > -1 )
						{
							String hostname = u.substring(0, i);
							u = u.substring( i+1 );

							if( u.startsWith("data/") )
								u = u.substring( 5 );

							if( u.startsWith("audio/") )
								u = u.substring( 6 );

							if( u.startsWith("ra/") )
								u = u.substring( 3 );

							if( u.startsWith("/") )
								u = hostname + u;
							else
								u = hostname + "/" + u; 
						}
					}
					result = u ;
				}
			}
		}
		if( url != null && !url.equals(""))
		{
			if(debug)
				debug("getBestMirrorUrl("+url+"): result("+result+")");
		}
		else
			debug("getBestMirrorUrl("+url+"): ERROR: No url found for this node on page("+sp.getUrl()+"), ref("+sp.req.getHeader("Referer")+")") ;

		return result;
	}


	private static String filterBestUrl( scanpage sp, Hashtable urls, boolean isInternal ) {
		String 		result 	= null;
		String 		key 	= null;
		String 		value	= null;	
		Enumeration e 		= urls.keys();	

		if( debug ) 
			if( isInternal )
				debug("filterBestUrl("+urls+","+isInternal+"): internal user detected.");
			else
				debug("filterBestUrl("+urls+","+isInternal+"): external user detected.");

		while( e.hasMoreElements() )
		{
			key 	= (String) e.nextElement();
			value 	= (String) urls.get( key );

			// not null or empty
			// -----------------
			if( checkstring( "filterBestUrl","value",value) )
			{
				if( value.startsWith("station") || value.startsWith("beep") )
				{
					if( isInternal )
						// always use this one
						// -------------------
						result = value;
					else
						// only use if none better found
						// -----------------------------
						if( result == null )
							result = value;
				}
				else
				if( value.startsWith("streams") )
				{
					if( isInternal )
					{
						// only use if none better found
						// -----------------------------
						if( result == null )
							result = value;
					}
					else
						// always use this one
						// -------------------
						result = value;
				}
				else
					debug("filterBestUrl("+urls+","+isInternal+"): WARNING: Found url("+value+") with unknown server!");
			}
		}

		if( debug ) 
			debug("filterBestUrl("+urls+","+isInternal+"): found url("+result+")");

		if( result == null ) {
			debug("filterBestUrl("+sp.getUrl()+","+urls+","+isInternal+"): ERROR: No valid url found in table, urls: ");
			Enumeration e2 = urls.keys();
			int i = 1;

			String k = null; 
			String v = null;

			while( e2.hasMoreElements() ) {
				k = (String)e2.nextElement();
				v = (String)urls.get( k );
				debug("url("+i+"): key("+k+"), value("+v+")");
				i++;
			}	

			result = "beep.vpro.nl";
		}

		return result;
	}

	/**
	* Get all the tags in form 'H<nr>=<url>' out of this string and put urls in hashtable
	*/
	private static Hashtable getUrls( String url )	
	{
		Hashtable 		result	= new Hashtable();
		StringTokenizer tok 	= new StringTokenizer( url );
		String 			other	= null;
		int 			i 		= 0;

		String			snumber = null;
		int				number	= 0;

		if( debug ) debug("getUrls("+url+")");

		while( tok.hasMoreTokens() )
		{
			other = tok.nextToken();			
			if( other.startsWith("F=") )
			{ 
				// do nothing, have already
				// ------------------------
			}
			else
			{
				if( other.startsWith("H") )
				{
					i = other.indexOf("=");
					
					// -1 is def wrong, 0 = serious trouble in the JVM :) 
					// --------------------------------------------------

					if( i > 0 )
					{
						snumber = other.substring(1, i).trim();
						try
						{
							// number not actually needed (yet?) but fetch it anyway
							// -----------------------------------------------------
	
							number 	= Integer.parseInt( snumber );
							other 	= other.substring( i+1 ).trim();

							// add url in hashtable
							// --------------------
							result.put( snumber, other );
							if( debug ) debug("urls(): got url("+number+","+other+")");

						}
						catch( NumberFormatException e )
						{
							debug("urls("+url+"): ERROR: While parsing url("+other+"): This is not a number("+snumber+")!");
						}	

					}
					else
						debug("urls("+url+"): ERROR: This url("+other+") is malformed! (Where is the '='?)");	
				}
				else
					debug("urls("+url+"): WARNING: got something, dunno what("+other+")! (not 'F=..' or 'H<nr>=..')");	
			}
		}	

		return result;	
	}


	private static boolean checkstring( String method, String name, String value )
	{
		boolean result = false;

	//			if(method == null) 		debug("checkstring("+method+","+name+","+value+"): ERROR: method("+method+") is null!");
	//	else 	if(method.equals("")) 	debug("checkstring("+method+","+name+","+value+"): ERROR: method("+method+") is empty!");
	//	else 	if(name == null) 		debug("checkstring("+method+","+name+","+value+"): ERROR: name("+method+") is null!");
	//	else 	if(name.equals("")) 	debug("checkstring("+method+","+name+","+value+"): ERROR: name("+method+") is empty!");
	//	else	
				if(value == null)		debug( method+"(): ERROR: "+name+"("+value+") is null!");
		else	if(value.equals(""))	debug( method+"(): ERROR: "+name+"("+value+") is empty!");
		else	
				result = true;
			
		return result;
	}


	public static void main( String args[] )
	{
		String filename;

		System.out.println( classname + ": Test the methods:");
		
		filename = "F=/1426/40_1.ra H1=station.vpro.nl/ H2=streams.omroep.nl/vpro/";
		System.out.println( classname +": -> " + filename + " : " + org.mmbase.util.media.MediaUtils.getBestMirrorUrl(null, filename) );
		filename = "http://station.vpro.nl/audio/ra/1200/40_1.ra";
		System.out.println( classname +": -> " + filename + " : " + org.mmbase.util.media.MediaUtils.getBestMirrorUrl(null, filename) );
		filename = "rtsp://station.vpro.nl/audio/ra/1200/40_1.ra";
		System.out.println( classname +": -> " + filename + " : " + org.mmbase.util.media.MediaUtils.getBestMirrorUrl(null, filename) );
	}
}
