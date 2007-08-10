<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
	<link href="../css/main.css" type="text/css" rel="stylesheet" />
	<script type="text/javascript" src="../utils/transparent_png.js" ></script>
	
<title><fmt:message key="user.title" /></title>
</head>
<mm:cloud jspvar="cloud" loginpage="../login.jsp">
<body style="overflow: auto">
    <div class="tabs">
        <div class="tab_active">
            <div class="body">
                <div>
                    <a href="#"><fmt:message key="rights.${mode}.title" /></a>
                </div>
            </div>
        </div>
    </div>

	<div class="editor">
		<div class="body">
		<p>
	 		<mm:node number="${param.number}" jspvar="channel">
				<br/>
				<b><fmt:message key="rights.content.userson" /> 
				<c:if test="${mode == 'page'}"><mm:field name="title"/></c:if>
				<c:if test="${mode != 'page'}"><mm:field name="name"/></c:if>
				</b>:	
				<br/>
				<div style="padding: 5px">
					<table class="compact">
						<mm:listnodes type="mmbaseusers" jspvar="user" orderby="username">
							<c:choose>
								<c:when test="${mode == 'page'}">
									<c:set var="rank"><%=com.finalist.cmsc.navigation.NavigationUtil.getUserRole(channel, user).getRole().getName()%></c:set>
								</c:when>
								<c:otherwise>
									<c:set var="rank"><%=com.finalist.cmsc.repository.RepositoryUtil.getUserRole(channel, user).getRole().getName()%></c:set>
								</c:otherwise>
							</c:choose>
								
							<c:if test="${rank != 'none'}">
								<tr>
									<td>
										<img src="<cmsc:staticurl page="/editors/gfx/icons/type/user_${rank}.png"/>" alt="<fmt:message key="role.${rank}" />" title="<fmt:message key="role.${rank}" />" align="top"/>
										<font style="color: #999">(<fmt:message key="role.${rank}" />)</font>
									</td>
									<td>
										<mm:field name="username"/>
									</td>
									<td>
										<a href="mailto:<mm:field name="emailaddress"/>"><mm:field name="emailaddress"/></a>
									</td>
								</tr>
							</c:if>
						</mm:listnodes>
					</table>
				</div>
			</mm:node>
		</p>
		</div>
		<div class="side_block_end"></div>
	</div>	
</body>
</mm:cloud>
</html:html>
</mm:content>