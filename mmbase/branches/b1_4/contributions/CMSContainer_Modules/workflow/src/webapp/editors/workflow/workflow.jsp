<%--@elvariable id="status" type="String"--%>

<%@ page import="com.finalist.cmsc.workflow.forms.Utils" %>
<%@ page import="static com.finalist.cmsc.workflow.forms.Utils.tabClass" %>
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="workflow.module.title">
   <script src="workflow.js" type="text/javascript"></script>
   <link href="../css/workflow.css" rel="stylesheet" type="text/css"/>
   <script type="text/javascript">
      function info(objectNumber) {
         openPopupWindow("info", 500, 500, "../repository/showitem.jsp?objectnumber=" + objectNumber);
      }
   </script>
</cmscedit:head>

<body>

<div id="left">
   <cmscedit:sideblock title="workflow.status.header">
      <mm:import externid="statusInfo" required="true"/>

      <table class="centerData">
         <thead>
            <tr>
               <th></th>
               <th><fmt:message key="workflow.status.draft"/></th>
               <th><fmt:message key="workflow.status.finished"/></th>
               <c:if test="${acceptedEnabled}">
                  <th><fmt:message key="workflow.status.approved"/></th>
               </c:if>
               <th><fmt:message key="workflow.status.published"/></th>
            </tr>
         </thead>
         <tbody>
            <tr>
               <td><fmt:message key="workflow.status.content"/></td>
               <td><a href="ContentWorkflowAction.do?status=draft">${statusInfo.contentDraft}</a></td>
               <td><a href="ContentWorkflowAction.do?status=finished">${statusInfo.contentFinished}</a></td>
               <c:if test="${acceptedEnabled}">
                  <td><a href="ContentWorkflowAction.do?status=approved">${statusInfo.contentApproved}</a></td>
               </c:if>
               <td><a href="ContentWorkflowAction.do?status=published">${statusInfo.contentPublished}</a></td>
            </tr>
            <tr>
               <td><fmt:message key="workflow.status.page"/></td>
               <td><a href="PageWorkflowAction.do?status=draft">${statusInfo.pageDraft}</a></td>
               <td><a href="PageWorkflowAction.do?status=finished">${statusInfo.pageFinished}</a></td>
               <c:if test="${acceptedEnabled}">
                  <td><a href="PageWorkflowAction.do?status=approved">${statusInfo.pageApproved}</a></td>
               </c:if>
               <td><a href="PageWorkflowAction.do?status=published">${statusInfo.pagePublished}</a></td>
            </tr>
            <tr>
               <td><fmt:message key="workflow.status.link"/></td>
               <td></td>
               <td><a href="LinkWorkflowAction.do?status=finished">${statusInfo.linkFinished}</a></td>
               <c:if test="${acceptedEnabled}">
                  <td><a href="LinkWorkflowAction.do?status=approved">${statusInfo.linkApproved}</a></td>
               </c:if>
               <td><a href="LinkWorkflowAction.do?status=published">${statusInfo.linkPublished}</a></td>
            </tr>
         </tbody>
      </table>
   </cmscedit:sideblock>
</div>

<div id="content">
<mm:cloud jspvar="cloud" loginpage="login.jsp">
<mm:import externid="status">draft</mm:import>
<mm:import externid="results" jspvar="nodeList" vartype="List"/>

<div class="content">
   <div class="tabs" id="${status}">
      <div class="<%=tabClass(pageContext,"draft")%>">
         <div class="body">
            <div>
               <a href="#" onclick="selectTab('draft');"><fmt:message key="workflow.tab.draft"/></a>
            </div>
         </div>
      </div>

      <div class="<%=tabClass(pageContext,"finished")%>">
         <div class="body">
            <div>
               <a href="#" onclick="selectTab('finished');"><fmt:message key="workflow.tab.finished"/></a>
            </div>
         </div>
      </div>

      <c:if test="${acceptedEnabled}">
         <div class="<%=tabClass(pageContext,"approved")%>">
            <div class="body">
               <div>
                  <a href="#" onclick="selectTab('approved');"><fmt:message key="workflow.tab.approved"/></a>
               </div>
            </div>
         </div>
      </c:if>
      <div class="<%=tabClass(pageContext,"published")%>">
         <div class="body">
            <div>
               <a href="#" onclick="selectTab('published');"><fmt:message key="workflow.tab.published"/></a>
            </div>
         </div>
      </div>
   </div>
</div>

