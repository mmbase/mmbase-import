<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<mm:cloud name="mmbase">
<mm:import externid="channel" from="parameters" />
<mm:import externid="thread" from="parameters" ><mm:write referid="channel" /></import>
<mm:import externid="username" from="parameters" />
<mm:import externid="subject" from="parameters" />
<mm:import externid="body" from="parameters" />

<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>MMBase Forum</title>
<link rel="stylesheet" type="text/css" href="../../css/mmbase.css" />
</head>
<body class="basic">
<table summary="forum threads" width="93%" cellspacing="1" cellpadding="3" border="0">
<mmcommunity:testchannel channel="${channel}" condition="readonly" reverse="true">
<mmcommunity:post jspvar="msg">
    <mm:setfield name="thread"><mm:write referid="thread" /></mm:setfield>
    <mm:setfield name="channel"><mm:write referid="channel" /></mm:setfield>
    <mm:setfield name="username"><mm:write referid="username" /></mm:setfield>
    <mm:setfield name="subject"><mm:write referid="subject" /></mm:setfield>
    <mm:setfield name="body"><mm:write referid="body" /></mm:setfield>
</mmcommunity:post>
<mm:node number="<%=msg%>">
<tr align="left">
  <th class="header" colspan="2"><mm:field name="html(subject)" /></th>
</tr>
<tr>
  <td class="multidata" colspan="2">
     <mmcommunity:getinfo key="name" skiponempty="true" jspvar="infoname">
     <p><em>Posted by <%=infoname%></em> on
        <mm:field name="day(timestampsec)" />
        <mm:field name="month(timestampsec)" />
        <mm:field name="year(timestampsec)" /></p>
     </mmcommunity:getinfo>
     <p><mm:field name="html(body)" /></p>
     &nbsp;
</td></tr>
</mm:node>
</mmcommunity:testchannel>
<mmcommunity:testchannel channel="${channel}" condition="readonly">
<tr align="left">
  <th class="header" colspan="2">Post error</th>
</tr>
<tr>
  <td class="multidata" colspan="2">
     <p>You cannot post a message to this channel, it is read-only.</p>
     &nbsp;
</td></tr>
</mmcommunity:testchannel>
<tr><td>&nbsp;</td></tr>
<tr>
<td class="navigate"><a href="<mm:url page="forum.jsp" referids="channel" />" ><img src="../../images/back.gif" alt="back" border="0" align="left" /></a></td>
<td class="data">Return to forum</td>
</tr>
</table>
</body></html>
</mm:cloud>
