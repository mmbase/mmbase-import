<%@ include file="jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">

<mm:import externid="adminmode">false</mm:import>
<mm:import externid="forumid" />
<mm:import externid="pathtype">poster_newposter</mm:import>
<mm:import externid="postareaid" />
<mm:import externid="feedback">none</mm:import>

<%-- login part --%>
<%@ include file="getposterid.jsp" %>
<%@ include file="thememanager/loadvars.jsp" %>
<%-- end login part --%>

<mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
    <mm:import id="hasnick"><mm:field name="hasnick" /></mm:import>
</mm:nodefunction>
<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
<mm:compare value="createposter" referid="action">
        <mm:import reset="true" id="account" externid="newaccount" />
        <mm:import reset="true" id="password" externid="newpassword" />
        <mm:import reset="true" id="confirmpassword" externid="newconfirmpassword" />
        <mm:import id="firstname" externid="newfirstname" />
        <mm:import id="lastname" externid="newlastname" />
        <mm:import id="email" externid="newemail" />
        <mm:import id="location" externid="newlocation" />
        <mm:import id="gender" externid="newgender" />
    <mm:compare referid="hasnick" value="false">
        <mm:import id="feedback" reset="true"><mm:function set="mmbob" name="createPoster" referids="forumid,account,password,confirmpassword,firstname,lastname,email,gender,location" /></mm:import>
    </mm:compare>
    <mm:compare referid="hasnick" value="true">
    <mm:import id="nick" externid="newnick" />
        <mm:import id="feedback" reset="true"><mm:function set="mmbob" name="createPosterNick" referids="forumid,account,password,confirmpassword,nick,firstname,lastname,email,gender,location" /></mm:import>
    </mm:compare>
</mm:compare>
</mm:present>
<!-- end action check -->

<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

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

<mm:import externid="rulesaccepted">no</mm:import>

<mm:compare referid="rulesaccepted" value="no">
<mm:node referid="forumid">
    <mm:relatednodes type="forumrules">
        <mm:import id="rulesid"><mm:field name="number" /></mm:import>
    </mm:relatednodes>
</mm:node>
</mm:compare>

<mm:include page="path.jsp?type=$pathtype" />

<mm:present referid="rulesid">
<mm:node referid="rulesid">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="80%">
        <tr><th colspan="2"><mm:field name="title" /></th></tr>        <tr><td colspan="2"><br /><br /><mm:field name="body" escape="p" /><br /><br /></td></tr>
    <tr>
    <form action="<mm:url page="newposter.jsp" referids="forumid" />" method="post">
    <td align="middle" width="50%"><center><input type="submit" value="<mm:write referid="mlg.I_ACCEPT_THESE_RULES"/>" /><input type="hidden" name="rulesaccepted" value="yes" /></center></td>
    </form>
    <form action="<mm:url page="index.jsp" referids="forumid" />" method="post">
    <td><center><input type="submit" value="<mm:write referid="mlg.I_REFUSE_THESE_RULES"/>" /></center></td>
    </form>
    </tr>
</table>

