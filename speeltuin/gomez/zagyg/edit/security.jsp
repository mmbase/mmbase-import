<%@page language="java" contentType="text/html;charset=utf-8" errorPage="error.jsp"
%><%@ include file="util/headernocache.jsp"
%><mm:content language="$language" postprocessor="reducespace" expires="0">
<html>
  <head>
    <title>Security</title>
    <link rel="icon" href="<mm:url id="favi" page="images/edit.ico" />" type="image/x-icon" />
    <link rel="shortcut icon" href="<mm:write referid="favi" />" type="image/x-icon" />
    <link rel="stylesheet" href="css/edit.css" />
  </head>
  <mm:cloud loginpage="login.jsp" jspvar="cloud">
  <body>
    <mm:import id="tab">security</mm:import>
    <%@ include file="util/navigation.jsp"%>
    <div id="content">
      <mm:cloudinfo type="rank">
        <mm:isgreaterthan value="1999">
          <div id="security">
            <mm:import externid="parameters" />
            <mm:import externid="url">index_users.jsp</mm:import>
            <mm:import externid="location">../mmbase/security/</mm:import>
            <mm:import externid="visibleoperations">read,write,delete,change context</mm:import>

            <mm:notpresent referid="parameters">
              <mm:import id="extrauserlink">/<mm:write referid="thisdir" />/userlink.jsp</mm:import>

              <mm:import id="thisparameters">location,tab,extrauserlink,language,visibleoperations</mm:import>
              <mm:include referids="thisparameters@parameters,$thisparameters" page="${location}${url}" />
            </mm:notpresent>
            <mm:present referid="parameters">
              <mm:include page="${location}${url}" />
            </mm:present>
          </div>
        </mm:isgreaterthan>
        <mm:islessthan value="100">
        Access Denied.
        </mm:islessthan>
      </mm:cloudinfo>
    </div>
  </body>
  </mm:cloud>
</html>
</mm:content>