<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%-- expires is 0; show always new content --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:node number="$user">
  <%-- Get the mayforward setting for the email-component --%>
  <mm:listrelations role="settingrel">
    <mm:relatednodes type="settings" role="related">
      <mm:remove referid="setting"/>
      <mm:import id="setting"><mm:field name="name"/></mm:import>
      <mm:remove referid="value"/>
      <mm:import id="value"><mm:field name="value"/></mm:import>
      <mm:compare referid="setting" value="mayforward">
        <mm:import id="mayforwardvalue"><mm:write referid="value" /></mm:import>
      </mm:compare>
    </mm:relatednodes>
  </mm:listrelations>
  <mm:present referid="mayforwardvalue" inverse="true">
    <mm:import id="mayforwardvalue">0</mm:import>
  </mm:present>
  <tr>
    <td/>
    <td>
      <mm:compare referid="mayforwardvalue" value="0">
        <input type="checkbox" name="mayforward"/>
      </mm:compare>
      <mm:compare referid="mayforwardvalue" value="1">
        <input type="checkbox" name="mayforward" checked/>
      </mm:compare>
      <di:translate key="core.mayforwardemail" />
    </td>
  </tr>
</mm:node>
</mm:cloud>
</mm:content>
