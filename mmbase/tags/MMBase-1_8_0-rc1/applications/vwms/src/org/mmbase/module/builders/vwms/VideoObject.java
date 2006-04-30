/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 * @deprecated not used anywhere
 * @author vpro
 * @version $Id: VideoObject.java,v 1.8 2004-10-08 10:48:07 pierre Exp $
 */
public class VideoObject {

    private static Logger log = Logging.getLoggerInstance(VideoObject.class.getName());

    Execute exec=new Execute();
    private String filename;
    private int samples;
    private int channels;
    private int frequency;
    private int time; // Ending time
    private float length;

    public VideoObject() {
    }

    public static VideoObject get(String pool) {
        return getInfo(pool+"/0.mov");
    }

    public static VideoObject getInfo(String filename) {
        Execute sexec=new Execute();
        String inf,t,dat="",tim="";
        VideoObject ao=new VideoObject();
        StringTokenizer tok;
        int line=0,word=0;
        float len;


        log.info("file " + filename);
        ao.setFilename(filename);
        inf=sexec.execute("/usr/local/bin/InfoVideo "+filename);

        tok=new StringTokenizer(inf," \n\r\t",true);
        while(tok.hasMoreTokens()) {
            t=tok.nextToken();
            log.debug(t);
            if (t.equals(" ") || t.equals("\t") || t.equals("\r")) {
                // skip
            } else if (t.equals("\n")) {
                word=0;
                line++;
            } else {
                switch(line) {
                    case 0:  // filetype and filename
                        break;
                    case 1:  // number of samples and date and time
                        if (word==4) { // should be number of samples
                            ao.setSamples(Integer.parseInt(t));
                            // Note divide by channels is needed;
                        }
                        if (word==5) {
                            dat=t; // datum
                        }
                        if (word==6) {
                            tim=t; // time
                        }
                        // we convert to time_t later
                        break;
                    case 2:	 // sampling frequency
                        if (word==2) {
                            ao.setFrequency(Integer.parseInt(t));
                        }
                        break;
                    case 3:	 // number of channels and sample-size
                        if (word==3) {
                            ao.setChannels(Integer.parseInt(t));
                        }
                        break;
                    default:
                        break;
                }
                word++;
            }
        }
        // Info video reports *total* samples not per channel
        ao.setSamples(ao.getSamples()/ao.getChannels());
        // Decode the time (as usual)
        TimeZone tz=TimeZone.getTimeZone("GMT");
        Calendar calendar = new GregorianCalendar(tz);
        Calendar cl=DateSupport.parseDateRev(calendar,dat+" "+tim);
        Date d = cl.getTime ();
        long l = d.getTime ();
        if ((cl.getTimeZone ()).inDaylightTime (d)) {
                l += 60 * 60 * 1000;
        }
        // RICO timetrouble looks good as it uses GMT with MilliOffset
        ao.setTime((int)((l-DateSupport.getMilliOffset())/1000));
        len=(float)(ao.getSamples()/(1.0*ao.getFrequency()));
        ao.setLength(len);

        return ao;
    }

    public VideoObject cut(String name,int start,int stop,int len) {
        int astart;
        int sampstart,sampstop;
        String ex;

        astart=getTime()-(getSamples()/getFrequency());
        if (log.isDebugEnabled()) {
            log.debug("VideoObject start "+astart+","+DateSupport.date2string(astart));
            log.debug("VideoObject cutting from "+DateSupport.date2string(start)+" to "+DateSupport.date2string(stop));
        }
        if (astart>start) {
                log.warn("VideoObject : start smaller than start of wav");
                sampstart=0;
        } else {
                sampstart=(start-astart)*getFrequency();
        }
        if (stop>time) {
                log.warn("VideoObject stop larger than end of wav");
                sampstop=getSamples();
        } else {
                sampstop=(stop-astart)*getFrequency();
        }
        if (log.isDebugEnabled()) {
            log.debug("VideoObject about to cut "+sampstart+","+sampstop);
        }
                ex="/usr/local/bin/CopyVideo -l "+sampstart+":"+sampstop+" "+getFilename()+" -F WAVE "+name;
        if (log.isDebugEnabled()) {
            log.debug("VideoObject cut exec "+ex);
        }
        String s=exec.execute(ex);
        log.info("VideoObject Exec result "+s);
        return getInfo(name);
    }

    public String getFilename() {
            return filename;
    }

    public void setFilename(String s) {
            filename=s;
    }

    public int getSamples() {
            return samples;
    }

    public void setSamples(int s) {
            samples=s;
    }

    public int getChannels() {
            return channels;
    }

    public void setChannels(int s) {
            channels=s;
    }

    public int getFrequency() {
            return frequency;
    }

    public void setFrequency(int s) {
            frequency=s;
    }

    public int getTime() {
            return time;
    }

    public void setTime(int s) {
            time=s;
    }

    public void setLength(float l) {
            length=l;
    }

    public float getLength() {
            return length;
    }

    public String toString() {
            return "VideoObject w="+filename+",s="+samples+",c="+channels+",f="+frequency+",t="+time+"=="+DateSupport.date2string(time)+",l="+getLength();
    }

    public static void main(String args[]) {
            VideoObject ao;

            ao=VideoObject.getInfo(args[0]);
            log.info(ao);
    }
}
