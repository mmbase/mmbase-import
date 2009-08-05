<%@page language="java" contentType="text/html; charset=utf-8" 
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
%><%@taglib uri="http://finalist.com/cmsc" prefix="cmsc" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@ taglib tagdir="/WEB-INF/tags/" prefix="cmscf" 
%><mm:content type="text/html" encoding="UTF-8">
<cmsc:location var="cur" sitevar="site" />
<cmsc:screen>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="content-type" content="application/xhtml+xml; charset=UTF-8" />
	<title><cmsc:title/></title>
	<cmsc:headercontent dublin="false"/>

	<cmsc:insert-stylesheet var="stylesheet"/>
	<c:forEach var="style" items="${stylesheet}">
		<link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/${style.resource}'/>" media="${style.media}"/>
	</c:forEach>

	<link rel="shortcut icon" href="<cmsc:staticurl page='/gfx/CMSC_16x16.ico'/>"/>

	<script src="<cmsc:staticurl page='/js/start.js' />" type="text/javascript"></script>
	<script src="<cmsc:staticurl page='/js/nav.js' />" type="text/javascript"></script>
	
   <cmsc:google-analytics />
   	
	<cmscf:editresources />
</head>
<body>
    <div id="houder">
    	<div id="header">
	        <cmsc:insert-portlet layoutid="header" />
	    </div>
        <%-- De main1 div --%>
	    <div id="contentPart">
        <div id="main1">             
            <cmsc:insert-portlet layoutid="links1" />
            <cmsc:insert-portlet layoutid="links2" />
            <cmsc:insert-portlet layoutid="links3" />
            <cmsc:insert-portlet layoutid="links4" />
            <cmsc:insert-portlet layoutid="links5" /> 
            <cmsc:insert-portlet layoutid="links6" />          
        </div><!-- /#main1 -->       
		<!-- Begin van de tweede extra-div -->
        <div id="extra2">            
            <cmsc:insert-portlet layoutid="rechts1" />
            <cmsc:insert-portlet layoutid="rechts2" />
            <cmsc:insert-portlet layoutid="rechts3" />
            <cmsc:insert-portlet layoutid="rechts4" />
            <cmsc:insert-portlet layoutid="rechts5" />
            <cmsc:insert-portlet layoutid="rechts6" />
        </div><!-- /#extra2-->             
        </div>
        <div id="footer">
        	<cmsc:insert-portlet layoutid="footer" />
        </div>        
    </div><!-- /#houder -->
</body>
</html>
</cmsc:screen>
</mm:content>