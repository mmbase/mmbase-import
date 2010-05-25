<%@ include file="jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">

<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>


<!-- login part -->
<%@ include file="getposterid.jsp" %>
<!-- end login part -->

<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
<mm:compare value="remail" referid="action">
    <mm:import externid="wantedaccount" />
    <mm:node referid="forumid">
        <mm:import id="wforum"><mm:field name="name" /></mm:import>
            <mm:relatednodes type="posters" constraints="(account='$wantedaccount')" max="1">
        <mm:import id="wemail"><mm:field name="email" /></mm:import>
        <mm:import id="posterid" reset="true"><mm:field name="number" /></mm:import>
        <mm:import id="email"><mm:field name="email" /></mm:import>

        <mm:import id="waccount"><mm:field name="account" /></mm:import>

                <mm:import id="firstname"><mm:field name="firstname" /></mm:import>
                <mm:import id="lastname"><mm:field name="lastname" /></mm:import>
                <mm:import id="gender"><mm:field name="gender" /></mm:import>
                <mm:import id="location"><mm:field name="location" /></mm:import>
                <% String newPassword =
                Integer.toHexString(
                    (int)(Math.random() * 0xfffffff)); %>
        <mm:import id="wpassword"><%=newPassword %></mm:import>
                <mm:import id="newpassword"><mm:write referid="wpassword"/></mm:import>
               <mm:import id="newconfirmpassword"><mm:write referid="wpassword"/></mm:import>
                <!--  create the email node -->
                <mm:createnode id="mail1" type="email">
                        <mm:setfield name="from"><mm:function set="mmbob" name="getForumFromEmailAddress" referids="forumid" /></mm:setfield>
                        <mm:setfield name="to"><mm:write referid="wemail" /></mm:setfield>
                        <mm:setfield name="subject"><mm:write referid="mlg.Your_account_information_for_the_MMBob_forum"/></mm:setfield>
                        <mm:setfield name="body"> <mm:write referid="mlg.Your_account_information_for_the_MMBob_forum"/>: <mm:write referid="wforum" /> :


            <mm:write referid="mlg.login_name" />=<mm:write referid="waccount" />
            <mm:write referid="mlg.password" />=<mm:write referid="wpassword" />
            </mm:setfield>
                </mm:createnode>


                <!-- send the email node -->                    <mm:node referid="mail1">
                        <mm:field name="mail(oneshot)" />
                </mm:node>
        <mm:import id="mailed">true</mm:import>
                </mm:relatednodes>
    </mm:node>

        <%-- setting new password into poster --%>
    <mm:import id="feedback"><mm:function set="mmbob" name="editPoster" referids="forumid,posterid,firstname,lastname,email,gender,location,newpassword,newconfirmpassword"/></mm:import>






</mm:compare>
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

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="40%">
<mm:present referid="mailed">
<form action="<mm:url page="index.jsp" referids="forumid" />" method="post">
<tr><th align="left" ><p />
<mm:write referid="mlg.Login_mail_sent" />. <br />
<%--<mm:write referid="wpassword" /> - <mm:write referid="feedback"/>--%>
</th></tr>
<tr><td align="center">
<input type="submit" value="Terug naar het forum">
</form>
</td></tr>
</mm:present>
<mm:notpresent referid="mailed">
<form action="<mm:url page="remail.jsp" referids="forumid" />" method="post">
<tr><th colspan="3" align="left" >
<mm:present referid="action">
<p />
<center><mm:write referid="mlg.Login_name_not_found" /></center>
</mm:present>
<p />
<mm:write referid="mlg.Please_enter_your_login_name" /><p />
<mm:write referid="mlg.login_name"/> : <input name="wantedaccount" size="15">
</th></tr>
<tr><td align="center">
<input type="hidden" name="action" value="remail">
<input type="submit" value="<mm:write referid="mlg.Ok"/>, <mm:write referid="mlg.send"/>">
</form>
</td>
<td align="center">
<form action="<mm:url page="remail.jsp" referids="forumid" />" method="post">
<p />
<input type="submit" value="<mm:write referid="mlg.Cancel"/>">
</form>
</td>
</tr>
</mm:notpresent>

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

