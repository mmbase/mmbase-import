<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<%@ include file="thememanager/loadvars.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<mm:import externid="adminmode">false</mm:import>
<mm:import externid="forumid" />
<mm:import externid="pathtype">poster_newposter</mm:import>
<mm:import externid="postareaid" />
<mm:import externid="feedback">none</mm:import>


<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
<mm:compare value="createposter" referid="action">
        <mm:import id="account" externid="newaccount" />
        <mm:import id="password" externid="newpassword" />
        <mm:import id="firstname" externid="newfirstname" />
        <mm:import id="lastname" externid="newlastname" />
        <mm:import id="email" externid="newemail" />
        <mm:import id="location" externid="newlocation" />
        <mm:import id="gender" externid="newgender" />
        <mm:import id="feedback" reset="true"><mm:function set="mmbob" name="createPoster" referids="forumid,account,password,firstname,lastname,email,gender,location" /></mm:import>   
</mm:compare>
</mm:present>
<!-- end action check -->

<center>
<mm:include page="path.jsp?type=$pathtype" />
<mm:compare referid="feedback" value="none">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="50%">
    <form action="<mm:url page="newposter.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        </mm:url>" method="post">
            <tr><th width="150" >Account</th><td>
                <input name="newaccount" value="" style="width: 100%" />
            </td></tr>
            <tr><th width="150" >Wachtwoord</th><td>
                <input name="newpassword" value="" style="width: 100%" />
            </td></tr>
            <tr><th>Voornaam</th><td>
                <input name="newfirstname" value="" style="width: 100%" />
                </td></tr>
            <tr><th>Achternaam</th><td>
                <input name="newlastname" value="" style="width: 100%" />
                </td></tr>
            <tr><th>Email</th><td>
                <input name="newemail" value="" style="width: 100%" />
                </td></tr>
            <tr><th>Lokatie</th><td>
                <input name="newlocation" value="" style="width: 100%" />
                </td></tr>
            <tr><th>Geslacht</th><td>
                <select name="newgender">
                <option value="male">Man
                <option value="female">Vrouw
                </select>
            </td></tr>
    <tr><th colspan="2">
        <input type="hidden" name="action" value="createposter">
        <center><input type="submit" value="Aanmaken">
    </form>
    </th></tr>
</table>
</mm:compare>

<mm:compare referid="feedback" value="inuse">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="50%">
    <tr><th colspan="2">** account al in gebruik, kies een andere **</th></tr>
    <form action="<mm:url page="newposter.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        </mm:url>" method="post">
            <tr><th width="150" >Account</th><td>
                <input name="newaccount" value="" style="width: 100%" />
            </td></tr>
            <tr><th width="150" >Wachtwoord</th><td>
                <input name="newpassword" value="" style="width: 100%" />
            </td></tr>
            <tr><th>Voornaam</th><td>
                <input name="newfirstname" value="" style="width: 100%" />
                </td></tr>
            <tr><th>Achternaam</th><td>
                <input name="newlastname" value="" style="width: 100%" />
                </td></tr>
            <tr><th>Email</th><td>
                <input name="newemail" value="" style="width: 100%" />
                </td></tr>
            <tr><th>Lokatie</th><td>
                <input name="newlocation" value="" style="width: 100%" />
                </td></tr>
            <tr><th>Geslacht</th><td>
                <select name="newgender">
                <option value="male">Man
                <option value="female">Vrouw
                </select>
            </td></tr>
    <tr><th colspan="2">
        <input type="hidden" name="action" value="createposter">
        <center><input type="submit" value="Aanmaken">
    </form>
    </th></tr>
</table>
</mm:compare>

<mm:compare referid="feedback" value="ok">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="60%">
            <tr><th >Account aangemaakt</th></tr>
            <tr><td>Uw account is aangemaakt, u kan nu <a href="<mm:url page="index.jsp" referids="forumid" />">inloggen</a></td><tr>
</table>
</mm:compare>

</mm:cloud>
</center>
</html>
