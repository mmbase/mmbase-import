<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp" %>
<mm:import externid="bottomurl" from="parameters"/>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<cmscedit:head title="topmenu.title">
   <link href="<cmsc:staticurl page='/editors/css/topmenu.css'/>" type="text/css" rel="stylesheet" />
   <script type="text/javascript" src="topmenu.js"></script>
</cmscedit:head>
<body onload="initMenu();">
      <mm:cloud loginpage="login.jsp" rank="basic user">

         <div id="header">
	         <div class="title_image">
	            <img src="gfx/logo_editors.png" alt="Editors logo"/>
            </div>
            <div class="title">
               <fmt:message key="editors.title" />
            </div>
            <mm:cloudinfo type="user" id="cloudusername" write="false" />
            <mm:listcontainer path="user">
               <mm:constraint field="user.username" operator="EQUAL" referid="cloudusername" />
               <mm:list>
                  <mm:import id="fullname" jspvar="fullname"><mm:field name="user.firstname"/> <mm:field name="user.prefix"/> <mm:field name="user.surname"/></mm:import>
                  <div class="userinfo">
                     <fmt:message key="topmenu.user.title" />
                     <% if ("".equals(fullname.trim())) { %>
                        <mm:write referid="cloudusername"/>
                     <% } else { %>
                        <mm:write referid="fullname"/>
                     <% } %>
                     <mm:haspage page="/editors/help/">
                        | <a href="<mm:url page="help/"/>" target="bottompane" id="tutorial"><fmt:message key="topmenu.help" /></a>
                     </mm:haspage>
                     <mm:haspage page="/editors/logout.jsp">
                        | <a href="<mm:url page="logout.jsp"/>" target="_top" id="logout"><fmt:message key="topmenu.logout" /></a>
                    </mm:haspage>
                  </div>
               </mm:list>
            </mm:listcontainer>
         </div>
         <div id="menu">
            <ul>
               <li><a href="<mm:url page="dashboard.jsp" />" target="bottompane" class="active" onclick="selectMenu(this.parentNode)"><fmt:message key="topmenu.home" /></a></li>
               <mm:haspage page="/editors/workflow/">
                  <li><a href="<mm:url page="workflow/index.jsp" />" target="bottompane" onclick="selectMenu(this.parentNode)"><fmt:message key="topmenu.workflow" /></a></li>
               </mm:haspage>
               <mm:haspage page="/editors/site/">
                  <li><a href="<mm:url page="site/index.jsp" />" target="bottompane" onclick="selectMenu(this.parentNode)"><fmt:message key="topmenu.site" /></a></li>
               </mm:haspage>
               <mm:haspage page="/editors/repository/">
                  <li><a href="<mm:url page="repository/index.jsp" />" target="bottompane" onclick="selectMenu(this.parentNode)"><fmt:message key="topmenu.repository" /></a></li>
               </mm:haspage>
               <mm:haspage page="/editors/taskmanagement/">
                  <li><a href="<mm:url page="taskmanagement/index.jsp" />" target="bottompane" onclick="selectMenu(this.parentNode)"><fmt:message key="topmenu.taskmanagement" /></a></li>
               </mm:haspage>    
                  <li><a href="<mm:url page="usermanagement/index.jsp" />" target="bottompane" onclick="selectMenu(this.parentNode)"><fmt:message key="topmenu.profile" /></a></li>
               <mm:haspage page="/editors/modules/">
                  <mm:hasrank minvalue="siteadmin">
                     <li><a href="<mm:url page="modules/index.jsp" />" target="bottompane" onclick="selectMenu(this.parentNode)"><fmt:message key="topmenu.modules" /></a></li>
                  </mm:hasrank>
               </mm:haspage>
               <mm:haspage page="/editors/admin/">
                  <mm:hasrank minvalue="administrator">
                     <li><a href="<mm:url page="admin/index.jsp" />" target="bottompane" onclick="selectMenu(this.parentNode)"><fmt:message key="topmenu.admin" /></a></li>
                  </mm:hasrank>
               </mm:haspage>
            </ul>

            <div style="float:right;"></div>
         </div>
      </mm:cloud>
   </body>
</html:html>
</mm:content>