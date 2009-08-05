<%@page language="java" contentType="text/html;charset=utf-8" errorPage="error.jsp"
%><%@ include file="util/headernocache.jsp"
%><mm:content language="$language" postprocessor="reducespace" expires="0">
<html>
  <head>
    <title>Meta Structure Editors</title>
    <link rel="icon" href="<mm:url id="favi" page="images/edit.ico" />" type="image/x-icon" />
    <link rel="shortcut icon" href="<mm:write referid="favi" />" type="image/x-icon" />
    <link rel="stylesheet" href="css/edit.css" />
  </head>
  <mm:cloud loginpage="login.jsp" jspvar="cloud">
  <body>
    <mm:import id="tab">meta</mm:import>
    <%@ include file="util/navigation.jsp"%>
    <mm:import id="wizard">tasks/meta/categorytypes/categorytype</mm:import>
    <div id="content">
      <mm:cloudinfo type="rank">
        <mm:isgreaterthan value="999">
          <table style="width:100%;">
            <tr>
              <os:cache key="<%="edit_meta_"+cloud.getUser().getIdentifier()%>" time="<%=cacheperiod%>" refresh="<%=needsRefresh%>" scope="application">
              <td style="width:30%">
                <p>
                  <h1>Meta Structure</h1>
                <mm:node number="categorytype_main" >
                  <mm:relatednodes type="categorytypes" role="posrel" orderby="posrel.pos" searchdir="destination">
                    <ul><li><mm:field name="title" /></li><ul>
                    <mm:relatednodescontainer type="categorytypes" role="posrel" searchdirs="destination">
                      <mm:sortorder field="posrel.pos" />
                      <mm:relatednodes id="mainlist">
                        <li><span class="editintro">
                          <mm:field name="title" />
                        </span></li>
                        <mm:relatednodescontainer type="categorytypes" role="posrel" searchdirs="destination">
                          <mm:sortorder field="posrel.pos" />
                          <mm:relatednodes>
                            <mm:first>
                              <ul>
                            </mm:first>
                            <li><span class="editintro">
                              <mm:field name="title" />
                            </span></li>
                            <mm:context>
                              <mm:relatednodescontainer type="categorytypes" role="posrel" searchdirs="destination">
                                <mm:sortorder field="posrel.pos" />
                                <mm:relatednodes>
                                  <mm:first>
                                    <ul>
                                  </mm:first>
                                  <li><span class="editintro">
                                    <mm:field name="title" />
                                  </span></li>
                                  <mm:last>
                                    </ul>
                                  </mm:last>
                                </mm:relatednodes>
                              </mm:relatednodescontainer>
                            </mm:context>
                            <mm:last>
                              </ul>
                            </mm:last>
                          </mm:relatednodes>
                        </mm:relatednodescontainer>
                      </mm:relatednodes>
                    </mm:relatednodescontainer>
                    </ul></li></ul>
                  </mm:relatednodes>
                </mm:node>
                </p>
              </td>
              </os:cache>
              <td style="width:40%">
                <p>
                  <h1>Category Types
                    <a href="<mm:url referids="referrer,language,wizard" page="${jsps}wizard.jsp">
                      <mm:param name="context">admin</mm:param>
                      <mm:param name="objectnumber">new</mm:param>
                      </mm:url>">New Type
                    </a>
                  </h1>
                  <ul>
                  <mm:listnodescontainer type="categorytypes">
                    <mm:sortorder field="title" />
                    <mm:listnodes>
                      <%@include file="meta.li.jsp" %>
                    </mm:listnodes>
                  </mm:listnodescontainer>
                  </ul>
                </p>
              </td>
              <td style="width:30%">
              <p>
                <h1>Templates
                  <a href="<mm:url referids="referrer,language" page="${jsps}wizard.jsp">
                    <mm:param name="context">admin</mm:param>
                    <mm:param name="wizard">tasks/meta/templates/template</mm:param>
                    <mm:param name="objectnumber">new</mm:param>
                    </mm:url>">New Template
                  </a>
                </h1>
                <ul>
                <mm:listnodescontainer type="templates">
                  <mm:sortorder field="name" />
                  <mm:listnodes>
                    <%@include file="meta.template.li.jsp" %>
                  </mm:listnodes>
                </mm:listnodescontainer>
                </ul>
              </p>
              </td>
            </tr>
          </table>
          <br />
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
