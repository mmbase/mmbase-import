<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@page import="com.finalist.newsletter.services.NewsletterSubscriptionServices"%>
<%@page import="com.finalist.newsletter.services.NewsletterServiceFactory"%>
good!
<%
	//System.out.println(request.getParameter("newsletterId"));
	//System.out.println(request.getParameter("tagId"));
	//System.out.println(request.getParameter("select"));
	//System.out.println(request.getParameter("format"));
	System.out.println("^^^^^^^^^^^^^^"+request.getParameter("status"));
	System.out.println("action="+request.getParameter("action"));
	NewsletterSubscriptionServices service = NewsletterServiceFactory.getNewsletterSubscriptionServices();
	int userId = 12345;
	int newsletterId = 0;
	int tagId = 0;
	boolean hasSelect = false;
	String status = "unSubscription";
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
	if(null!=request.getParameter("tagId"))
	{
	tagId = Integer.parseInt(request.getParameter("tagId"));
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
	//add newrecord
	if(service.noSubscriptionRecord(userId,newsletterId))
		{
			System.out.println("add");
			service.addNewRecord(userId,newsletterId);
		}
	
	
	//	modify status	
	if("modifyStatus".equals(action))
	{
		System.out.println("modifyStatus="+status);
		if("subscription".equals(status))
		{
			if(hasSelect){
				service.modifyStauts(userId,newsletterId,"subscription");
			}else{
				service.modifyStauts(userId,newsletterId,"unSubscription");
			}		
		}
		if("pause".equals(status))
		{
			if(hasSelect){
				service.modifyStauts(userId,newsletterId,"pause");
			}else{
				service.modifyStauts(userId,newsletterId,"subscription");
			}		
		}
	}
	
	
	//modify format
	if("modifyFormat".equals(action))
	{	
		System.out.println("modifyFormat");
		service.modifyFormat(userId,newsletterId,format);
	}
	
	//select tag
	if("modifyTag".equals(action))
	{	
		System.out.println("modifyTag");
		if(hasSelect){
				System.out.println("select");
				service.selectTagInLetter(userId,newsletterId,tagId);	
		}
			else{
			System.out.println("unselect");
				service.unSelectTagInLetter(userId,newsletterId,tagId);	
		}
	}
		
	
%>