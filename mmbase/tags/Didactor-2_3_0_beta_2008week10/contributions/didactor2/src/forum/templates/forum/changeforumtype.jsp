<%@page language="java" contentType="text/html; charset=utf-8" autoFlush="false"%>
<%--
This jsp is used for changing the type of the forum.
(open for students, or closed for students)
After changes, forwards back to the forum.
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:cloud jspvar="cloud" name="mmbase" loginpage="/login.jsp">
  <%@ include file="/shared/setImports.jsp"%>

  <mm:import id="number" externid="number"/>
  <mm:import id="forumtype" externid="forumtype"/>

  <mm:import id="mayManage">false</mm:import>
  <mm:listnodes type="people" constraints="username='${username}'">
    <mm:remove referid="mayManage"/>
    <mm:import id="mayManage">true</mm:import>
  </mm:listnodes>

  <mm:compare referid="mayManage" value="false">

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title>Forum</title>
  </mm:param>
</mm:treeinclude>
   <di:translate key="forum.notallowed_to_remove" />
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

  </mm:compare>

  <mm:compare referid="mayManage" value="true">
    <mm:node referid="number">
      <mm:setfield name="type"><mm:write referid="forumtype"/></mm:setfield>
    </mm:node>
    <mm:treefile jspvar="forward" write="false" page="/forum/forum.jsp" objectlist="$includePath" referids="$referids" escapeamps="false">
      <mm:param name="forum"><mm:write referid="number"/></mm:param>
    </mm:treefile>
    <%
      response.sendRedirect(forward);
    %>
  </mm:compare>
</mm:cloud>
