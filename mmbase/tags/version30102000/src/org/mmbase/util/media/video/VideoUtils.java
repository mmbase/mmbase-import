/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

/*
	(c) 2000 VPRO

	- rawvideo related file. 
*/

package org.mmbase.util.media.video;

import java.util.*;

import org.mmbase.util.media.MediaUtils;
import org.mmbase.util.media.video.videoparts.*;
import org.mmbase.util.*;

import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;

import java.util.Vector;

public class VideoUtils extends MediaUtils
{
	private String classname = getClass().getName();
	private static boolean debug 	 = false;
	private void debug( String msg ) { System.out.println( classname +":"+ msg ); }
	private static void debug2( String msg ) { System.out.println( "org.mmbase.util.media.video.VideoUtils():"+ msg ); }

	// ---	

	/**
	* Vector with MMObjectNodes (rawvideos), number can be cdtrack or videopart
	*/

	public static Vector getRawVideos( MMBase mm, int number )
	{
		Vector			result 	= null;
		MMObjectBuilder builder	= mm.getMMObject("rawvideos");
		String			where	= "id==" + number;

		result = builder.searchVector( where );	
		debug2("getRawVideos("+number+"): found "+result.size()+" rawvideos");

		return result;
	}

	/*
    * Find a best match in rawvideos:
    *   - if a g2 file is found which is encoded (status=done), set this as preffered
    *   - else find r5 file with best match with respect to wanted speed/channels
    */
    public static RawVideoDef getBestRawVideo( Vector rawvideos, int wantedspeed, int wantedchannels )
    {
        RawVideoDef result = null;

        Enumeration e  = null;
        RawVideoDef ra = null;
        RawVideoDef rawvideo = null;

        if( wantedspeed < RawVideoDef.MINSPEED )
        {
            debug2("getBestRawVideo("+wantedspeed+","+wantedchannels+"): ERROR: wantedspeed("+wantedspeed+") less than minspeed("+RawVideoDef.MINSPEED+")");
            wantedspeed = RawVideoDef.MINSPEED;
        }
        if( wantedspeed > RawVideoDef.MAXSPEED )
        {
            debug2("getBestRawVideo("+wantedspeed+","+wantedchannels+"): ERROR: wantedspeed("+wantedspeed+") greater than maxspeed("+RawVideoDef.MAXSPEED+")"
);
            wantedspeed = RawVideoDef.MAXSPEED;
        }
        if( wantedchannels < 1 )
        {
            debug2("getBestRawVideo("+wantedspeed+","+wantedchannels+"): ERROR: wantedchannels("+wantedchannels+") less than 1!");
            wantedchannels = 1;
        }
        if( wantedchannels > 2 )
        {
            debug2("getBestRawVideo("+wantedspeed+","+wantedchannels+"): ERROR: wantedchannels("+wantedchannels+") greater than 2!");
            wantedchannels = 2;
        }

        // if a g2 with status=done is found, set this as default
        // ------------------------------------------------------
        if( rawvideos != null )
        {
            e = rawvideos.elements();
            if( e.hasMoreElements() )
            {
                while( e.hasMoreElements() && result==null )
                {
                    ra = (RawVideoDef) e.nextElement();
                    if( ra.status == RawVideoDef.STATUS_GEDAAN )
                    {
                        if( ra.format == RawVideoDef.FORMAT_G2 )
                        {
                            result = ra;
                        }
                        else
                        if( ra.format == RawVideoDef.FORMAT_R5 )
                        {
                            if( rawvideo != null )
                            {
                                // cuurent one equal or less than i want?
                                // --------------------------------------
                                if( ra.speed <= wantedspeed )
                                    if( ra.channels == wantedchannels )

                                        // and better than the one i already have?
                                        // ---------------------------------------
                                        if( rawvideo.speed < wantedspeed )
                                        {
                                            if( ra.speed >= rawvideo.speed )
                                                rawvideo = ra;
                                        }
                                        else
                                        {
                                            // one i have is more than needed, use the new one,
                                            // which is equal or less than wanted
                                            // ------------------------------------------------
                                            rawvideo = ra;
                                        }

                            }
                            else
                                // case of having only one available videotrack covered
                                // ----------------------------------------------------
                                rawvideo = ra;
                        }
                        else
                            debug2("getBestRawVideo("+wantedspeed+","+wantedchannels+"): ERROR: format("+ra.format+") of unknown type!");
                    }
                }

                // found one? signal success!
                // --------------------------
                if( rawvideo != null )
                {
                    result = rawvideo;
                }
            }
            else
                debug2("getBestRawVideo("+wantedspeed+","+wantedchannels+"): ERROR: no videoelements to search in vector (empty)!");
        }
        else
            debug2("getBestRawVideo("+wantedspeed+","+wantedchannels+"): ERROR: no rawvideo-elements("+rawvideos+") to search (null)!");

        return result;
    }

