<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
 %><%@page import="org.mmbase.bridge.*,org.mmbase.module.core.MMBase" 
%><mm:content expires="0">
<mm:cloud rank="administrator">
  
<mm:import externid="server" required="true" />
  
<html>
  
<head>
  <title>Administrate Server <mm:write referid="server" /></title>
  <link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
</head>
<body class="basic" >
  <table summary="server actions">
    <tr><th class="header" colspan="2" >Administrate Server: <mm:write referid="server" /></th></tr>
    <tr>
      <td class="multidata" colspan="2">
        <p>
          JVM memory size : <%=(Runtime.getRuntime().totalMemory()*10/1048576)/10.0%> Mb (<%=(Runtime.getRuntime().totalMemory()*10/1024)/10.0%> Kb)
        </p>
        <p>
          JVM free memory : <%=(Runtime.getRuntime().freeMemory()*10/1048576)/10.0%> Mb (<%=(Runtime.getRuntime().freeMemory()*10/1024)/10.0%> Kb)
        </p>
        <p>
          Uptime: 
          <%        
          int timeDiff =  ((int)(System.currentTimeMillis()/1000) - MMBase.getMMBase().startTime);
          
          int days = timeDiff / (60 * 60 * 24);
          int hours =(timeDiff / (60  * 60)) % 24;
          int minutes = (timeDiff / 60) % 60 ;
          int seconds = timeDiff % 60;
          out.println("" + (days > 0 ? (days +" days ") : "") + hours +":" + (minutes < 10 ? "0" : "") + minutes +":" + (seconds  < 10 ? "0" : "") + seconds);
          %>
        </p>
      </td>
    </tr>   
    <tr class="footer">
      <td class="navigate"><a href="<mm:url page="../servers.jsp" />"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
      <td class="data">Return&nbsp;to&nbsp;Server&nbsp;Overview</td>
    </tr>
  </table>
</body>
</html>
</mm:cloud>
</mm:content>
