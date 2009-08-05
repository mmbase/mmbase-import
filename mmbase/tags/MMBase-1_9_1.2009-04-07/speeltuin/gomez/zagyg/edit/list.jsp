<%@page language="java" contentType="text/html;charset=utf-8" errorPage="error.jsp"
%><%@ include file="util/headernocache.jsp"
%><mm:content language="$language" postprocessor="reducespace" expires="0">
<html>
  <head>
    <title>Overview lists</title>
    <link rel="icon" href="<mm:url id="favi" page="images/edit.ico" />" type="image/x-icon" />
    <link rel="shortcut icon" href="<mm:write referid="favi" />" type="image/x-icon" />
    <link rel="stylesheet" href="css/edit.css" />
  </head>
  <mm:cloud loginpage="login.jsp" jspvar="cloud">
  <mm:import externid="types" vartype="list">images,attachments,news,people,urls</mm:import>
  <body>
    <mm:import id="tab">list</mm:import>
    <%@ include file="util/navigation.jsp"%>
    <div id="content">
      <mm:cloudinfo type="rank">
        <mm:isgreaterthan value="199">
          <os:cache key="<%="edit_list_"+cloud.getUser().getIdentifier()%>" time="<%=cacheperiod%>" refresh="<%=needsRefresh%>" scope="application">
          <h2>Overview lists</h2>
          <ul>
          <mm:stringlist id="list" referid="types">
            <mm:import id="wizard">tasks/<mm:write writer="list" />/<mm:write writer="list" /></mm:import>
            <mm:maycreate type="$_">
              <li>
                <a href="<mm:url referids="referrer,language" page="${jsps}list.jsp">
                  <mm:param name="wizard"><mm:write referid="wizard" /></mm:param>
                  <mm:param name="nodepath"><mm:write writer="list" /></mm:param>
                  <mm:param name="orderby">number</mm:param>
                  <mm:param name="directions">down</mm:param>
                  <mm:param name="search">yes</mm:param>
                  </mm:url>">
                  Alle <mm:nodeinfo nodetype="$_" type="plural_guitype" />
                </a>
              </li>
            </mm:maycreate>
          </mm:stringlist>
          </ul>
          </os:cache>
        </mm:isgreaterthan>
        <mm:islessthan value="100">
        Access Denied.
        </mm:islessthan>
      </mm:cloudinfo>
    </div>
  </body>
</html>
    </mm:context>
  </mm:cloud>
</mm:content>
