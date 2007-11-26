package com.finalist.portlets.banner.search;

import java.util.Calendar;
import java.util.Date;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mmbase.storage.search.SortOrder;

import com.finalist.cmsc.resources.forms.SearchForm;

@SuppressWarnings("serial")
public class BannerForm extends SearchForm {

   private String name;
   private String pagepath;
   private String position;
   private int period;
   private boolean isRemote;
   private String allPositions;
   private int fromDay;
   private int fromMonth;
   private int fromYear;
   private int toDay;
   private int toMonth;
   private int toYear;


   public BannerForm() {
      Calendar now = Calendar.getInstance();
      // use current date for to date
      toDay = now.get(Calendar.DAY_OF_MONTH);
      // using one based months on the JSP
      toMonth = now.get(Calendar.MONTH) + 1;
      toYear = now.get(Calendar.YEAR);

      // one month before for from date
      now.add(Calendar.MONTH, -1);
      fromDay = now.get(Calendar.DAY_OF_MONTH);
      fromMonth = now.get(Calendar.MONTH) + 1;
      fromYear = now.get(Calendar.YEAR);
   }


   protected BannerForm(String contenttypes) {
      super(contenttypes);
   }


   public ActionErrors validate(ActionMapping actionMapping, javax.servlet.http.HttpServletRequest httpServletRequest) {
      // ensure valid direction
      if (getDirection() != SortOrder.ORDER_DESCENDING) {
         setDirection(SortOrder.ORDER_ASCENDING);
      }

      // set default order field
      if (StringUtil.isEmpty(getOrder())) {
         setOrder("name");
      }

      return super.validate(actionMapping, httpServletRequest);
   }


   public String getName() {
      return name;
   }


   public void setName(String name) {
      this.name = name;
   }


   public String getPagepath() {
      return pagepath;
   }


   public void setPagepath(String pagepath) {
      this.pagepath = pagepath;
   }


   public String getPosition() {
      return position;
   }


   public void setPosition(String position) {
      this.position = position;
   }


   public int getPeriod() {
      return period;
   }


   public void setPeriod(int period) {
      this.period = period;
   }


   public boolean isRemote() {
      return isRemote;
   }


   public void setRemote(boolean isRemote) {
      this.isRemote = isRemote;
   }


   public String getAllPositions() {
      return allPositions;
   }


   public void setAllPositions(String allPositions) {
      this.allPositions = allPositions;
   }


   public int getFromDay() {
      return fromDay;
   }


   public void setFromDay(int fromDay) {
      this.fromDay = fromDay;
   }


   public int getFromMonth() {
      return fromMonth;
   }


   public void setFromMonth(int fromMonth) {
      this.fromMonth = fromMonth;
   }


   public int getFromYear() {
      return fromYear;
   }


   public void setFromYear(int fromYear) {
      this.fromYear = fromYear;
   }


   public int getToDay() {
      return toDay;
   }


   public void setToDay(int toDay) {
      this.toDay = toDay;
   }


   public int getToMonth() {
      return toMonth;
   }


   public void setToMonth(int toMonth) {
      this.toMonth = toMonth;
   }


   public int getToYear() {
      return toYear;
   }


   public void setToYear(int toYear) {
      this.toYear = toYear;
   }


   public Date getFromDate() {
      return getDate(getFromYear(), getFromMonth(), getFromDay());
   }


   public Date getToDate() {
      return getDate(getToYear(), getToMonth(), getToDay());
   }


   private Date getDate(int year, int month, int day) {
      Calendar cal = Calendar.getInstance();
      // using 1 based months on the JSP
      cal.set(year, month - 1, day);
      return cal.getTime();
   }
}
