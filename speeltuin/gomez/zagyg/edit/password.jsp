<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page language="java" contentType="text/html;charset=utf-8"
%><%@page errorPage="passworderror.jsp"
%><%@ include file="util/nocache.jsp"
%><mm:import externid="language">nl</mm:import>
<mm:import externid="submit"  />
<mm:content language="$language" postprocessor="reducespace" expires="0">
<html>
  <head>
    <title>Change Password</title>
    <link rel="icon" href="<mm:url id="favi" page="images/edit.ico" />" type="image/x-icon" />
    <link rel="shortcut icon" href="<mm:write referid="favi" />" type="image/x-icon" />
    <link rel="stylesheet" href="css/edit.css" />
  </head>
  <body>
    <mm:cloud loginpage="login.jsp" jspvar="cloud">
      <mm:listnodescontainer type="mmbaseusers">
        <mm:constraint field="username" value="<%=cloud.getUser().getIdentifier()%>" />
        <mm:size>
          <mm:compare value="1" inverse="true">
            <mm:log>Did not find one user for user <%=cloud.getUser().getIdentifier()%> but <mm:write /></mm:log>
          </mm:compare>
        </mm:size>
        <mm:listnodes id="usernode">
          <mm:notpresent referid="submit">
            <h1>Change your password</h1>
            Account: <mm:field name="username" /><br />
            <form action="">
              <mm:fieldlist fields="password">
                <mm:fieldinfo type="guiname" />: <mm:fieldinfo type="input" /><br />
              </mm:fieldlist>
              <mm:fieldlist fields="confirmpassword">
                <mm:fieldinfo type="guiname" />: <mm:fieldinfo type="input" /><br />
              </mm:fieldlist>
              <input type="submit" name="submit" value="submit" /><br />
            </form>
          </mm:notpresent>
          <mm:present referid="submit">
            <mm:fieldlist fields="password">
              <mm:fieldinfo type="useinput" />
            </mm:fieldlist>
            <mm:setfield name="status">1</mm:setfield>
            Thank you.
            <a href="<mm:url page="index.jsp" />">To the editors</a>
          </mm:present>
        </mm:listnodes>
      </mm:listnodescontainer>
    </mm:cloud>
  </body>
</html>
</mm:content>