<div class="editor">
<c:if test="${not empty errors}">
   <mm:import externid="errors" vartype="List"/>

   <div class="messagebox_red">
      <div class="box">
         <div class="top">
            <div></div>
         </div>
         <div class="body">
            <p><fmt:message key="workflow.publish.failed"/></p>
            <table>
               <thead>
                  <tr>
                     <th><fmt:message key="workflow.content.type"/></th>
                     <th><fmt:message key="workflow.title"/></th>
                     <th><fmt:message key="workflow.lastmodifier"/></th>
                     <th><fmt:message key="workflow.lastmodifieddate"/></th>
                  </tr>
               </thead>
               <tbody>
                  <mm:listnodes referid="errors">
                     <tr>
                        <td><mm:nodeinfo type="guitype"/></td>
                        <td>
                           <mm:hasfield name="title"> <mm:field name="title"/> </mm:hasfield>
                           <mm:hasfield name="name"> <mm:field name="name"/> </mm:hasfield>
                        </td>
                        <td>
                           <mm:hasfield name="lastmodifier"> <mm:field name="lastmodifier"/> </mm:hasfield>
                        </td>
                        <td>
                           <mm:hasfield name="lastmodifieddate">
                              <mm:field name="lastmodifieddate">
                                 <cmsc:dateformat displaytime="true"/></mm:field>
                           </mm:hasfield>
                        </td>
                     </tr>
                  </mm:listnodes>
               </tbody>
            </table>
         </div>
         <div class="bottom">
            <div></div>
         </div>
      </div>
   </div>
</c:if>

<div class="ruler_green">
   <div>
      <c:if test="${workflowType == 'content' }">
         <fmt:message key="workflow.title.content"/>
      </c:if>
      <c:if test="${workflowType == 'link' }">
         <fmt:message key="workflow.title.link"/>
      </c:if>
      <c:if test="${workflowType == 'page' }">
         <fmt:message key="workflow.title.page"/>
      </c:if>
   </div>
</div>

<div class="body" style="display: none;" id="workflow-wait">
   <fmt:message key="workflow.wait"/>
</div>
<div class="body" id="workflow-canvas">
   <c:set var="orderby" value="${param.orderby}"/>
   <form action='?' method="post" onsubmit="return submitValid(false);">
      <input type="hidden" name="orderby" value="${orderby}"/>
      <input type="hidden" name="status" value="${status}"/>
      <input type="hidden" name="laststatus"/>
      <c:set var="lastvalue" value='<%=request.getAttribute("laststatus")%>'/>

      <c:set var="resultsPerPage" value="50"/>
      <c:set var="offset" value="${param.offset}"/>
      <c:set var="listSize">${fn:length(nodeList)}</c:set>

      <c:if test="${fn:length(results) > 0}">
         <%@ include file="../pages.jsp" %>
         <%@ include file="workflow_list_table_fragment.jsp" %>
         <%@ include file="../pages.jsp" %>
      </c:if>

      <c:set var="remark">
         <fmt:message key="workflow.action.reject.remark"/>
      </c:set>
      &nbsp;&nbsp;&nbsp; <input type="checkbox" name="checkAll" onclick="checkAllElement(this, '')"/> <fmt:message key="workflow.select_all"/>
      <input type="hidden" name="actionvalue" value=""/>
      <input type='hidden' id="remark" name="remark" value="[unchanged-item]"/>
      <br/>
      <c:if test="${status == 'draft' }">
         <input name="action" value="<fmt:message key="workflow.action.finish" />"
                onclick="return setActionValue('finish')"
                type="submit"/>
      </c:if>
      <c:if test="${status == 'finished' }">
         <input name="action" value="<fmt:message key="workflow.action.reject" />"
                onclick="return setActionValue('reject','','${remark}')" type="submit"/>
         <c:if test="${acceptedEnabled}">
            <input name="action" value="<fmt:message key="workflow.action.accept" />"
                   onclick="return setActionValue('accept')" type="submit"/>
         </c:if>
         <input name="action" value="<fmt:message key="workflow.action.publish" />"
                onclick="return setActionValue('publish')" type="submit"/>
      </c:if>
      <c:if test="${status == 'approved' }">
         <input name="action" value="<fmt:message key="workflow.action.reject" />"
                onclick="return setActionValue('reject','','${remark}')" type="submit"/>
         <input name="action" value="<fmt:message key="workflow.action.publish" />"
                onclick="return setActionValue('publish')" type="submit"/>
      </c:if>
      <c:if test="${status == 'published' }">
         <input name="action" value="<fmt:message key="workflow.action.reject" />"
                onclick="return setActionValue('reject','','${remark}')" type="submit"/>
      </c:if>
   </form>

</div>
</div>
</mm:cloud>
</div>
</body>
</html:html>
</mm:content>
