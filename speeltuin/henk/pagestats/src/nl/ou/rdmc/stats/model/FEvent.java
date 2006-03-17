/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Initial Developer of the Original Code is the
 * Ruud de Moor Centrum of the Open University.
 */
package nl.ou.rdmc.stats.model;

import java.util.*;

import org.mmbase.util.logging.*;

//import nl.ou.rdmc.stats.ILogger;
//import nl.ou.rdmc.stats.SystemLogger;

public abstract class FEvent {

  private static final Logger log = Logging.getLoggerInstance(FEvent.class);
   //private static final ILogger log = new SystemLogger();

  private boolean isFinished;
  private FFinishReason finishReason;

  private Date start;
  private Date end;

  private Date activity; // last event's activity

  protected Map pages;

  public FEvent(Date start) {
    log.info("FEvent for date " + start);
    this.start = start;
    activity = start;
    isFinished = false;
    pages = new TreeMap();
  }

  public void updateActivity(Date date) {
    activity = date;
  }
  public Date getActivity() {
    return activity;
  }

  public Long getId() {
    return new Long(start.getTime());
  }

  public Map getPages() {
    return pages;
  }

  public void addPage(FPage page, Date date) {
    // register in common list
    pages.put(date,page);
    childrenAddPage(page, date);
  }

  protected void childrenAddPage(FPage page, Date date) {
    // no action by default
  }

  public void finish(Date end, FFinishReason reason) {
    log.info("finish for date " + end + " and reason " + reason);
    this.end = end;
    isFinished = true;
    finishReason = reason;
    childrenFinish(end, reason);
  }

  protected void childrenFinish(Date end, FFinishReason reason) {
    // no action by default
  }

  public boolean isFinished() {
    return isFinished;
  }
  public FFinishReason getFinishReason() {
    return finishReason;
  }

  public String getStart() {
    return dateToString(start);
  }

  public String getEnd() {
    return dateToString(end);
  }

  public static String dateToString(Date date) {
    if (date==null) return "*";
    Calendar cal = Calendar.getInstance(); cal.setTime(date);
    String result =
        cal.get(Calendar.YEAR)
        + "." + (cal.get(Calendar.MONTH)<10 ? "0" : "" ) +  (cal.get(Calendar.MONTH)+1)
        + "." + (cal.get(Calendar.DAY_OF_MONTH)<10 ? "0" : "" ) + cal.get(Calendar.DAY_OF_MONTH)
        + "-" + (cal.get(Calendar.HOUR_OF_DAY)<10 ? "0" : "" ) + cal.get(Calendar.HOUR_OF_DAY)
        + "." + (cal.get(Calendar.MINUTE)<10 ? "0" : "" ) + cal.get(Calendar.MINUTE)
        + "." + (cal.get(Calendar.SECOND)<10 ? "0" : "" ) + cal.get(Calendar.SECOND);
    return result;
  }

  static String timeToString(int hours, int mins, int secs) {
    StringBuffer sb = new StringBuffer();
    if (hours<10) sb.append("0");
    sb.append(hours); sb.append(":");
    if (mins<10) sb.append("0");
    sb.append(mins); sb.append(":");
    if (secs<10) sb.append("0");
    sb.append(secs);
    return sb.toString();
  }

  public String getLength() {
    if ((start==null) || (end==null)) return "*";
    Calendar stC = Calendar.getInstance(); stC.setTime(start);
    Calendar enC = Calendar.getInstance(); enC.setTime(end);
    int hours = enC.get(Calendar.HOUR_OF_DAY) - stC.get(Calendar.HOUR_OF_DAY);
    //if ( enC.get(Calendar.DAY_OF_YEAR) > stC.get(Calendar.DAY_OF_YEAR) )  hours +=24;
    if (hours<0) return "-";

    int mins = enC.get(Calendar.MINUTE) - stC.get(Calendar.MINUTE);
    if (mins<0) {
      hours -= 1;
      mins +=60;
    }
    int secs = enC.get(Calendar.SECOND) - stC.get(Calendar.SECOND);
    if (secs<0) {
      mins -= 1;
      secs +=60;
    }

    return timeToString(hours, mins, secs);
  }


  int getLengthInSecunds() {
    if ((start==null) || (end==null)) return 0;
    Calendar stC = Calendar.getInstance(); stC.setTime(start);
    Calendar enC = Calendar.getInstance(); enC.setTime(end);
    int hours = enC.get(Calendar.HOUR_OF_DAY) - stC.get(Calendar.HOUR_OF_DAY);
    //if ( enC.get(Calendar.DAY_OF_YEAR) > stC.get(Calendar.DAY_OF_YEAR) ) hours +=24;
    if (hours<0) return 0;

    int mins = enC.get(Calendar.MINUTE) - stC.get(Calendar.MINUTE);
    if (mins<0) {
      hours -= 1;
      mins +=60;
    }
    int secs = enC.get(Calendar.SECOND) - stC.get(Calendar.SECOND);
    if (secs<0) {
      mins -= 1;
      secs +=60;
    }
    return hours*3600 + mins*60 + secs;
  }


}
