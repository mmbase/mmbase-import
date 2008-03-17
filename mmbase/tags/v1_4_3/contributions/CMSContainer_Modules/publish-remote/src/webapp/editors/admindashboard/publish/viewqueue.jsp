<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../../publish-remote/globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="publish.title">
	<link href="../../css/main.css" type="text/css" rel="stylesheet" />
</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" loginpage="../../login.jsp">
	<mm:hasrank minvalue="administrator">
	
		<div class="editor">
		     <div class="ruler_green"><div><fmt:message key="admindashboard.publish.viewqueue.header" /></div></div>
		</div>
		<div class="editor">
			<div class="body">
			<mm:listnodescontainer type="publishqueue">
				<mm:constraint field="status" operator="EQUAL" value="init"/>
				<br/>
				<b><fmt:message key="admindashboard.publish.viewqueue.size" />:</b> <mm:size/>
				<br/>
				
				
          <table>
            <thead>
               <tr>
                  <th><fmt:message key="admindashboard.publish.viewqueue.number" /></th>
                  <th><fmt:message key="admindashboard.publish.viewqueue.action" /></th>
                  <th><fmt:message key="admindashboard.publish.viewqueue.timestamp" /></th>
                  <th><fmt:message key="admindashboard.publish.viewqueue.nodetype" /></th>
                  <th><fmt:message key="admindashboard.publish.viewqueue.author" /></th>
                  <th><fmt:message key="admindashboard.publish.viewqueue.name" /></th>
                  <th><fmt:message key="admindashboard.publish.viewqueue.publishdate" /></th>
               </tr>
            </thead>


				
				<mm:listnodes orderby="number">
					<mm:field name="action" id="action" write="false"/>
					<mm:field name="timestamp" write="false"><mm:time id="timestamp" write="false" format="dd/MM/yyyy hh:mm"/></mm:field>
					<mm:field name="sourcenumber" id="number" write="false"/>
					<mm:field name="publishdate"><mm:time id="publishdate" write="false" format="dd/MM/yyyy hh:mm"/></mm:field>
					
					<c:set var="type" value=""/>
					<c:set var="name" value=""/>
					<c:set var="author" value=""/>
					<c:set var="nodetype" value=""/>
					
					<mm:node number="${number}" notfound="skip">
						<mm:nodeinfo type="guitype" jspvar="nodetype" write="false" vartype="String"/>
						<mm:hasfield name="lastmodifier">
							<mm:field name="lastmodifier" id="author" write="false"/>
						</mm:hasfield>

						<mm:hasfield name="name">
							<mm:field name="name" id="name" write="false"/>
						</mm:hasfield>
						<c:if test="${empty name}">
							<mm:hasfield name="title">
								<mm:field name="title" id="name" write="false"/>
							</mm:hasfield>
						</c:if>
						<c:if test="${empty name}">
							<mm:hasfield name="key">
								<mm:field name="key" id="name" write="false"/>
							</mm:hasfield>
						</c:if>
					</mm:node>
					
			      <tr <mm:even inverse="true">class="swap"</mm:even>>
						<td>${number}</td>
						<td><fmt:message key="publish.action.${action}" /></td>
						<td>${timestamp}</td>
						<td>${nodetype}</td>
						<td>${author}</td>
						<td>${name}</td>
						<td>${publishdate}</td>
					</tr>
				</mm:listnodes>
				
				</table>
			</mm:listnodescontainer>
			<br/>
			<a href="../index.jsp"><fmt:message key="admindashboard.publish.viewqueue.back" /></a>
			</div>
		</div>
		
	</mm:hasrank>
</mm:cloud>
</body>
</html:html>
</mm:content>
