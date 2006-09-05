<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../../globals.jsp" %>
<fmt:setBundle basename="cmsc-repository" scope="request" />
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<head>
	<title><fmt:message key="imageinfo.title" /></title>
	<link href="../css/main.css" type="text/css" rel="stylesheet" />
</head>
<body>
    <div class="tabs">
        <div class="tab_active">
            <div class="body">
                <div>
                    <a href="#"><fmt:message key="imageinfo.title" /></a>
                </div>
            </div>
        </div>
    </div>

	<div class="editor">
		<div class="body">
			<mm:cloud>
				<mm:node number="${param.objectnumber}">
					<div style="float:left">
			           	<img src="<mm:image template="s(430x430)"/>"/><br/>
			        </div>
			        <div style="float:left; padding:5px;">
			            <h1><mm:field name="filename"/></h1>
			           	<fmt:message key="imageinfo.titlefield" />: <b><mm:field name="title"/></b><br/>
			           	<fmt:message key="imageinfo.description" />: <mm:field name="description"/><br/>
			           	<br/>
			           	<fmt:message key="imageinfo.filesize" />: <mm:field name="filesize"/> <fmt:message key="imageinfo.bytes" /><br/>
			           	<fmt:message key="imageinfo.width" />: <mm:field name="width"/><br/>
			           	<fmt:message key="imageinfo.height" />: <mm:field name="height"/><br/>
			           	<fmt:message key="imageinfo.itype" />: <mm:field name="itype"/><br/>
			           	<br/>
			            <b><fmt:message key="imageinfo.related" /></b>:<br/>
			            <ul>
			            <mm:relatednodes type="contentelement">
			            	<li>
			            		<mm:field name="title"/><br/>
			            		<fmt:message key="imageinfo.otype" />: <mm:nodeinfo type="guitype"/><br/>
			            		<fmt:message key="imageinfo.number" />: <mm:field name="number"/>
			            	</li>
			            </mm:relatednodes>
			           	</ul>
					</div>
					<div style="clear:both; float:left">
						<ul class="shortcuts">
			               <li class="close">
				               <a href="#" onClick="window.close()"><fmt:message key="imageinfo.close" /></a>
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