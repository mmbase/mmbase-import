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
package nl.ou.rdmc.stats.process;

import java.util.Calendar;
import java.util.Date;

public class LineInfo {

  private Date date = null;
  private String userStr = null;
  private String ipStr = null;
  private String sessionStr = null;


  public LineInfo() {
  }

  void setUserStr(String userStr) {
    this.userStr = userStr;
  }

  void setIpStr(String ipStr) {
    this.ipStr = ipStr;
  }

  void setSessionStr(String sessionStr) {
    this.sessionStr = sessionStr;
  }

  void setDateStr(String dateStr) {
    if (dateStr!=null) date = parseDate(dateStr);
  }

  boolean validate() {
    return (userStr!=null) && (date!=null);
  }


  String getUser() {
    return userStr;
  }

  String getIp() {
    return ipStr;
  }

  String getSessionId() {
    return sessionStr;
  }

  Date getDate() {
    return date;
  }


  private Date parseDate(String dateStr) {
    /* the date string  formed as

      String date =
        cal.get(Calendar.YEAR)
        + "." + (cal.get(Calendar.MONTH)<10 ? "0" : "" ) +  cal.get(Calendar.MONTH)
        + "." + (cal.get(Calendar.DAY_OF_MONTH)<10 ? "0" : "" ) + cal.get(Calendar.DAY_OF_MONTH)
        + "-" + (cal.get(Calendar.HOUR_OF_DAY)<10 ? "0" : "" ) + cal.get(Calendar.HOUR_OF_DAY)
        + "." + (cal.get(Calendar.MINUTE)<10 ? "0" : "" ) + cal.get(Calendar.MINUTE)
        + "." + (cal.get(Calendar.SECOND)<10 ? "0" : "" ) + cal.get(Calendar.SECOND);

     */

    String dt[] = dateStr.split("-");
    if (dt.length!=2) return null;
    String d[] = dt[0].split("\\.");
    String t[] = dt[1].split("\\.");
    if (d.length!=3) return null;
    if (t.length!=3) return null;

    try {
      Calendar date = Calendar.getInstance();
      date.set(Integer.parseInt(d[0]), // year,
               Integer.parseInt(d[1]), // month,
               Integer.parseInt(d[2]), // date,
               Integer.parseInt(t[0]), // hourOfDay,
               Integer.parseInt(t[1]), // minute,
               Integer.parseInt(t[2]) // second
               );
      return date.getTime();
    } catch(NumberFormatException e) {
      return null;
    }
  }

  public String toString() {
    return "\""+date + "--" + userStr + "--" + ipStr + "--" + sessionStr+"\"";
  }

}
