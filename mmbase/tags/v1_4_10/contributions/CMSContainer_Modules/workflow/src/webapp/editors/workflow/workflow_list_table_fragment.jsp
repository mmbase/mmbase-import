<%@ page import="static com.finalist.cmsc.workflow.forms.Utils.*" %>
<%@ include file="globals.jsp" %>
<table>
<thead>
   <tr>
      <th style="width: 20px;">&nbsp;</th>
      <th style="width: 10px;">&nbsp;</th>
      <th style="width: 75px;">&nbsp;</th>
      <th style="width: 300px;" nowrap="true">
         <a href="#" <%=onClickandStyle(pageContext, "title")%>>
            <fmt:message key="workflow.title"/>
         </a>
      </th>
      <th style="width: 68px;"><fmt:message key="workflow.type"/></th>
      <th style="width: 68px;"><fmt:message key="workflow.number"/></th>
      <th style="width: 50px;">
         <a href="#" <%=onClickandStyle(pageContext, "remark")%>><fmt:message key="workflow.remark"/></a>
      </th>
      <th style="width: 145px;">
         <a href="#" <%=onClickandStyle(pageContext, "lastmodifier")%>>
            <fmt:message key="workflow.lastmodifier"/>
         </a>                                                                                                                       
      </th>
      <c:if test="${workflowType == 'page' || workflowType == 'content' }">
         <th style="width: 140px;">
            <a href="#" <%=onClickandStyle(pageContext, "lastmodifieddate")%>>
               <fmt:message key="workflow.lastmodifieddate"/>
            </a>
         </th>
      </c:if>
      <c:if test="${workflowType == 'content' }">
         <th style="width: 140px;">
            <a href="#" <%=onClickandStyle(pageContext, "contentchannel")%>>
               <fmt:message key="workflow.contentchannel"/>
            </a>
         </th>
      </c:if>
   </tr>
</thead>

<tbody class="hover">
<mm:list referid="results" max="${resultsPerPage}" offset="${offset*resultsPerPage}">

<tr <mm:even inverse="true">class="swap"</mm:even>>
<td>
   <mm:field name="workflowitem.number" id="workflowNumber" write="false"/>
   <input type="checkbox" name="check_${workflowNumber}" value="on"/>
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
<td align="left">
   <mm:node number="${workflowNumber}">
      <mm:field name="stacktrace" id="stacktrace" write="false"/>
   </mm:node>
   <mm:haspage page="/editors/modules/">
      <mm:hasrank minvalue="administrator">
         <c:if test="${status != 'published' and not empty stacktrace}">
            <div class="tip" style="left:5px;width:20px;padding: 1px 1px 1px 1px; margin: 0px;">
               <span id="tip_info">${stacktrace}</span>
               <img src="../gfx/icons/error.png" align="left"/>
            </div>
         </c:if>
      </mm:hasrank>
   </mm:haspage>
</td>
<td align="left" style="white-space: nowrap;">
   <mm:field name="${type}.number" jspvar="number" write="false"/>
   <c:if test="${type == 'contentelement'}">
      <a href="javascript:info('${number}')">
         <img src="../gfx/icons/info.png" title="<fmt:message key="workflow.info" />"
              alt="<fmt:message key="workflow.info"/>"/>
      </a>
      <a href="<cmsc:contenturl number="${number}"/>" target="_blank">
         <img src="../gfx/icons/preview.png" alt="<fmt:message key="workflow.preview.title"/>"
              title="<fmt:message key="workflow.preview.title"/>"/>
      </a>
      <mm:haspage page="/editors/versioning">
         <c:url value="/editors/versioning/ShowVersions.do" var="showVersions">
            <c:param name="nodenumber">${number}</c:param>
         </c:url>
         <a href="#" onclick="openPopupWindow('versioning', 750, 550, '${showVersions}')">
            <img src="../gfx/icons/versioning.png"
                 title="<fmt:message key="workflow.icon.versioning.title" />"
                 alt="<fmt:message key="workflow.icon.versioning.title"/>"/>
         </a>
      </mm:haspage>
   </c:if>
   <mm:url page="../WizardInitAction.do" jspvar="url" write="false">
      <mm:param name="objectnumber" value="${number}"/>
      <mm:param name="returnurl" value="workflow/${returnAction}?status=${param.status}"/>
   </mm:url>
   <a href="${url}">
      <img src="../gfx/icons/edit.png" align="top" alt="<fmt:message key="workflow.editelement"/>"
           title="<fmt:message key="workflow.editelement"/>"/>
   </a>
</td>
<td style="white-space: nowrap;">
   <mm:field jspvar="value" write="false" name="${type}.${field}"/>
   <c:if test="${fn:length(value) > 50}">
      <c:set var="value">${fn:substring(value,0,49)}...</c:set>
   </c:if>
   ${value}
</td>
<td style="white-space: nowrap;">
   <mm:node number="${number}"> <mm:nodeinfo type="guitype"/> </mm:node>
</td>
<td style="white-space: nowrap;">
   ${number}
</td>
<td>
   <mm:field name="workflowitem.remark" escape="none" jspvar="w_remar" write="false"/>
   <a href="javascript:editRemark(${workflowNumber},'${w_remar}')">
      <img src="../gfx/icons/edit2.png" align="top" alt="<fmt:message key="workflow.editremark"/>"
           title="<fmt:message key="workflow.editremark"/>"/>
   </a>
   <mm:field name="workflowitem.remark" escape="none"/>
</td>
<td style="white-space: nowrap;">
   <mm:field name="workflowitem.lastmodifier"/>
</td>
<c:if test="${workflowType == 'page' || workflowType == 'content' }">
   <td style="white-space: nowrap;">
      <mm:field name="${type}.lastmodifieddate"><cmsc:dateformat displaytime="true"/></mm:field>
   </td>
</c:if>
<c:if test="${workflowType == 'content' }">
   <td style="white-space: nowrap;">
      <mm:field name="contentchannel.name"/>
   </td>
</c:if>
</tr>
</mm:list>
</tbody>
</table>