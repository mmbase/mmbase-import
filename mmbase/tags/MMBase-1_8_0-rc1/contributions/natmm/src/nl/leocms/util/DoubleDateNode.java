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
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.util;

import org.mmbase.util.logging.*;
import java.util.*;
import org.mmbase.bridge.Node;

/**
 * DoubleDateNode
 *
 * @author Henk Hangyi
 * @version $Revision: 1.3 $, $Date: 2006-03-14 13:37:15 $
 *
 */
 
 
public class DoubleDateNode implements Comparable
{
  private static final Logger log = Logging.getLoggerInstance(DoubleDateNode.class);
  private String nodeNumber;
  private Date beginDate;
  private Date endDate;
                           
  public DoubleDateNode() {
    this.nodeNumber = "new";
    this.beginDate = new Date();
    this.endDate = new Date();
  }

  public DoubleDateNode(int nextNewNodeNumber) {
    this.nodeNumber = "" + nextNewNodeNumber;
    this.beginDate = new Date();
    this.endDate = new Date();
  }

  public DoubleDateNode(Node node) {
    this.nodeNumber =  "" + node.getNumber();
    this.beginDate = new Date(node.getLongValue("begindatum")*1000);
    this.endDate = new Date(node.getLongValue("einddatum")*1000);
  }
  
  public void setNumber(String nodeNumber) { this.nodeNumber = nodeNumber; }
  public String getNumber() { return nodeNumber; }
  public void setBegin(Date beginDate) { this.beginDate = beginDate; }
  public Date getBegin() { return beginDate; }
  public void setEnd(Date endDate) { this.endDate = endDate; }
  public Date getEnd() { return endDate; }
   
  public void clipBeginOnToday() {
      Date now = new Date();
      if(beginDate.getTime()<now.getTime()) {
         Calendar cal = Calendar.getInstance();
         cal.setTime(now);
         int year = cal.get(Calendar.YEAR);
         int month = cal.get(Calendar.MONTH);
         int day_of_month =cal.get(Calendar.DAY_OF_MONTH);
         cal.setTime(beginDate); // to set start time
         cal.set(year,month,day_of_month); // to clip begin on today
         this.beginDate = cal.getTime();
      }  
  }

  public String getKey() {
      return beginDate.getTime()/1000 + "," + endDate.getTime()/1000;
  }
  
  public String getValue() {
      return beginDate + " " + endDate;
  }            
  
  public String getSeparatedBeginTime() {
      Calendar cal = Calendar.getInstance();
      cal.setTime(beginDate);
      String seperatedValue = "" + cal.get(Calendar.YEAR) + ".";
      if (cal.get(Calendar.MONTH)+1<10) {
         seperatedValue += "0";
      }
      seperatedValue += (cal.get(Calendar.MONTH)+1) + ".";
      if (cal.get(Calendar.DAY_OF_MONTH)<10){
         seperatedValue += "0";
      }
      seperatedValue += cal.get(Calendar.DAY_OF_MONTH) + "-";
      if (cal.get(Calendar.HOUR_OF_DAY)<10) {
         seperatedValue += "0";
      }
      seperatedValue += cal.get(Calendar.HOUR_OF_DAY) + ".";
      if (cal.get(Calendar.MINUTE)<10){
         seperatedValue += "0";
      }
      seperatedValue += cal.get(Calendar.MINUTE);
      return seperatedValue;
  }


 public String getCommaSeparatedValue() {
      Calendar cal = Calendar.getInstance();
      cal.setTime(beginDate);
      String commaSeperatedValue = cal.get(Calendar.YEAR) + "," + (cal.get(Calendar.MONTH)+1) + "," + cal.get(Calendar.DAY_OF_MONTH)
         + "," + cal.get(Calendar.HOUR_OF_DAY) + "," + cal.get(Calendar.MINUTE);
      cal.setTime(endDate);
      commaSeperatedValue += "," + cal.get(Calendar.YEAR) + "," + (cal.get(Calendar.MONTH)+1) + "," + cal.get(Calendar.DAY_OF_MONTH)
         + "," + cal.get(Calendar.HOUR_OF_DAY) + "," + cal.get(Calendar.MINUTE);
      commaSeperatedValue += "," + nodeNumber;
      return commaSeperatedValue;
  }

  public String getReadableDate() {
      return getReadableDate(" ");
  }

