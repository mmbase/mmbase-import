<%@ include file="inc.jsp" %>

<body class="basic">
<mm:cloud name="mmbase" method="http" rank="administrator">
<mm:import externid="channel" from="parameters" />
<mm:import externid="thread"  from="parameters" />
<mm:node number="$thread">
  <h2>Edit/Moderate a message</h2>
<a name="post" />
<mmcommunity:getinfo key="name">
     <mm:isnotempty>
     <p><em>Originally posted by <mm:write /></em> on
        <mm:field name="day(timestampsec)" />
        <mm:field name="month(timestampsec)" />
        <mm:field name="year(timestampsec)" /></p>
     </mm:isnotempty>
</mmcommunity:getinfo>
<form method="post" action="<mm:url page="updatemessage.jsp" referids="channel,thread" />">
<input type="checkbox" name="username" value="moderator" />Change original poster to 'moderator' <br />
<input type="text" name="subject" size="75" maxlength="80" value="<mm:field name="subject" />" /><br />
<textarea name="body" cols="75" rows="12" wrap="on"><mm:field name="body" /></textarea><br />
<input type="submit" name="action" value="edit message">
</form>
  &nbsp;
[ <a href="<mm:url page="removemessage.jsp" referids="channel,thread" />">ADMIN: Remove this message and all its descendants</a> ]
</mm:node>
[ <a href="<mm:url page="forum.jsp" referids="channel" />" >Return to Forum</a> ]
</mm:cloud>
</body>
</html>
