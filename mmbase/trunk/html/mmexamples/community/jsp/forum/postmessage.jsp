<%@ include file="inc.jsp" %>
<%@ page errorPage="posterror.jsp" %>
<body>
<mm:cloud name="mmbase">
<mm:import id="channelnode" externid="channel" from="parameters" />
<mm:import externid="thread" from="parameters" />
<mm:import externid="username" from="parameters" />
<mm:import externid="subject" from="parameters" />
<mm:import externid="body" from="parameters" />

<mm:node referid="channelnode">
 <mm:field id="channel" name="number" write="false" />
</mm:node>
<% System.err.println("aaaa"); %>
<mmcommunity:testchannel channel="$channel" condition="readonly" reverse="true">

<% System.err.println("aaaa1"); %>
<mmcommunity:post jspvar="msg">
<% System.err.println("aaaa0"); %>
  <mm:present referid="thread">
<% System.err.println("aaaa00"); %>
    <mm:setfield name="thread"><mm:write referid="thread" /></mm:setfield>
  </mm:present>
<% System.err.println("aaaa2"); %>
    <mm:setfield name="channel"><mm:write referid="channel" /></mm:setfield>
    <mm:setfield name="username"><mm:write referid="username" /></mm:setfield>
<% System.err.println("aaaa3"); %>
    <mm:setfield name="subject"><mm:write referid="subject" /></mm:setfield>
    <mm:setfield name="body"><mm:write referid="body" /></mm:setfield>
</mmcommunity:post>

<% System.err.println("aaaa"); %>
<mm:node number="<%=msg%>">
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
</mm:node>
</mmcommunity:testchannel>
<mmcommunity:testchannel channel="${channel}" condition="readonly">
<h2>Post error</h2>
     <p>You cannot post a message to this channel, it is read-only.</p>
     &nbsp;
</mmcommunity:testchannel>
[ <a href="<mm:url page="forum.jsp" referids="channel" />" >Return to Forum</a> ]
</body></html>
</mm:cloud>
</body></html>
