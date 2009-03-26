<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@include file="globals.jsp"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="tasks.title" />
<body>
   <c:choose>
      <c:when test="${requestScope.taskShowType eq 'task.showtype.assignedtome'}">
         <c:set var="tmpRole">assignedrel</c:set>
      </c:when>
      <c:when test="${requestScope.taskShowType eq 'task.showtype.createdbyme'}">
         <c:set var="tmpRole">creatorrel</c:set>
      </c:when>
      <c:otherwise>
         <c:set var="tmpRole"></c:set>
      </c:otherwise>
   </c:choose>

   <mm:cloud jspvar="cloud" loginpage="../login.jsp">
      <cmscedit:contentblock title="tasks.title" titleClass="content_block_pink" bodyClass="body_table">

         <mm:cloudinfo type="user" id="cloudusername" write="false" />
         <mm:listnodescontainer type="user">
            <mm:constraint field="user.username" operator="EQUAL" referid="cloudusername" />
            <mm:maxnumber value="10" />
            <p style="font-size:12px; padding-left:10px;">
               <html:messages id="createSuccess" message="true" bundle="TASKS">
                  <bean:write name="createSuccess"/>
               </html:messages>
            </p>
            <mm:listnodes>
               <mm:relatednodescontainer type="task" role="${tmpRole}" searchdirs="source">
                  <%@ include file="tasklist_table.jspf"%> 
               </mm:relatednodescontainer>
            </mm:listnodes>
         </mm:listnodescontainer>

         <html:form action="/editors/taskmanagement/showTaskAction">
               <html:select property="taskShowType" value="${requestScope.taskShowType}">
                  <html:option value="task.showtype.alltasks" bundle="TASKS" key="task.showtype.alltasks"/>
                  <html:option value="task.showtype.assignedtome" bundle="TASKS" key="task.showtype.assignedtome"/>
                  <html:option value="task.showtype.createdbyme" bundle="TASKS" key="task.showtype.createdbyme"/>
               </html:select>&nbsp;&nbsp;
               <html:submit><bean:message bundle="TASKS" key="task.showtype.submit"/></html:submit>
         </html:form>

      </cmscedit:contentblock>
   </mm:cloud>
</body>
</html:html>
</mm:content>
