<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
  <title><fmt:message key="download.title" /></title>
  <link href="../../css/main.css" type="text/css" rel="stylesheet" />
  <script>
  		function openError(staticDownload) {
  			openWindow("error", staticDownload);
  		}
  		
  		function openReport(staticDownload) {
  			openWindow("report", staticDownload);
  		}
  		
  		function openWindow(field, staticDownload) {
	  		window.open("popup.jsp?field="+field+"&number="+staticDownload, field, "width=400,height=300,scrollbars=1");  
	  	}
  		
  </script>
</head>
<body>
    <div class="tabs">
        <div class="tab_active">
            <div class="body">
                <div>
                    <a href="#"><fmt:message key="download.title" /></a>
                </div>
            </div>
        </div>
    </div>

<mm:cloud jspvar="cloud" loginpage="../../login.jsp">
	<mm:hasrank minvalue="administrator">

		<div class="editor">
	      <div class="ruler_green"><div><fmt:message key="download.ruler.new" /></div></div>
			<div class="body">
				
				<c:if test="${param.start}">
					<cmsc-sd:start startedVar="started"/>
					<p>
						<c:choose>
							<c:when test="${started}">
								<fmt:message key="download.start.started" />
							</c:when>
							<c:otherwise>
								<fmt:message key="download.start.notstarted" />
							</c:otherwise>
						</c:choose>
					</p>
				</c:if>
				<p>
					<a href="?start=true" class="button"><fmt:message key="download.start"/></a>
					<br/><br/>
				</p>
	
			</div>						
	      <div class="ruler_green"><div><fmt:message key="download.ruler.old" /></div></div>
			<div class="body">
				<table>
					<tr>
						<th><fmt:message key="download.header.started" /></th>
						<th><fmt:message key="download.header.busy" /></th>
						<th></th>
						<th></th>
						<th></th>
					</tr>
					
				<mm:cloud>
					<mm:listnodes type="staticdownload" orderby="number" directions="DOWN">
						<mm:field name="started" jspvar="started" vartype="Long"><mm:time format="yyyy/M/d H:mm:ss" jspvar="startTime" write="false"/></mm:field>
						<mm:field name="completed" jspvar="completed" write="false" vartype="Long"/>
						<c:set var="running" value="${empty completed}"/>
						<c:if test="${running}">
							<script>
								setTimeout("document.location.href = 'download.jsp'",5000);
							</script>
							<c:set var="completed"><%=System.currentTimeMillis()/1000%></c:set>
						</c:if>
						
						<tr <mm:even inverse="true">class="swap"</mm:even> <c:if test="${running}">style="background-color: #f1f400"</c:if>>
							<td>
								${startTime}
							</td>
							<td>
								${completed-started}
								<fmt:message key="download.header.seconds" />
							</td>
							<td>
	 							<mm:field name="error" jspvar="error" write="false">
	 								<c:if test="${!empty error}">
										<a href="javascript:openError(<mm:field name="number"/>)" class="button"><fmt:message key="download.button.errors" /></a>
									</c:if>
								</mm:field>
							</td>
							<td>
	 							<mm:field name="report" jspvar="report" write="false">
									<c:if test="${!empty report}">
										<a href="javascript:openReport(<mm:field name="number"/>)" class="button"><fmt:message key="download.button.report" /></a>
									</c:if>
								</mm:field>
							</td>
							<td>
	 							<mm:field name="filename" jspvar="filename" write="false">
									<c:if test="${!empty filename && filename != 'null'}">
										<a href="${filename}" class="button"><fmt:message key="download.download" /></a>
									</c:if>
								</mm:field>
							</td>
						</tr>
					</mm:listnodes>
				</mm:cloud>
				
				</table>
			</div>
			<div class="side_block_end"></div>
		</div>	
	</mm:hasrank>
</mm:cloud>
</body>
</html:html>
</mm:content>
