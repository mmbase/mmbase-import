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
                  <table>
                     <thead>
                        <tr>
                          <th><fmt:message key="task.created" /></th>
                          <th><fmt:message key="task.deadline" /></th>
                          <th><fmt:message key="task.title" /></th>
                          <th><fmt:message key="task.status" /></th>
                          <th><fmt:message key="task.contenttitle" /></th>
                          <th><fmt:message key="task.nodetype" /></th>
                          <th><fmt:message key="task.description" /></th>
                        </tr>
                     </thead>
                     <tbody class="hover">
                        <c:set var="taskList" value="" /><c:set var="isSwapClass" value="true"/>
                        <mm:relatednodes comparator="com.finalist.cmsc.tasks.TaskUrgencyComparator">
                           <c:set var="taskId"><mm:field name="number"/></c:set>
                           <c:if test="${not fn:contains(taskList, taskId)}">
                           <tr <c:if test="${isSwapClass}">class="swap"</c:if>>
                              <td><mm:field name="creationdate" id="created"><mm:time time="${created}" format="d/M/yyyy HH:mm" /></mm:field></td>
                              <td><mm:field name="deadline" id="deadl"><mm:time time="${deadl}" format="d/M/yyyy HH:mm"/></mm:field></td>
                              <td><mm:field name="title"/></td>
                              <c:set var="elementtitel"><mm:field name="title"/></c:set>
                              <c:set var="elementnumber"/>
                              <c:set var="elementtype"/>
                              <mm:relatednodescontainer type="contentelement" role="taskrel" searchdirs="destination">
                                 <mm:maxnumber value="1" />
                                 <mm:relatednodes>
                                    <c:set var="elementtitel"><mm:field name="title"/></c:set>
                                    <c:set var="elementnumber"><mm:field name="number"/></c:set>
                                    <c:set var="elementtype"><mm:field name="number"><mm:isnotempty><mm:nodeinfo type="guitype"/></mm:isnotempty></mm:field></c:set>
                                 </mm:relatednodes>
                              </mm:relatednodescontainer>
                              <c:set var="status"><mm:field name="status" /></c:set>
                              <td><fmt:message key="${status}" /></td>
                              <td>
                                 <c:choose>
                                    <c:when test="${empty elementnumber}">
                                       <fmt:message key="task.noelement"/>
                                    </c:when>
                                    <c:otherwise>
                                       <mm:hasrank minvalue="basic user">
                                          <mm:url page="/editors/taskmanagement/tasklist.jsp" id="returnTasklist" write="false" />
                                          <a href="<mm:url page="../WizardInitAction.do">
                                             <mm:param name="objectnumber" value="${elementnumber}"/>
                                             <mm:param name="returnurl" value="${returnTasklist}"/>
                                             </mm:url>" target="rightpane"><img src="../gfx/icons/edit.png" align="top" alt="<fmt:message key="task.editelement"/>" title="<fmt:message key="task.editelement"/>"/></a> ${elementtitel}
                                       </mm:hasrank>
                                    </c:otherwise>
                                 </c:choose>
                              </td>
                              <td>${elementtype}</td>
                              <td>
                                 <mm:hasrank minvalue="basic user">
                                    <mm:field name="number" jspvar="number" write="false"/>
                                    <mm:url page="/editors/taskmanagement/tasklist.jsp" id="returnTaskedit" write="false" />
                                    <a href="<mm:url page="/editors/WizardInitAction.do">
                                       <mm:param name="objectnumber" value="${number}"/>
                                       <mm:param name="contenttype" value="task"/>
                                       <mm:param name="returnurl" value="${returnTaskedit}"/>
                                       </mm:url>" target="rightpane"><img src="../gfx/icons/edit2.png" align="top" alt="<fmt:message key="task.edit"/>" title="<fmt:message key="task.edit"/>"/></a> <mm:field name="description" />
                                 </mm:hasrank>
                              </td>
                           </tr>
                           <c:set var="taskList">${taskList},${taskId}</c:set>
                           <c:choose>
                              <c:when test="${isSwapClass eq 'false'}"><c:set var="isSwapClass" value="true"/></c:when>
                              <c:when test="${isSwapClass eq 'true'}"><c:set var="isSwapClass" value="false"/></c:when>
                           </c:choose>
                           </c:if>
                        </mm:relatednodes>
                     </tbody>
                  </table>
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
