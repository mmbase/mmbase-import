<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@include file="globals.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="Clean Non-Visible Portlets">
    <link href="style.css" type="text/css" rel="stylesheet"/>
</cmscedit:head>

<body>
<div class="tabs">
     <div class="tab_active">
        <div class="body">
           <div>
              <a href="#">Clean Non-Visible Portlets</a>
           </div>
        </div>
     </div>

   <div class="editor">
   <div class="body">

<mm:cloud jspvar="cloud" loginpage="../editors/login.jsp" rank="administrator">
<mm:log jspvar="log">

<mm:import externid="confirm"/>
<form method="post">
   <input type="submit" value="Clean them"/>
   <input type="hidden" name="confirm"/>
</form>

<%
ArrayList<Integer> numbers = new ArrayList<Integer>();
%>
   Searching of portlets who don't exist at a layout but are still 'used'...<br/><br/>
   <mm:list path="portlet,portletrel,page,layout">
      <c:set var="layoutId"><mm:field name="layout.number"/></c:set>
      <c:set var="portletrelName"><mm:field name="portletrel.name"/></c:set>
      <c:set var="found" value="${false}"/>
      <c:set var="constraints">layout.number = ${layoutId} AND namedallowrel.name LIKE '%${portletrelName}%'</c:set>
      <mm:list path="layout,namedallowrel,portletdefinition" constraints="${constraints}" max="1">
         <c:set var="found" value="${true}"/>
      </mm:list>

      <c:if test="${!found}">
         Portlet.number:<mm:field name="portlet.number" jspvar="nodenumber" write="true">
<% numbers.add((Integer)nodenumber); %></mm:field>
         <br />
         Portletrel.name:<mm:field name="portletrel.name" /><br />
         layout.title:<mm:field name="layout.title" /><hr />
      </c:if>
   </mm:list>

<mm:present referid="confirm">
   <%
   for(int i : numbers) {
   %>
      <mm:deletenode number="<%=String.valueOf(i)%>" deleterelations="true"/><b> - Deleted: <%=i%></b><br/>
   <%             
   }
   %>
</mm:present>

</mm:log>
</mm:cloud>
      <b>Done!</b><br/>
</div>
</div>
   </body>
</html:html>