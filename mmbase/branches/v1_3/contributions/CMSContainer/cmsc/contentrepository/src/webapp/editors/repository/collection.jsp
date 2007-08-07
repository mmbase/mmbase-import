<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<%@page import="com.finalist.cmsc.repository.RepositoryUtil" %>
<%@page import="com.finalist.cmsc.security.*" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="collection.title" />
<body>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
	  <mm:import externid="parentchannel" jspvar="parentchannel" vartype="Integer" from="parameters" required="true"/>
      <mm:import jspvar="returnurl" id="returnurl">/editors/repository/Content.do?parentchannel=<mm:write referid="parentchannel"/>&direction=down</mm:import>

      <div class="tabs">
         <!-- actieve TAB -->
         <div class="tab_active">
            <div class="body">
               <div>
                  <a name="activetab"><fmt:message key="collection.title" /></a>
               </div>
            </div>
         </div>
      </div>

    <div class="editor">
   <mm:node number="$parentchannel" jspvar="parentchannelnode">
      <div class="body">
      <p>
         <fmt:message key="content.channel" >
            <fmt:param ><mm:field name="path"/></fmt:param>
         </fmt:message>
      </p>
      </div>
   </mm:node>
   </div>

</mm:cloud>
	</body>
</html:html>
</mm:content>