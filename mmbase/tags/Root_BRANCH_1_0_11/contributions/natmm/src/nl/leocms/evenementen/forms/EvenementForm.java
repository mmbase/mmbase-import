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
package nl.leocms.evenementen.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionError;
import java.util.*;

import java.lang.reflect.Array;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeList;
import org.mmbase.util.logging.*;
import nl.leocms.evenementen.Evenement;
import com.finalist.mmbase.util.CloudFactory;


/**
 * @author Henk Hangyi Date :Oct 11, 2004
 *
 * @struts:form name="EvenementForm"
 */
public class EvenementForm extends ActionForm {
   private static final Logger log = Logging.getLoggerInstance(EvenementForm.class);
   
   private String action;
   private EvenementButtons buttons = null;
   private String showPastDates;
   
   private String name;
   private String node;
   private int nextNewNodeNumber;
   private String beginYear;
   private String beginMonth;
   private String beginDay;
   private String beginHour;
   private String beginMinute;
   private String endYear;
   private String endMonth;
   private String endDay;
   private String endHour;
   private String endMinute;
   private String isSpareDate;
   private String isOnInternet;
   private String isCanceled;
   private TreeSet dates;
   private TreeSet oldDates;
   private String[] selectedDates; 
   private String selectedEvent;
   private TreeMap daysOfWeek;
   private String[] selectedDaysOfWeek;
   private String userId;   

   public void reset(ActionMapping mapping, HttpServletRequest request) { // reset is necessary for checkboxes in session
      this.isSpareDate = "";
      this.isOnInternet = "";
      this.isCanceled = "";
      this.selectedDaysOfWeek = new String[10];
      buttons = new EvenementButtons();
   }    

   public String getAction() { return action; }
   public void setAction(String action ) { this.action = action; }
   
   public EvenementButtons getButtons() { return buttons; }
   public void setButtons(EvenementButtons buttons) { this.buttons = buttons; }
   
   public String getShowPastDates() { return showPastDates; }
   public void setShowPastDates(String showPastDates) { this.showPastDates = showPastDates; }
   
   public String getName() { return name; }
   public void setName(String name) { this.name = name; }
   
   public String getNode() { return node; }
   public void setNode(String node) { this.node = node; }
   
   public int nextNewNodeNumber() { nextNewNodeNumber--; return (nextNewNodeNumber+1); }
   public void setNextNewNodeNumber(int nextNewNodeNumber) { this.nextNewNodeNumber = nextNewNodeNumber; }
 
   public String getBeginYear() { return beginYear; }
   public void setBeginYear(String beginYear) { this.beginYear = beginYear; }
   
   public String getBeginMonth() { return beginMonth; }
   public void setBeginMonth(String beginMonth) { this.beginMonth = beginMonth; }

   public String getBeginDay() { return beginDay ; }
   public void setBeginDay(String beginDay) { this.beginDay = beginDay; }   
 
   public String getBeginHour() { return beginHour; }
   public void setBeginHour(String beginHour) { this.beginHour = beginHour; }
   
   public String getBeginMinute() { return beginMinute; }
   public void setBeginMinute(String beginMinute ) { this.beginMinute = beginMinute; }
   
   public String getEndYear() { return endYear; }
   public void setEndYear(String endYear) { this.endYear = endYear; }

   public String getEndMonth() { return endMonth; }
   public void setEndMonth(String endMonth) { this.endMonth = endMonth; }
   
   public String getEndDay() { return endDay ; }
   public void setEndDay(String endDay) { this.endDay = endDay; }  
 
   public String getEndHour() { return endHour; }
   public void setEndHour(String endHour) { this.endHour = endHour; }
   
   public String getEndMinute() { return endMinute; }
   public void setEndMinute(String endMinute ) { this.endMinute = endMinute; }

   public String getIsSpareDate() { return isSpareDate; }
   public void setIsSpareDate(String isSpareDate ) { this.isSpareDate = isSpareDate; }
 
   public String getIsOnInternet() { return isOnInternet; }
   public void setIsOnInternet(String isOnInternet ) { this.isOnInternet = isOnInternet; }
   
   public String getIsCanceled() { return isCanceled; }
   public void setIsCanceled(String isCanceled ) { this.isCanceled = isCanceled; }
   
