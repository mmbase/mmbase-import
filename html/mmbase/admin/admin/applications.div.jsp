<%@ page import="org.mmbase.module.core.MMBase" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud rank="administrator" loginpage="login.jsp">
<mm:import externid="application" />
<div
  class="component ${requestScope.componentClassName}"
  id="${requestScope.componentId}">
<mm:notpresent referid="application">
  <h3>Administrate Applications</h3>
  <table summary="applications" border="0" cellspacing="0" cellpadding="3">
    <caption>
      This overview lists all MMBase applications known to this system.
    </caption>
    <tr>
      <th>Name</th>
      <th>Version</th>
      <th>Installed</th>
      <th>Maintainer</th>
      <th>Auto-Deploy</th>
      <th class="center">Manage</th>
    </tr>
    <mm:nodelistfunction module="mmadmin" name="APPLICATIONS">
      <tr>
        <td>
          <mm:link page="applications" component="core">
            <mm:param name="application"><mm:field name="item1" /></mm:param>
            <a href="${_}" title="manage application"><mm:field name="item1" /></a>
          </mm:link>
        </td>
        <td><mm:field name="item2" /></td>
        <td><mm:field name="item3" /></td>
        <td><mm:field name="item4" /></td>
        <td><mm:field name="item5" /></td>
        <td class="center">
          <mm:link page="applications" component="core">
            <mm:param name="application"><mm:field name="item1" /></mm:param>
            <a href="${_}" title="manage application"><img src="<mm:url page="/mmbase/style/images/next.png" />" alt="manage" /></a>
          </mm:link>
        </td>
      </tr>
    </mm:nodelistfunction>
  </table>
</mm:notpresent>

<mm:present referid="application">

  <mm:import id="version"><mm:function module="mmadmin" name="VERSION" referids="application" /></mm:import>
  <mm:import id="installedversion"><mm:function module="mmadmin" name="INSTALLEDVERSION" referids="application" /></mm:import>

  <h3>Application <mm:write referid="application" /></h3>
  <p><mm:function module="mmadmin" name="DESCRIPTION" referids="application" /></p>
  
  <table border="0" cellspacing="0" cellpadding="3">
    <caption>Install the application <mm:write referid="application" />.</caption>
    <tr>
      <th>Action</th>
      <th>Version</th>
      <th>Installed version</th>
      <th class="center">Confirm</th>
    </tr>
    <tr>
     <td>Install <mm:write referid="application" /></td>
     <td>Version: <mm:function module="mmadmin" name="VERSION" referids="application" /></td>
     <td>
       <mm:compare referid="installedversion" value="-1" inverse="true">
         Version: <mm:write referid="installedversion" />
       </mm:compare>
     </td>
     <td class="center">
       <mm:islessthan referid="installedversion" value="$version">
         <mm:link page="applications-action" referids="application">
           <mm:param name="cmd" value="LOAD" />
           <a href="${_}"><img src="<mm:url page="/mmbase/style/images/ok.png" />" alt="OK" /></a>
         </mm:link>
       </mm:islessthan>
     </td>
    </tr>
  </table>

  <mm:link page="applications-action" referids="application" component="core">
    <form action="${_}" method="post">
  </mm:link>
    <input name="cmd" type="hidden" value="SAVE" />
    <table border="0" cellspacing="0" cellpadding="3">
      <caption>
        Backup or save the application <mm:write referid="application" />.
      </caption>
      <tr>
        <th>Action</th>
        <th>Path</th>
        <th class="center">Confirm</th>
      </tr><tr>
       <td>Save <mm:write referid="application" /></td>
       <td><input type="text" name="path" value="/tmp" size="72" /></td>
       <td class="center">
         <input type="image" src="<mm:url page="/mmbase/style/images/ok.png" />" alt="OK" />
       </td>
      </tr>
    </table>
  </form>

  <p>
    <mm:link page="applications">
      <a href="${_}"><img src="<mm:url page="/mmbase/style/images/back.png" />" alt="back" /></a>
    </mm:link>
    Return to Applications Overview
  </p>
</mm:present>

</div>
</mm:cloud>
