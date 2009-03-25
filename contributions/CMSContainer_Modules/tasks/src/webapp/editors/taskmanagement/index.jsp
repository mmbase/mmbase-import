<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<mm:cloud jspvar="cloud" loginpage="../login.jsp">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">

<mm:import externid="tasknumber" from="parameters" />

<mm:notpresent referid="tasknumber">
   <mm:url page="/editors/taskmanagement/tasklist.jsp" id="taskurl" write="false" />
</mm:notpresent>

<mm:present referid="tasknumber">
   <mm:node number="${tasknumber}" jspvar="node">
       <mm:import id="returnurl">/editors/taskmanagement/tasklist.jsp</mm:import>
       <mm:url page="../WizardInitAction.do" id="taskurl" write="false" >
          <mm:param name="objectnumber" value="${tasknumber}"/>
          <mm:param name="contenttype" value="task"/>
          <mm:param name="returnurl" value="${returnurl}" />
       </mm:url>
   </mm:node>
</mm:present>

<html:html xhtml="true">
	<head><title><fmt:message key="tasks.title" /></title>
	</head>
	<frameset cols="293,*" framespacing="0" frameborder="0">
		<frame src="tasksmenu.jsp" name="leftpane" frameborder="0" scrolling="no">
		<frame src="<mm:url referid="taskurl"/>" name="rightpane" frameborder="0">
	</frameset>
</html:html>
</mm:cloud>
</mm:content>