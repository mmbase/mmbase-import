<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<mm:context id="login">
<mm:import externid="login" from="parameters" required="true" />
<mm:import externid="pwd"   from="parameters" required="true" />
<mm:import externid="orgpwd" from="parameters" />

<mm:cloud logon="forum" pwd="forum">

<mm:present referid="orgpwd">
  <mm:compare referid="orgpwd" value="${pwd}">
   <mm:createnode type="chatter" >
    <mm:setfield name="username"><mm:write referid="login" /></mm:setfield>
    <mm:setfield name="password"><mm:write referid="pwd"   /></mm:setfield>
   </mm:createnode>     
  </mm:compare>  
</mm:present>


<mm:import id="chattername"><mm:write referid="login" /></mm:import>
<mm:listnodes type="chatter" constraints="username='${login}' and password='${pwd}'"  max="1">
<mm:relatednodes type="people" max="1">
   <mm:remove referid="chattername" />
   <mm:import id="chattername"><mm:field name="firstname" /> <mm:field name="lastname" /></mm:import>
</mm:relatednodes>
<mm:import id="chatter"><mm:field name="number" /></mm:import>
<mm:write referid="chatter" jspvar="chatter">
<%
   session.setAttribute("chatter",chatter);
%>
</mm:write>
<mm:write referid="chattername" jspvar="chattername">
<%
   session.setAttribute("chattername",chattername);
%>
</mm:write>
</mm:listnodes>
<html>
<mm:notpresent referid="chatter">
 <mm:listnodes type="chatter" constraints="username='${login}'" max="1">
   <mm:import id="usernamefound" />
 </mm:listnodes>
 <mm:present referid="usernamefound">
   <body>   
   <strong>The given password was incorrect.</strong>
   <form action="login.jsp" method="post">
   <table>
   <tr><td>Login:</td><td><input type="text" name="login" size="10" value="<mm:write referid="login" />"></td></tr>
   <tr><td>Pasword:</td><td><input type="password" name="pwd" size="10"></td></tr>
   </table>
   <input type="submit" value="Submit">   
   </form>
  </mm:present>
  <mm:notpresent referid="usernamefound">
   <body>
   <strong>You're a new chatter, retype your password.</strong>
   <form action="login.jsp" method="post">
   <input type="hidden" name="orgpwd" value="<mm:write referid="pwd" />" />
   <input type="hidden" name="login"  value="<mm:write referid="login" />" />
   <table>
   <tr><td>Login:</td><td><mm:write referid="login" /></td></tr>
   <tr><td>Password:</td><td><input type="password" name="pwd" size="10"></td></tr>
   </table>
      <input type="submit" value="Submit">
   </p>
   </form>
  </mm:notpresent>
</mm:notpresent>
<mm:present referid="chatter">
  <head>
  <META HTTP-EQUIV="refresh" content="1; url=<mm:url page="chats.jsp" />" /> 
  </head>
  <body>
  <mmcommunity:connection channel="Chat" user="${chatter}" action="join" />
  <p>You're logged on as <mm:write referid="chattername" /></p>
  <p>You can now use the <a href="<mm:url page="chats.jsp" />">MMBase chat</a></p>
  <mm:node number="Chat" id="channel">
   <mmcommunity:post>
      <mm:setfield name="username"><mm:write referid="chattername"/></mm:setfield>
      <mm:setfield name="user"><mm:write referid="chatter"/></mm:setfield>
      <mm:setfield name="channel"><mm:field name="number" node="channel"/></mm:setfield>
      <mm:setfield name="body">enters the MMBase chatbox.</mm:setfield>
   </mmcommunity:post>    
  </mm:node>
</mm:present>

</mm:cloud>
</mm:context>
</body>
</html>