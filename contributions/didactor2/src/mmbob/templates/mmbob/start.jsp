<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:content type="text/html">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
   <%

      String bundleMMBob = null;

   %>

   <mm:write referid="lang_code" jspvar="sLangCode" vartype="String" write="false">

      <%

         bundleMMBob = "nl.didactor.component.mmbob.MMBobMessageBundle_" + sLangCode;

      %>

   </mm:write>

<fmt:bundle basename="<%= bundleMMBob %>">
<%@ include file="thememanager/loadvars.jsp" %>
<%@ include file="settings.jsp" %>
<HTML>
<HEAD>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title><fmt:message key="MMBaseForums"/></title>
</head>
<mm:import externid="forumid" jspvar="forumid">unknown</mm:import>
<mm:compare referid="forumid" value="unknown">
    <table align="center" cellpadding="0" cellspacing="0" class="list" style="margin-top : 40px;" width="75%">
        <tr><th><fmt:message key="SystemError"/></th></tr>
        <tr><td height="40"><b><fmt:message key="Error"/> : </b> <fmt:message key="NoForums1"/> <a href="forums.jsp">forums.jsp</a> <fmt:message key="NoForums2"/>.</td></tr>
    </table>
</mm:compare>

<mm:compare referid="forumid" value="unknown" inverse="true">
<%@ include file="getposterid.jsp" %>

<mm:locale language="$lang"> 

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<center>
<mm:include page="path.jsp?type=index" />
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
          <mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
          <mm:import id="adminmode"><mm:field name="isadministrator" /></mm:import>
           <tr>
                <mm:compare referid="posterid" value="-1">
                <th width="100"><a href="newposter.jsp?forumid=<mm:write referid="forumid" />"><img src="images/guest.gif" border="0"></a></th>
                <td align="left">
                <form action="login.jsp?forumid=<mm:write referid="forumid" />" method="post">
                <mm:present referid="loginfailed">
                    <br />
                    <center><h4><fmt:message key="WrongLogin"/></h4></center>
                    <center> <a href="<mm:url page="remail.jsp" referids="forumid" />"><fmt:message key="ForgotPasswordLink"/></a></center>

                    <p />
                </mm:present>
                <mm:notpresent referid="loginfailed">
                    <mm:field name="description" />
                    <p />
                    <b><fmt:message key="Login"/></b><p />
                </mm:notpresent>
                <fmt:message key="account"/> : <input size="12" name="account">
                <fmt:message key="password"/> : <input size="12" type="password" name="password">
                <input type="submit" value="<fmt:message key="Login"/>" />
                </form><p />
                </mm:compare>
               <mm:compare referid="posterid" value="-1" inverse="true">
                <th width="100">
                <a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids"></mm:treefile>" target=_top>
                <%--hh <mm:field name="active_account" />--%><fmt:message key="ClickForProfile"/><br />
                <mm:field name="active_avatar"><mm:compare value="-1" inverse="true">
                        <mm:node number="$_" notfound="skip">
                         <img src="<mm:image template="s(80x80)" />" width="80" border="0">
                        </mm:node>
                </mm:compare></mm:field></a>
                <%-- hh a href="logout.jsp?forumid=<mm:write referid="forumid" />">Logout</a> --%>
                </th>
                <td align="left" valign="top">
                    <mm:compare referid="image_logo" value="" inverse="true">
                    <br />
                    <center><img src="<mm:write referid="image_logo" />" width="98%"></center>
                    <br />
                    </mm:compare>
                    <mm:compare referid="image_logo" value="">
                    <h4><fmt:message key="WelcomeToForum1"/> <mm:field name="active_firstname" /> <mm:field name="active_lastname" /> <%-- hh (<mm:field name="active_account" />) --%> <br /> <fmt:message key="WelcomeToForum2"/> <mm:field name="name" /> <fmt:message key="WelcomeToForum3"/>.</h4><p />

                    </mm:compare>
                    <fmt:message key="LastTimeLogin"/> : <mm:field name="active_lastseen"><mm:compare value="" inverse="true"><mm:field name="active_lastseen"><mm:time format="<%= timeFormat %>" /></mm:field></mm:compare></mm:field><br />
                    <fmt:message key="MemberSince"/> : <mm:field name="active_firstlogin"><mm:compare value="" inverse="true"><mm:field name="active_firstlogin"><mm:time format="<%= timeFormat %>" /></mm:field></mm:compare></mm:field><br />
                    <%-- hh <fmt:message key="numberofmessages" />: <mm:field name="active_postcount" />
                    Level : <mm:field name="active_level" /> 
                    <b>Je hebt 0 nieuwe en 0 ongelezen <a href="<mm:url page="privatemessages.jsp" referids="forumid" />">prive berichten</a></b>
                    <h4>Op dit moment: <mm:field name="postersonline" /> bezoekers online.</h4> --%>
                </mm:compare>
                </td>
                <th width="250" align="left" valign="top">
                <b><fmt:message key="InfoAboutForum" /></b><br/>
                <b><fmt:message key="numberofareas" /></b> : <mm:field name="postareacount" /><br />
                <b><fmt:message key="numberoftopics" /></b> : <mm:field name="postthreadcount" /><br />
                <b><fmt:message key="numberofmessages" /></b> : <mm:field name="postcount" /><br />
                <b><fmt:message key="numberofviews" /></b> : <mm:field name="viewcount" /><br />
                <b><fmt:message key="numberofmembers" /></b> : <mm:field name="posterstotal" /><br />
                <b><fmt:message key="numberofnew" /></b> : <mm:field name="postersnew" /><br />
                <b><fmt:message key="numberonline" /></b> : <mm:field name="postersonline" /><p /><br />
                <b><fmt:message key="lastmessage" /></b> : <mm:field name="lastposttime"><mm:compare value="-1" inverse="true"><mm:field name="lastposttime"><mm:time format="<%= timeFormat %>" /></mm:field> <fmt:message key="VisitorsOnline1" /> <mm:field name="lastposter" /> '<mm:field name="lastsubject" />'</mm:compare><mm:compare value="-1"><fmt:message key="VisitorsOnline2" /></mm:compare></mm:field>
                </th>
            </tr> 
          </mm:nodefunction>
