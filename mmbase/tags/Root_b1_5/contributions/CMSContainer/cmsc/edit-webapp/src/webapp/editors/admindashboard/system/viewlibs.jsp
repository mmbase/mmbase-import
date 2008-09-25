<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../../globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="admindashboard.title">
	<link href="../../css/compact.css" type="text/css" rel="stylesheet" />
</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" loginpage="../../login.jsp">
	<mm:hasrank minvalue="administrator">
	
		<div class="editor">
		     <div class="ruler_green"><div><fmt:message key="admindashboard.system.viewlibs.header" /></div></div>
		</div>
		<div class="editor">
			<div class="body">
			<cmsc:version type="libs" var="libs"/>
          <table>
            <thead>
               <tr>
                  <th style="width:250px"><fmt:message key="admindashboard.system.viewlibs.lib" /></th>
                  <th><fmt:message key="admindashboard.system.viewlibs.version" /></th>			
               </tr>
            </thead>
            <c:forEach var="lib" items="${libs}">
            	<tr <mm:even inverse="true">class="swap"</mm:even>>
            		<td>${lib.key}</td>
            		<td><b>${lib.value}</b></td>
            	</tr>
				</c:forEach>
			</table>
			<p>
				<a href="../index.jsp"><fmt:message key="admindashboard.system.viewlibs.back" /></a>
			</p>
			</div>
		</div>
	</mm:hasrank>
</mm:cloud>
</body>
</html:html>
</mm:content>
