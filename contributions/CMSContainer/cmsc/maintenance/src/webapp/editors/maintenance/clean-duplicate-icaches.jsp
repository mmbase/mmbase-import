<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@include file="globals.jsp"
%><%@ page import="com.finalist.cmsc.maintenance.sql.*"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html:html xhtml="true">
<cmscedit:head title="maintenance.icaches.cleanduplicates">
   <link href="style.css" type="text/css" rel="stylesheet"/>
</cmscedit:head>
   <body>
<mm:cloud jspvar="cloud" rank="basic user" method="http">

<div class="tabs">
  <div class="tab_active">
     <div class="body">
        <div>
           <a href="#"><fmt:message key="maintenance.icaches.cleanduplicates" /></a>
        </div>
     </div>
  </div>
</div>

<div class="editor">
   <div class="body">

<form method="post" action="#">
   <input type="submit" name="action" value="view"/>
   <input type="submit" name="action" value="remove"/>
</form>

<mm:import externid="action">view</mm:import>

   <p>
   <b><fmt:message key="maintenance.icaches.zerosize"/></b><br/>
   <c:set var="size" value="0"/>
   <mm:listnodes type="icaches" constraints="filesize=0">
      <mm:field name="number"/>
      <mm:present referid="action">
         <c:if test="${action eq 'remove'}">
            <mm:deletenode />removed<br/>
         </c:if>
      </mm:present>
      <mm:size id="size" write="false"/>
   </mm:listnodes>
   <br/>
   <fmt:message key="maintenance.icaches.emptyicachesfound"/><b>${size}</b><br/>
   </p>
   
   <p>
   <mm:present referid="action">
      <b><fmt:message key="maintenance.icaches.duplicates"/></b><br/>
      <mm:write referid="action" jspvar="action" vartype="String">
            <%= new SqlExecutor().execute(new DuplicateICaches(action)) %>
      </mm:write>
   </mm:present>
   </p>
   <br/>
   <br/>
   
</mm:cloud>
</div>
</div>
   </body>
</html:html>
