<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="admin.title" />
<body>
<mm:cloud jspvar="cloud" loginpage="login.jsp">
<mm:haspage page="/editors/admin/">
	<mm:hasrank minvalue="administrator">
		<cmscedit:sideblock title="admin.title">
			<ul class="shortcuts">
             <li class="users">
					<a href="../usermanagement/userlist.jsp" target="rightpane"><fmt:message key="admin.users" /></a>
				</li>
               <li class="properties">
					<a href="../WizardListAction.do?nodetype=properties" target="rightpane"><fmt:message key="admin.settings" /></a>
				</li>
               <li class="layouts">
					<a href="../WizardListAction.do?nodetype=layout"  target="rightpane"><fmt:message key="admin.layouts" /></a>
				</li>
               <li class="views">
					<a href="../WizardListAction.do?nodetype=view"  target="rightpane"> <fmt:message key="admin.views" /></a>
				</li>
               <li class="stylesheets">
					<a href="../WizardListAction.do?nodetype=stylesheet" target="rightpane"><fmt:message key="admin.stylesheets" /></a>
				</li>
               <li class="portletdefinitions">
					<a href="../WizardListAction.do?wizardname=singleportletdefinition" target="rightpane"><fmt:message key="admin.singleportletdefinitions" /></a>
				</li>
               <li class="portletdefinitions">
					<a href="../WizardListAction.do?wizardname=multiportletdefinition" target="rightpane"><fmt:message key="admin.multiportletdefinitions" /></a>
				</li>
               <li class="dumpdefaults">
					<a href="dumpdefaults.jsp" target="rightpane"><fmt:message key="admin.dumpdefaults" /></a>
				</li>
               <li class="clear">
					<a href="resetsitecache.jsp" target="rightpane"><fmt:message key="admin.resetsitecache" /></a>
				</li>
             <li class="admindashboard">
					<a href="../admindashboard" target="rightpane"><fmt:message key="admin.admindashboard" /></a>
				</li>

            <cmsc:hasfeature name="luceusmodule">
            <li class="luceus">
               <a href="../luceus/fullindex.jsp" target="rightpane"><fmt:message key="modules.fullindex" /></a>
            </li>
            </cmsc:hasfeature>
            <cmsc:hasfeature name="spidermodule">
               <li class="luceus">
                  <a href="../nijmegen/spiders.jsp" target="rightpane">Nijmegen indexering GNS etc.</a>
               </li>
            </cmsc:hasfeature>
            <mm:haspage page="/editors/publish-remote">
               <li class="advancedpublish">
                  <c:url var="publishUrl" value="/editors/publish-remote/index.jsp"/>
                  <a href="${publishUrl}" target="rightpane"><fmt:message key="modules.publish" /></a>
               </li>
            </mm:haspage>
            <mm:haspage page="/editors/workflow">
               <li class="workflow">
                  <c:url var="workflowUrl" value="/editors/workflow/admin/WorkflowAdminAction.do"/>
                  <a href="${workflowUrl}" target="rightpane"><fmt:message key="modules.workflow" /></a>
               </li>
            </mm:haspage>
            <mm:haspage page="/editors/knownvisitor-ntlm">
               <li class="visitor">
                  <c:url var="visitorNtlmUrl" value="/editors/knownvisitor-ntlm/index.jsp"/>
                  <a href="${visitorNtlmUrl}" target="rightpane"><fmt:message key="modules.knowvisitor-html" /></a>
               </li>
            </mm:haspage>


			</ul>
		</cmscedit:sideblock>		
      	
	</mm:hasrank>
</mm:haspage>
</mm:cloud>
</body>
</html:html>
</mm:content>