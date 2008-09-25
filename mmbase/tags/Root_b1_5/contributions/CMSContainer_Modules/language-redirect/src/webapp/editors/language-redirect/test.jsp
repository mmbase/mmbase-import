<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>

<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
	<title><fmt:message key="test.title" /></title>
	<link href="../css/main.css" type="text/css" rel="stylesheet" />

	<script type="text/javascript" src="../utils/transparent_png.js" ></script>
</head>

<body>

 <div class="tabs">
    <div class="tab_active">
	      <div class="body">
          <div>
	            <a href="#"><fmt:message key="test.title" /></a>
          </div>
     		</div>
	   </div>
 </div>

<div class="editor" style="height:500px">
 	<div class="body">
		<table>
			<thead>
				<tr>
					<th><fmt:message key="test.column.title" /></th>
					<th><fmt:message key="test.column.liname" /></th>
					<th><fmt:message key="test.column.englishpath" /></th>
				</tr>
			</thead>
<mm:cloud jspvar="cloud" loginpage="login.jsp">

	<mm:haspage page="/editors/admin/">
		<mm:hasrank minvalue="administrator">
		
	   <cmsc:list-pages var="sites"/>
	   <c:forEach var="site" items="${sites}">
	   	<c:if test="${site.urlfragment == 'www.nai.nl'}">
	   		<tr <c:if test="${swap}">class="swap"</c:if>>
	   			<c:set var="swap" value="${!swap}"/>
		   		<td>${site.title}</td>
		   	</tr>
	   		
	   		<cmsc:list-pages var="topchannels" origin="${site}" mode="all"/>
   			<c:set var="swap" value="true"/>
			   <c:forEach var="topchannel" items="${topchannels}" varStatus="topstatus" >
		   		<tr <c:if test="${swap}">class="swap"</c:if>>
		   			<c:set var="swap" value="${!swap}"/>
			   		<td>* ${topchannel.title}</td>
		   			<td>
		   				<mm:node number="${topchannel.id}">
		   					<mm:field name="liname"/>
		   				</mm:node>
		   			</td>
	   				<td>
			   			<mm:import jspvar="id" vartype="Integer">${topchannel.id}</mm:import>
		   				<%=com.finalist.cmsc.languageredirect.LanguageRedirectUtil.translate("en", id.intValue())%>
		   			</td>
		   		</tr>
					
				   <cmsc:list-pages var="channels" origin="${topchannel}" mode="all"/>
					<c:forEach var="channel" items="${channels}" varStatus="status" >
			   		<tr <c:if test="${swap}">class="swap"</c:if>>
			   			<c:set var="swap" value="${!swap}"/>
			   			<td>&nbsp;&nbsp;- ${channel.title}</td>
			   			<td>
				   			<mm:node number="${channel.id}">
				   				<mm:field name="liname"/>
				   			</mm:node>
				   		</td>
				   		<td>
				   			<mm:import jspvar="id" vartype="Integer">${channel.id}</mm:import>
				   			<%
				   				try { out.print(com.finalist.cmsc.languageredirect.LanguageRedirectUtil.translate("en", id.intValue())); }
			   					catch (Exception e) { out.print("<b>?? (Warning: language independent name not unique?)</b>"); }
				   			%>
			   			</td>
			   		</tr>
						
		   			<cmsc:list-pages var="subchannels" origin="${channel}" mode="all"/>
						<c:forEach var="subchannel" items="${subchannels}" varStatus="substatus" >
				   		<tr <c:if test="${swap}">class="swap"</c:if>>
				   			<c:set var="swap" value="${!swap}"/>
								<td>&nbsp;&nbsp;&nbsp;&nbsp;* ${subchannel.title}</td>
				   			<td>
					   			<mm:node number="${subchannel.id}">
					   				<mm:field name="liname"/>
					   			</mm:node>
					   		</td>
					   		<td>
					   			<mm:import jspvar="id" vartype="Integer">${subchannel.id}</mm:import>
				   				<%
				   					try { out.print(com.finalist.cmsc.languageredirect.LanguageRedirectUtil.translate("en", id.intValue())); }
			   						catch (Exception e) { out.print("<b>?? (Warning: language independent name not unique?)</b>"); }
				   				%>
				   			</td>
				   		</tr>
							
						</c:forEach>
						
					</c:forEach>
			   	
			   </c:forEach>
			   
	   	</c:if>
		</c:forEach>
		
		</mm:hasrank>	
	</mm:haspage>
</mm:cloud>
</table>
	</div>
</div>
</body>
</html:html>
</mm:content>