   public TreeSet getDates() { 
      TreeSet thisDates = null;
      if(dates!=null) {
         thisDates = (TreeSet) dates.clone(); // to avoid ConcurrentModificationException
      } else {
         thisDates = new TreeSet();
      }
      return thisDates;
   } 
   public void setDates(TreeSet dates ) { this.dates = (TreeSet) dates.clone(); }
   
   public TreeSet getOldDates() { return oldDates; }
   public void setOldDates(TreeSet oldDates ) { this.oldDates = (TreeSet) oldDates.clone(); }
   
   public String[] getSelectedDates() { return selectedDates; } 
   public void setSelectedDates(String[] selectedDates) { this.selectedDates = selectedDates; }
   
   public String getSelectedEvent() { return selectedEvent; }
   public void setSelectedEvent(String selectedEvent) { this.selectedEvent = selectedEvent; }
   
   public TreeMap getDaysOfWeek() { return daysOfWeek; }
   public void setDaysOfWeek(TreeMap daysOfWeek ) { this.daysOfWeek = (TreeMap) daysOfWeek.clone(); }
   
   public String[] getSelectedDaysOfWeek() { return selectedDaysOfWeek; } 
   public void setSelectedDaysOfWeek(String[] selectedDaysOfWeek) { this.selectedDaysOfWeek = selectedDaysOfWeek; }
   
   public String getUserId() { return userId; }
   public void setUserId(String userId ) { this.userId = userId; }

   public Date getBeginTime() {
      Date beginTime = new Date(0);
      try {
          Calendar cal = Calendar.getInstance();
          int beginyear = (new Integer(this.getBeginYear())).intValue();
          int beginmonth = (new Integer(this.getBeginMonth())).intValue()-1;
          int beginday = (new Integer(this.getBeginDay())).intValue();
          int beginhour = (new Integer(this.getBeginHour())).intValue();
          int beginminute = (new Integer(this.getBeginMinute())).intValue();
          cal.set(beginyear,beginmonth,beginday,beginhour,beginminute);
          beginTime = cal.getTime();
      } catch (Exception e) { }
      return beginTime;
   }
   
   public Date getEndTime() {
      int endyear = 0;
      try {
         endyear = (new Integer(this.getEndYear())).intValue();
      } catch (Exception e) { // *** set endYear to beginYear, if no valid endYear ***
         endyear = (new Integer(this.getBeginYear())).intValue();
      }
      int endmonth = 0;
      try {
         endmonth = (new Integer(this.getEndMonth())).intValue()-1;
      } catch (Exception e) { // *** set endMonth to beginMonth, if no valid endMonth ***
         endmonth = (new Integer(this.getBeginMonth())).intValue()-1;
      }
      int endday = 0;
      try {
         endday = (new Integer(this.getEndDay())).intValue();
      } catch (Exception e) { // *** set endDay to beginDay, if no valid endDay ***
         endday = (new Integer(this.getBeginDay())).intValue();
      }
      int endhour = 0;
      try {
         endhour = (new Integer(this.getEndHour())).intValue();
      } catch (Exception e) { // *** set endHour to beginHour, if no valid endHour ***
         endhour = (new Integer(this.getBeginHour())).intValue();
      }
      int endminute = 0;
      try {
         endminute = (new Integer(this.getEndMinute())).intValue();
      } catch (Exception e) { // *** set endMinute to beginMinute, if no valid endMinute ***
         endminute = (new Integer(this.getBeginMinute())).intValue();
      }   
      Calendar cal = Calendar.getInstance();
      cal.set(endyear,endmonth,endday,endhour,endminute);
      return cal.getTime();
   }
   
