<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="forumid" />
<mm:import externid="postareaid" />
<mm:import externid="postthreadid" />
<mm:import externid="page">1</mm:import>

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

<!-- moderator check -->
<%-- Administrative / Moderative functions --%>
<mm:nodefunction set="mmbob" name="getPostAreaInfo" referids="forumid,postareaid,posterid,page">
   <mm:import id="ismoderator"><mm:field name="ismoderator" /></mm:import>
</mm:nodefunction>

<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<body>

<div class="header">
</div>
                                                                                              
<div class="bodypart" align="center">

<mm:compare referid="ismoderator" value="false">
  <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="45%">
    <tr><th><mm:write referid="mlg_Edit_postthread" /></th></tr>
    <tr><td><font color="red"><b><mm:write referid="mlg_Access_denied" /></font></b></td></tr>
  </table>
</mm:compare>

<mm:compare referid="ismoderator" value="true">

  <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="45%">
    <mm:node referid="postthreadid">
    <tr><th colspan="3"><mm:write referid="mlg_Edit_postthread" /></th></tr>
    <form action="<mm:url page="postarea.jsp" referids="forumid,postareaid,postthreadid" />" method="post">
	<tr><th width="200"><mm:write referid="mlg_Status" /></th><td colspan="2" align="middle">
		<select name="state">
		<mm:field name="state">
		<option value="normal" <mm:compare value="normal">selected</mm:compare>><mm:write referid="mlg_normal" />
		<option value="closed" <mm:compare value="closed">selected</mm:compare>><mm:write referid="mlg_closed" />
		<option value="pinned" <mm:compare value="pinned">selected</mm:compare>><mm:write referid="mlg_pinned" />
		</mm:field>
		</select>
	</td></th>
	<tr><th><mm:write referid="mlg_Mood"/></th><td align="middle" colspan="2">
		<select name="mood">
		<mm:field name="mood">
		<option value="normal" <mm:compare value="normal">selected</mm:compare>><mm:write referid="mlg_normal" />
		<option value="mad" <mm:compare value="mad">selected</mm:compare>><mm:write referid="mlg_mad"/>
		<option value="happy" <mm:compare value="happy">selected</mm:compare>><mm:write referid="mlg_happy"/>
		<option value="sad" <mm:compare value="sad">selected</mm:compare>><mm:write referid="mlg_sad"/>
		<option value="question" <mm:compare value="question">selected</mm:compare>><mm:write referid="mlg_question"/>
		<option value="warning" <mm:compare value="warning">selected</mm:compare>><mm:write referid="mlg_warning"/>
		<option value="joke" <mm:compare value="joke">selected</mm:compare>><mm:write referid="mlg_joke"/>
		<option value="idea" <mm:compare value="idea">selected</mm:compare>><mm:write referid="mlg_idea"/>
		<option value="suprised" <mm:compare value="suprised">selected</mm:compare>><mm:write referid="mlg_surprised"/>
		</mm:field>
		</select>
	<tr><th>Type</th><td align="middle" colspan="2">
		<select name="ttype">
		<mm:field name="ttype">
		<option value="normal" <mm:compare value="normal">selected</mm:compare>><mm:write referid="mlg_normal"/>
		<option value="note" <mm:compare value="note">selected</mm:compare>><mm:write referid="mlg_note"/>
		<option value="faq" <mm:compare value="faq">selected</mm:compare>><mm:write referid="mlg_faq"/>
		<option value="announcement" <mm:compare value="announcement">selected</mm:compare>><mm:write referid="mlg_announcement"/>
		</select>
		</mm:field>
		</td></th>

	<tr><th>&nbsp;</th><td>
	<input type="hidden" name="action" value="editpostthread">
	<center><input type="submit" value="<mm:write referid="mlg_Save"/>">
	</td>
	<td>
	</mm:node>
        </form>
  	<form action="<mm:url page="postarea.jsp">
	<mm:param name="forumid" value="$forumid" />
	<mm:param name="postareaid" value="$postareaid" />
	</mm:url>"
 	method="post">
	<p />
	<center>
	<input type="submit" value="<mm:write referid="mlg_Cancel"/>">
  	</form>
	</td>
	</tr>
  </table>
</mm:compare>

</div>

<div class="footer">
</div>
                                                                                              
</body>
</html>
</mm:locale>
</mm:content>
</mm:cloud>
