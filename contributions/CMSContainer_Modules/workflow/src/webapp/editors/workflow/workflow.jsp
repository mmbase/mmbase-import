<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="workflow.title">
	<script src="workflow.js" type="text/javascript"></script>
	<link href="../css/workflow.css" rel="stylesheet" type="text/css" />
</cmscedit:head>
<body>
<div id="left">
	<cmscedit:sideblock title="workflow.status.header">
		<mm:import externid="statusInfo" required="true" />

		<table class="centerData">
			<thead>
				<tr>
					<th></th>
					<th><fmt:message key="workflow.status.draft" /></th>
					<th><fmt:message key="workflow.status.finished" /></th>
               <c:if test="${acceptedEnabled}">
               <th><fmt:message key="workflow.status.approved" /></th>
               </c:if>
               <th><fmt:message key="workflow.status.published" /></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td><fmt:message key="workflow.status.content" /></td>
					<td><a href="ContentWorkflowAction.do?status=draft">${statusInfo.contentDraft}</a></td>
					<td><a href="ContentWorkflowAction.do?status=finished">${statusInfo.contentFinished}</a></td>
               <c:if test="${acceptedEnabled}">
               <td><a href="ContentWorkflowAction.do?status=approved">${statusInfo.contentApproved}</a></td>
               </c:if>
               <td><a href="ContentWorkflowAction.do?status=published">${statusInfo.contentPublished}</a></td>
				</tr>
				<tr>
					<td><fmt:message key="workflow.status.page" /></td>
					<td><a href="PageWorkflowAction.do?status=draft">${statusInfo.pageDraft}</a></td>
					<td><a href="PageWorkflowAction.do?status=finished">${statusInfo.pageFinished}</a></td>
               <c:if test="${acceptedEnabled}">
               <td><a href="PageWorkflowAction.do?status=approved">${statusInfo.pageApproved}</a></td>
               </c:if>
               <td><a href="PageWorkflowAction.do?status=published">${statusInfo.pagePublished}</a></td>
				</tr>
            <tr>
               <td><fmt:message key="workflow.status.link" /></td>
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
<mm:import externid="results" jspvar="nodeList" vartype="List" />

<div class="content">
	<div class="tabs">
		<c:choose>
			<c:when test="${status == 'draft' }">
				<div class="tab_active">
			</c:when>
			<c:otherwise>
				<div class="tab">
			</c:otherwise>
		</c:choose>
		<div class="body">
			<div>
			<a href="#" onClick="selectTab('draft');"><fmt:message key="workflow.tab.draft" /></a>
			</div>
		</div>
	</div>

	<c:choose>
		<c:when test="${status == 'finished' }">
			<div class="tab_active">
		</c:when>
		<c:otherwise>
			<div class="tab">
		</c:otherwise>
	</c:choose>
	<div class="body">
		<div>
			<a href="#" onClick="selectTab('finished');"><fmt:message key="workflow.tab.finished" /></a>
		</div>
	</div>
</div>

<c:if test="${acceptedEnabled}">
	<c:choose>
		<c:when test="${status == 'approved' }">
        <div class="tab_active">
        </c:when>
        <c:otherwise>
        <div class="tab">
        </c:otherwise>
	</c:choose>
		<div class="body">
			<div>
				<a href="#" onClick="selectTab('approved');"><fmt:message key="workflow.tab.approved" /></a>
			</div>
		</div>
		</div>
</c:if>
<c:choose>
	<c:when test="${status == 'published' }">
		<div class="tab_active">
	</c:when>
    <c:otherwise>
    <div class="tab">
    </c:otherwise>
</c:choose>
	<div class="body">
		<div>
			<a href="#" onClick="selectTab('published');"><fmt:message key="workflow.tab.published" /></a>
		</div>
	</div>
	</div>
	</div>
	</div>
	<div class="editor" style="width: a">
		<c:if test="${not empty errors}">
		<mm:import externid="errors" vartype="List" />
			<div class="messagebox_red">
			<div class="box">
				<div class="top"><div></div></div>
					<div class="body">
						<p><fmt:message key="workflow.publish.failed" /></p>
							<table>
								<thead>
									<tr>
										<th><fmt:message key="workflow.content.type" /></th>
										<th><fmt:message key="workflow.title" /></th>
										<th><fmt:message key="workflow.lastmodifier" /></th>
										<th><fmt:message key="workflow.lastmodifieddate" /></th>
									</tr>
								</thead>
								<tbody>
									<mm:listnodes referid="errors">
									<tr>
									   <td><mm:nodeinfo type="guitype" /></td>
									   <td><mm:hasfield name="title"> <mm:field name="title"/> </mm:hasfield>
										   <mm:hasfield name="name"> <mm:field name="name"/> </mm:hasfield></td>
									   <td><mm:hasfield name="lastmodifier"> <mm:field name="lastmodifier" /> </mm:hasfield></td>
									   <td><mm:hasfield name="lastmodifieddate"> <mm:field name="lastmodifieddate"><cmsc:dateformat displaytime="true" /></mm:field> </mm:hasfield></td>
									</tr>
									</mm:listnodes>
								</tbody>
							</table>
					</div>
					<div class="bottom"><div></div></div>
				</div>
			</div>
		</c:if>

		<div class="ruler_green"><div>
		<c:if test="${workflowType == 'content' }">
			<fmt:message key="workflow.title.content" />
		</c:if>
		<c:if test="${workflowType == 'link' }">
		    <fmt:message key="workflow.title.link" />
		</c:if>
		<c:if test="${workflowType == 'page' }">
		    <fmt:message key="workflow.title.page" />
		</c:if>
		</div></div>

	<div class="body" style="display: none;" id="workflow-wait">
		<fmt:message key="workflow.wait" />
	</div>
	<div class="body" id="workflow-canvas">
	<c:set var="orderby" value="${param.orderby}"/>
     <form action='?' method="post" onSubmit="return submitValid(false);">
        <input type="hidden" name="orderby" value="${orderby}"/>
        <input type="hidden" name="status" value="${status}" />
		<input type="hidden" name="laststatus"/>
		<c:set var="lastvalue" value="<%=request.getAttribute("laststatus")%>"/>