</mm:node>
</mm:present>
<mm:notpresent referid="rulesid">
<mm:compare referid="feedback" value="none">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="50%">
    <form action="<mm:url page="newposter.jsp" referids="forumid,rulesaccepted">
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        </mm:url>" method="post">
        <mm:compare referid="entree" value="null" inverse="false">
            <tr><th width="150" ><mm:write referid="mlg.Account"/></th><td>
                <input name="newaccount" value="" style="width: 100%" />
            </td></tr>
            <mm:compare referid="hasnick" value="true">
            <tr><th width="150" >Nick</th><td>
                <input name="newnick" value="" style="width: 100%" />
            </td></tr>
            </mm:compare>
            <tr><th width="150" ><mm:write referid="mlg.Password"/></th><td>
                <input name="newpassword" style="width: 100%" type="password"/>
            </td></tr>
            <tr><th width="150" ><mm:write referid="mlg.ConfirmPassword"/></th><td>
                <input name="newconfirmpassword" style="width: 100%" type="password"/>
            </td></tr>
            <tr><th><mm:write referid="mlg.Firstname"/></th><td>
                <input name="newfirstname" value="" style="width: 100%" />
                </td></tr>
            <tr><th><mm:write referid="mlg.Lastname"/></th><td>
                <input name="newlastname" value="" style="width: 100%" />
                </td></tr>
            <tr><th><mm:write referid="mlg.Email"/></th><td>
                <input name="newemail" value="" style="width: 100%" />
                </td></tr>
            <tr><th><mm:write referid="mlg.Location"/></th><td>
                <input name="newlocation" value="" style="width: 100%" />
                </td></tr>
            <tr><th><mm:write referid="mlg.Gender"/></th><td>
                <select name="newgender">
                <option value="unknown">Unknown
                <option value="male"><mm:write referid="mlg.Male"/>
                <option value="female"><mm:write referid="mlg.Female"/>
                </select>
            </td></tr>
        </mm:compare>
    <mm:compare referid="entree" value="null" inverse="true">
            <tr><th width="150" ><mm:write referid="mlg.Account"/></th><td>
                    <input type="hidden" name="newaccount" value="<%= request.getHeader("sm_user") %>">
                <%= request.getHeader("sm_user") %>
                    <input type="hidden" name="newpassword" value="<%= request.getHeader("aad_nummer") %>">
                    <input type="hidden" name="newconfirmpassword" value="<%= request.getHeader("aad_nummer") %>">
            </td></tr>
            <mm:compare referid="hasnick" value="true">
            <tr><th width="150" >Nick</th><td>
                <input name="newnick" value="" style="width: 100%" />
            </td></tr>
            </mm:compare>
            <tr><th><mm:write referid="mlg.Firstname"/></th><td>
                    <input type="hidden" name="newfirstname" value="<%= request.getHeader("aad_voornaam") %>">
                <%= request.getHeader("aad_voornaam") %>
                </td></tr>
            <tr><th><mm:write referid="mlg.Lastname"/></th><td>
                <mm:import id="tan"><%= request.getHeader("aad_achternaam") %></mm:import>
                <mm:compare referid="tan" value="null" inverse="true">
                    <input type="hidden" name="newlastname" value="<%= request.getHeader("aad_achternaam") %>">
                <%= request.getHeader("aad_achternaam") %>
                </td></tr>
                </mm:compare>
                <mm:compare referid="tan" value="null">
                    <input type="hidden" name="newlastname" value="   ">
                missing
                </td></tr>
                </mm:compare>
            <tr><th><mm:write referid="mlg.Email"/></th><td>
                <input name="newemail" value="" style="width: 100%" value="<%= request.getHeader("aad_emailadres") %>" />
                </td></tr>
            <tr><th><mm:write referid="mlg.Location"/></th><td>
                <input name="newlocation" value="" style="width: 100%" />
                </td></tr>
            <tr><th><mm:write referid="mlg.Gender"/></th><td>
                <select name="newgender">
                <option value="male"><mm:write referid="mlg.Male"/>
                <option value="female"><mm:write referid="mlg.Female"/>
                </select>
            </td></tr>
    </mm:compare>
    <tr><th colspan="2">
        <input type="hidden" name="action" value="createposter">
        <center><input type="submit" value="<mm:write referid="mlg.Save"/>"></center>
    </form>
    </th></tr>
</table>
</mm:compare>
</mm:notpresent>

