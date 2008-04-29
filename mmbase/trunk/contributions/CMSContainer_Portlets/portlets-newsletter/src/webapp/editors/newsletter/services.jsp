<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@page import="com.finalist.newsletter.services.NewsletterSubscriptionServices"%>
<%@page import="com.finalist.newsletter.services.NewsletterServiceFactory"%>
<%@page import="java.util.*"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="com.finalist.newsletter.services.CommunityModuleAdapter" %>
<%
	NewsletterSubscriptionServices service = NewsletterServiceFactory.getNewsletterSubscriptionServices();
	int userId = CommunityModuleAdapter.getCurrentUser().getId().intValue();
	int newsletterId = 0;
	int termId = 0;
	boolean hasSelect = false;
	String status = "INACTIVE";
	String format = "html";
	String action = null;
	
	if(null!=request.getParameter("action"))
	{
	action = request.getParameter("action");
	}
	if(null!=request.getParameter("newsletterId"))
	{
	newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
	}
	if(null!=request.getParameter("termId"))
	{
	termId = Integer.parseInt(request.getParameter("termId"));
	}
	if(null!=request.getParameter("select"))
	{
	hasSelect =  Boolean.parseBoolean(request.getParameter("select"));
	}
	if(null!=request.getParameter("format"))
	{
	 format = request.getParameter("format");
	}
	if(null!=request.getParameter("status"))
	{
	 status = request.getParameter("status");
	}

	if(service.noSubscriptionRecord(userId,newsletterId))
		{
			service.addNewRecord(userId,newsletterId);
		}
	
	
	//	modify status	
	if("modifyStatus".equals(action))
	{
		if("ACTIVE".equals(status))
		{
			if(hasSelect){
				service.modifyStauts(userId,newsletterId,"ACTIVE");
			}else{
				service.modifyStauts(userId,newsletterId,"INACTIVE");
			}		
		}
		if("PAUSED".equals(status))
		{
			if(hasSelect){ 
				service.modifyStauts(userId,newsletterId,"PAUSED");
			}else{
				service.modifyStauts(userId,newsletterId,"ACTIVE");
			}		
		}
	}
	
	
	//modify format
	if("modifyFormat".equals(action))
	{	
		service.modifyFormat(userId,newsletterId,format);
	}
	
	//select tag
	if("modifyTag".equals(action))
	{	
		if(hasSelect){
				service.selectTermInLetter(userId,newsletterId, termId);
		}
			else{
				service.unSelectTermInLetter(userId,newsletterId, termId);
		}
	}
		
%>