<c:set var="resultsPerPage" value="50"/>
<c:set var="offset" value="${param.offset}"/>
<c:set var="listSize">${fn:length(nodeList)}</c:set>
<mm:list referid="results" max="${resultsPerPage}" offset="${offset*resultsPerPage}">
  <mm:first>
	<%@include file="../pages.jsp" %>
      <table>
        <thead>
           <tr>
           <th style="width: 20px;"></th>
           <th style="width: 100px;" nowrap="true">
               <a href="#"
			   <c:if test="${orderby==null ||orderby=='' || orderby!='title'}">
					onClick="selectTab('${status}', 'title', 'false');"
			   </c:if>
			   <c:if test="${orderby=='title'}">
					<c:choose>
						<c:when test="${lastvalue}"> class="sortup"</c:when>
						<c:otherwise> class="sortdown"</c:otherwise>
					</c:choose>
					onClick="selectTab('${status}', 'title', '${lastvalue}');"
			   </c:if>
			   >
                  <fmt:message key="workflow.title" />
               </a>
           </th>
           <th>
               <a href="#"
				<c:if test="${orderby==null || orderby=='' || orderby!='remark'}">
					onClick="selectTab('${status}','remark','false');"
			   </c:if>
			   <c:if test="${orderby=='remark'}">
					<c:choose>
						<c:when test="${lastvalue}"> class="sortup"</c:when>
						<c:otherwise> class="sortdown"</c:otherwise>
					</c:choose>
					onClick="selectTab('${status}', 'remark', '${lastvalue}');"
			   </c:if>
			   >
                  <fmt:message key="workflow.remark" />
               </a>
           </th>
           <th style="width: 140px;">
				<a href="#"
				<c:if test="${orderby==null || orderby=='' || orderby!='lastmodifier'}">
					onClick="selectTab('${status}','lastmodifier','false');"
			   </c:if>
			   <c:if test="${orderby=='lastmodifier'}">
					<c:choose>
						<c:when test="${lastvalue}"> class="sortup"</c:when>
						<c:otherwise> class="sortdown"</c:otherwise>
					</c:choose>
					onClick="selectTab('${status}', 'lastmodifier', '${lastvalue}');"
			   </c:if>
			   >
                  <fmt:message key="workflow.lastmodifier" />
               </a>
           </th>
		<c:if test="${workflowType == 'page' || workflowType == 'content' }">
           <th style="width: 140px;">
               <a href="#"
               <c:if test="${orderby==null || orderby=='' || orderby=='undefined'}">
			   		onClick="selectTab('${status}','lastmodifieddate','true');" class="sortup"
			   </c:if>
			   <c:if test="${orderby=='lastmodifieddate'}">
			   		<c:choose>
						<c:when test="${lastvalue}"> class="sortup"</c:when>
						<c:otherwise> class="sortdown"</c:otherwise>
					</c:choose>
					onClick="selectTab('${status}','lastmodifieddate','${lastvalue}');"
			   </c:if>
			   <c:if test="${orderby!=null && orderby!='' && orderby!='lastmodifieddate'}">
			   		onClick="selectTab('${status}','lastmodifieddate','false');"
			   </c:if>
			   >
                  <fmt:message key="workflow.lastmodifieddate" />
               </a>
           </th>
		</c:if>
		<c:if test="${workflowType == 'content' }">
           <th style="width: 140px;">
               <a href="#"
			   <c:if test="${orderby==null || orderby=='' || orderby!='contentchannel'}">
			   		onClick="selectTab('${status}','contentchannel','false');"
			   </c:if>
			   <c:if test="${orderby=='contentchannel'}">
			   		<c:choose>
						<c:when test="${lastvalue}"> class="sortup"</c:when>
						<c:otherwise> class="sortdown"</c:otherwise>
					</c:choose>
					onClick="selectTab('${status}', 'contentchannel', '${lastvalue}');"
			   </c:if>
			   >
                  <fmt:message key="workflow.contentchannel" />
               </a>
           </th>
		</c:if>
           </tr>
        </thead>
        <tbody class="hover">
  </mm:first>
  <tr <mm:even inverse="true">class="swap"</mm:even>>
         <td>
            <mm:field name="workflowitem.number" id="workflowNumber" write="false"/>
            <input type="checkbox" name="check_${workflowNumber}" value="on" />
         </td>

		<c:if test="${workflowType == 'content' }">
			<c:set var="type" value="contentelement"/>
			<c:set var="field" value="title"/>
			<c:set var="returnAction" value="ContentWorkflowAction.do"/>
		</c:if>
		<c:if test="${workflowType == 'link' }">
			<c:set var="type" value="contentchannel"/>
			<c:set var="field" value="name"/>
			<c:set var="returnAction" value="LinkWorkflowAction.do"/>
		</c:if>
		<c:if test="${workflowType == 'page' }">
			<c:set var="type" value="page"/>
			<c:set var="field" value="title"/>
			<c:set var="returnAction" value="PageWorkflowAction.do"/>
		</c:if>

         <td style="white-space: nowrap;">

	        <mm:field name="${type}.number" jspvar="number" write="false"/>
			<mm:url page="../WizardInitAction.do" jspvar="url" write="false">
		       <mm:param name="objectnumber" value="${number}"/>
		       <mm:param name="returnurl" value="workflow/${returnAction}?status=${param.status}"/>
			</mm:url>
            <a href="${url}"><img src="../gfx/icons/edit.png" align="top" alt="<fmt:message key="workflow.editelement"/>"  title="<fmt:message key="workflow.editelement"/>"/></a>
            <mm:field jspvar="value" write="false" name="${type}.${field}" />
			<c:if test="${fn:length(value) > 50}">
				<c:set var="value">${fn:substring(value,0,49)}...</c:set>
			</c:if>
			${value}
         </td>
         <td>
            <a href="javascript:editRemark(${workflowNumber}, '<mm:field name="workflowitem.remark" escape="none"/>')"><img src="../gfx/icons/edit2.png" align="top" alt="<fmt:message key="workflow.editremark"/>" title="<fmt:message key="workflow.editremark"/>"/></a>
            <mm:field name="workflowitem.remark" escape="none"/>
         </td>
         <td style="white-space: nowrap;">
            <mm:field name="workflowitem.lastmodifier" />
         </td>
	<c:if test="${workflowType == 'page' || workflowType == 'content' }">
         <td style="white-space: nowrap;">
            <mm:field name="${type}.lastmodifieddate"><cmsc:dateformat displaytime="true" /></mm:field>
         </td>
	</c:if>
	<c:if test="${workflowType == 'content' }">
		<td style="white-space: nowrap;">
            <mm:field name="contentchannel.name"/>
		</td>
	</c:if>
  </tr>
  <mm:last>
        </tbody>
     </table>
      <%@include file="../pages.jsp" %>
   </mm:last>
