<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@ include file="thememanager/loadvars.jsp" %>
<%@ include file="settings.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title><di:translate key="mmbob.mmbaseforum" /></title>
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
            <b><di:translate key="mmbob.area" /></b> : <mm:field name="name" /><br />
            <b><di:translate key="mmbob.numberoftopics" /></b> : <mm:field name="postthreadcount" /><br />
            <b><di:translate key="mmbob.numberofmessages" /></b> : <mm:field name="postcount" /><br />
            <b><di:translate key="mmbob.numberofviews" /></b> : <mm:field name="viewcount" /><br />
            <b><di:translate key="mmbob.lastmessage" /></b> : <mm:field name="lastposttime"><mm:compare value="-1" inverse="true"><mm:field name="lastposttime"><mm:time format="<%= timeFormat %>" /></mm:field> <b><di:translate key="mmbob.visitorsonline1" /></b> <mm:field name="lastposter" /> <b> : '</b><mm:field name="lastsubject" /><b>'</b></mm:compare><mm:compare value="-1"><di:translate key="mmbob.visitorsonline2" /></mm:compare></mm:field><br />
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
        <th><di:translate key="mmbob.topic" /></th><th><di:translate key="mmbob.startedby" /></th><th><di:translate key="mmbob.numberofmessages" /></th><th><di:translate key="mmbob.numberofviews" /></th><th><di:translate key="mmbob.lastmessage" /></th><mm:compare referid="isadministrator" value="true"><th><di:translate key="mmbob.admin" /></th></mm:compare></tr>
      <mm:nodelistfunction set="mmbob" name="getPostThreads" referids="forumid,postareaid,posterid,page">
            <tr>
            <%-- hh <td><mm:field name="state"><mm:write referid="image_state_$_" /></mm:field></td><td><mm:field name="mood"><mm:write referid="image_mood_$_" /></mm:field></td> --%>
            <td align="left"><a href="thread.jsp?forumid=<mm:write referid="forumid" />&postareaid=<mm:write referid="postareaid" />&postthreadid=<mm:field name="id" />"><mm:field name="name" /></a> <mm:field name="navline" /></td><td align="left"><mm:field name="creator" /></td><td align="left"><mm:field name="replycount" /></td><td align="left"><mm:field name="viewcount" /></td><td align="left"><mm:field name="lastposttime"><mm:time format="<%= timeFormat %>" /></mm:field> door <mm:field name="lastposter" /></td><mm:compare referid="isadministrator" value="true">
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
    <di:translate key="mmbob.pages" /> : <mm:write referid="navline" />
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
        <tr><th align="lef"><di:translate key="mmbob.adminfunctions" /></th></tr>
        <td>
        <p />
                <a href="<mm:url page="changepostarea.jsp" referids="forumid,postareaid" />"><di:translate key="mmbob.changearea" /></a><br />

                <a href="<mm:url page="removepostarea.jsp" referids="forumid,postareaid" />"><di:translate key="mmbob.removearea" /></a><br />

                <a href="<mm:url page="newmoderator.jsp">
                <mm:param name="forumid" value="$forumid" />
                <mm:param name="postareaid" value="$postareaid" />
                </mm:url>"><di:translate key="mmbob.addmoderator" /></a><br />
                <a href="<mm:url page="removemoderator.jsp">
                <mm:param name="forumid" value="$forumid" />
                <mm:param name="postareaid" value="$postareaid" />
                </mm:url>"><di:translate key="mmbob.removemoderator" /></a><br />
    </td></tr>
</table>
</mm:compare>

</center>
</mm:locale>
</html>
</mm:cloud>