   public void createDatesFromPeriod(Evenement thisEvent) {
      Date beginTime = thisEvent.getBegin();
      Date endTime = thisEvent.getEnd();
      String nodeNumber = thisEvent.getNumber();
      Calendar cal = Calendar.getInstance();
      cal.setTime(endTime);
      int endHour = cal.get(Calendar.HOUR_OF_DAY);
      int endMinute = cal.get(Calendar.MINUTE); 
      cal.setTime(beginTime);
      int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
      String [] selectedDaysOfWeek = this.getSelectedDaysOfWeek();
      Evenement newEvent = thisEvent; // overwrite the current multi-day event
      for(int d=0; d< selectedDaysOfWeek.length; d++) {
         String thisSelection = selectedDaysOfWeek[d];
         if(thisSelection!=null) {
            for(int day=0; day<7; day++) {
               if(thisSelection.equals("" + nodeNumber + "," + day)) {
                  selectedDaysOfWeek[d] = ""; // *** reset this entry in the selectedDaysOfWeek array ***
                  cal.setTime(beginTime);
                  cal.set(Calendar.DAY_OF_WEEK,day+1);
                  if((day+1)<dayOfWeek) { // *** this would mean setting the time before beginTime, so add one week ***
                     cal.add(Calendar.WEEK_OF_YEAR,1);
                  }
                  Date nextTime = cal.getTime();
                  while(nextTime.getTime()<endTime.getTime()) {
                     if(newEvent==null) { newEvent = new Evenement(this.nextNewNodeNumber()); }
                     newEvent.setBegin(nextTime);
                     cal.set(Calendar.HOUR_OF_DAY,endHour);
                     cal.set(Calendar.MINUTE,endMinute);
                     newEvent.setEnd(cal.getTime());
                     newEvent.setIsSpareDate(thisEvent.getIsSpareDate());
                     newEvent.setIsOnInternet(thisEvent.getIsOnInternet());
                     newEvent.setIsCanceled(thisEvent.getIsCanceled());
                     this.dates.add(newEvent);
                     newEvent = null;
                     cal.setTime(nextTime); cal.add(Calendar.WEEK_OF_YEAR,1); nextTime = cal.getTime();   
                  }
               }
            }
         }
      }
      this.setSelectedDaysOfWeek(selectedDaysOfWeek);
   }
   
   public void update(Evenement thisEvent) {
      thisEvent.setBegin(this.getBeginTime());
      thisEvent.setEnd(this.getEndTime());
      thisEvent.setIsSpareDate(this.getIsSpareDate());
      thisEvent.setIsOnInternet(this.getIsOnInternet());
      thisEvent.setIsCanceled(this.getIsCanceled());
      this.dates.add(thisEvent);
   }
   
   public TreeMap getDatesMap(){
      TreeMap datesMap = new TreeMap();
      TreeSet dates = this.getDates();
      Iterator datesIterator = dates.iterator();
      while(datesIterator.hasNext()) {
         Evenement thisEvent = (Evenement) datesIterator.next();
         datesMap.put(thisEvent.getKey(),thisEvent);
      }
      return datesMap;
   }

   private void logDates() {
      Iterator datesIterator = this.getDates().iterator();
      while(datesIterator.hasNext()) {
         Evenement thisEvent = (Evenement) datesIterator.next();
         log.info(thisEvent);
      }
   }
   
   private Vector selectedDays(Node thisNode){
      String dayDescr = thisNode.getStringValue("dagomschrijving");
      Vector selectedDays = new Vector();
      if(dayDescr!=null&&!dayDescr.equals("")) {
         String nodeNumber = "" + thisNode.getNumber();
         if(dayDescr.indexOf("zo")>-1) { selectedDays.add(nodeNumber + "," + 0); }
         if(dayDescr.indexOf("ma")>-1) { selectedDays.add(nodeNumber + "," + 1); }
         if(dayDescr.indexOf("di")>-1) { selectedDays.add(nodeNumber + "," + 2); }
         if(dayDescr.indexOf("wo")>-1) { selectedDays.add(nodeNumber + "," + 3); }
         if(dayDescr.indexOf("do")>-1) { selectedDays.add(nodeNumber + "," + 4); }
         if(dayDescr.indexOf("vr")>-1) { selectedDays.add(nodeNumber + "," + 5); }
         if(dayDescr.indexOf("za")>-1) { selectedDays.add(nodeNumber + "," + 6); }  
      }
      return selectedDays;  
   }
   
   public String selectedDays(String nodeNumber){
      String selectedDays = "";
      String [] selectedDaysOfWeek = this.getSelectedDaysOfWeek();
      for(int d=0; d< selectedDaysOfWeek.length; d++) {
         String thisSelection = selectedDaysOfWeek[d];
         if(thisSelection!=null) {
            if(thisSelection.equals("" + nodeNumber + "," + 0)) selectedDays += ",zo";
            if(thisSelection.equals("" + nodeNumber + "," + 1)) selectedDays += ",ma";
            if(thisSelection.equals("" + nodeNumber + "," + 2)) selectedDays += ",di";
            if(thisSelection.equals("" + nodeNumber + "," + 3)) selectedDays += ",wo";
            if(thisSelection.equals("" + nodeNumber + "," + 4)) selectedDays += ",do";
            if(thisSelection.equals("" + nodeNumber + "," + 5)) selectedDays += ",vr";
            if(thisSelection.equals("" + nodeNumber + "," + 6)) selectedDays += ",za";
         }
      }
      if(!selectedDays.equals("")) selectedDays = selectedDays.substring(1);
      return selectedDays;  
   } 
  
