<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<cmscedit:head title="subsite.module.title"/>

<body>
<mm:import externid="subsite"/>
<h3><fmt:message key="subsite.module.title" /></h3>
      
<c:set var="subsite-page" value="${subsite}" scope="request"/>
      
<mm:cloud>
   <cmsc:select var="subsite-page">
	  <mm:listnodes type="subsite">
        <cmsc:option value="${_node.number}" name="${_node.title}" />
        <mm:size jspvar="grootte"/>
     </mm:listnodes>
   </cmsc:select>
   Aantal subsites: ${grootte}<br>
   
   <mm:listnodes type="subsite">
	   <c:if test="${subsite == _node.number}">
		   <mm:field name="title"/> at path <mm:field name="path"/><br>
	      ${_node.number} has title: ${_node.title}<br>
	      <mm:field name="number" write="false" id="personalpagemember"/>
	      <mm:relatednodes type="personalpage" role="navrel" searchdir="destination">
	        - <mm:field name="title"/><br>
	      </mm:relatednodes>
      </c:if>
   </mm:listnodes>
   
<%--
      <cmsc:list-pages var="children" origin="${personalpagemember}" mode="all"/>
      <c:forEach var="personalpage" items="${children}">
         $(personalpage.title)<br>
      </c:forEach>  
--%>

	
</mm:cloud>
</body>
</html:html>
</mm:content>