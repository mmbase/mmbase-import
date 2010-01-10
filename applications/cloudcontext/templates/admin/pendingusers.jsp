<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html; charset=UTF-8" errorPage="../error.jsp"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0"   prefix="mm"
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
    <mm:cloud loginpage="../login.jsp" rank="administrator">
      <h1>This page links all users which are not in any group to one certain group</h1>
      <mm:import externid="submit" />
      <mm:notpresent referid="submit">
        <form>
          Select group
          <select name="group">
            <mm:listnodes type="mmbasegroups">
              <option value="${_node}"><mm:field name="name" /></option>
            </mm:listnodes>
          </select>
          Select rank
          <select name="rank">
            <mm:listnodes type="mmbaseranks">
              <option value="${_node}"><mm:field name="name" /></option>
            </mm:listnodes>
          </select>
          <input type="submit" name="submit" />
        </form>
      </mm:notpresent>
      <mm:present referid="submit">
        <mm:import externid="group" />
        <mm:node referid="group" id="group">
          <h2>Connecting to group <mm:field name="name" /></h2>
        </mm:node>
        <mm:import externid="rank" />
        <mm:node referid="rank" id="rank">
          <h2>Connecting to rank <mm:field name="name" /></h2>
        </mm:node>
        <mm:listnodes type="mmbaseusers">
          <mm:countrelations type="mmbasegroups">
            <mm:compare value="0">
              <p>
                <mm:field name="username" />: needs linking with group
                <mm:createrelation source="group" destination="_node" role="contains" />
              </p>
            </mm:compare>
          </mm:countrelations>
          <mm:countrelations type="mmbaseranks">
            <mm:compare value="0">
              <p>
                <mm:field name="username" />: needs linking with rank
                <mm:createrelation source="_node" destination="rank" role="rank" />
              </p>
            </mm:compare>
          </mm:countrelations>
        </mm:listnodes>
      </mm:present>
    </mm:cloud>

  </body>
</html>
</mm:content>
