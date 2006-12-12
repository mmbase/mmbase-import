<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp" %>
<fmt:setBundle basename="cmsc-reactions" scope="request" />
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<head>
	<title><fmt:message key="reactioninfo.title" /></title>
	<link href="../css/main.css" type="text/css" rel="stylesheet" />
</head>
<body>
    <div class="tabs">
        <div class="tab_active">
            <div class="body">
                <div>
                    <a href="#"><fmt:message key="reactioninfo.title" /></a>
                </div>
            </div>
        </div>
    </div>

	<div class="editor">
		<div class="body">
			<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp"> 
				<mm:node number="${param.objectnumber}">
			        <div style="float:left; padding:5px;">
			            <h1></h1>
			           	<fmt:message key="reactioninfo.namefield" />: <b><mm:field name="name"/></b><br/>
                        <fmt:message key="reactioninfo.emailfield" />: <mm:field name="email"/><br/>
                        <fmt:message key="reactioninfo.titlefield" />: <mm:field name="title"/><br/>
                        <fmt:message key="reactioninfo.bodyfield" />: <mm:field name="body"/><br/>
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
			            <b><fmt:message key="reactioninfo.related" /></b>:<br/>
			            <ul>
			            <mm:relatednodes type="contentelement">
			            	<li>
			            		<mm:field name="title"/><br/>
			            		<fmt:message key="reactioninfo.otype" />: <mm:nodeinfo type="guitype"/><br/>
			            		<fmt:message key="reactioninfo.number" />: <mm:field name="number"/>
			            	</li>
			            </mm:relatednodes>
			           	</ul>
					</div>
					<div style="clear:both; float:left">
						<ul class="shortcuts">
			               <li class="close">
				               <a href="#" onClick="window.close()"><fmt:message key="reactioninfo.close" /></a>
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