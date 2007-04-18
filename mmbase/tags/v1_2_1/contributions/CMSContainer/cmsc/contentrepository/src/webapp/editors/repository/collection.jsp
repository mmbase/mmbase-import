<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<%@page import="com.finalist.cmsc.repository.RepositoryUtil" %>
<%@page import="com.finalist.cmsc.security.*" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
	<head>
		<title><fmt:message key="collection.title" /></title>
		<link rel="stylesheet" type="text/css" href="../css/main.css" />
		<script src="../utils/window.js" type="text/javascript"></script>
		<script src="../utils/rowhover.js" type="text/javascript"></script>
	    <script type="text/javascript" src="../utils/transparent_png.js" ></script>
	</head>
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