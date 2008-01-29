<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>

<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">	
<html:html xhtml="true">
<cmscedit:head title="messageoftheday.title" />
<body>
  <mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
  	<div class="tabs">
  		<div class="tab_active">
  			<div class="body">
  				<div>
  					<a href="#"><fmt:message key="community.title" /></a>
  				</div>
    		</div>
  		</div>
    </div>

    <div class="editor" style="height:500px">
    	<div class="body">
    		
    		<p>Welcome to the community Module!</p>
    		
		  </div>
	  </div>
  </mm:cloud>
</body>
</html:html>
</mm:content>
