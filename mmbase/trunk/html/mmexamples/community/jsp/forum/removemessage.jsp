<%@ page errorPage="actionerror.jsp" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<%@ include file="../gui.jsp" %><%= header("forum", request)  %>
<mm:cloud name="mmbase" method="http" logon="admin">
<mm:import externid="channel" type="parameters" />
<mm:import externid="thread" type="parameters" />
<mmcommunity:delete message="${thread}" />
  <h2>Result of your action</h2>
     <p>Message is removed.</p>
[ <a href="<mm:url page="forum.jsp" referids="channel" />" >Return to Forum</a> ]
</mm:cloud>
<%= footer(request)  %>