    public static Vector getRawVideos( MMBase mmbase, int tracknumber, boolean sort )
    {
        Vector result = null;

        if( tracknumber > 0 )
        {
            String where = "id==" + tracknumber;
            MMObjectBuilder builder = mmbase.getMMObject("rawvideos");
            if( builder != null )
            {
                Vector v = (Vector) builder.searchVector(where);
                if( v != null )
                {
                    Enumeration     e   = v.elements();
                    MMObjectNode    ra  = null;
                    RawVideoDef     rad = null;

                    if( e.hasMoreElements() )
                    {
                        result = new Vector();

                        while( e.hasMoreElements() )
                        {
                            ra  = (MMObjectNode) e.nextElement();
                            rad = new RawVideoDef( ra );
                            result.addElement( rad );
                        }

                        if( sort )
                            result = sort( result );
                    }
                    else
                        if( debug )
                            debug2("getRawVideos("+mmbase+","+tracknumber+"): no rawvideos found for this number!");
                }
                else
                    if( debug )
                        debug2("getRawVideos("+mmbase+","+tracknumber+"): no vector(rawvideos) found for this number!");
            }
            else
                debug2("getRawVideos("+mmbase+","+tracknumber+"): ERROR: mmbase did not return valid MMObject(rawvideos)!");
        }
        else
            debug2("getRawVideos("+mmbase+","+tracknumber+"): ERROR: number("+tracknumber+") is is less than 1!");

        return result;
    }

    private static Vector sort( Vector unsorted )
    {
		return SortedVector.SortVector(unsorted);
    }

    public static String getVideoUrl( MMBase mmbase, scanpage sp, int number, int speed, int channels )
    {
        String url = null;
        if( number > 0 && speed > 0 && channels > 0 )
        {
            MMObjectBuilder b = mmbase.getMMObject( "videoparts" );
            MMObjectNode    n = b.getNode( number );
            if( n != null )
            {
                if( n.getName().equals("videoparts"))
                {
                    debug2("getVideoUrl("+number+","+speed+","+channels+"): number("+number+") is an videopart");
                    VideoPartDef ap = new VideoPartDef();
                    if( ap.setparameters( mmbase, n ))
                    {
                        if( ap.getRawVideos( mmbase, speed, channels, true) )
                        {
                            url = ap.getRealVideoUrl(sp);
                            if( debug )
                            {
                                debug2("getVideoUrl("+number+","+speed+","+channels+"): for videopart: Found a node for number("+number+"), speed("+speed+"), channels("+channels+"), printing. ..");
                                debug2( ap.toText() );
                                debug2("getVideoUrl("+number+","+speed+","+channels+"): for videopart: url(" + url +")");
                            }

                            String author=null;
                            MMObjectBuilder builder = mmbase.getMMObject("videoparts");
                            MMObjectNode    node    = builder.getNode( number );
                            Enumeration g=mmbase.getInsRel().getRelated(node.getIntValue("number"),1573);
                            if (g.hasMoreElements()) {
                                node=(MMObjectNode)g.nextElement();
                                String an = node.getStringValue("name");
                                if( an != null && !an.equals(""))
                                {
                                    if( author != null )
                                        author += an;
                                    else
                                        author  = an;
                                }
                            }
                            if (author==null) author="";
                            debug2("getVideoUrl("+number+","+speed+","+channels+"): for videopart: found author("+author+").");
                            url += "&author=\""+author+"\"";
                        }
                    }
                    else
                        debug2("getVideoUrl("+number+","+speed+","+channels+"): ERROR: Could not find a best match speed("+speed+")/channels("+channels+") for this videopart!");
                }
                else
                    debug2("getVideoUrl("+number+","+speed+","+channels+"): ERROR: number("+number+") buildername("+n.getName()+"), this is not a videopart!");
            }
            else
                debug2("getVideoUrl("+number+","+speed+","+channels+"): ERROR: No videonode found with this number, maybe deleted?!");
        }
        else
            debug2("getVideoUrl("+number+","+speed+","+channels+"): ERROR: parameters not correct!");

        return url;
    }	
}
