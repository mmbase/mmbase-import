<%@ include file="jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="postareaid" />
<mm:import externid="postthreadid" />
<mm:import externid="postingid" />

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

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
    <tr>
        <th colspan="3"><mm:write referid="mlg.send"/> <mm:write referid="mlg.private_message" /></th>
    </tr>
    <mm:link page="thread.jsp" referids="forumid,postareaid,postthreadid,postingid" >
        <form action="${_}" method="post">
        <mm:node number="${postingid}" notfound="skipbody">
            <tr>
                <th><mm:write referid="mlg.To"/></th>
                <td colspan="2">
                    <mm:field name="c_poster" write ="true">
                        <input name="to" type="hidden" value="${_}">
                    </mm:field>
                    <p>test: <mm:write referid="posterid"/>::${posterid}</p>
                    <mm:node referid="posterid" notfound="skipbody">
                        <mm:field name="account" >
                            <input name="poster" type="hidden" value="${_}">
                        </mm:field>
                    </mm:node>
                </td>
            </tr>
            <tr>
                <th><mm:write referid="mlg.Subject"/></th>
                <td colspan="2"><input name="subject" style="width: 100%" value="Re: <mm:field name="subject" />"></td>
            </tr>
        </mm:node>
        <tr>
            <th><mm:write referid="mlg.Message" /></th>
            <td colspan="2"><textarea name="body" rows="20" style="width: 100%"></textarea></td>
        </tr>
        <tr>
            <th>&nbsp;</th>
            <td>
                <input type="hidden" name="action" value="newprivatemessage">
                <center><input type="submit" value="<mm:write referid="mlg.Send"/> <mm:write referid="mlg.message"/>"></center>
        </form>
    </mm:link>
    </td>
    <td>
    <mm:link page="postarea.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:param name="postareaid" value="$postareaid" />
        <form action="${_}" method="post">
            <p />
            <center> <input type="submit" value="<mm:write referid="mlg.Cancel"/>"> </center>
        </form>
    </mm:link>
    </td>
    </tr>
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
