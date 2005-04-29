<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="adminmode">false</mm:import>
<mm:import externid="forumid" />
<mm:import externid="searchkey" />
<mm:import externid="pathtype">moderatorteam</mm:import>
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
   <title>MMBob</title>
</head>
<body>

<div class="header">
    <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=headerpath%>"/>
</div>
                                                                                                       
<div class="bodypart">

<mm:include page="path.jsp?type=$pathtype" />
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="50%">
	<form action="<mm:url page="search.jsp" referids="forumid" />" method="post">
	<tr>
		<th>Search key</th>
		<td>
		<input name="searchkey" size="20" value="<mm:write referid="searchkey" />">
		</td>
	</td>
	</form>
</table>
<mm:present referid="searchkey">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="90%">
	<tr><th><mm:write referid="mlg.Area" /></th><th><mm:write referid="mlg.Topic" /></th><th>Poster</th></tr>
	<mm:listcontainer path="forums,postareas,postthreads,postings" fields="forums.number,postareas.number,postthreads.number,postthreads.subject,postings.c_poster,postareas.name">
	  <mm:constraint field="postings.body" operator="LIKE" value="%$searchkey%" />
  	   <mm:list max="10">
	    <tr>
 	      <td>
		<mm:field name="postareas.name" />
	      </td>
	      <td>
    		<a href="<mm:url page="thread.jsp">
		<mm:param name="forumid"><mm:field name="forums.number" /></mm:param>
		<mm:param name="postareaid"><mm:field name="postareas.number" /></mm:param>
		<mm:param name="postthreadid"><mm:field name="postthreads.number" /></mm:param>
		<mm:param name="postingid"><mm:field name="postings.number" /></mm:param>
		</mm:url>#p<mm:field name="postings.number" />"><mm:field name="postthreads.subject" /></a>
	      </td>
	      <td>
		<mm:field name="postings.c_poster" /> 
	      </td>
  	   </mm:list>
	</mm:listcontainer>
</table>
</mm:present>


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
