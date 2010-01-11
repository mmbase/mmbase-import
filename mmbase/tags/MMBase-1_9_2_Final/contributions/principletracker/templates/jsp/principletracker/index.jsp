<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<html>   
  <mm:cloud>
    <head>
      <link rel="stylesheet" type="text/css" href="css/default.css" />
      <title>PrincipeTracker</title>
    </head>
    <mm:import externid="main" />
    <mm:import externid="sub" >none</mm:import>
    <mm:import externid="principleset">Principle.default</mm:import>

    <mm:notpresent referid="main">
      <mm:import id="main" reset="true" >principlesets</mm:import>
      <mm:node referid="principleset" notfound="skip">
        <mm:import id="main" reset="true" >principles</mm:import>
      </mm:node>
    </mm:notpresent>


    <body>
      <!-- first the selection part -->
      <center><!-- center is not XHTML ! -->
        <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;"
               width="95%"><!-- arch -->

          <tr>
            <th colspan="8">PrincipeTracker - version 0.3</th>
          </tr>
        </table>

        <jsp:directive.include file="headers/main.jsp" />
        <mm:write referid="main">
          <!-- how about dynamic includes here ? -->
          <mm:compare value="principles"><%@ include file="principles/index.jsp" %></mm:compare>
          <mm:compare value="principlesets"><%@ include file="principlesets/index.jsp" %></mm:compare>
          <mm:compare value="importexport"><%@ include file="importexport/index.jsp" %></mm:compare>
        </mm:write>
      </center>
    </body>
  </mm:cloud>
</html>
