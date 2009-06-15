<%@ page import="org.mmbase.bridge.*" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:cloud rank="administrator" loginpage="login.jsp" jspvar="cloud">
<div
  class="mm_c mm_c_core mm_c_b_email ${requestScope.componentClassName}"
  id="${requestScope.componentId}">
  
  <h3>${mm:string(requestScope['org.mmbase.framework.state'].renderer.block.title)}</h3>



<mm:hasnodemanager name="email">
<table summary="email test">
<%
    // mm:hasnodemanager already did the check for NotFoundException
    NodeManager email=email=cloud.getNodeManager("email");
%>

<tr>
  <th class="header" colspan="4">Dynamic &amp; Timed Email System - Queue Monitor - v1.0</th>
</tr>
<tr>
  <td class="multidata" colspan="4"><p>This tool shows the performamce of the timed MMBase email builder.</p></td>
</tr>

<tr><td>&nbsp;</td></tr>
<tr>
  <td class="data" colspan="3">Max number of queued messages in memory</td>
  <td class="data" ><%=email.getInfo("MAXMEMTASKS")%></td>
</tr>
<tr>
  <td class="data" colspan="3">Messages queued time</td>
  <td class="data" ><%=email.getInfo("DBQUEUEDTIME")%></td>
</tr>
<tr>
  <td class="data" colspan="3">Queued probe time</td>
  <td class="data" ><%=email.getInfo("DBQUEUEPROBETIME")%></td>
</tr>
<tr>
  <td class="data" colspan="3">Number of queued messages in database</td>
  <td class="data" ><%=email.getInfo("DBQUEUED")%></td>
</tr>
<tr>
  <td class="data" colspan="3">Number of queued messages in memory</td>
  <td class="data" ><%=email.getInfo("MEMTASKS")%></td>
</tr>
<tr>
  <td class="data" colspan="3">Number of messages send</td>
  <td class="data" ><%=email.getInfo("NUMBEROFMAILSEND")%></td>
</tr>
<tr>
  <td class="data" colspan="3">Show first 500 entry's of the queue</td>
  <td class="navigate">
    <a href="<mm:url page="email/emailqueue.jsp" />" ><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="view" border="0" /></a>
  </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr class="footer">
<td class="navigate"><a href="<mm:url page="../default.jsp" />" target="_top"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
<td class="data" colspan="3">Return to home page</td>
</tr>
</table>
</mm:hasnodemanager>
<mm:hasnodemanager name="email" inverse="true">
  <p>email not installed</p>
</mm:hasnodemanager>

</div>
</mm:cloud>