<mm:compare referid="feedback" value="none" inverse="true">
<mm:compare referid="feedback" value="ok" inverse="true">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="50%">
    <tr><th colspan="2">
        <font color="red"> ***
        <mm:compare referid="feedback" value="inuse"><mm:write referid="mlg.Account_allready_in_use"/></mm:compare>
        <mm:compare referid="feedback" value="nickinuse"><mm:write referid="mlg.Nick_allready_in_use"/></mm:compare>
        <mm:compare referid="feedback" value="passwordnotequal"><mm:write referid="mlg.Password_notequal"/></mm:compare>
        <mm:compare referid="feedback" value="firstnameerror">Firstname invalid</mm:compare>
        <mm:compare referid="feedback" value="lastnameerror">Surname invalid</mm:compare>
        <mm:compare referid="feedback" value="emailerror">Email invalid</mm:compare>
        ***
        </font>
    </th></tr>
    <form action="<mm:url page="newposter.jsp" referids="forumid,rulesaccepted">
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        </mm:url>" method="post">
        <mm:compare referid="entree" value="null">
            <tr><th width="150" ><mm:write referid="mlg.Account"/></th><td>
                <mm:import externid="newaccount" />
                <input name="newaccount" value="<mm:write referid="newaccount" />" style="width: 100%" />
            </td></tr>
            <mm:compare referid="hasnick" value="true">
            <tr><th width="150" >Nick</th><td>
                <mm:import externid="newnick" />
                <input name="newnick" value="<mm:write referid="newnick"/>" style="width: 100%" />
            </td></tr>
            </mm:compare>
            <tr><th width="150" ><mm:write referid="mlg.Password"/></th><td>
                <mm:import externid="newpassword" />
                <input name="newpassword" value="<mm:write referid="newpassword" />" style="width: 100%" type="password"/>
            </td></tr>
            <tr><th width="150" ><mm:write referid="mlg.ConfirmPassword"/></th><td>
                <mm:import externid="newconfirmpassword" />
                <input name="newconfirmpassword" value="<mm:write referid="newconfirmpassword" />" style="width: 100%" type="password"/>
            </td></tr>
            <tr><th><mm:write referid="mlg.Firstname"/></th><td>
                <mm:import externid="newfirstname" />
                <input name="newfirstname" value="<mm:write referid="newfirstname" />" style="width: 100%" />
                </td></tr>
            <tr><th><mm:write referid="mlg.Lastname"/></th><td>
                <mm:import externid="newlastname" />
                <input name="newlastname" value="<mm:write referid="newlastname" />" style="width: 100%" />
                </td></tr>
            <tr><th><mm:write referid="mlg.Email"/></th><td>
                <mm:import externid="newemail" />
                <input name="newemail" value="<mm:write referid="newemail" />" style="width: 100%" />
                </td></tr>
            <tr><th><mm:write referid="mlg.Location"/></th><td>
                <mm:import externid="newlocation" />
                <input name="newlocation" value="<mm:write referid="newlocation" />" style="width: 100%" />
                </td></tr>
            <tr><th><mm:write referid="mlg.Gender"/></th><td>
                <select name="newgender">
                <mm:import externid="newgender" />
                <mm:write referid="newgender">
                <option value="unknown" <mm:compare value="unknown">selected</mm:compare>>Unknown
                <option value="male" <mm:compare value="male">selected</mm:compare>><mm:write referid="mlg.Male"/>
                <option value="female" <mm:compare value="female">selected</mm:compare>><mm:write referid="mlg.Female"/>
                </mm:write>
                </select>
            </td></tr>
    </mm:compare>
        <mm:compare referid="entree" value="null" inverse="true">
            <tr><th width="150" ><mm:write referid="mlg.Account"/></th><td>
                    <input type="hidden" name="newaccount" value="<%= request.getHeader("sm_user") %>">
                <%= request.getHeader("sm_user") %>
                    <input type="hidden" name="newpassword" value="<%= request.getHeader("aad_nummer") %>">
                    <input type="hidden" name="newconfirmpassword" value="<%= request.getHeader("aad_nummer") %>">
            </td></tr>
            <mm:compare referid="hasnick" value="true">
            <tr><th width="150" >Nick</th><td>
                <mm:import externid="newnick" />
                <input name="newnick" value="<mm:write referid="newnick"/>" style="width: 100%" />
            </td></tr>
            </mm:compare>
            <tr><th><mm:write referid="mlg.Firstname"/></th><td>
                    <input type="hidden" name="newfirstname" value="<%= request.getHeader("aad_voornaam") %>">
                <%= request.getHeader("aad_voornaam") %>
                </td></tr>
            <tr><th><mm:write referid="mlg.Lastname"/></th><td>
                <mm:import id="tan"><%= request.getHeader("aad_achternaam") %></mm:import>
                <mm:compare referid="tan" value="null" inverse="true">
                    <input type="hidden" name="newlastname" value="<%= request.getHeader("aad_achternaam") %>">
                <%= request.getHeader("aad_achternaam") %>
                </td></tr>
                </mm:compare>
                <mm:compare referid="tan" value="null">
                    <input type="hidden" name="newlastname" value="   ">
                missing
                </td></tr>
                </mm:compare>
            <tr><th><mm:write referid="mlg.Email"/></th><td>
                <input type="hidden" name="newemail" value="<%= request.getHeader("aad_emailadres") %>" />
                <%= request.getHeader("aad_emailadres") %>
                </td></tr>
            <tr><th><mm:write referid="mlg.Location"/></th><td>
                <mm:import externid="newlocation" />
                <input name="newlocation" value="<mm:write referid="newlocation" />" style="width: 100%" />
                </td></tr>
            <tr><th><mm:write referid="mlg.Gender"/></th><td>
                <select name="newgender">
                <mm:import externid="newgender" />
                <mm:write referid="newgender">
                <option value="unknown" <mm:compare value="unknown">selected</mm:compare>>Unknown
                <option value="male" <mm:compare value="male">selected</mm:compare>><mm:write referid="mlg.Male"/>
                <option value="female" <mm:compare value="female">selected</mm:compare>><mm:write referid="mlg.Female"/>
                </mm:write>
                </select>
            </td></tr>
    </mm:compare>
    <tr><th colspan="2">
        <input type="hidden" name="action" value="createposter">
        <center><input type="submit" value="<mm:write referid="mlg.Save"/>"></center>
    </form>
    </th></tr>
</table>
</mm:compare>
</mm:compare>

<mm:compare referid="feedback" value="ok">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="60%">
            <tr><th><mm:write referid="mlg.Account_created"/></th></tr>
            <tr><td><mm:write referid="mlg.Your_account_is_created_you_may"/> <a href="<mm:url page="index.jsp" referids="forumid" />"><mm:write referid="mlg.login"/></a></td><tr>
</table>
</mm:compare>

</div>

<div class="footer">
    <mm:import id="footerpath" jspvar="footerpath"><mm:function set="mmbob" name="getForumFooterPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=footerpath%>"/>
</div>

</mm:locale>

</body>
</html>

</mm:content>
</mm:cloud>
