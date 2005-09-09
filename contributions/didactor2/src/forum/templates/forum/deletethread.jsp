<%@page language="java" contentType="text/html; charset=utf-8" autoFlush="false"%>
<%--
This page allows a teacher or administrator to completely remove a thread, including all related messages
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="oscache" prefix="os" %>
<mm:cloud jspvar="cloud" name="mmbase" loginpage="/login.jsp">
  <%@ include file="/shared/setImports.jsp"%>
  <mm:import id="name" externid="name" />
  <mm:import id="forum" externid="forum" jspvar="forum"/>
  <mm:import id="delthread" externid="delthread" />

  <di:hasrole role="teacher" inverse="false">
    <mm:import id="isTeacher">true</mm:import>
  </di:hasrole>
  <di:hasrole role="teacher" inverse="true">
    <mm:import id="isTeacher">false</mm:import>
  </di:hasrole>

  <mm:isnotempty referid="delthread">
    <mm:compare referid="isTeacher" value="true">
      <mm:node number="$delthread">
        <mm:relatednodes type="forummessages">
          <mm:deletenode deleterelations="true" />
        </mm:relatednodes>  
        <mm:deletenode deleterelations="true" />
      </mm:node>

      <os:flush group="<%="forum_"+forum%>" scope="application" />

      <mm:treefile jspvar="forward" write="false" page="/forum/forum.jsp" objectlist="$includePath" referids="$referids" escapeamps="false">
        <mm:param name="forum"><mm:write referid="forum" /></mm:param>
      </mm:treefile>
      <%
        response.sendRedirect(forward);
      %>
    </mm:compare>
  </mm:isnotempty>  
</mm:cloud>
