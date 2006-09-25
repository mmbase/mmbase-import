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
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
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
                        <mm:setfield name="subject"><di:translate key="mmbob.youraccinfo" /></mm:setfield>
                        <mm:setfield name="body"> <di:translate key="mmbob.youraccinfo1" /> <mm:write referid="wforum" />  <di:translate key="mmbob.youraccinfo2" /> :


            <di:translate key="mmbob.account" />=<mm:write referid="waccount" />
            <di:translate key="mmbob.password" />=<mm:write referid="wpassword" />
            </mm:setfield>
                </mm:createnode>


                <!-- send the email node (Didactor way) -->
                <mm:node referid="mail1">
                   <mm:setfield name="type">1</mm:setfield>
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
    <di:translate key="mmbob.emailwithinfohbsend" /> : <mm:write referid="wemail" />, <br />
    <di:translate key="mmbob.withinfoyoucanlogon" />.<p />
    </th></tr>
    <tr><td>
    <center>
    <input type="submit" value="<di:translate key="mmbob.backtoforum" />">
    </form>
    </td></tr>
    </mm:present>
    <mm:notpresent referid="mailed">
    <form action="<mm:url page="remail.jsp" referids="forumid" />" method="post">
    <tr><th colspan="3" align="left" >
    <mm:present referid="action">
    <p />
    <center>    <di:translate key="mmbob.accountnotfound" /> </center>
    </mm:present>
    <p />
    <di:translate key="mmbob.enterlogin" /><p />
    <di:translate key="mmbob.loginname" /> : <input name="wantedaccount" size="15">
    </th></tr>
  <tr><td>
    <input type="hidden" name="action" value="remail">
    <center>
    <input type="submit" value="<di:translate key="mmbob.commit" />">
    </form>
    </td>
    <td>
    <form action="<mm:url page="remail.jsp" referids="forumid" />" method="post">
    <p />
    <center>
    <input type="submit" value="<di:translate key="mmbob.cancel" />">
    </form>
    </td>
    </tr>
    </mm:notpresent>

</table>
</html>
</mm:cloud>

