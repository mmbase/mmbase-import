<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="forumid" />
<mm:import externid="postareaid">-1</mm:import>
<mm:import externid="page">1</mm:import>
<mm:import externid="pagesize">100</mm:import>
<mm:import externid="pathtype">bookmarked</mm:import>
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

<mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
  <mm:import id="pagesize" reset="true"><mm:field name="postingsperpage" /></mm:import>
</mm:nodefunction>

<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title><mm:compare referid="forumid" value="unknown" inverse="true"><mm:node referid="forumid"><mm:field name="name"/></mm:node> / Bookmarks</mm:compare></title> 
</head>
<body>

<div class="header">
    <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=headerpath%>"/>
</div>
                                                                                                       
<div class="bodypart">

<mm:include page="path.jsp?type=$pathtype" />
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="90%" >

  <tr>
    <th>&nbsp;</th>
    <th>&nbsp;</th>
    <th><mm:write referid="mlg.topic"/></th>
    <th><mm:write referid="mlg.area_name"/></th>
    <th><mm:write referid="mlg.author"/></th>
    <th><mm:write referid="mlg.replies"/></th>
    <th><mm:write referid="mlg.views"/></th>
    <th><mm:write referid="mlg.last_posting"/></th>
  </tr>
	<mm:nodelistfunction set="mmbob" name="getBookmarkedThreads" referids="forumid,postareaid,posterid,page,pagesize">
	<tr>

    		<td><mm:field name="state"><mm:write referid="image_state_$_" /></mm:field></td>
    		<td><mm:field name="mood"><mm:write referid="image_mood_$_" /></mm:field></td>
		<td><a href="<mm:url page="thread.jsp" referids="forumid">
			 <mm:param name="postareaid"><mm:field name="postareaid" /></mm:param>
			 <mm:param name="postthreadid"><mm:field name="postthreadid" /></mm:param>
			 </mm:url>"><mm:field name="lastsubject" /></a> <mm:field name="navline" /> </td>
		<td><mm:field name="postareaname" /></td>
		<td><mm:field name="creator" /></td>
		<td><mm:field name="replycount" /></td>
		<td><mm:field name="viewcount" /></td>

    		<td align="left">
      		<mm:field name="lastposttime"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field> 
     		 <mm:write referid="mlg.by"/>
    		  <mm:field name="lastposternumber">
       		 <mm:compare value="-1" inverse="true">
          		<a href="profile.jsp?forumid=<mm:write referid="forumid" />&posterid=<mm:field name="lastposternumber" />"><mm:field name="lastposter" /></a>
       		 </mm:compare>
      		  <mm:compare value="-1" ><mm:field name="lastposter" /></mm:compare>
     		 </mm:field>
		<a href="thread.jsp?forumid=<mm:write referid="forumid" />&postareaid=<mm:field name="postareaid" />&postthreadid=<mm:field name="postthreadid" />&page=<mm:field name="pagecount" />#reply">></a>
		 </td>
	</tr>
	</mm:nodelistfunction>
</table>
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
