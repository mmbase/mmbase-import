<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@include file="globals.jsp"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="tasks.title" >
<script src="../taskmanagement/task.js" language="JavaScript" type="text/javascript"></script>
</cmscedit:head>
<body>
   <c:choose>
      <c:when test="${param.taskShowType eq 'task.showtype.assignedtome'}">
         <c:set var="tmpRole">assignedrel</c:set>
      </c:when>
      <c:when test="${param.taskShowType eq 'task.showtype.createdbyme'}">
         <c:set var="tmpRole">creatorrel</c:set>
      </c:when>
      <c:otherwise>
         <c:set var="tmpRole"></c:set>
      </c:otherwise>
   </c:choose>
   <c:if test="${param.showFinishedTask eq 'yes'}">
      <c:set var="status">task.status.done</c:set>
   </c:if>

   <mm:cloud jspvar="cloud" loginpage="../login.jsp">

	<div class="content">
	   <div class="tabs">
	      <div class="tab_active">
	         <div class="body">
	            <div>
	               <a href="#"><fmt:message key="tasks.title" /></a>
	            </div>
	         </div>
	      </div>
	   </div>
	</div>

   <mm:cloudinfo type="user" id="cloudusername" write="false" />
	<mm:listnodescontainer type="user">
		<mm:constraint field="user.username" operator="EQUAL" referid="cloudusername" />
		<mm:maxnumber value="10" />
		<mm:listnodes>
			<mm:relatednodescontainer type="task" role="${tmpRole}" searchdirs="source">
				<mm:present referid="status">
					<mm:constraint field="status" value="${status}" operator="EQUAL" />
				</mm:present>
				<mm:notpresent referid="status">
					<mm:constraint field="status" value="task.status.done" operator="EQUAL" inverse="true" />
				</mm:notpresent>
				<mm:relatednodes comparator="com.finalist.cmsc.tasks.TaskUrgencyComparator" max="${dashboardTaskSize}" id="resultList" />
			</mm:relatednodescontainer>
		</mm:listnodes>
	</mm:listnodescontainer>

   <div class="editor">
   <div class="body">

		<p style="font-size: 12px; padding-left: 10px;"><html:messages id="createSuccess" message="true" bundle="TASKS"><bean:write name="createSuccess" /></html:messages></p>

      <c:url value="/editors/taskmanagement/MassDeleteTaskAction.do" var="taskFormAction" />
		<form action="${taskFormAction}" name="taskForm">
      <mm:hasrank minvalue="basic user">
			<c:if test="${fn:length(resultList)>1}">
				<input type="submit" class="button" value="<fmt:message key="task.delete.massdelete"/>" onclick="massDeleteTask('<fmt:message key="task.massdeleteconfirm" />');return false;" />
			</c:if>
		</mm:hasrank> 
		<%@ include file="tasklist_table.jspf"%> 
      <mm:hasrank minvalue="basic user">
			<c:if test="${fn:length(resultList)>1}">
				<input type="submit" class="button" value="<fmt:message key="task.delete.massdelete"/>" onclick="massDeleteTask('<fmt:message key="task.massdeleteconfirm" />');return false;" />
			</c:if>
		</mm:hasrank>
      </form>

		<html:form action="/editors/taskmanagement/showTaskAction">
			<html:select property="taskShowType" onchange="this.form.submit();">
				<html:option value="task.showtype.alltasks" bundle="TASKS" key="task.showtype.alltasks" />
				<html:option value="task.showtype.assignedtome" bundle="TASKS" key="task.showtype.assignedtome" />
				<html:option value="task.showtype.createdbyme" bundle="TASKS" key="task.showtype.createdbyme" />
			</html:select>
			<html:checkbox property="showFinishedTask" value="yes" onclick="this.form.submit();" />
			<fmt:message key="task.show.finished" />
		</html:form>

	</div>
	</div>

	</mm:cloud>
</body>
</html:html>
</mm:content>
