<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<mm:cloud name="mmbase">
<mm:import externid="thread" from="parameters" />
<mm:import externid="channel" from="parameters" />
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>MMBase Forum</title>
<link rel="stylesheet" type="text/css" href="../../../share/community/css/mmbase.css" />
</head>
<body class="basic"><mm:node number="${thread}">
<table summary="forum threads" width="93%" cellspacing="1" cellpadding="3" border="0">
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
<mmcommunity:testchannel channel="${channel}" condition="readonly" reverse="true">
<tr>
<td class="data">Post reply</td>
<td class="navigate" align="right"><a href="<mm:url page="createmessage.jsp" referids="channel,thread" />" >
<img src="../../../share/community/images/next.gif" alt="reply" border="0" align="left" /></a></td>
</tr>
</mmcommunity:testchannel>
<tr>
<td class="data">ADMIN: Moderate message</td>
<td class="navigate" align="right"><a href="<mm:url page="editmessage.jsp" referids="channel,thread" />" >
<img src="../../../share/community/images/change.gif" alt="change" border="0" align="left" /></a></td>
</tr>
<mmcommunity:tree thread="param:thread" max="50" id="thread">
   <mm:first>
<tr><td>&nbsp;</td></tr>
<tr align="left">
  <th class="header" colspan="2">Follow-ups:</th>
</tr>
<tr><td class="multidata" colspan="2">
   </mm:first>
   <mm:field name="listhead" />
     <li><a href="<mm:url page="message.jsp" referids="channel,thread" />"><mm:field name="html(subject)" /></a>
     <mmcommunity:getinfo key="name" skiponempty="true" jspvar="infoname">
     <em>by <%=infoname%> on
        <mm:field name="day(timestampsec)" />
        <mm:field name="month(timestampsec)" />
        <mm:field name="year(timestampsec)" /></em>
     </mmcommunity:getinfo></li>
   <mm:field name="listtail" />
   <mm:last>
</td></tr>
   </mm:last>
</mmcommunity:tree>
<tr><td>&nbsp;</td></tr>
<tr>
<td class="navigate"><a href="<mm:url page="forum.jsp" referids="channel" />" ><img src="../../../share/community/images/back.gif" alt="back" border="0" align="left" /></a></td>
<td class="data">Return to forum</td>
</tr>
</table>
</mm:node>
</body></html>
</mm:cloud>