   static Object arrayExpand(Object a) {
       Class cl = a.getClass();
       if (!cl.isArray()) return null;
       int length = Array.getLength(a);
       int newLength = length + (length / 2); // 50% more
       Class componentType = a.getClass().getComponentType();
       Object newArray = Array.newInstance(componentType, newLength);
       System.arraycopy(a, 0, newArray, 0, length);
       return newArray;
   }
   
   public void initDates(String nodeNumber, String selectedNumber) {
      this.initDates(nodeNumber);
      if(selectedNumber!=null&&!selectedNumber.equals("")) {
         Cloud cloud = CloudFactory.getCloud();
         Node node = cloud.getNode(selectedNumber);
         Evenement selectedEvent = new Evenement(node);
         Calendar cal = Calendar.getInstance();
         cal.setTime(selectedEvent.getBegin());
         this.setBeginYear(""+ cal.get(Calendar.YEAR));
         this.setBeginMonth(""+ (cal.get(Calendar.MONTH)+1));
         this.setBeginDay(""+ cal.get(Calendar.DAY_OF_MONTH));
         this.setBeginHour(""+ cal.get(Calendar.HOUR_OF_DAY));
         this.setBeginMinute(""+ cal.get(Calendar.MINUTE));
         cal.setTime(selectedEvent.getEnd());
         this.setEndYear(""+ cal.get(Calendar.YEAR));
         this.setEndMonth(""+ (cal.get(Calendar.MONTH)+1));
         this.setEndDay(""+ cal.get(Calendar.DAY_OF_MONTH));
         this.setEndHour(""+ cal.get(Calendar.HOUR_OF_DAY));
         this.setEndMinute(""+ cal.get(Calendar.MINUTE));
         this.setIsSpareDate(node.getStringValue("isspare"));
         this.setIsOnInternet(node.getStringValue("isoninternet"));
         this.setIsCanceled(node.getStringValue("iscanceled"));
      }
   }

   public void initDates(String nodeNumber) {
      TreeSet initDates = new TreeSet();
      String [] selectedDaysOfWeek = new String[10];
      
      if(nodeNumber!=null) {
         Cloud cloud = CloudFactory.getCloud();
         Node node = cloud.getNode(nodeNumber);
         this.setNode(nodeNumber);
         this.setName(node.getStringValue("titel")); 
         NodeList evenementList = node.getRelatedNodes("evenement","partrel","destination");
         evenementList.add(node);
         // *** selectedDays uses "eventnr,daynr" as key ***
         int dayCtr = 0;
         long now = (new Date()).getTime()/1000;
         for(int eventCtr=0; eventCtr<evenementList.size(); eventCtr++) {
            Node thisEventNode =  evenementList.getNode(eventCtr);
            if(thisEventNode.getLongValue("einddatum")<now) { this.setShowPastDates("false"); }
            Vector selectedDays = selectedDays(thisEventNode);
            for(int d=0; d<selectedDays.size();d++) {
                  if(dayCtr==selectedDaysOfWeek.length) {
                     selectedDaysOfWeek = (String []) arrayExpand(selectedDaysOfWeek);
                  }
                  selectedDaysOfWeek[dayCtr] = (String) selectedDays.get(d);
                  dayCtr++;
            }
            Evenement thisEvent = new Evenement(thisEventNode);
            initDates.add(thisEvent);
         }
      } else {
         this.setNode(null);
         this.setName("");
         this.setBeginYear(""); this.setBeginMonth(""); this.setBeginDay(""); this.setBeginHour(""); this.setBeginMinute("");
         this.setEndYear(""); this.setEndMonth(""); this.setEndDay(""); this.setEndHour(""); this.setEndMinute("");
         this.setIsSpareDate("false");
         this.setIsOnInternet("true");
         this.setIsCanceled("false");
      }
      this.setDates(initDates);
      this.setOldDates(initDates);
      this.setSelectedDaysOfWeek(selectedDaysOfWeek);
      this.setNextNewNodeNumber(-1);
 
   }
  
   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
      log.info("EvenementForm.validate()");
        
