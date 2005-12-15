<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace" expires="0" type="text/html" encoding="UTF-8" escaper="entities">
<mm:cloud jspvar="cloud" name="mmbase">

<mm:import externid="installanyway">false</mm:import>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="nl">
  <head>
    <link rel="stylesheet" type="text/css" href="css/base.css" />
  </head>
  <body>

<div class="columns">
  <div class="columnMiddle">
    <img src="gfx/logo_didactor.gif" border="0" alt="Didactor logo" />

<mm:compare referid="installanyway" value="false"> 
  <h1>installation warning</h1>
  The admin user,default provider and component(s) were probably allready installed during autodeploy of applications. <br />
  Please choose from the following:
  <br />
  <ul>
    <li><a href="install.jsp?installanyway=true">I'm sure, install it anyway.</a></li>
    <li>Well okay, let me <a href="index.jsp">login</a> (recommended)</li>
  </ul>
</mm:compare>

<mm:compare referid="installanyway" value="true">

  <mm:createnode type="people">
    <mm:setfield name="username">admin</mm:setfield>
    <mm:setfield name="password">${cloudprovider.adminpassword}</mm:setfield>
  </mm:createnode>
  <mm:createnode type="providers" id="provider">
    <mm:setfield name="name">provider</mm:setfield>
  </mm:createnode>
  <mm:listnodes type="components">
    <mm:node id="comp" />
    <mm:createrelation source="provider" destination="comp" role="settingrel" />
    <mm:remove referid="comp" />
  </mm:listnodes>
  <h1>installation successfull</h1>
  <ul>
    <li>User "admin" was created. </li>
    <li>Default provider and components are now installed. </li>
  </ul>
  You may now <a href="index.jsp">login</a>

</mm:compare>

</div>
</div>

</body>
</html>

</mm:cloud>
</mm:content>
