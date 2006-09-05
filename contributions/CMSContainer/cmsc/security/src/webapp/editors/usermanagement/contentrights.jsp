<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
	<link href="../css/main.css" type="text/css" rel="stylesheet" />
<title><fmt:message key="user.title" /></title>
</head>
<mm:cloud jspvar="cloud" loginpage="../login.jsp" rank='administrator'>
<body style="overflow: auto">
    <div class="tabs">
        <div class="tab_active">
            <div class="body">
                <div>
                    <a href="#"><fmt:message key="rights.content.title" /></a>
                </div>
            </div>
        </div>
    </div>

	<div class="editor">
		<div class="body" style="padding:10px">

 		<mm:node number="${param.number}" jspvar="channel">
			<b><fmt:message key="rights.content.userson" /> <mm:field name="title"/></b>:	
			<br/>
			<br/>
			
			<mm:listnodes type="mmbaseusers" jspvar="user">
				<c:set var="rank"><%=com.finalist.cmsc.repository.RepositoryUtil.getUserRole(channel, user).getRole().getName()%></c:set>
				<c:if test="${rank != 'none'}">
					<mm:field name="username"/>
					(<fmt:message key="role.${rank}" />)
					<br/>
				</c:if>
			</mm:listnodes>
		</mm:node>
		</div>
		<div class="side_block_end"></div>
	</div>	
</body>
</mm:cloud>
</html:html>
</mm:content>