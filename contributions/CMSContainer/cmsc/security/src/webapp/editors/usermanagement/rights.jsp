<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="user.title" />
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
	 		<mm:node number="${param.number}" jspvar="channel">
            <p>
               <br/>
               <b><fmt:message key="rights.groupson">
               	    <fmt:param><mm:nodeinfo type="guitype"/></fmt:param>
                      <fmt:param>
			               <c:if test="${mode == 'page'}"><mm:field name="title"/></c:if>
			               <c:if test="${mode != 'page'}"><mm:field name="name"/></c:if>
                      </fmt:param>
               </fmt:message></b>
               <br/>
            </p>
            <div style="padding: 5px">
               <table class="compact">
                  <mm:listnodes type="mmbasegroups" jspvar="group" orderby="name">
                     <c:choose>
                        <c:when test="${mode == 'page'}">
                           <c:set var="role"><%=com.finalist.cmsc.navigation.NavigationUtil.getRole(group, channel).getRole().getName()%></c:set>
                        </c:when>
                        <c:otherwise>
                           <c:set var="role"><%=com.finalist.cmsc.repository.RepositoryUtil.getRole(group, channel).getRole().getName()%></c:set>
                        </c:otherwise>
                     </c:choose>
                        
                     <c:if test="${role != 'none'}">
                        <tr>
                           <td>
                              <img src="<cmsc:staticurl page="/editors/gfx/icons/type/group_${role}.png"/>" alt="<fmt:message key="role.${role}" />" title="<fmt:message key="role.${role}" />" align="top"/>
                              <font style="color: #999">(<fmt:message key="role.${role}" />)</font>
                           </td>
                           <td>
                              <mm:field name="name"/>
                           </td>
                        </tr>
                     </c:if>
                  </mm:listnodes>
               </table>
            </div>
            <p>
               <br/>
               <b><fmt:message key="rights.userson" >
                        <fmt:param><mm:nodeinfo type="guitype"/></fmt:param>
                        <fmt:param>
                           <c:if test="${mode == 'page'}"><mm:field name="title"/></c:if>
                           <c:if test="${mode != 'page'}"><mm:field name="name"/></c:if>
                        </fmt:param>
               </fmt:message></b>
               <br/>
            </p>
				<div style="padding: 5px">
					<table class="compact">
						<mm:listnodes type="mmbaseusers" jspvar="user" orderby="username">
							<c:choose>
								<c:when test="${mode == 'page'}">
									<c:set var="role"><%=com.finalist.cmsc.navigation.NavigationUtil.getUserRole(channel, user).getRole().getName()%></c:set>
								</c:when>
								<c:otherwise>
									<c:set var="role"><%=com.finalist.cmsc.repository.RepositoryUtil.getUserRole(channel, user).getRole().getName()%></c:set>
								</c:otherwise>
							</c:choose>

							<c:if test="${role != 'none'}">
								<tr>
									<td>
										<img src="<cmsc:staticurl page="/editors/gfx/icons/type/user_${role}.png"/>" alt="<fmt:message key="role.${role}" />" title="<fmt:message key="role.${role}" />" align="top"/>
										<font style="color: #999">(<fmt:message key="role.${role}" />)</font>
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
		</div>
		<div class="side_block_end"></div>
	</div>	
</body>
</mm:cloud>
</html:html>
</mm:content>