<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="forumid" />
<mm:import externid="rulesid" />
<mm:import externid="pathtype">rules</mm:import>
<mm:import externid="posterid" id="profileid" />

<!-- login part -->
<%@ include file="getposterid.jsp" %>
<!-- end login part -->

<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title><mm:compare referid="forumid" value="unknown" inverse="true"><mm:node referid="forumid"><mm:field name="name"/></mm:node></mm:compare></title>
</head>
<body>

<div class="header">
    <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=headerpath%>"/>
</div>
                                                                                                       
<div class="bodypart">

<mm:include page="path.jsp?type=$pathtype" />
<mm:node referid="rulesid">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="80%">
	<tr><th><mm:field name="title" /></th></tr>
	<tr><td><br /><br /><mm:field name="body" escape="p" /><br /><br /></td></tr>
</table>

</mm:node>
</div>                                                                                                        
<div class="footer">
    <mm:import id="footerpath" jspvar="footerpath"><mm:function set="mmbob" name="getForumFooterPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=footerpath%>"/>
</div>
                                                                                                       
</body>
</html>                                                                                                        

</mm:locale>
</mm:content>
</mm:cloud>