      ActionErrors errors = new ActionErrors();
      if(this.dates==null) { // *** browser lost session ***
         errors.add("eindtijd",new ActionError("evenementen.session.lostsession"));
      }
      if (this.getName()==null || this.getName().equals("")) {
         errors.add("naam",new ActionError("evenementen.naam.verplicht"));
      }
      if(this.getAction()!=null&&!this.getAction().equals("")) {
         
         if(this.getAction().equals("Voeg toe")||this.getAction().equals("Wijzig")) {  // *** Add / Change date ****
            boolean hoursAndMinutesOk = true;
            if (this.getBeginHour()!=null) {
               try {
                     int hours = Integer.parseInt(this.getBeginHour());
                     if(hours<0||hours>24) {
                        errors.add("begintijd",new ActionError("evenementen.beginuren.getalverplicht"));
                        hoursAndMinutesOk = false;
                     }
               } catch (Exception e) {
                     errors.add("begintijd",new ActionError("evenementen.beginuren.getalverplicht"));
                     hoursAndMinutesOk = false;
               }
            }
            if (this.getBeginMinute()!=null) {
               try {
                     int minutes = Integer.parseInt(this.getBeginMinute());
                     if(minutes<0||minutes>60) {
                        errors.add("begintijd",new ActionError("evenementen.beginminuten.getalverplicht"));
                        hoursAndMinutesOk = false;
                     }
               } catch (Exception e) {
                     errors.add("begintijd",new ActionError("evenementen.beginminuten.getalverplicht")); 
                     hoursAndMinutesOk = false;
               }
            } 
            if(hoursAndMinutesOk) {
               if(getBeginTime().getTime()==0) {
                  errors.add("begintijd",new ActionError("evenementen.begintijd.geldigedatum"));
               } else if(getEndTime().getTime()<getBeginTime().getTime()) {
                  errors.add("eindtijd",new ActionError("evenementen.einddatum.beginvooreind"));
               }
            }
            
         } else if(this.getAction().indexOf("selectie")>-1
                  ||this.getAction().equals("Periode -> data")) {        // *** Do something with selection ****
         
           if(this.getSelectedDates()==null||this.getSelectedDates().length==0) {
         
               errors.add("lijst",new ActionError("evenementen.lijst.selectienoodzakelijk"));
         
           } else if(this.getAction().equals("Periode -> data")) {        // *** Change period in seperate dates ****
         
               boolean multiday = false;
               String [] selectedDates = this.getSelectedDates();
               TreeMap datesMap = this.getDatesMap();
               for(int i=0; i< selectedDates.length; i++) {
                  if(datesMap.containsKey(selectedDates[i])) {
                     multiday = multiday || ((Evenement) datesMap.get(selectedDates[i])).getMultiDay();
                  }
               }
               
               if(!multiday) errors.add("lijst",new ActionError("evenementen.lijst.multidaynoodzakelijk")); 
            
           } else if(this.getAction().equals("Verwijder selectie")) {   // *** Delete selection ****
               
               boolean hasChild = false;
               boolean hasBooking = false;
               String [] selectedDates = this.getSelectedDates();
               TreeMap datesMap = this.getDatesMap();
               for(int i=0; i< selectedDates.length; i++) {
                  if(datesMap.containsKey(selectedDates[i])) {
                     hasChild = hasChild || ((Evenement) datesMap.get(selectedDates[i])).hasChild();
                     hasBooking = hasBooking || ((Evenement) datesMap.get(selectedDates[i])).hasBooking();
                  }
               }
               if(hasBooking) {
                   errors.add("lijst",new ActionError("evenementen.lijst.hasbooking")); 
               }
               if(hasChild) {
                   errors.add("lijst",new ActionError("evenementen.lijst.haschild")); 
               }
           }
            
         } else if(this.getAction().indexOf("Opslaan")>-1) {            // *** Save ***
            
            if(this.getDates()==null||this.getDates().size()==0) {
               errors.add("opslaan",new ActionError("evenementen.opslaan.minimaaleenevent"));
            }
         }
      }
      
      return errors;
   }
}

