<%@ page isErrorPage="true" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<%@ include file="../gui.jsp" %><%= header("forum", request)  %>
<mm:cloud name="mmbase">
  <mm:import externid="channel" />
  <h2>Result of your action</h2>
     <p>Action failed :<br />
      <%= exception.getMessage() %></p>
     &nbsp;
[ <a href="<mm:url page="forum.jsp" referids="channel" />" >Return to Forum</a> ]
</mm:cloud>
<%= footer(request)  %>
