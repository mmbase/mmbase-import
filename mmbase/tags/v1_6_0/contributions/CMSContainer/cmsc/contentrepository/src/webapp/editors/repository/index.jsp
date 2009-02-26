<%@page language="java" contentType="text/html;charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<%@include file="globals.jsp" %>
<%@page import="com.finalist.cmsc.repository.RepositoryUtil"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<mm:import externid="channel" from="parameters" />
<mm:import externid="contentnumber" from="parameters" />

<mm:cloud loginpage="../login.jsp">

<mm:present referid="channel">
   <mm:url page="/editors/repository/Navigator.do" id="channelsurl" write="false" >
      <mm:param name="channel" value="${channel}"/>
   </mm:url>
   <mm:compare referid="channel" value="notfound" inverse="true">
      <mm:node referid="channel">
         <mm:nodeinfo type="type" jspvar="nodetype" write="false"/>
         <c:if test="${nodetype == 'contentchannel'}">
            <mm:url page="/editors/repository/Content.do" id="contenturl" write="false" >
               <mm:param name="parentchannel" value="${channel}"/>
            </mm:url>
         </c:if>
         <c:if test="${nodetype == 'collectionchannel'}">
            <mm:url page="/editors/repository/ChannelEdit.do" id="contenturl" write="false" >
               <mm:param name="number" value="${channel}"/>
            </mm:url>
         </c:if>
      </mm:node>
   </mm:compare>
</mm:present>

<mm:present referid="contentnumber">
   <mm:node number="$contentnumber" jspvar="node">
   <% if (RepositoryUtil.hasCreationChannel(node)) { %>
      <% Node cc = RepositoryUtil.getCreationChannel(node); %>
      <% if (cc != null) { %>
         <mm:url page="/editors/repository/Navigator.do" id="channelsurl" write="false" >
            <mm:param name="channel" value="<%= String.valueOf(cc.getNumber()) %>"/>
         </mm:url>
         <mm:import id="returnurl">/editors/repository/Content.do?parentchannel=<%= String.valueOf(cc.getNumber()) %></mm:import>
         <mm:url page="../WizardInitAction.do" id="contenturl" write="false" >
            <mm:param name="objectnumber" value="$contentnumber"/>
            <mm:param name="returnurl" value="$returnurl" />
         </mm:url>
      <% } %>
   <% } %>
   </mm:node>
</mm:present>

<mm:notpresent referid="channelsurl">
   <c:if test="${not empty param.title}">
      <mm:url page="/editors/repository/Navigator.do?title=${param.title}" id="channelsurl" write="false" />
   </c:if>
   <c:if test="${empty param.title}">
      <mm:url page="/editors/repository/Navigator.do" id="channelsurl" write="false" />
   </c:if>
</mm:notpresent>
<mm:notpresent referid="contenturl">
   <c:if test="${not empty param.title}">
      <mm:url page="/editors/repository/ContentSearchAction.do?&title=${param.title}&index=yes" id="contenturl" write="false" />
   </c:if>
   <c:if test="${empty param.title}">
      <mm:url page="/editors/repository/SearchInitAction.do?index=yes" id="contenturl" write="false" />
   </c:if>
</mm:notpresent>


<html:html xhtml="true">
   <head><title><fmt:message key="repository.title" /></title>
   </head>
   <frameset cols="250,*" framespacing="0" border="0">
      <frame scrolling="auto" frameborder="0"  src="<mm:url referid="channelsurl"/>" name="channels"/>
      <frame frameborder="0" src="<mm:url referid="contenturl"/>" name="content" />
   </frameset>
</html:html>
</mm:cloud>
</mm:content>