  public String getReadableDate(String seperator) {
      String [] days_lcase = { "zo","ma","di","wo","do","vr","za" }; 
      String [] months_lcase = { "januari","februari","maart","april","mei","juni","juli",
					"augustus","september","oktober","november","december" };
	   Calendar cal = Calendar.getInstance();
      cal.setTime(beginDate);
      int beginDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
      int beginDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)-1;
      int beginMonth = cal.get(Calendar.MONTH);
      int beginYear = cal.get(Calendar.YEAR);
      cal.setTime(endDate);
      int endDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
      int endDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)-1;
      int endMonth = cal.get(Calendar.MONTH);
      int endYear = cal.get(Calendar.YEAR);
      boolean singleDay = false;
      
      String readableValue = "" + beginDayOfMonth;
      if(beginYear==endYear) { // *** same year ***
        if(beginMonth==endMonth) { // *** same month ***
            if(beginDayOfMonth!=endDayOfMonth) { // *** different day ***
               readableValue += 
                    " t/m " + days_lcase[endDayOfWeek]
                    + " " + endDayOfMonth;
            } else {
                singleDay = true;
            }
            readableValue += 
                " " + months_lcase[beginMonth];
        } else { // *** different month ***
            readableValue += 
                " " + months_lcase[beginMonth ] 
                + " t/m  " + days_lcase[endDayOfWeek]
                + " " + endDayOfMonth
                + " " + months_lcase[endMonth];
        }
        readableValue += 
            " " + beginYear;
      } else { // *** different year ***
        readableValue += 
            " " + months_lcase[beginMonth] 
            + " " + beginYear
            + " t/m  " + days_lcase[endDayOfWeek]
            + " " + endDayOfMonth
            + " " + months_lcase[endMonth]
            + " " + endYear;
      }
      if(singleDay) { // use seperator
          readableValue = days_lcase[beginDayOfWeek] + seperator + readableValue;
      } else {
          readableValue = days_lcase[beginDayOfWeek] + " " + readableValue;
      }
      return readableValue;
  } 
    
  public String getReadableTime() {
      String readableValue = "";
      Calendar cal = Calendar.getInstance();
      cal.setTime(beginDate);
      int beginHour = cal.get(Calendar.HOUR_OF_DAY);
      int beginMinute = cal.get(Calendar.MINUTE); 
      cal.setTime(endDate);
      int endHour = cal.get(Calendar.HOUR_OF_DAY);
      int endMinute = cal.get(Calendar.MINUTE); 
      
      readableValue += beginHour + ":";
      if(beginMinute<10) { readableValue += "0"; } 
      readableValue += beginMinute;
      if(beginHour!=endHour||beginMinute!=endMinute) {  // *** different times ***
          readableValue += 
            "-" + endHour
            + ":";
          if(endMinute<10) { readableValue += "0"; } 
          readableValue += endMinute;
      }
      readableValue += "u";
      return readableValue;
   } 
      
   public String getReadableStartTime() {
      String readableValue = "";
      Calendar cal = Calendar.getInstance();
      cal.setTime(beginDate);
      int beginHour = cal.get(Calendar.HOUR_OF_DAY);
      int beginMinute = cal.get(Calendar.MINUTE); 
      
      readableValue += beginHour + ":";
      if(beginMinute<10) { readableValue += "0"; } 
      readableValue += beginMinute;
      readableValue += "u";
      return readableValue;
  } 
  
  public String getReadableEndTime() {
      String readableValue = "";
      Calendar cal = Calendar.getInstance();
      cal.setTime(endDate);
      int beginHour = cal.get(Calendar.HOUR_OF_DAY);
      int beginMinute = cal.get(Calendar.MINUTE); 
      
      readableValue += beginHour + ":";
      if(beginMinute<10) { readableValue += "0"; } 
      readableValue += beginMinute;
      readableValue += "u";
      return readableValue;
  } 

  public String getReadableValue() {
      return  getReadableDate() + ", " + getReadableTime();
  }
  
  public boolean getMultiDay() {
      Calendar cal = Calendar.getInstance();
      cal.setTime(beginDate);
      int beginYear = cal.get(Calendar.YEAR);
      int beginDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
      cal.setTime(endDate);
      int endYear = cal.get(Calendar.YEAR);
      int endDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
      return !(beginYear==endYear&&beginDayOfYear==endDayOfYear);
  }

  public String toString() {
      return beginDate.toString() + " - " + endDate.toString() + " (" + nodeNumber + ")";
  }

  public int compareTo(Object obj) { 
    // compare on basis of seconds
    long xBegin = this.beginDate.getTime()/1000;
    long xEnd = this.endDate.getTime()/1000;
    String xNumber = this.nodeNumber;
    long yBegin = ((DoubleDateNode) obj).getBegin().getTime()/1000;
    long yEnd = ((DoubleDateNode) obj).getEnd().getTime()/1000;
    String yNumber = ((DoubleDateNode) obj).nodeNumber;

    int compareTo = 0;

    if(xBegin < yBegin || (xBegin==yBegin && xEnd < yEnd)) {
      /* instance lt received */
      compareTo = -1;
      
    } else if(xBegin > yBegin || (xBegin==yBegin && xEnd > yEnd)) {
      /* instance gt received */        
      compareTo = 1;
      
    } else {
      /* instance == received */
      compareTo = xNumber.compareTo(yNumber);
   
    }
    log.debug(this + " compared to " + (DoubleDateNode) obj + " is " + compareTo);
    return compareTo;  
  }
  
}