</table>


<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%">
  <tr>
   <td align="right">
    <a href="<mm:url page="moderatorteam.jsp" referids="forumid" />"><fmt:message key="ModeratorTeam"/></a> | <a href="<mm:url page="onlineposters.jsp" referids="forumid" />"><fmt:message key="MembersOnline"/> | <a href="<mm:url page="allposters.jsp" referids="forumid" />"><fmt:message key="AllMembers"/></a></a>
   </td>
  </tr>
</table>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="95%">
   <tr><th><fmt:message key="areas" /></th>
        <th><fmt:message key="numberoftopics" /></th>
        <th><fmt:message key="numberofmessages" /></th>
        <th><fmt:message key="numberofviews" /></th>
        <th><fmt:message key="lastmessage" /></th></tr>
          <mm:nodelistfunction set="mmbob" name="getPostAreas" referids="forumid,posterid">
            <tr><td align="left"><a href="postarea.jsp?forumid=<mm:write referid="forumid" />&postareaid=<mm:field name="id" />"><mm:field name="name" /></a>
            <p/>
            <mm:field name="description" />
            <%-- hh <p />
            Moderators : <mm:field name="moderators" />
            <p /> --%>
             </td>
                <td><mm:field name="postthreadcount" /></td>
                <td><mm:field name="postcount" /></td>
                <td><mm:field name="viewcount" /></td>
                <td align="left" valign="top"><mm:field name="lastposttime"><mm:compare value="-1" inverse="true"><mm:field name="lastposttime"><mm:time format="<%= timeFormat %>" /></mm:field> <fmt:message key="VisitorsOnline1"/> <mm:field name="lastposter" /><p /><mm:field name="lastsubject" /></mm:compare><mm:compare value="-1"><fmt:message key="VisitorsOnline2"/></mm:compare></mm:field></td>
            </tr>
          </mm:nodelistfunction>
</table>
  <mm:compare referid="adminmode" value="true">
    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
    <tr><th align="lef"><fmt:message key="AdminFunctions"/></th></tr>
    <td>
    <p />
    <a href="<mm:url page="changeforum.jsp">
                  <mm:param name="forumid" value="$forumid" />
                 </mm:url>"><fmt:message key="ChangeForum"/></a><br />
    <a href="<mm:url page="newpostarea.jsp">
                  <mm:param name="forumid" value="$forumid" />
                 </mm:url>"><fmt:message key="AddNewArea"/></a>
    <p />
    </td>
    </tr>
    </table>
  </mm:compare>

</mm:locale>

</mm:compare>
</center>
</html>
</fmt:bundle>
</mm:cloud>
</mm:content>
