<%@ include file="inc.jsp" %>
<body>
<mm:cloud name="mmbase">
<mm:import externid="thread" from="parameters" />
<mm:import externid="channel" from="parameters" />
<mm:node number="${thread}">
  <h2><mm:field name="html(subject)" /></h2>
     <mmcommunity:getinfo key="name">
     <mm:isnotempty>
     <p><em>Posted by <mm:write /></em> on
        <mm:field name="day(timestampsec)" />
        <mm:field name="month(timestampsec)" />
        <mm:field name="year(timestampsec)" /></p>
     </mm:isnotempty>
     </mmcommunity:getinfo>
     <p><mm:field name="html(body)" /></p>
     &nbsp;
<mmcommunity:testchannel channel="${channel}" condition="readonly" reverse="true">
<p>[ <a href="<mm:url page="createmessage.jsp" referids="channel,thread" />" >Post Reply </a> ]</p>
</mmcommunity:testchannel>
<p> [ <a href="<mm:url page="editmessage.jsp" referids="channel,thread" />" >ADMIN: Moderate Message</a> ]</p>
<mmcommunity:tree thread="${thread}" max="50">
   <mm:first>
    <strong>Follow-Ups</strong>:<br />
   </mm:first>
   <mm:field name="listhead" />
     <li><a href="<mm:url page="message.jsp" referids="channel" ><mm:param name="thread"><mm:field name="number" /></mm:param></mm:url>"><mm:field name="html(subject)" /></a>
     <mmcommunity:getinfo key="name">
     <mm:isnotempty>
     <em>by <mm:write /> on
        <mm:field name="day(timestampsec)" />
        <mm:field name="month(timestampsec)" />
        <mm:field name="year(timestampsec)" /></em>
     </mm:isnotempty>
     </mmcommunity:getinfo></li>
   <mm:field name="listtail" />
</mmcommunity:tree>

[ <a href="<mm:url page="forum.jsp" referids="channel" />" >Return to Forum</a> ]
</mm:node>
</mm:cloud>
</body></html>

