<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html; charset=UTF-8" errorPage="../error.jsp" import="org.mmbase.security.implementation.cloudcontext.ConvertTool"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><mm:import externid="language">en</mm:import>
<mm:content language="$language"  type="text/html" expires="0">
<html>
  <head>
    <title>Cloud Context Admin page</title>
    <link href="../style/default.css" rel="stylesheet" type="text/css" />
    <link rel="icon" href="../images/favicon.ico" type="image/x-icon" />
    <link rel="shortcut icon" href="../images/favicon.ico" type="image/x-icon" />
  </head>
  <body>
    <mm:cloud loginpage="../login.jsp" rank="administrator" jspvar="cloud">
      <mm:import externid="submit" />
      <mm:notpresent referid="submit">
      <form>
        <table>
          <tr>
            <td>
              Read security configuration from
            </td>
            <td>
              <input type="text" name="readfrom" value="<%=request.getRealPath("/")%>WEB-INF/config/security/context/config.xml" />
            </td>         
            <td>
              <input type="submit" name="submit" value="read" />
            </td>
          </tr>
          <tr>
            <td>
              Write to
            </td>
            <td>
              <input type="text" name="writeto" value="/tmp/config.xml" />
            </td>         
            <td>
              <input type="submit" name="submit" value="write" />
            </td>
          </tr>
        </table>
      </form>
      </mm:notpresent>
      <mm:compare referid="submit" value="write">
        <mm:import externid="writeto" jspvar="file" vartype="string" />
        Reading...
        <% ConvertTool tool = new ConvertTool(cloud);
           tool.writeXml(new java.io.File(file));
        %>
        <pre><%= tool.getResult()%></pre>
      </mm:compare>
      <mm:compare referid="submit" value="read">
        <mm:import externid="readfrom" jspvar="file" vartype="string" />
        Reading...
        <% ConvertTool tool = new ConvertTool(cloud);
           tool.readXml(new java.io.File(file));
        %>
        <pre><%= tool.getResult()%></pre>
      </mm:compare>
    </mm:cloud>
  </body>
</html>
</mm:content>