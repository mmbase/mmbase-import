<%@page language="java" contentType="text/html;charset=utf-8" errorPage="error.jsp"
%><%@ include file="util/headernocache.jsp"
%><mm:content language="$language" postprocessor="reducespace" expires="0">
<html>
  <head>
    <title>Structure Editors - Jumpers</title>
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
          <mm:import externid="maintopic" />
          <mm:import externid="subtopic" />
          <mm:import externid="detail" />
          <mm:import externid="level" />
          <mm:import externid="name" />
          <mm:import externid="jumper" />
          <mm:import externid="submit" />

          <mm:import id="node" ><mm:write referid="$level"/></mm:import>

          <mm:node number="$node">
            <mm:relatednodes type="templates" role="related" max="1">
              <mm:field name="position">
                <mm:issmallerthan value="2"> <!-- page or submenu page -->
                  <mm:field name="url">
                    <mm:url id="jumperurl" page="$_" referids="maintopic,subtopic?,detail?" escapeamps="false" write="false" />
                  </mm:field>
                </mm:issmallerthan>
                <mm:compare value="3"> <!-- container -->
                  <mm:url id="jumperurl" page="index.jsp" referids="maintopic,subtopic?,detail?" escapeamps="false" write="false" />
                </mm:isgreaterthan>
              </mm:field>
              <mm:field name="name" id="templatetype" write="false" />
            </mm:relatednodes>

            <mm:compare referid="submit" value="Change">
              <mm:node number="$jumper">
                <mm:setfield name="name"><mm:write referid="name" escape="none" /></mm:setfield>
                <mm:setfield name="id"><mm:write referid="node" /></mm:setfield>
                <mm:setfield name="url"><mm:write referid="jumperurl" escape="none" /></mm:setfield>
              </mm:node>
            </mm:compare>
            <mm:compare referid="submit" value="Delete">
              <mm:deletenode number="$jumper" />
              <mm:remove referid="jumper" />
              <mm:import id="name" reset="true"></mm:import>
            </mm:compare>
            <mm:compare referid="submit" value="Add">
              <mm:createnode type="jumpers">
                <mm:setfield name="name"><mm:write referid="name" escape="none" /></mm:setfield>
                <mm:setfield name="id"><mm:write referid="node" /></mm:setfield>
                <mm:setfield name="url"><mm:write referid="jumperurl" escape="none" /></mm:setfield>
              </mm:createnode>
            </mm:compare>

            <mm:notpresent referid="jumper">
              <mm:listnodescontainer type="jumpers">
                <mm:constraint field="id" value="$node" />
                <mm:maxnumber value="1" />
                <mm:listnodes>
                  <mm:import id="jumper" reset="true" ><mm:field name="number" /></mm:import>
                  <mm:import id="name" reset="true"><mm:field name="name" /></mm:import>
                </mm:listnodes>
              </mm:listnodescontainer>
            </mm:notpresent>

            <h2>Jumper for: <mm:field name="title" /></h2>
            <form action="<mm:url page="jumper.jsp" referids="level,maintopic,subtopic?,detail?" escape="none" />" method="post">
              <table>
                <tr>
                  <td>Path:</td>
                  <td><input type="text" name="name" value="<mm:write referid="name" escape="none" />" /></td>
                </tr>
                <tr>
                  <td>Jumper url:</td>
                  <td>
                    <input type="hidden" name="url" value="<mm:write referid="jumperurl" escape="none" />" />
                    <a href="../<mm:write referid="jumperurl" escape="none" />" target="_blank"><mm:write referid="jumperurl" /></a>
                    (type: <mm:write referid="templatetype" />)
                </td>
                </tr>
              </table>
              <mm:present referid="jumper">
                <input type="hidden" name="jumper" value="<mm:write referid="jumper" />" />
                <input type="submit" name="submit" value="Change">
                <input type="submit" name="submit" value="Delete">
              </mm:present>
              <mm:notpresent referid="jumper">
                <input type="submit" name="submit" value="Add">
              </mm:notpresent>
            </form>
            <a href="structure.jsp">Back</a>
          </mm:node>
        </mm:isgreaterthan>
        <mm:islessthan value="100">
        Access Denied.
        </mm:islessthan>
      </mm:cloudinfo>
    </div>
  </body>
</html>
  </mm:cloud>
</mm:content>
