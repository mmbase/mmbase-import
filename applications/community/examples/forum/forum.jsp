<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd"><mm:cloud name="mmbase">
<mm:import externid="channel" />
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>MMBase Forum</title>
<link rel="stylesheet" type="text/css" href="../../css/mmbase.css" />
</head>
<% // code to determine offset... since we don't have a tag that does this.
   int pagesize=30;
   String spage=request.getParameter("page");
   int thispage=0;
   if (spage!=null) thispage=Integer.parseInt(spage);
   if (thispage<0) thispage=0;
   int count=0;
%>
<body class="basic"><mm:node referid="channel" fields="number">
<table summary="forum threads" width="93%" cellspacing="1" cellpadding="3" border="0">
<tr align="left">
  <th class="header" colspan="4"><mm:field name="title" /></th>
</tr>

<mmcommunity:testchannel condition="readonly" reverse="true">
<tr>
<td class="data" colspan="3">Start new thread</td>
<td class="navigate" align="right"><a href="<mm:url page="createnewthread.jsp" referids="channel" />" ><img src="../../images/next.gif" alt="post" border="0" align="right" /></a></td>
</tr>
</mmcommunity:testchannel>
<tr>
  <td class="multidata" colspan="4">
<mmcommunity:tree thread="<%=number%>" max="<%=pagesize%>" maxdepth="5" offset="<%=thispage*pagesize%>" id="thread">
  <mm:field name="listhead" />
     <li><a href="<mm:url page="message.jsp" referids="channel,thread" />"><mm:field name="html(subject)" /></a> (<mm:field name="replycount" />)
     <mmcommunity:getinfo key="name" skiponempty="true" jspvar="infoname">
     <em>by <%=infoname%> on
        <mm:field name="day(timestampsec)" />
        <mm:field name="month(timestampsec)" />
        <mm:field name="year(timestampsec)" /></em>
     </mmcommunity:getinfo></li>
     <% count++; %>
  <mm:field name="listtail" />
</mmcommunity:tree>
&nbsp;
</td></tr>
<tr>
<% if (thispage>0) { %>
 <td class="navigate" align="left"><a href="<mm:url page="forum.jsp" referids="channel" ><mm:param name="page"><%=thispage-1%></mm:param></mm:url>" ><img src="../../images/back.gif" alt="back" border="0" align="left" /></a></td>
 <td class="data">Previous page</td>
<% } else { %>
 <td class="data" colspan="2">(First page)</td>
<% }
   if (count==pagesize) {
%>
<td class="data">Next page</td>
<td class="navigate" align="right"><a href="<mm:url page="forum.jsp"referids="channel" ><mm:param name="page"><%=thispage+1%></mm:param></mm:url>" ><img src="../../images/next.gif" alt="next" border="0" align="right" /></a></td>
<% } else { %>
 <td class="data" colspan="2" align="right">(Last page)</td>
<% } %>
</tr>

<tr>
<td class="data" colspan="3" >ADMIN: Administer Forum</td>
<td class="navigate" align="right"><a href="<mm:url page="forumadmin.jsp" referids="channel" />" ><img src="../../images/change.gif" alt="change" border="0" align="right" /></a></td>
</tr>
</table>
</mm:node>
</body></html>
</mm:cloud>