</mm:list>
<c:set var="remark">
	<fmt:message key="workflow.action.reject.remark"/>
</c:set>
&nbsp;&nbsp;&nbsp; <input type="checkbox" name="checkAll" onClick="checkAllElement(this, '')"/>
          <input type="hidden" name="actionvalue" value=""/>
             <input type='hidden' id="remark" name="remark" value="[unchanged-item]" />
             <br/>
          <c:if test="${status == 'draft' }">
             <input name="action" value="<fmt:message key="workflow.action.finish" />" onclick="return setActionValue('finish')" type="submit"/>
          </c:if>
          <c:if test="${status == 'finished' }">
             <input name="action" value="<fmt:message key="workflow.action.reject" />" onclick="return setActionValue('reject','','${remark}')" type="submit"/>
             <c:if test="${acceptedEnabled}">
             <input name="action" value="<fmt:message key="workflow.action.accept" />" onclick="return setActionValue('accept')" type="submit"/>
             </c:if>
             <input name="action" value="<fmt:message key="workflow.action.publish" />" onclick="return setActionValue('publish')" type="submit"/>
          </c:if>
          <c:if test="${status == 'approved' }">
             <input name="action" value="<fmt:message key="workflow.action.reject" />" onclick="return setActionValue('reject','','${remark}')" type="submit"/>
             <input name="action" value="<fmt:message key="workflow.action.publish" />" onclick="return setActionValue('publish')" type="submit"/>
          </c:if>
          <c:if test="${status == 'published' }">
             <input name="action" value="<fmt:message key="workflow.action.reject" />" onclick="return setActionValue('reject','','${remark}')" type="submit"/>
          </c:if>
       </form>

   </div>
   </div>
</mm:cloud>
</div>
</body>
</html:html>
</mm:content>
