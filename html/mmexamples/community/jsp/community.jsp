<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd"><mm:cloud name="mmbase">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>MMBase Communities</title>
<link rel="stylesheet" type="text/css" href="../css/mmbase.css" />
</head>
<body class="basic">
<table summary="communities" width="93%" cellspacing="1" cellpadding="3" border="0">

<mm:context id="actions">
 <mm:import externid="community" />
 <mm:import externid="action" />
 <mm:import externid="log" />
 <mm:import externid="channel" />
 <mm:present referid="community">
    <mmcommunity:community community="${community}" action="${action}" />
 </mm:present>
 <mm:present referid="channel">
    <mm:present referid="action">
        <mmcommunity:channel channel="${channel}" action="${action}" />
     </mm:present>
    <mm:present referid="log">
        <mmcommunity:log channel="${channel}" action="${log}" />
     </mm:present>
 </mm:present>
</mm:context>

<mm:listnodes type="community" id="community" fields="kind">
<tr align="left">
  <th class="header" colspan="2"><mm:field name="title" /></th>
  <th class="linkdata"><a href="<mm:url page="community.jsp?action=open" referids="community" />">Open all channels</a></th>
  <th class="linkdata"><a href="<mm:url page="community.jsp?action=close" referids="community" />">Close all channels</a></th>
</tr>
<tr>
  <td class="multidata" colspan="4">
   <ul>
    <mm:relatednodes type="channel" fields="open" id="channel">
     <li>
      <% if(kind.equals("FORUM")) { %>
        <mmcommunity:testchannel condition="open">
            <a href="<mm:url page="forum/forum.jsp" referids="channel" />"><strong><mm:field name="html(title)" /></strong></a> is open<br />
            Security : <mm:field name="gui(state)" /><br />
            Maximum users : <mm:field name="gui(maxusers)" /><br />
            <a href="<mm:url page="community.jsp?action=close" referids="channel" />">Close channel</a><br />
            <a href="<mm:url page="community.jsp?action=readonly" referids="channel" />">Make channel read only</a><br />
        </mmcommunity:testchannel>
        <mmcommunity:testchannel condition="readonly">
            <a href="<mm:url page="forum/forum.jsp" referids="channel" />"><strong><mm:field name="html(title)" /></strong></a> is open for reading only<br />
            Security : <mm:field name="gui(state)" /><br />
            Maximum users : <mm:field name="gui(maxusers)" /><br />
            <a href="<mm:url page="community.jsp?action=close" referids="channel" />">Close channel</a><br />
            <a href="<mm:url page="community.jsp?action=open" referids="channel" />">Open channel</a><br />
        </mmcommunity:testchannel>
        <mmcommunity:testchannel condition="closed">
            <strong><mm:field name="html(title)" /></strong> is closed.<br />
            Security : <mm:field name="gui(state)" /><br />
            Maximum users : <mm:field name="gui(maxusers)" /><br />
            <a href="<mm:url page="community.jsp?action=open" referids="channel" />">Open channel</a><br />
            <a href="<mm:url page="community.jsp?action=readonly" referids="channel" />">Make channel read only</a><br />
        </mmcommunity:testchannel>
      <% } else { %>
        <mmcommunity:testchannel condition="open">
            <a href="<mm:url page="chats/chats.jsp" referids="channel" />"><strong><mm:field name="html(title)" /></strong></a> is open<br />
            Security : <mm:field name="gui(state)" /><br />
            Maximum users : <mm:field name="gui(maxusers)" /><br />
            <a href="<mm:url page="community.jsp?action=close" referids="channel" />">Close channel</a><br />
            <a href="<mm:url page="community.jsp?log=file" referids="channel" />">Start Logging</a><br />
            <a href="<mm:url page="community.jsp?log=stop" referids="channel" />">Stop Logging</a><br />
        </mmcommunity:testchannel>
        <mmcommunity:testchannel condition="open" reverse="true">
            <strong><mm:field name="html(name)" /></strong> is closed.<br />
            Security : <mm:field name="gui(state)" /><br />
            Maximum users : <mm:field name="gui(maxusers)" /><br />
            <a href="<mm:url page="community.jsp?action=open" referids="channel" />">Open channel</a><br />
        </mmcommunity:testchannel>
      <% } %>
        &nbsp;
     </li>
    </mm:relatednodes>
  </ul>
</td></tr>

</mm:listnodes>
</table>
</body></html>
</mm:cloud>
