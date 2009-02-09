<%@ page import="static com.finalist.cmsc.workflow.forms.Utils.*" %>
<%@ include file="globals.jsp" %>
<table>
<thead>
   <tr>
      <th style="width: 20px;" >&nbsp;</th>
      <th style="width: 10px;">&nbsp;</th>
      <th style="width: 75px;">&nbsp;</th>
      <th style="width: 68px;">
         <a class="headerlink" href="#" <%=onClickandStyle(pageContext, "type")%> >
            <fmt:message key="workflow.type"/>
         </a>
      </th>
      <th style="width: 170px;" nowrap="true">
         <a href="#" <%=onClickandStyle(pageContext, "title")%>>
            <fmt:message key="workflow.title"/>
         </a>
      </th>
      <th style="width: 145px;">
         <a href="#" <%=onClickandStyle(pageContext, "lastmodifier")%>>
            <fmt:message key="workflow.lastmodifier"/>
         </a>
      </th>
      <c:if test="${workflowType == 'page' || workflowType == 'content' || workflowType == 'asset' || workflowType == 'allcontent'}">
         <th style="width: 140px;">
            <a href="#" <%=onClickandStyle(pageContext, "lastmodifieddate")%>>
               <fmt:message key="workflow.lastmodifieddate"/>
            </a>
         </th>
      </c:if>
      <c:if test="${workflowType == 'content' || workflowType == 'asset' || workflowType == 'allcontent'}">
         <th style="width: 140px;">
            <a href="#" <%=onClickandStyle(pageContext, "contentchannel")%>>
               <fmt:message key="workflow.contentchannel"/>
            </a>
         </th>
      </c:if>
      <th style="width: 50px;">
         <a href="#" <%=onClickandStyle(pageContext, "remark")%>><fmt:message key="workflow.remark"/></a>
      </th>
   </tr>
</thead>

<tbody class="hover">
<mm:list referid="results" max="${resultsPerPage}" offset="${offset*resultsPerPage}">

   <c:if test="${workflowType == 'allcontent' }">
      <mm:field name="workflowitem.type" id="itemType" write="false"/>
      <c:if test="${itemType == 'content' }"><c:set var="type" value="contentelement"/></c:if>
      <c:if test="${itemType == 'asset' }"><c:set var="type" value="assetelement"/></c:if>
      <c:set var="field" value="title"/>
      <c:set var="returnAction" value="AllcontentWorkflowAction.do"/>
   </c:if>
   <c:if test="${workflowType == 'content' }">
      <c:set var="type" value="contentelement"/>
      <c:set var="field" value="title"/>
      <c:set var="returnAction" value="ContentWorkflowAction.do"/>
   </c:if>
   <c:if test="${workflowType == 'asset' }">
      <c:set var="type" value="assetelement"/>
      <c:set var="field" value="title"/>
      <c:set var="returnAction" value="AssetWorkflowAction.do"/>
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
   <mm:field name="${type}.number" jspvar="number" write="false"/>
   <mm:node number="${number}">
      <c:set var="nodeType"><mm:node number="${number}"><mm:nodeinfo type="guitype"/></mm:node></c:set>
   </mm:node>

<c:if test="${(not empty workflowNodetypeGUI && nodeType eq workflowNodetypeGUI)||(empty workflowNodetypeGUI)}">
<tr <mm:even inverse="true">class="swap"</mm:even>>
   <td>
      <mm:field name="workflowitem.number" id="workflowNumber" write="false"/>
      <input type="checkbox" name="check_${workflowNumber}" value="on"/>
   </td>
   <td align="left">
      <mm:node number="${workflowNumber}">
         <mm:field name="stacktrace" id="stacktrace" write="false"/>
      </mm:node>
      <mm:haspage page="/editors/modules/">
         <mm:hasrank minvalue="siteadmin">
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
      <mm:url page="../WizardInitAction.do" id="url" write="false">
         <mm:param name="objectnumber" value="${number}"/>
         <mm:param name="returnurl" value="workflow/${returnAction}?status=${param.status}"/>
      </mm:url>
      <a href="${url}">
         <img src="../gfx/icons/edit.png" align="top" alt="<fmt:message key="workflow.editelement"/>"
              title="<fmt:message key="workflow.editelement"/>"/></a>
      <c:if test="${type == 'contentelement' || type == 'assetelement' }">
         <a href="<cmsc:contenturl number="${number}"/>" target="_blank">
            <img src="../gfx/icons/preview.png" alt="<fmt:message key="workflow.preview.title"/>"
                 title="<fmt:message key="workflow.preview.title"/>"/></a>
         <a href="javascript:info('${number}')">
            <img src="../gfx/icons/info.png" title="<fmt:message key="workflow.info" />"
                 alt="<fmt:message key="workflow.info"/>"/></a>
         <mm:haspage page="/editors/versioning">
            <c:url value="/editors/versioning/ShowVersions.do" var="showVersions">
               <c:param name="nodenumber">${number}</c:param>
            </c:url>
            <a href="#" onclick="openPopupWindow('versioning', 750, 550, '${showVersions}')">
               <img src="../gfx/icons/versioning.png"
                    title="<fmt:message key="workflow.icon.versioning.title" />"
                    alt="<fmt:message key="workflow.icon.versioning.title"/>"/></a>
         </mm:haspage>
      </c:if>
   </td>
   <td style="white-space: nowrap;">
      <mm:node number="${number}"> <mm:nodeinfo type="guitype"/> </mm:node>
   </td>
   <td style="white-space: nowrap;">
      <mm:field jspvar="value" write="false" name="${type}.${field}"/>
      <c:if test="${fn:length(value) > 50}">
         <c:set var="value">${fn:substring(value,0,49)}...</c:set>
      </c:if>
      ${value}
   </td>
   <td style="white-space: nowrap;">
      <mm:field name="workflowitem.lastmodifier"/>
   </td>
   <c:if test="${workflowType == 'page' || workflowType == 'content' || workflowType == 'asset' || workflowType == 'allcontent'}">
      <td style="white-space: nowrap;">
         <mm:field name="${type}.lastmodifieddate"><cmsc:dateformat displaytime="true"/></mm:field>
      </td>
   </c:if>
   <c:if test="${workflowType == 'content' || workflowType == 'asset' || workflowType == 'allcontent'}">
      <td style="white-space: nowrap;">
         <mm:field name="contentchannel.name"/>
      </td>
   </c:if>
   <td>
      <mm:field name="workflowitem.remark" escape="none" jspvar="w_remar" write="false"/>
      <a href="javascript:editRemark(${workflowNumber},'${w_remar}')">
         <img src="../gfx/icons/edit2.png" align="top" alt="<fmt:message key="workflow.editremark"/>"
              title="<fmt:message key="workflow.editremark"/>"/>
      </a>
      <mm:field name="workflowitem.remark" escape="none"/>
   </td>
</tr>
</c:if>
</mm:list>
</tbody>
</table>