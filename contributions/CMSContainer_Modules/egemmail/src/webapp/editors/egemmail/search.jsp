<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>

<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
  <title><fmt:message key="egemmail.title" /></title>
  <link href="../css/main.css" type="text/css" rel="stylesheet" />
  <script src="../repository/search.js"type="text/javascript" ></script>
</head>

<body onload="refreshChannels()">
    <div class="tabs">
        <div class="tab_active">
            <div class="body">
                <div>
                    <a href="#"><fmt:message key="egemmail.title" /></a>
                </div>
            </div>
        </div>
    </div>
<mm:cloud>
	<div class="editor">
		<div class="body">
			<html:form action="/editors/egemmail/EgemSearchAction">
				<label><fmt:message key="egemmail.field.title" />:</label>
				<html:text property="title"/><br/>
				<label><fmt:message key="egemmail.field.keywords" />:</label>
				<html:text property="keywords"/><br/>
				<label><fmt:message key="egemmail.field.author" />:</label>
 				<html:select property="author">
					<html:option value=""><fmt:message key="egemmail.all_users" /></html:option>
 					<mm:listnodes type="mmbaseusers" orderby="username">
						<c:set var="username"><mm:field name="username"/></c:set>
						<c:if test="${username != 'anonymous'}">
	 						<html:option value="${username}"/>
	 					</c:if>
					</mm:listnodes>
				</html:select><br/>
				
				<c:set var="submittext"><fmt:message key="egemmail.button.search" /></c:set>
				<input type="submit" value="${submittext}"/>
			</html:form>

			<mm:present referid="results">

				<c:set var="resultsPerPage" value="50"/>
				<c:set var="offset" value="${param.offset}"/>
				<c:set var="listSize">${fn:length(results)}</c:set>

				<mm:list referid="results" max="${resultsPerPage}" offset="${offset*resultsPerPage}">

				  <mm:first>
				      <%@include file="../pages.jsp" %>
				      <form action="EgemExportAction.do" name="exportForm">
			          <table>
			            <thead>
			               <tr>
			                  <th></th>
			                  <th><fmt:message key="egemmail.field.title" /></th>
			                  <th><fmt:message key="egemmail.field.type" /></th>
			                  <th><fmt:message key="egemmail.field.author" /></th>
			                  <th><fmt:message key="egemmail.field.number" /></th>
			               </tr>
		                </thead>
		            </mm:first>
		            <tr <mm:even inverse="true">class="swap"</mm:even>>
						<mm:field name="number" jspvar="number" write="false"/>
		            	<td>
		            		<input type="checkbox" name="export_${number}"/>
		            	</td>
		            	<td>
				            <mm:field jspvar="title" write="false" name="title" />
							<c:if test="${fn:length(title) > 50}">
								<c:set var="title">${fn:substring(title,0,49)}...</c:set>
							</c:if>
							${title}
		            	</td>
		            	<td><mm:nodeinfo type="guitype"/></td>
		            	<td><mm:field name="creator"/></td>
		            	<td>${number}</td>
			    	</tr>
			    	<mm:last>
			    		</table>
				      <%@include file="../pages.jsp" %>
                        &nbsp;&nbsp;&nbsp;<input type="checkbox" onChange="selectAll(this.checked, 'exportForm', 'export_');" value="on" name="selectall" />
                        <br/>
						<c:set var="submittext"><fmt:message key="egemmail.button.export" /></c:set>
						<input type="submit" value="${submittext}"/>
			    		</form>
			    	</mm:last>
			    </mm:list>
			</mm:present>
			
		</div>
		<div class="side_block_end"></div>
	</div>	
</mm:cloud>
</body>
</html:html>
</mm:content>