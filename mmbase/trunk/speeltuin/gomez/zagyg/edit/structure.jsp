<%@page language="java" contentType="text/html;charset=utf-8" errorPage="error.jsp"
%><%@ include file="util/headernocache.jsp"
%><mm:content language="$language" postprocessor="reducespace" expires="0">
<html>
  <head>
    <title>Site Structure Editor</title>
    <link rel="icon" href="<mm:url id="favi" page="images/edit.ico" />" type="image/x-icon" />
    <link rel="shortcut icon" href="<mm:write referid="favi" />" type="image/x-icon" />
    <link rel="stylesheet" href="css/edit.css" />
  </head>
  <mm:cloud loginpage="login.jsp" jspvar="cloud">
  <body>
    <mm:import id="tab">structure</mm:import>
    <%@ include file="util/navigation.jsp"%>
    <div id="content">
      <mm:cloudinfo type="rank">
        <mm:isgreaterthan value="499">
          <mm:import externid="nodenumber" />
          <mm:isnotempty referid="nodenumber">
            <form method="post">
              <p>
                <mm:node number="$nodenumber">
                  <%@include file="structelement.jsp" %>
                </mm:node>
              </p>
              <p>
                <input type="hidden" value="tab" value="struct" />
                <input type="submit" name="submit"  value="Change security context" />
              </p>
            </form>
            <p><a href="<mm:url referids="tab" />">Back</a></p>
          </mm:isnotempty>
          <mm:isempty referid="nodenumber">
            <p>
            <mm:node number="category_main">
              <mm:relatednodescontainer type="categories" role="posrel" searchdirs="destination">
                <mm:sortorder field="posrel.pos" />
                <mm:relatednodes id="mainlist">
                  <h1><mm:field name="title" />:
                    <mm:maywrite>
                      <a href="<mm:url referids="referrer,language" page="${jsps}wizard.jsp">
                      <mm:param name="wizard">tasks/structure/category</mm:param>
                      <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                      </mm:url>">Edit Structure</a>
                    </mm:maywrite>
                  </h1>
                  <mm:import id="level" reset="true">maintopic</mm:import>
                  <mm:tree type="categories" role="posrel" orderby="posrel.pos" searchdir="destination">
                    <mm:first inverse="true">
                      <mm:grow>
                        <ul>
                        <mm:onshrink></ul></mm:onshrink>
                      </mm:grow>
                      <mm:depth>
                        <mm:compare value="3">
                          <mm:import id="level" reset="true">maintopic</mm:import>
                          <mm:remove referid="subtopic"/>
                          <mm:remove referid="detail"/>
                        </mm:compare>
                        <mm:compare value="4">
                          <mm:import id="level" reset="true">subtopic</mm:import>
                          <mm:remove referid="detail"/>
                        </mm:compare>
                        <mm:isgreaterthan value="4">
                          <mm:import id="level" reset="true">detail</mm:import>
                        </mm:isgreaterthan>
                      </mm:depth>
                      <mm:import id="$level" reset="true"><mm:field name="number" /></mm:import>
                      <%@include file="struct.li.jsp" %>
                      <mm:shrink />
                    </mm:first>
                  </mm:tree>
                </mm:relatednodes>
              </mm:relatednodescontainer>
            </mm:node>
            </p>
          </mm:isempty>
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
