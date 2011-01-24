<%@ include file="jspbase.jsp" %>
<mm:cloud name="mmbase" method="http" rank="administrator">
<mm:import externid="forumid" jspvar="forumid">unknown</mm:import>
<%@ include file="thememanager/loadvars.jsp" %>
<HTML>
<HEAD>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>

<mm:import id="posterid">-1</mm:import>
<mm:import id="mode">stats</mm:import>
<body>

<div class="header">
</div>

<div class="bodypart">

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%" align="center">
  <mm:nodelistfunction set="mmbob" name="getForums" referids="mode">
    <mm:import id="forumid" reset="true"><mm:field name="id"/></mm:import>
    <mm:import id="forumname" reset="true"><mm:field name="name"/></mm:import>

        <tr><th>Forum naam</th><th>threads</th><th>berichten</th><th>views</th><th>threadsloaded</th><th>postingsloaded</th><th>memory size</th></tr>
        <tr>
        <td><a href="index.jsp?forumid=<mm:write referid="forumid" />"><mm:write referid="forumname" /></a></td>
                <td><mm:field name="postthreadcount" /></td>
        <td><mm:field name="postcount" /></td>
        <td><mm:field name="viewcount" /></td>
        <td><mm:field name="postthreadloadedcount" /></td>
        <td><mm:field name="postingsloadedcount" /></td>
                <td><mm:field name="memorysize" /></td>
        </tr>
        <mm:nodelistfunction set="mmbob" name="getPostAreas" referids="forumid,posterid,mode">
        <tr>
        <td><a href="postarea.jsp?forumid=<mm:write referid="forumid" />&postareaid=<mm:field name="id" />"><mm:write referid="forumname" />.<mm:field name="name" /></a></td>
                <td><mm:field name="postthreadcount" /></td>
        <td><mm:field name="postcount" /></td>
        <td><mm:field name="viewcount" /></td>
        <td><mm:field name="postthreadloadedcount" /></td>
        <td><mm:field name="postingsloadedcount" /></td>
                <td><mm:field name="memorysize" /></td>
        </tr>

        </mm:nodelistfunction>
  </mm:nodelistfunction>
</mm:cloud>
</div>

<div class="footer">
</div>

</body>
</html>
