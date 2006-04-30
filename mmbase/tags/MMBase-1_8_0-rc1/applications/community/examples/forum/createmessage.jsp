<%@ include file="inc.jsp" %>
<body>
<mm:cloud name="mmbase">
<mm:import externid="channel" from="parameters"/>
<mm:import externid="thread" from="parameters"/>
<mm:node number="${thread}">
<h2>Post a message:</h2>
<a name="post" />
<form method="post" action="<mm:url page="postmessage.jsp" referids="channel,thread" />">
Name : <input type="text" name="username" value="" /> <br />
<input type="text" name="subject" size="75" maxlength="80" value="<mm:field name="resubject" />" /><br />
<textarea name="body" cols="75" rows="12" wrap="on"></textarea><br />
<input type="submit" name="action" value="post message">
</form>
</mm:node>
  &nbsp;
[ <a href="<mm:url page="forum.jsp" referids="channel" />" >Return to Forum</a> ]
</mm:cloud>
</body></html>
