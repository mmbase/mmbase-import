<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<%@ include file="thememanager/loadvars.jsp" %>
<%@ include file="settings.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBase Forum</title>
</head>
<mm:import externid="adminmode">false</mm:import>
<mm:import externid="forumid" />
<mm:import externid="postareaid" />
<mm:import externid="page">1</mm:import>

<!-- login part -->
<%@ include file="getposterid.jsp" %>
<!-- end login part -->


<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<mm:locale language="$lang"> 

<center>
<mm:include page="path.jsp?type=postarea" />
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
          <mm:nodefunction set="mmbob" name="getPostAreaInfo" referids="forumid,postareaid,posterid,page">
            <mm:import id="navline"><mm:field name="navline" /></mm:import>
            <mm:import id="pagecount"><mm:field name="pagecount" /></mm:import>
            <tr><th colspan="2" align="left">
                    <mm:compare referid="image_logo" value="" inverse="true">
                    <center><img src="<mm:write referid="image_logo" />" width="100%" ></center>
                    <br />
                    </mm:compare>
            <b><mm:write referid="area" /></b> : <mm:field name="name" /><br />
            <b><mm:write referid="numberoftopics" /></b> : <mm:field name="postthreadcount" /><br />
            <b><mm:write referid="numberofmessages" /></b> : <mm:field name="postcount" /><br />
            <b><mm:write referid="numberofviews" /></b> : <mm:field name="viewcount" /><br />
            <b><mm:write referid="lastmessage" /></b> : <mm:field name="lastposttime"><mm:compare value="-1" inverse="true"><mm:field name="lastposttime"><mm:time format="<%= timeFormat %>" /></mm:field> <b>door</b> <mm:field name="lastposter" /> <b> : '</b><mm:field name="lastsubject" /><b>'</b></mm:compare><mm:compare value="-1">nog geen bericten</mm:compare></mm:field><br />
            <mm:import id="isadministrator"><mm:field name="isadministrator" /></mm:import>
          </mm:nodefunction>
    <br />
    <%-- hh
    <b>Moderators</b> :
          <mm:nodelistfunction set="mmbob" name="getModerators" referids="forumid,postareaid">
            <mm:field name="account" /> (<mm:field name="firstname" /> <mm:field name="lastname" />)<br />
          </mm:nodelistfunction> --%>
    </td>
    </tr>
</table>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
   <tr><%-- hh <th width="15">&nbsp;</th><th width="15">&nbsp;</th> --%>
        <th><mm:write referid="topic" /></th><th>Gestart door</th><th><mm:write referid="numberofmessages" /></th><th><mm:write referid="numberofviews" /></th><th><mm:write referid="lastmessage" /></th><mm:compare referid="isadministrator" value="true"><th>admin</th></mm:compare></tr>
      <mm:nodelistfunction set="mmbob" name="getPostThreads" referids="forumid,postareaid,posterid,page">
            <tr>
            <%-- hh <td><mm:field name="state"><mm:write referid="image_state_$_" /></mm:field></td><td><mm:field name="mood"><mm:write referid="image_mood_$_" /></mm:field></td> --%>
            <td align="left"><a href="thread.jsp?forumid=<mm:write referid="forumid" />&postareaid=<mm:write referid="postareaid" />&postthreadid=<mm:field name="id" />">
                  <mm:field name="name"><mm:isnotempty><mm:write /></mm:isnotempty><mm:isempty>bericht zonder onderwerp</mm:isempty></mm:field>
               </a> <mm:field name="navline" /></td><td align="left"><mm:field name="creator" /></td><td align="left"><mm:field name="replycount" /></td><td align="left"><mm:field name="viewcount" /></td><td align="left"><mm:field name="lastposttime"><mm:time format="<%= timeFormat %>" /></mm:field> door <mm:field name="lastposter" /></td><mm:compare referid="isadministrator" value="true">
            <td><a href="<mm:url page="removepostthread.jsp" referids="forumid,postareaid"><mm:param name="postthreadid"><mm:field name="id" /></mm:param></mm:url>"><img src="<mm:write referid="image_mdelete" />"  border="0" /></a>
            <%-- hh / <a href="<mm:url page="editpostthread.jsp" referids="forumid,postareaid"><mm:param name="postthreadid"><mm:field name="id" /></mm:param></mm:url>">E</a> --%>
            </td></mm:compare>
            </tr>
      </mm:nodelistfunction>
</table>
<mm:compare referid="pagecount" value="1" inverse="true">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 5px; margin-right : 30px;" align="right">
    <tr>
    <td>
    Pagina's : <mm:write referid="navline" />
    </td></tr>
</table>
</mm:compare>
<table cellpadding="0" cellspacing="0" style="margin-top : 5px; margin-left : 25px" align="left">
    <tr><td><a href="<mm:url page="newpost.jsp"><mm:param name="forumid" value="$forumid" /><mm:param name="postareaid" value="$postareaid" /></mm:url>"><img src="<mm:write referid="image_newmsg" />" border="0" /></a> 
    </td></tr>
</table>
<%-- hh 
<br />
<br />
<br /> 
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 5px; margin-left : 30px" align="left">
    <tr><td align="left">
    <br />
    <mm:write referid="image_state_normal" /> Open onderwerp<p />
    <mm:write referid="image_state_normalnew" /> Open onderwerp met ongelezen reacties<p />
    <mm:write referid="image_state_hot" /> Open populair onderwerp<p />
    <mm:write referid="image_state_hotnew" /> Open populair onderwerp met ongelezen reacties&nbsp;<p />
    <mm:write referid="image_state_pinned" /> Vastgezet onderwerp<p />
    <mm:write referid="image_state_closed" /> Gesloten onderwerp<p />
    <mm:write referid="image_state_normalme" />Onderwerp waaraan u hebt bijgedragen<p />
    </td></tr>
</table>
<br /><br />
<br /><br />
<br /><br />
--%>
<br /><br /><br />
<br /><br /><br />
<br /><br /><br />
<br /><br /><br />
<mm:compare referid="isadministrator" value="true">
        <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;margin-left : 20px;" width="95%" align="left">
        <tr><th align="lef">Administratie Functies</th></tr>
        <td>
        <p />
                <a href="<mm:url page="changepostarea.jsp" referids="forumid,postareaid" />">gebied aanpassen</a><br />

                <a href="<mm:url page="removepostarea.jsp" referids="forumid,postareaid" />">gebied verwijder</a><br />

                <a href="<mm:url page="newmoderator.jsp">
                <mm:param name="forumid" value="$forumid" />
                <mm:param name="postareaid" value="$postareaid" />
                </mm:url>">moderator toevoegen</a><br />
                <a href="<mm:url page="removemoderator.jsp">
                <mm:param name="forumid" value="$forumid" />
                <mm:param name="postareaid" value="$postareaid" />
                </mm:url>">moderator verwijderen</a><br />
    </td></tr>
</table>
</mm:compare>

</mm:locale>
</mm:cloud>
</center>
</html>
