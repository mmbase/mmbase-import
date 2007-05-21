<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="versioning.admin.header" />
<mm:cloud jspvar="cloud" loginpage="../login.jsp">

	<mm:hasrank minvalue="administrator">

		<c:if test="${param.action == 'deleteall'}">
			<mm:listnodes type="archive">
				<mm:deletenode/>
			</mm:listnodes>
			<c:set var="message"><fmt:message key="versioning.admin.removeall.done"/></c:set>
		</c:if>
		
		<%java.util.HashSet archiveSet = new java.util.HashSet();%>
		<%java.util.HashSet<org.mmbase.bridge.Node> missingSet = new java.util.HashSet<org.mmbase.bridge.Node>();%>
		<mm:listnodes type="archive">
			<mm:field name="original_node" jspvar="archiveNumber" write="false">
				<%archiveSet.add(archiveNumber);%>
			</mm:field>
		</mm:listnodes>
	
		
		<mm:listnodes type="contentelement" orderby="number" jspvar="editNode">
				<mm:field name="number" jspvar="number" write="false">
					<%if(!archiveSet.contains(number)) {
						missingSet.add(editNode);
					}%>
				</mm:field>
		</mm:listnodes>

		
		<%
		int missing = missingSet.size();
		int archive = archiveSet.size();
		%>
		
		<c:if test="${param.action == 'createmissing'}">
			<%
			long now = System.currentTimeMillis();
			int created = 0;
			
			for(java.util.Iterator<org.mmbase.bridge.Node> i = missingSet.iterator(); i.hasNext();) {
				Node editNode = i.next();
				com.finalist.cmsc.services.versioning.Versioning.addVersion(editNode);
				created++;
			}
			missing -= created;
			archive += created;
			
			%>
			<c:set var="message"><fmt:message key="versioning.admin.createmissing.done">
				<fmt:param><%=created%></fmt:param>
				<fmt:param><%=(System.currentTimeMillis()-now)/1000%></fmt:param>
				</fmt:message></c:set>
		</c:if>
		
		
		<body
			<c:if test="${! empty message}">onload="alert('${message}')"</c:if>
		>
		
			<div class="side_block">
				<!-- bovenste balkje -->
				<div class="header">
					<div class="title"><fmt:message key="versioning.admin.header" /></div>
					<div class="header_end"></div>
				</div>
				<div class="body">
					<p>
						<fmt:message key="versioning.admin.numberarchive"><fmt:param><%=archive%></fmt:param></fmt:message><br/>
						<a href="?action=deleteall" onclick="return confirm('<fmt:message key="versioning.admin.removeall.confirm"/>');"><fmt:message key="versioning.admin.removeall"/></a>
					</p>
					<p>
						<fmt:message key="versioning.admin.numbermissing"><fmt:param><%=missing%></fmt:param></fmt:message><br/>
						<a href="?action=createmissing" onclick="return confirm('<fmt:message key="versioning.admin.createmissing.confirm"/>');"><fmt:message key="versioning.admin.createmissing"/></a>
					</p>
				</div>
				<!-- einde block -->
				<div class="side_block_end"></div>
			</div>
		</body>
	</mm:hasrank>
</mm:cloud>
</html:html>
</mm:content>