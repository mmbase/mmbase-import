package com.finalist.newsletter.forms;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.MessageResourcesFactory;

import com.finalist.newsletter.util.DateUtil;


public class NewsLetterLogSearchForm extends org.apache.struts.action.ActionForm {


   private String newsletters;
   /*private String startDate_year;
    private String startDate_month;
    private String startDate_day;
    private String endDate_year;
    private String endDate_month;
    private String endDate_day;*/
   private String startDate;
   private String endDate;
   private boolean flag = false;

   /*public void joinDateStart(){

       startDate=startDate_year+"-"+startDate_month+"-"+startDate_day;
    }
    public void joinDateEnd(){

       endDate= endDate_year+"-"+endDate_month+"-"+endDate_day;
    }*/
   public boolean isFlag() {
      return flag;
   }

   public void setFlag(boolean flag) {
      this.flag = flag;
   }


   public String getNewsletters() {
      return newsletters;
   }

   public void setNewsletters(String newsletters) {
      this.newsletters = newsletters;
   }

//	@Override
//	public ActionErrors validate(ActionMapping mapping,
//			HttpServletRequest request) {
//		ActionErrors errors = new ActionErrors();
//		MessageResourcesFactory factory = org.apache.struts.util.MessageResourcesFactory.createFactory();
//		MessageResources res = factory.createResources("cmsc-newsletter");
//		
//		
//		if(startDate!=null&&endDate!=null){
//			try {
//				Date dateStart = DateUtil.parser(startDate);
//				Date dateEnd = DateUtil.parser(endDate);
//				if(dateStart.after(dateEnd)){
//					errors.add("error1", new ActionMessage("error1"));
//					
//
//					
//					System.out.println("res=="+res);
//					String value = res.getMessage("error1");
//					System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiii-->"+value);
//					System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiii-->"+new ActionMessage(res.getMessage("error1")));
//					System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiii"+new ActionMessage("error1"));
//				}
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		
//		}
//		else if(startDate==null&&endDate==null&&newsletters==null){
//			errors.add("error2", new ActionMessage("error2"));
//		}
//		
//		System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiii==="+errors.size());
//		System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiii==="+errors.get("error1").toString());
//		ActionErrors e = new ActionErrors();
//      e.add("myProperty",new ActionMessage("edit_defaults.title"));
//
//	     
//		return e;
//	}


   public String getStartDate() {
      return startDate;
   }

   public void setStartDate(String startDate) {
      this.startDate = startDate;
   }

   public String getEndDate() {
      return endDate;
   }

   public void setEndDate(String endDate) {
      this.endDate = endDate;
   }


}
