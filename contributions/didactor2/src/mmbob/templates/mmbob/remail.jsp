<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
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
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</HEAD>
<mm:import externid="forumid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
<mm:compare value="remail" referid="action">
    <mm:import externid="wantedaccount" />
    <mm:node referid="forumid">
        <mm:import id="wforum"><mm:field name="name" /></mm:import>
            <mm:relatednodes type="posters" constraints="(account='$wantedaccount')" max="1">
        <mm:import id="wemail"><mm:field name="email" /></mm:import>
        <mm:import id="waccount"><mm:field name="account" /></mm:import>
        <mm:import id="wpassword"><mm:field name="password" /></mm:import>
                <!--  create the email node -->
                <mm:createnode id="mail1" type="email">
                        <mm:setfield name="from"><mm:write referid="webmastermail" /></mm:setfield>
                        <mm:setfield name="to"><mm:write referid="wemail" /></mm:setfield>
                        <mm:setfield name="subject"><fmt:message key="YourAccInfo" /></mm:setfield>
                        <mm:setfield name="body"> <fmt:message key="YourAccInfo1" /> <mm:write referid="wforum" />  <fmt:message key="YourAccInfo2" /> :


            <fmt:message key="account" />=<mm:write referid="waccount" />
            <fmt:message key="wachtwoord" />=<mm:write referid="wpassword" />
            </mm:setfield>
                </mm:createnode>


                <!-- send the email node -->                    <mm:node referid="mail1">
                        <mm:field name="mail(oneshot)" />
                </mm:node>
        <mm:import id="mailed">true</mm:import>
                </mm:relatednodes>
    </mm:node>
</mm:compare>
</mm:present>
<!-- end action check -->
<center>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="40%">
    <mm:present referid="mailed">
    <form action="<mm:url page="start.jsp" referids="forumid" />" method="post">
    <tr><th align="left" ><p />
    <fmt:message key="EmailWithInfoHBSend" /> : <mm:write referid="wemail" />, <br />
    <fmt:message key="WithInfoYouCanLogon" />.<p />
    </th></tr>
    <tr><td>
    <center>
    <input type="submit" value="<fmt:message key="BacktoForum" />">
    </form>
    </td></tr>
    </mm:present>
    <mm:notpresent referid="mailed">
    <form action="<mm:url page="remail.jsp" referids="forumid" />" method="post">
    <tr><th colspan="3" align="left" >
    <mm:present referid="action">
    <p />
    <center>    <fmt:message key="AccountNotFound" /> </center>
    </mm:present>
    <p />
    <fmt:message key="EnterLogin" /><p />
    <fmt:message key="LoginName" /> : <input name="wantedaccount" size="15">
    </th></tr>
  <tr><td>
    <input type="hidden" name="action" value="remail">
    <center>
    <input type="submit" value="<fmt:message key="commit" />">
    </form>
    </td>
    <td>
    <form action="<mm:url page="remail.jsp" referids="forumid" />" method="post">
    <p />
    <center>
    <input type="submit" value="<fmt:message key="cancel" />">
    </form>
    </td>
    </tr>
    </mm:notpresent>

</table>
</html>
</fmt:bundle>
</mm:cloud>

