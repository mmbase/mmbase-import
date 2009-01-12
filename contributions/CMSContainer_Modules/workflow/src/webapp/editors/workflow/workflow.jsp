<%--@elvariable id="status" type="String"--%>

<%@ page import="com.finalist.cmsc.workflow.forms.Utils"%>
<%@ page import="static com.finalist.cmsc.workflow.forms.Utils.tabClass"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
	<html:html xhtml="true">
	<cmscedit:head title="workflow.module.title">
      <script src="../../js/prototype.js" type="text/javascript"></script>
		<script src="workflow.js" type="text/javascript"></script>
		<link href="../css/workflow.css" rel="stylesheet" type="text/css" />
      <c:url value="/editors/workflow/WorkflowTreeStatusAction.do" var="treeStatusAction"/>
		<script type="text/javascript">
      function info(objectNumber) {
         openPopupWindow("info", 500, 500, "../repository/showitem.jsp?objectnumber=" + objectNumber);
      }
      var treeHandler = {
          toggle: function (oItem) { 
		      var parentTableId = oItem.id.replace('-plus', '');
		      var originImgSrc = oItem.src;
		      if(originImgSrc.toLowerCase().indexOf('plus.png')>=0){
		         oItem.src = originImgSrc.replace('plus', 'minus');
		         document.getElementById(parentTableId + '-cont').style.display = 'block';
		      }else{
		         oItem.src = originImgSrc.replace('minus', 'plus');
		         document.getElementById(parentTableId + '-cont').style.display = 'none';
		      }
		      new Ajax.Request(
		              "${treeStatusAction}",
		              {
		                 method:'put',
		                 parameters: 'treeItem=' + parentTableId.replace('tree-', ''),
		                 asynchronous : true
		              }
		           );
		      }
      };
      
   </script>
	</cmscedit:head>

	<body>
	<div id="left"><cmscedit:sideblock title="workflow.status.header">
		<mm:import externid="statusInfo" required="true" />
      <c:set var="treeStatus" value="${sessionScope.workflowTreeStatus}" />

   <div id="tree">
      <div id="tree-head">
		<table class="centerData">
			<tbody>
				<tr>
				   <td class="indent" />
				   <td class="indent" />
					<td class="indent" />
					<td class="leftData"></td>
					<th><fmt:message key="workflow.status.draft" /></th>
					<th><fmt:message key="workflow.status.finished" /></th>
					<c:if test="${acceptedEnabled}">
						<th><fmt:message key="workflow.status.approved" /></th>
					</c:if>
					<th><fmt:message key="workflow.status.published" /></th>
				</tr>
			</tbody>
		</table>
		</div>
		<div id="tree-allcontent">
		<table class="centerData">
			<tbody>
				<tr>
					<td class="indent"><img id="tree-allcontent-plus"
						onclick="treeHandler.toggle(this);"
						<c:if test="${treeStatus.allcontent eq 1}">src="../utils/ajaxtree/images/minus.png"</c:if>
						<c:if test="${treeStatus.allcontent eq 0}">src="../utils/ajaxtree/images/plus.png"</c:if> /></td>
					<td class="leftData"><fmt:message
						key="workflow.status.allcontent" /></td>
					<td class="indent" />
					<td class="indent" />
					<td><a href="AllcontentWorkflowAction.do?status=draft">${statusInfo.allcontentDraft}</a></td>
					<td><a href="AllcontentWorkflowAction.do?status=finished">${statusInfo.allcontentFinished}</a></td>
					<c:if test="${acceptedEnabled}">
						<td><a href="AllcontentWorkflowAction.do?status=approved">${statusInfo.allcontentApproved}</a></td>
					</c:if>
					<td><a href="AllcontentWorkflowAction.do?status=published">${statusInfo.allcontentPublished}</a></td>
				</tr>
			</tbody>
		</table>
		</div>
		<div id="tree-allcontent-cont" <c:if test="${treeStatus.allcontent eq 0}">style="display:none"</c:if> >
		<div id="tree-content">
		<table class="centerData">
			<tbody>
				<tr>
					<td class="indent" />
					<td class="indent"><img id="tree-content-plus"
						onclick="treeHandler.toggle(this);"
						<c:if test="${treeStatus.content eq 1}">src="../utils/ajaxtree/images/Tminus.png"</c:if>
			         <c:if test="${treeStatus.content eq 0}">src="../utils/ajaxtree/images/Tplus.png"</c:if> /></td>
					<td class="leftData"><fmt:message
						key="workflow.status.content" /></td>
					<td class="indent" />
					<td><a href="ContentWorkflowAction.do?status=draft">${statusInfo.contentDraft}</a></td>
					<td><a href="ContentWorkflowAction.do?status=finished">${statusInfo.contentFinished}</a></td>
					<c:if test="${acceptedEnabled}">
						<td><a href="ContentWorkflowAction.do?status=approved">${statusInfo.contentApproved}</a></td>
					</c:if>
					<td><a href="ContentWorkflowAction.do?status=published">${statusInfo.contentPublished}</a></td>
				</tr>
			</tbody>
		</table>
		</div>
		<div id="tree-content-cont" <c:if test="${treeStatus.content eq 0}">style="display:none"</c:if>>
		<table class="centerData">
			<tbody>
				<tr>
					<td class="indent" />
					<td class="indent"><img src="../utils/ajaxtree/images/I.png" /></td>
					<td class="indent"><img src="../utils/ajaxtree/images/T.png" /></td>
					<td class="leftData"><fmt:message
						key="workflow.status.content.article" /></td>
					<td><a
						href="ContentWorkflowAction.do?workflowNodetype=article&status=draft">${statusInfo.contentArticleDraft}</a></td>
					<td><a
						href="ContentWorkflowAction.do?workflowNodetype=article&status=finished">${statusInfo.contentArticleFinished}</a></td>
					<c:if test="${acceptedEnabled}">
						<td><a
							href="ContentWorkflowAction.do?workflowNodetype=article&status=approved">${statusInfo.contentArticleApproved}</a></td>
					</c:if>
					<td><a
						href="ContentWorkflowAction.do?workflowNodetype=article&status=published">${statusInfo.contentArticlePublished}</a></td>
				</tr>
				<tr>
					<td class="indent" />
					<td class="indent"><img src="../utils/ajaxtree/images/I.png" /></td>
					<td class="indent"><img src="../utils/ajaxtree/images/T.png" /></td>
					<td class="leftData"><fmt:message
						key="workflow.status.content.banners" /></td>
					<td><a
						href="ContentWorkflowAction.do?workflowNodetype=banners&status=draft">${statusInfo.contentBannersDraft}</a></td>
					<td><a
						href="ContentWorkflowAction.do?workflowNodetype=banners&status=finished">${statusInfo.contentBannersFinished}</a></td>
					<c:if test="${acceptedEnabled}">
						<td><a
							href="ContentWorkflowAction.do?workflowNodetype=banners&status=approved">${statusInfo.contentBannersApproved}</a></td>
					</c:if>
					<td><a
						href="ContentWorkflowAction.do?workflowNodetype=banners&status=published">${statusInfo.contentBannersPublished}</a></td>
				</tr>
				<tr>
					<td class="indent" />
					<td class="indent"><img src="../utils/ajaxtree/images/I.png" /></td>
					<td class="indent"><img src="../utils/ajaxtree/images/T.png" /></td>
					<td class="leftData"><fmt:message
						key="workflow.status.content.link" /></td>
					<td><a
						href="ContentWorkflowAction.do?workflowNodetype=link&status=draft">${statusInfo.contentLinkDraft}</a></td>
					<td><a
						href="ContentWorkflowAction.do?workflowNodetype=link&status=finished">${statusInfo.contentLinkFinished}</a></td>
					<c:if test="${acceptedEnabled}">
						<td><a
							href="ContentWorkflowAction.do?workflowNodetype=link&status=approved">${statusInfo.contentLinkApproved}</a></td>
					</c:if>
					<td><a
						href="ContentWorkflowAction.do?workflowNodetype=link&status=published">${statusInfo.contentLinkPublished}</a></td>
				</tr>
				<tr>
					<td class="indent" />
					<td class="indent"><img src="../utils/ajaxtree/images/I.png" /></td>
					<td class="indent"><img src="../utils/ajaxtree/images/L.png" /></td>
					<td class="leftData"><fmt:message
						key="workflow.status.content.faqitem" /></td>
					<td><a
						href="ContentWorkflowAction.do?workflowNodetype=faqitem&status=draft">${statusInfo.contentFaqitemDraft}</a></td>
					<td><a
						href="ContentWorkflowAction.do?workflowNodetype=faqitem&status=finished">${statusInfo.contentFaqitemFinished}</a></td>
					<c:if test="${acceptedEnabled}">
						<td><a
							href="ContentWorkflowAction.do?workflowNodetype=faqitem&status=approved">${statusInfo.contentFaqitemApproved}</a></td>
					</c:if>
					<td><a
						href="ContentWorkflowAction.do?workflowNodetype=faqitem&status=published">${statusInfo.contentFaqitemPublished}</a></td>
				</tr>
			</tbody>
		</table>
		</div>
		<div id="tree-asset">
		<table class="centerData">
			<tbody>
				<tr>
					<td class="indent" />
					<td class="indent"><img id="tree-asset-plus"
						onclick="treeHandler.toggle(this);"
						<c:if test="${treeStatus.asset eq 1}">src="../utils/ajaxtree/images/Lminus.png"</c:if>
			         <c:if test="${treeStatus.asset eq 0}">src="../utils/ajaxtree/images/Lplus.png"</c:if> /></td>
					<td class="leftData"><fmt:message key="workflow.status.asset" /></td>
					<td class="indent" />
					<td><a href="AssetWorkflowAction.do?status=draft">${statusInfo.assetDraft}</a></td>
					<td><a href="AssetWorkflowAction.do?status=finished">${statusInfo.assetFinished}</a></td>
					<c:if test="${acceptedEnabled}">
						<td><a href="AssetWorkflowAction.do?status=approved">${statusInfo.assetApproved}</a></td>
					</c:if>
					<td><a href="AssetWorkflowAction.do?status=published">${statusInfo.assetPublished}</a></td>
				</tr>
			</tbody>
		</table>
		</div>
		<div id="tree-asset-cont" <c:if test="${treeStatus.asset eq 0}">style="display:none"</c:if> >
		<table class="centerData">
			<tbody>
				<tr>
					<td class="indent" />
					<td class="indent" />
					<td class="indent"><img src="../utils/ajaxtree/images/T.png" /></td>
					<td class="leftData"><fmt:message
						key="workflow.status.asset.images" /></td>
					<td><a
						href="AssetWorkflowAction.do?workflowNodetype=images&status=draft">${statusInfo.assetImagesDraft}</a></td>
					<td><a
						href="AssetWorkflowAction.do?workflowNodetype=images&status=finished">${statusInfo.assetImagesFinished}</a></td>
					<c:if test="${acceptedEnabled}">
						<td><a
							href="AssetWorkflowAction.do?workflowNodetype=images&status=approved">${statusInfo.assetImagesApproved}</a></td>
					</c:if>
					<td><a
						href="AssetWorkflowAction.do?workflowNodetype=images&status=published">${statusInfo.assetImagesPublished}</a></td>
				</tr>
				<tr>
					<td class="indent" />
					<td class="indent" />
					<td class="indent"><img src="../utils/ajaxtree/images/T.png" /></td>
					<td class="leftData"><fmt:message
						key="workflow.status.asset.attachments" /></td>
					<td><a
						href="AssetWorkflowAction.do?workflowNodetype=attachments&status=draft">${statusInfo.assetAttachmentsDraft}</a></td>
					<td><a
						href="AssetWorkflowAction.do?workflowNodetype=attachments&status=finished">${statusInfo.assetAttachmentsFinished}</a></td>
					<c:if test="${acceptedEnabled}">
						<td><a
							href="AssetWorkflowAction.do?workflowNodetype=attachments&status=approved">${statusInfo.assetAttachmentsApproved}</a></td>
					</c:if>
					<td><a
						href="AssetWorkflowAction.do?workflowNodetype=attachments&status=published">${statusInfo.assetAttachmentsPublished}</a></td>
				</tr>
				<tr>
					<td class="indent" />
					<td class="indent" />
					<td class="indent"><img src="../utils/ajaxtree/images/L.png" /></td>
					<td class="leftData"><fmt:message
						key="workflow.status.asset.urls" /></td>
					<td><a
						href="AssetWorkflowAction.do?workflowNodetype=urls&status=draft">${statusInfo.assetUrlsDraft}</a></td>
					<td><a
						href="AssetWorkflowAction.do?workflowNodetype=urls&status=finished">${statusInfo.assetUrlsFinished}</a></td>
					<c:if test="${acceptedEnabled}">
						<td><a
							href="AssetWorkflowAction.do?workflowNodetype=urls&status=approved">${statusInfo.assetUrlsApproved}</a></td>
					</c:if>
					<td><a
						href="AssetWorkflowAction.do?workflowNodetype=urls&status=published">${statusInfo.assetUrlsPublished}</a></td>
				</tr>
			</tbody>
		</table>
		</div>
    </div>
		<div id="tree-page">
		<table class="centerData">
			<tbody>
				<tr>
					<td class="leftData"><fmt:message key="workflow.status.page" /></td>
					<td class="indent" />
					<td class="indent" />
					<td class="indent" />
					<td><a href="PageWorkflowAction.do?status=draft">${statusInfo.pageDraft}</a></td>
					<td><a href="PageWorkflowAction.do?status=finished">${statusInfo.pageFinished}</a></td>
					<c:if test="${acceptedEnabled}">
						<td><a href="PageWorkflowAction.do?status=approved">${statusInfo.pageApproved}</a></td>
					</c:if>
					<td><a href="PageWorkflowAction.do?status=published">${statusInfo.pagePublished}</a></td>
				</tr>
			</tbody>
		</table>
		</div>
		<div id="tree-link">
		<table class="centerData">
			<tbody>
				<tr>
					<td class="leftData"><fmt:message key="workflow.status.link" /></td>
					<td class="indent" />
					<td class="indent" />
					<td class="indent" />
					<td></td>
					<td><a href="LinkWorkflowAction.do?status=finished">${statusInfo.linkFinished}</a></td>
					<c:if test="${acceptedEnabled}">
						<td><a href="LinkWorkflowAction.do?status=approved">${statusInfo.linkApproved}</a></td>
					</c:if>
					<td><a href="LinkWorkflowAction.do?status=published">${statusInfo.linkPublished}</a></td>
				</tr>
			</tbody>
		</table>
		</div>
	</div>
	</cmscedit:sideblock></div>

	<div id="content"><mm:cloud jspvar="cloud" loginpage="login.jsp">
		<mm:import externid="status">draft</mm:import>
		<mm:import externid="results" jspvar="nodeList" vartype="List" />

		<div class="content">
		<div class="tabs" id="${status}">
		<div class="<%=tabClass(pageContext,"draft")%>">
		<div class="body">
		<div><a href="#" onclick="selectTab('draft');"><fmt:message
			key="workflow.tab.draft" /></a></div>
		</div>
		</div>

		<div class="<%=tabClass(pageContext,"finished")%>">
		<div class="body">
		<div><a href="#" onclick="selectTab('finished');"><fmt:message
			key="workflow.tab.finished" /></a></div>
		</div>
		</div>

		<c:if test="${acceptedEnabled}">
			<div class="<%=tabClass(pageContext,"approved")%>">
			<div class="body">
			<div><a href="#" onclick="selectTab('approved');"><fmt:message
				key="workflow.tab.approved" /></a></div>
			</div>
			</div>
		</c:if>
		<div class="<%=tabClass(pageContext,"published")%>">
		<div class="body">
		<div><a href="#" onclick="selectTab('published');"><fmt:message
			key="workflow.tab.published" /></a></div>
		</div>
		</div>
		</div>
		</div>

		<div class="editor"><c:if test="${not empty errors}">
			<mm:import externid="errors" vartype="List" />

			<div class="messagebox_red">
			<div class="box">
			<div class="top">
			<div></div>
			</div>
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
							<td><mm:hasfield name="title">
								<mm:field name="title" />
							</mm:hasfield> <mm:hasfield name="name">
								<mm:field name="name" />
							</mm:hasfield></td>
							<td><mm:hasfield name="lastmodifier">
								<mm:field name="lastmodifier" />
							</mm:hasfield></td>
							<td><mm:hasfield name="lastmodifieddate">
								<mm:field name="lastmodifieddate">
									<cmsc:dateformat displaytime="true" />
								</mm:field>
							</mm:hasfield></td>
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
		<div> <c:if test="${workflowType == 'allcontent' }">
         <fmt:message key="workflow.title.allcontent" />
      </c:if><c:if test="${workflowType == 'content' }">
         <c:choose>
				<c:when test="${workflowNodetype == 'article' }">
					<fmt:message key="workflow.title.content.article" />
				</c:when>
				<c:when test="${workflowNodetype == 'banners' }">
					<fmt:message key="workflow.title.content.banners" />
				</c:when>
				<c:when test="${workflowNodetype == 'link' }">
					<fmt:message key="workflow.title.content.link" />
				</c:when>
				<c:when test="${workflowNodetype == 'faqitem' }">
					<fmt:message key="workflow.title.content.faqitem" />
				</c:when>
				<c:otherwise>
					<fmt:message key="workflow.title.content" />
				</c:otherwise>
			</c:choose>

		</c:if> <c:if test="${workflowType == 'asset' }">
			<c:choose>
				<c:when test="${workflowNodetype == 'images' }">
					<fmt:message key="workflow.title.asset.images" />
				</c:when>
				<c:when test="${workflowNodetype == 'attachments' }">
					<fmt:message key="workflow.title.asset.attachments" />
				</c:when>
				<c:when test="${workflowNodetype == 'urls' }">
					<fmt:message key="workflow.title.asset.urls" />
				</c:when>
				<c:otherwise>
					<fmt:message key="workflow.title.asset" />
				</c:otherwise>
			</c:choose>
		</c:if> <c:if test="${workflowType == 'link' }">
			<fmt:message key="workflow.title.link" />
		</c:if> <c:if test="${workflowType == 'page' }">
			<fmt:message key="workflow.title.page" />
		</c:if></div>
		</div>

		<div class="body" style="display: none;" id="workflow-wait"><fmt:message
			key="workflow.wait" /></div>
		<div class="body" id="workflow-canvas"><c:set var="orderby"
			value="${param.orderby}" />
		<form action='?' method="post" onsubmit="return submitValid(false);">
		<input type="hidden" name="orderby" value="${orderby}" /> <input
			type="hidden" name="status" value="${status}" /> <input type="hidden"
			name="laststatus" /> <c:set var="lastvalue"
			value='<%=request.getAttribute("laststatus")%>' /> <c:set
			var="resultsPerPage" value="50" /> <c:set var="offset"
			value="${param.offset}" /> <c:set var="listSize">${fn:length(nodeList)}</c:set>

		<c:if test="${fn:length(results) > 0}">
			<%@ include file="../pages.jsp"%>
			<%@ include file="workflow_list_table_fragment.jsp"%>
			<%@ include file="../pages.jsp"%>
		</c:if> <c:set var="remark">
			<fmt:message key="workflow.action.reject.remark" />
		</c:set> &nbsp;&nbsp;&nbsp; <input type="checkbox" name="checkAll"
			onclick="checkAllElement(this, '')" /> <fmt:message
			key="workflow.select_all" /> <input type="hidden" name="actionvalue"
			value="" /> <input type='hidden' id="remark" name="remark"
			value="[unchanged-item]" /> <br />
		<c:if test="${status == 'draft' }">
			<input name="action"
				value="<fmt:message key="workflow.action.finish" />"
				onclick="return setActionValue('finish')" type="submit" />
		</c:if> <c:if test="${status == 'finished' }">
			<input name="action"
				value="<fmt:message key="workflow.action.reject" />"
				onclick="return setActionValue('reject','','${remark}')"
				type="submit" />
			<c:if test="${acceptedEnabled}">
				<input name="action"
					value="<fmt:message key="workflow.action.accept" />"
					onclick="return setActionValue('accept')" type="submit" />
			</c:if>
			<input name="action"
				value="<fmt:message key="workflow.action.publish" />"
				onclick="return setActionValue('publish')" type="submit" />
		</c:if> <c:if test="${status == 'approved' }">
			<input name="action"
				value="<fmt:message key="workflow.action.reject" />"
				onclick="return setActionValue('reject','','${remark}')"
				type="submit" />
			<input name="action"
				value="<fmt:message key="workflow.action.publish" />"
				onclick="return setActionValue('publish')" type="submit" />
		</c:if> <c:if test="${status == 'published' }">
			<input name="action"
				value="<fmt:message key="workflow.action.reject" />"
				onclick="return setActionValue('reject','','${remark}')"
				type="submit" />
		</c:if></form>

		</div>
		</div>
	</mm:cloud></div>
	</body>
	</html:html>
</mm:content>
