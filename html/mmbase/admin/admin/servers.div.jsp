<%@ page import="org.mmbase.bridge.*,org.mmbase.module.core.MMBase" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase">
<mm:import externid="server" />
<div
  class="component mm_c_core mm_c_b_servers ${requestScope.className}"
  id="${requestScope.componentId}">
<h3>Server Overview</h3>
<mm:notpresent referid="server">
  <table summary="servers" border="0" cellspacing="0" cellpadding="3">
    <caption>
      This overview describes all MMBase servers running on this MMBase system
    </caption>
    <tr>
      <th>Machine</th>
      <th>State</th>
      <th>Last Seen</th>
      <th>Host</th>
      <th>OS</th>
      <th>Manage</th>
    </tr>
  <mm:listnodes type="mmservers" >
    <tr>
      <td>
        <mm:field name="name">
          <mm:compare value="<%= MMBase.getMMBase().getMachineName() %>">
            <mm:link referids="_@server">
              <mm:param name="component">core</mm:param>
              <a href="${_}"><mm:field name="name" /></a>
            </mm:link>
          </mm:compare>
          <mm:compare value="<%= MMBase.getMMBase().getMachineName() %>" inverse="true">
            <mm:write />
          </mm:compare>
        </mm:field>
      </td>
      <td><mm:field name="state"><mm:fieldinfo type="guivalue" /></mm:field></td>
      <td><mm:field name="atime"><mm:time format=":MEDIUM.MEDIUM" /></mm:field></td>
      <td><mm:field name="host" /></td>
      <td><mm:field name="os" /></td>
      <td class="view">
        <mm:field name="name">
          <mm:compare value="<%= MMBase.getMMBase().getMachineName() %>">
            <mm:link referids="_@server">
              <mm:param name="component">core</mm:param>
              <a href="${_}"><img src="<mm:url page="/mmbase/style/images/next.gif" />" alt="next" /></a>
            </mm:link>
          </mm:compare>
        </mm:field>
      </td>
    </tr>
  </mm:listnodes>
  </table>
</mm:notpresent>
<mm:present referid="server">
  <table summary="server actions">
    <caption>Administrate Server: <mm:write referid="server" /></caption>
    <tr>
      <td>
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
  </table>
</mm:present>

</div>
</mm:cloud>
