<%@page language="java" contentType="text/html;charset=utf-8" import="org.mmbase.security.Rank,java.util.*" errorPage="error.jsp"
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
    <% if (Rank.getRank(cloud.getUser().getRank()).getInt() >= 500) { %>
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
          <h1><mm:field name="title" />:
            <mm:maywrite>
              <a href="<mm:url referids="referrer,language" page="${jsps}wizard.jsp">
              <mm:param name="wizard">tasks/categories/overview</mm:param>
              <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
              </mm:url>">Edit Structure</a>
            </mm:maywrite>
          </h1>
          <mm:relatednodescontainer type="categories" role="posrel" searchdirs="destination">
            <mm:sortorder field="posrel.pos" />
            <mm:relatednodes id="mainlist">
              <h2><mm:field name="title" />:
                <mm:maywrite>
                  <a href="<mm:url referids="referrer,language" page="${jsps}wizard.jsp">
                  <mm:param name="wizard">tasks/categories/overview</mm:param>
                  <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                  </mm:url>">Edit Structure</a>
                </mm:maywrite>
              </h2>
              <mm:context>
                <mm:import id="level">topic</mm:import>
                <mm:import id="origin"><mm:field name="number" /></mm:import>
                <mm:relatednodescontainer type="categories" role="posrel" searchdirs="destination">
                  <mm:sortorder field="posrel.pos" />
                  <mm:relatednodes>
                    <mm:first>
                      <ul>
                    </mm:first>
                    <mm:context>
                      <mm:field name="title">
                        <mm:compare value="TV"><mm:import id="tri">true</mm:import></mm:compare>
                        <mm:compare value="Radio"><mm:import id="tri">true</mm:import></mm:compare>
                        <mm:compare value="Internet"><mm:import id="tri">true</mm:import></mm:compare>
                      </mm:field>
                      <mm:import id="topic"><mm:field name="number" /></mm:import>
                      <%@include file="struct.li.jsp" %>
                      <mm:import id="level">subtopic</mm:import>
                      <mm:import id="origin"><mm:field name="number" /></mm:import>
                      <mm:relatednodescontainer type="categories" role="posrel" searchdirs="destination">
                        <mm:sortorder field="posrel.pos" />
                        <mm:relatednodes>
                          <mm:first>
                            <ul>
                          </mm:first>
                          <mm:import id="subtopic" reset="true"><mm:field name="number" /></mm:import>
                          <%@include file="struct.li.jsp" %>
                          <mm:notpresent referid="tri">
                            <mm:context>
                              <mm:import id="level">detail</mm:import>
                              <mm:import id="origin"><mm:field name="number" /></mm:import>
                              <mm:relatednodescontainer type="categories" role="posrel" searchdirs="destination">
                                <mm:sortorder field="posrel.pos" />
                                <mm:relatednodes>
                                  <mm:first>
                                    <ul>
                                  </mm:first>
                                  <mm:import id="detail"><mm:field name="number" /></mm:import>
                                  <%@include file="struct.li.jsp" %>
                                  <mm:last>
                                    </ul>
                                  </mm:last>
                                </mm:relatednodes>
                              </mm:relatednodescontainer>
                            </mm:context>
                          </mm:notpresent>
                          <mm:last>
                            </ul>
                          </mm:last>
                        </mm:relatednodes>
                      </mm:relatednodescontainer>
                      <mm:present referid="tri">
                        <mm:relatednodescontainer type="programs" role="related" searchdirs="destination">
                          <mm:sortorder field="programs.title" />
                          <mm:relatednodes>
                            <mm:first>
                              <ul>
                            </mm:first>
                            <mm:import id="subtopic" reset="true"><mm:field name="number" /></mm:import>
                            <%@include file="struct.li.jsp" %>
                            <mm:last>
                              </ul>
                            </mm:last>
                          </mm:relatednodes>
                        </mm:relatednodescontainer>
                      </mm:present>
                    </mm:context>
                    <mm:last>
                      </ul>
                    </mm:last>
                  </mm:relatednodes>
                </mm:relatednodescontainer>
              </mm:context>
            </mm:relatednodes>
          </mm:relatednodescontainer>
        </mm:node>
        </p>
      </mm:isempty>
    <% } else { %>
          Access Denied.
    <% }%>
    </div>
  </body>
  </mm:cloud>
</html>
</mm:content>
