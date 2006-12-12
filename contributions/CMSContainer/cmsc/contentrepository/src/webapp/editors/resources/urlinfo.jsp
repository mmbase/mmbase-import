<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<head>
	<title><fmt:message key="urlinfo.title" /></title>
	<link href="../css/main.css" type="text/css" rel="stylesheet" />
</head>
<body>
    <div class="tabs">
        <div class="tab_active">
            <div class="body">
                <div>
                    <a href="#"><fmt:message key="urlinfo.title" /></a>
                </div>
            </div>
        </div>
    </div>

	<div class="editor">
		<div class="body">
			<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
				<mm:node number="${param.objectnumber}">
			        <div style="float:left; padding:5px;">
			           	<fmt:message key="urlinfo.name" />: <b><mm:field name="name"/></b><br/>
			           	<fmt:message key="urlinfo.description" />: <mm:field name="description"/><br/>
			           	<fmt:message key="urlinfo.url" />: <mm:field name="url"/><br/>
                        <fmt:message key="urlform.valid" />: 
                                 <mm:field name="valid" write="false" jspvar="isValidUrl"/>
                                 <c:choose>
                                    <c:when test="${empty isValidUrl}">
                                        <fmt:message key="urlsearch.validurl.unknown" />
                                    </c:when>
                                    <c:when test="${isValidUrl eq 0}">
                                        <fmt:message key="urlsearch.validurl.invalid" />
                                    </c:when>
                                    <c:when test="${isValidUrl eq 1}">
                                        <fmt:message key="urlsearch.validurl.valid" />
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:message key="urlsearch.validurl.unknown" />
                                    </c:otherwise>
                                </c:choose>
                        <br/>
			           	<br/>
			           	<mm:field name="creationdate" id="creationdate" write="false"/>
			           	<mm:present referid="creationdate">
				           	<fmt:message key="secondaryinfo.creator" />: <mm:field name="creator"/><br/>
				           	<fmt:message key="secondaryinfo.creationdate" />: <mm:write referid="creationdate"><mm:time format="dd-MM-yyyy hh:mm"/></mm:write><br/>
				        </mm:present>

			           	<mm:field name="lastmodifieddate" id="lastmodifieddate" write="false"/>
			           	<mm:present referid="lastmodifieddate">
				           	<fmt:message key="secondaryinfo.lastmodifier" />: <mm:field name="lastmodifier"/><br/>
				           	<fmt:message key="secondaryinfo.lastmodifieddate" />: <mm:write referid="lastmodifieddate"><mm:time format="dd-MM-yyyy hh:mm"/></mm:write><br/>
				        </mm:present>
			           	<br/>
			            <b><fmt:message key="urlinfo.related" /></b>:<br/>
			            <ul>
			            <mm:relatednodes type="contentelement">
			            	<li>
			            		<mm:field name="title"/><br/>
			            		<fmt:message key="urlinfo.otype" />: <mm:nodeinfo type="guitype"/><br/>
			            		<fmt:message key="urlinfo.number" />: <mm:field name="number"/>
			            	</li>
			            </mm:relatednodes>
			           	</ul>
					</div>
					<div style="clear:both; float:left">
						<ul class="shortcuts">
			               <li class="close">
				               <a href="#" onClick="window.close()"><fmt:message key="urlinfo.close" /></a>
							</li>
						</ul>
					</div>
				</mm:node>
			</mm:cloud>
		</div>
		<div class="side_block_end"></div>
	</div>	
</body>
</html:html>
</mm:content>	            