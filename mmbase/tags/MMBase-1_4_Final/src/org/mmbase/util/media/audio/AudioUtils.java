/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

/*
	(c) 2000 VPRO

	- rawaudio related file. 
*/

package org.mmbase.util.media.audio;

import java.util.*;
import java.net.URLEncoder;

import org.mmbase.util.*;
import org.mmbase.util.media.MediaUtils;
import org.mmbase.util.media.audio.audioparts.*;

import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;

import java.util.Vector;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class AudioUtils extends MediaUtils {

    private static Logger log = Logging.getLoggerInstance(AudioUtils.class.getName());


    // ---    

    /**
    * Vector with MMObjectNodes (rawaudios), number can be cdtrack or audiopart
    */
    public static Vector getRawAudios( MMBase mm, int number )
    {
        Vector            result     = null;
        MMObjectBuilder builder    = mm.getMMObject("rawaudios");
        String            where    = "id==" + number;
        result = builder.searchVector( where );    
        return result;
    }

    /*
    * Find a best match in rawaudios:
    *   - if a g2 file is found which is encoded (status=done), set this as preffered
    *   - else find r5 file with best match with respect to wanted speed/channels
    */
    public static RawAudioDef getBestRawAudio( Vector rawaudios, int wantedspeed, int wantedchannels )
    {
        RawAudioDef result = null;

        Enumeration e  = null;
        RawAudioDef ra = null;
        RawAudioDef rawaudio = null;

        if( wantedspeed < RawAudioDef.MINSPEED )
        {
            log.error("getBestRawAudio("+wantedspeed+","+wantedchannels+"): wantedspeed("+wantedspeed+") less than minspeed("+RawAudioDef.MINSPEED+")");
            wantedspeed = RawAudioDef.MINSPEED;
        }
        if( wantedspeed > RawAudioDef.MAXSPEED )
        {
            log.error("getBestRawAudio("+wantedspeed+","+wantedchannels+"): wantedspeed("+wantedspeed+") greater than maxspeed("+RawAudioDef.MAXSPEED+")");
            wantedspeed = RawAudioDef.MAXSPEED;
        }
        if( wantedchannels < 1 )
        {
            log.error("getBestRawAudio("+wantedspeed+","+wantedchannels+"): wantedchannels("+wantedchannels+") less than 1!");
            wantedchannels = 1;
        }
        if( wantedchannels > 2 )
        {
            log.error("getBestRawAudio("+wantedspeed+","+wantedchannels+"): wantedchannels("+wantedchannels+") greater than 2!");
            wantedchannels = 2;
        }

        // if a g2 with status=done is found, set this as default
        // ------------------------------------------------------
        if( rawaudios != null )
        {
            e = rawaudios.elements();
            if( e.hasMoreElements() )
            {
                while( e.hasMoreElements() && result==null )
                {
                    ra = (RawAudioDef) e.nextElement();
                    if( ra.status == RawAudioDef.STATUS_GEDAAN )
                    {
                        if( ra.format == RawAudioDef.FORMAT_WAV )
                        {
                            // do nothing with this original file
                        }
                        else
                        if( ra.format == RawAudioDef.FORMAT_G2 )
                        {
                            result = ra;
                        }
                        else
                        if( ra.format == RawAudioDef.FORMAT_R5 )
                        {
                            if( rawaudio != null )
                            {
                                // cuurent one equal or less than i want?
                                // --------------------------------------
                                if( ra.speed <= wantedspeed )
                                    if( ra.channels == wantedchannels )

                                        // and better than the one i already have?
                                        // ---------------------------------------
                                        if( rawaudio.speed < wantedspeed )
                                        {
                                            if( ra.speed >= rawaudio.speed )
                                                rawaudio = ra;
                                        }
                                        else
                                        {
                                            // one i have is more than needed, use the new one,
                                            // which is equal or less than wanted
                                            // ------------------------------------------------
                                            rawaudio = ra;
                                        }

                            }
                            else
                                // case of having only one available audiotrack covered
                                // ----------------------------------------------------
                                rawaudio = ra;
                        }
                        else
                            log.error("getBestRawAudio("+wantedspeed+","+wantedchannels+"): format("+ra.format+") of unknown type!");
                    }
                }

                // found one? signal success!
                // --------------------------
                if( rawaudio != null )
                {
                    result = rawaudio;
                }
            }
            else
                log.error("getBestRawAudio("+wantedspeed+","+wantedchannels+"): no audioelements to search in vector (empty)!");
        }
        else
            log.error("getBestRawAudio("+wantedspeed+","+wantedchannels+"): no rawaudio-elements("+rawaudios+") to search (null)!");

        return result;
    }

    public static Vector getRawAudios( MMBase mmbase, int tracknumber, boolean sort )
    {
        Vector result = null;

        if( tracknumber > 0 )
        {
            String where = "id==" + tracknumber;
            MMObjectBuilder builder = mmbase.getMMObject("rawaudios");
            if( builder != null )
            {
                Vector v = (Vector) builder.searchVector(where);
                if( v != null )
                {
                    Enumeration     e   = v.elements();
                    if( e.hasMoreElements() )
                    {
                        result = new Vector();

                        while( e.hasMoreElements() )
                            result.addElement( new RawAudioDef( (MMObjectNode) e.nextElement() ));

                        if( sort )
                            result = sort( result );
                    }
                    else
                        if (log.isDebugEnabled()) {
                            log.debug("getRawAudios("+mmbase+","+tracknumber+"): no rawaudios found for this number!");
                        }
                }
                else
                    if (log.isDebugEnabled()) {
                        log.debug("getRawAudios("+mmbase+","+tracknumber+"): no vector(rawaudios) found for this number!");
                    }
            }
            else
                log.error("getRawAudios("+mmbase+","+tracknumber+"): mmbase did not return valid MMObject(rawaudios)!");
        }
        else
            log.error("getRawAudios("+mmbase+","+tracknumber+"): number("+tracknumber+") is is less than 1!");

        return result;
    }

    private static Vector sort( Vector unsorted )
    {
        return SortedVector.SortVector(unsorted);
    }    


    public static String getAudioUrl( MMBase mmbase, scanpage sp, int number, int speed, int channels )
    {
        String url = null;
        if( number > 0 && speed > 0 && channels > 0 )
        {

// -----------------------
// we only have audioparts
// -----------------------
            url = getAudiopartUrl( mmbase, number, sp, speed, channels );
        }
        else
            log.error("getAudioUrl("+number+","+speed+","+channels+"): parameters not correct!");

        return url;
    }


    public static String getAudiopartUrl(MMBase mmbase, int number, scanpage sp, int speed, int channels)
    {
        String url = null;
        AudioPartDef ap = new AudioPartDef();
        if( ap.setparameters( mmbase, number )) {
            if( ap.getRawAudios( mmbase, speed, channels, true) ) {
                url = ap.getRealAudioUrl(sp);
                if (log.isDebugEnabled()) {
                    log.debug("getAudioUrl("+number+","+speed+","+channels+"): for audiopart: Found a node for number("+number+"), speed("+speed+"), channels("+channels+"), printing. ..");
                    log.debug( ap.toText() );
                    log.debug("getAudioUrl("+number+","+speed+","+channels+"): for audiopart: url(" + url +")");
                }

                String author=null;
                Enumeration g=mmbase.getInsRel().getRelated(number,"groups");
                String an = null;
                if (g.hasMoreElements()) {
                    an=((MMObjectNode)g.nextElement()).getStringValue("name");
                    if( an != null && !an.equals("")) {
                        if( author != null ) author += an;
                        else                  author  = an;
                    }
                }
                if (author==null) author="";
                log.info("getAudioUrl("+number+","+speed+","+channels+"): for audiopart: found author("+author+").");
                //url += "&author=\""+author+"\"";
                //Removed double quotes in author value since Real SMIL doesn't handle it correctly.
                //url += "&author="+plusToProcent20(URLEncoder.encode(author)); //URLEncode value.
                //Our Real server can't handle urlencoded strings, aargh.
                author=makeRealCompatible(author);
                url += "&author="+author;
            }
        } else
            log.error("getAudioUrl("+number+","+speed+","+channels+"): Could not find a best match speed("+speed+")/channels("+channels+") for this audiopart!"); 
        return url;
    }

/*
    public static String getCdtrackUrl(MMBase mmbase, int number, scanpage sp, int speed, int channels) {
        String url = null;
        if( debug ) debug2("getAudioUrl("+number+","+speed+","+channels+"): number("+number+") is a cdtrack");
        CdtrackDef cd = new CdtrackDef();
        if( cd.setparameters( mmbase, number )) {
            if( cd.getRawAudios( mmbase, speed, channels, true) ) {
                url = cd.getRealAudioUrl(sp);
                if( debug ) {
                    debug2("getAudioUrl("+number+","+speed+","+channels+"): for cdtrack: Found a cdtracknode for number("+number+"), speed("+speed+"), channels("+channels+"), printing...");
                    debug2( cd.toText() );
                    debug2("getAudioUrl("+number+","+speed+","+channels+"): for cdtrack: url(" + url +")");
                }
                String author=null;
                Enumeration g=mmbase.getInsRel().getRelated(number,1573);
                String an = null;
                if (g.hasMoreElements()) {
                    an = ((MMObjectNode)g.nextElement()).getStringValue("name");
                    if( an != null && !an.equals("")) {
                        if( author != null ) author += an;
                        else                  author  = an;
                    }
                }
                if (author==null) author="";
                if( debug )debug2("getAudioUrl("+number+","+speed+","+channels+"): for cdtrack: found author("+author+") for this cdtrack.");
                //url += "&author=\""+author+"\"";
                //Removed double quotes in author value since Real SMIL doesn't handle it correctly.
                //url += "&author="+plusToProcent20(URLEncoder.encode(author)); //URLEncode value.
                //Our Real server can't handle urlencoded strings, aargh.
                url += "&author="+author;
            }
        }
        else
            debug2("getAudioUrl("+number+","+speed+","+channels+"): ERROR: Could not find a best match speed("+speed+")/channels("+channels+") for this cdtrack!");

        return url;
    }
*/
}
