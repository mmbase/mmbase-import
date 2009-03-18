<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<%@page import="com.finalist.cmsc.util.ServerUtil"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="maintenance.title" />
<body>
		<%-- <cmscedit:sideblock title="maintenance.title">  --%>
		<div class="side_block">
			<!-- bovenste balkje -->
			<div class="header">
				<div class="title"><fmt:message key="maintenance.title" /></div>
				<div class="header_end"></div>
			</div>
			<div class="body">
			
<mm:cloud jspvar="cloud" loginpage="login.jsp" rank="administrator">
	<ul class="shortcuts">
       <li class="views">
          <c:url var="usageUrl" value="/editors/maintenance/view_usage.jsp"/>
          <a href="${usageUrl}" target="rightpane"><fmt:message key="maintenance.view_layout.etc_usage" /></a>
       </li>
        <li class="advancedpublish">
           <c:url var="threadsUrl" value="/editors/maintenance/threads.jsp"/>
           <a href="${threadsUrl}" target="rightpane"><fmt:message key="maintenance.threads" /></a>
        </li>
        <li class="advancedpublish">
           <c:url var="checksumUrl" value="/editors/maintenance/compute-checksums.jsp"/>
           <a href="${checksumUrl}" target="rightpane"><fmt:message key="maintenance.checksum" /></a>
        </li>
        <li class="advancedpublish">
           <c:url var="cleanNVPUrl" value="/editors/maintenance/clean-non-visible-portlets.jsp"/>
           <a href="${cleanNVPUrl}" target="rightpane"><fmt:message key="maintenance.cleannonvisportlets"/></a>
        </li>
        <li class="advancedpublish">
           <c:url var="cleanICaches" value="/editors/maintenance/clean-duplicate-icaches.jsp"/>
           <a href="${cleanICaches}" target="rightpane"><fmt:message key="maintenance.icaches.cleanduplicates"/></a>
        </li>

			<mm:haspage page="/editors/publish-remote">
            <li class="advancedpublish">
               <c:url var="compareUrl" value="/editors/maintenance/compare-models.jsp"/>
               <a href="${compareUrl}" target="rightpane"><fmt:message key="maintenance.publish.compare" /></a>
            </li>
            <li class="advancedpublish">
               <c:url var="unlinkUrl" value="/editors/maintenance/remotepublishing/unlink-remotenodes.jsp"/>
               <a href="${unlinkUrl}" target="rightpane"><fmt:message key="maintenance.publish.unlink-remotenodes" /></a>
            </li>
            <li class="advancedpublish">
               <c:url var="unpublishUrl" value="/editors/maintenance/remotepublishing/unpublish-remotenodes.jsp"/>
               <a href="${unpublishUrl}" target="rightpane"><fmt:message key="maintenance.publish.unpublish-remotenodes" /></a>
            </li>
            <% if (ServerUtil.isLive()) { %>
               <li class="advancedpublish">
                  <c:url var="removeImportedUrl" value="/editors/maintenance/live/remove-imported-nodes.jsp"/>
                  <a href="${removeImportedUrl}" target="rightpane"><fmt:message key="maintenance.publish.remove-imported-nodes" /></a>
               </li>
            <% } %>
            <% if (ServerUtil.isStaging()) { %>
               <li class="advancedpublish">
                  <c:url var="publishNodeUrl" value="/editors/maintenance/staging/publishnode.jsp"/>
                  <a href="${publishNodeUrl}" target="rightpane"><fmt:message key="maintenance.publish.node" /></a>
               </li>
               <li class="advancedpublish">
                  <c:url var="publishTypeUrl" value="/editors/maintenance/staging/publishtype.jsp"/>
                  <a href="${publishTypeUrl}" target="rightpane"><fmt:message key="maintenance.publish.type" /></a>
               </li>
               <li class="advancedpublish">
                  <c:url var="publishQueueUrl" value="/editors/maintenance/staging/remove-publishqueue.jsp"/>
                  <a href="${publishQueueUrl}" target="rightpane"><fmt:message key="maintenance.publish.remove-publishqueue" /></a>
               </li>
               <li class="advancedpublish">
                  <c:url var="repairStagingUrlsUrl" value="/editors/maintenance/richtext/repair-staging-urls.jsp"/>
                  <a href="${repairStagingUrlsUrl}" target="rightpane"><fmt:message key="maintenance.repairStagingUrls" /></a>
               </li>
            <% } %>
         </mm:haspage>
		<cmsc:hasfeature name="workflowitem">
        <li class="workflow">
           <a href="staging/workflow-remove.jsp" target="rightpane"><fmt:message key="maintenance.workflow" /></a>
        </li>
      </cmsc:hasfeature>
<%--  
        <cmsc:hasfeature name="luceusmodule">
			<li class="luceus">
				<a href="" target="rightpane"><fmt:message key="maintenance.luceus" /></a>
			</li>
        </cmsc:hasfeature>
 --%>
<%-- 
        <mm:haspage page="/editors/egemmail">
           <li class="egem">
              <a href="" target="rightpane"><fmt:message key="maintenance.egemmail" /></a>
           </li>
        </mm:haspage>
--%>
       <li><br/></li>
       <li class="advancedpublish">
         <c:url var="logoutUrl" value="../logout.jsp"/>
         <a href="${logoutUrl}" target="rightpane"><fmt:message key="maintenance.logout" /></a>
      </li>
    </ul>
</mm:cloud>
         </div>
         	<div class="side_block_end"></div>
		</div>
		<%-- </cmscedit:sideblock>  --%>
</body>
</html:html>
</mm:content>