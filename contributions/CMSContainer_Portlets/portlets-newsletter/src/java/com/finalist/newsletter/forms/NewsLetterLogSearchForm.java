package com.finalist.newsletter.forms;

import java.text.DateFormat;
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

public class NewsLetterLogSearchForm extends
		org.apache.struts.action.ActionForm {

	private String newsletters;

	private String startDate;

	private String endDate;

	private String detailOrSum;

	public String getDetailOrSum (){

		return detailOrSum;
	}

	public void setDetailOrSum (String detailorsum){

		this.detailOrSum = detailorsum;
	}

	public String getNewsletters (){

		return newsletters;
	}

	public void setNewsletters (String newsletters){

		this.newsletters = newsletters;
	}

	@Override
	public ActionErrors validate (ActionMapping mapping,
			HttpServletRequest request){

		ActionErrors errors = new ActionErrors();

		if (startDate == "" && endDate != "") {
			this.startDate = "2007-12-30";

		}
		else if (startDate != "" && endDate == "") {
			Long time = System.currentTimeMillis();
			Date date = new Date(time);
			this.endDate = DateFormat.getDateInstance().format(date);

		}
		return errors;
	}

	public String getStartDate (){

		return startDate;
	}

	public void setStartDate (String startDate){

		this.startDate = startDate;
	}

	public String getEndDate (){

		return endDate;
	}

	public void setEndDate (String endDate){

		this.endDate = endDate;
	}

}
