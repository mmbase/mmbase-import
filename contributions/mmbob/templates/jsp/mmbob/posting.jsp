<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="page" />
<mm:import externid="pagesize" />
<mm:import externid="postareaid" />
<mm:import externid="postthreadid" />

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

<mm:node referid="postthreadid">
  <mm:field name="state">
	<mm:import id="tstate"><mm:field name="state" /></mm:import>
  	<mm:compare value="closed"><mm:import id="noedit">true</mm:import></mm:compare>
  </mm:field>
</mm:node>
<mm:nodefunction set="mmbob" name="getPostThreadNavigation" referids="forumid,postareaid,postthreadid,posterid,page,pagesize">
<mm:field name="pagecount" id="pagecount" write="false" />
</mm:nodefunction>

<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
   <script language="JavaScript1.1" type="text/javascript" src="js/smilies.js"></script>
</head>
<body>

<div class="header">
    <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=headerpath%>"/>
</div>
                                                                                              
<div class="bodypart">
<mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
<mm:import id="logoutmodetype"><mm:field name="logoutmodetype" /></mm:import>
<mm:import id="navigationmethod"><mm:field name="navigationmethod" /></mm:import>
<mm:import id="active_nick"><mm:field name="active_nick" /></mm:import>
<mm:import id="active_firstname"><mm:field name="active_firstname" /></mm:import>
<mm:import id="active_lastname"><mm:field name="active_lastname" /></mm:import>
<mm:include page="path.jsp?type=postthread" referids="logoutmodetype,posterid,forumid,active_nick" />
</mm:nodefunction>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <mm:import externid="postingid" />
  
  <mm:notpresent referid="noedit">
  <tr><th colspan="3"><mm:write referid="mlg.Compose_message" /></th></tr>
  <mm:node referid="postingid">
  <form action="<mm:url page="thread.jsp">
	<mm:param name="forumid" value="$forumid" />
	<mm:param name="postareaid" value="$postareaid" />
	<mm:param name="postthreadid" value="$postthreadid" />
	<mm:param name="page" value="$pagecount" />
	</mm:url>#reply" method="post" name="posting">
	<tr><th width="20%"><mm:write referid="mlg.Name" /></th><td colspan="2">
		<mm:compare referid="posterid" value="-1" inverse="true">
		<mm:write referid="active_nick" /> (<mm:write referid="active_firstname" /> <mm:write referid="active_lastname" />)
		<input name="poster" type="hidden" value="<mm:write referid="active_nick" />" >
		</mm:compare>
		<mm:compare referid="posterid" value="-1">
		<input name="poster" size="32" value="gast" >
		</mm:compare>
	</td></tr>
	<tr><th><mm:write referid="mlg.Topic" /></th><td colspan="2"><input name="subject" style="width: 100%" value="Re: <mm:field name="subject" />" ></td></th>
	<tr><th valign="top"><mm:write referid="mlg.Message" /><center><table width="99"><tr><th><%@ include file="includes/smilies.jsp" %></th></tr></table></center> </th><td colspan="2"><textarea name="body" rows="20" style="width: 100%"><quote poster="<mm:field name="c_poster"/>"><mm:formatter xslt="xslt/posting2textarea.xslt"><mm:field name="body" /></mm:formatter></quote></textarea></td></tr>
	<tr><th>&nbsp;</th><td>
	<input type="hidden" name="action" value="postreply">
	<center><input type="submit" value="<mm:write referid="mlg.Save" />"></center>
  	</form>
	</td>
	<td>
	</mm:node>
  	<form action="<mm:url page="thread.jsp">
	<mm:param name="forumid" value="$forumid" />
	<mm:param name="postareaid" value="$postareaid" />
	<mm:param name="postthreadid" value="$postthreadid" />
	<mm:param name="page" value="$pagecount" />
	</mm:url>"
 	method="post">
	<p />
	<center>
	<input type="submit" value="<mm:write referid="mlg.Cancel" />">
        </center>
  	</form>
	</td>
	</tr>
	</mm:notpresent>
        <mm:present referid="noedit">
  	<tr><th colspan="3"><mm:write referid="mlg.Topic_closed_by_moderator" /></th></tr>
	<td>
  	<form action="<mm:url page="thread.jsp">
	<mm:param name="forumid" value="$forumid" />
	<mm:param name="postareaid" value="$postareaid" />
	<mm:param name="postthreadid" value="$postthreadid" />
	</mm:url>"
 	method="post">
	<p />
	<center>
	<input type="submit" value="<mm:write referid="mlg.Ok"/>">
        </center>
  	</form>
	</td>
	</tr>
	</mm:present>
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


