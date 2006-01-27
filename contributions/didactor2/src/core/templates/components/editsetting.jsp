<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="nl.didactor.component.Component,java.util.*,org.mmbase.bridge.Cloud,java.io.PrintWriter" %>
<%!
  public String printSetting(JspWriter out, Component.Setting setting, Component component, Cloud cloud, String objectnum, String settingDefault) throws java.io.IOException {
    String value = component.getObjectSetting(setting.getName(), Integer.parseInt(objectnum), cloud);
    out.print("<span id='val_" + objectnum + "'>");
    if (value == null && settingDefault == null) {
      out.print("<i><font color='#aaaaaa'>(empty)</font></i>");
    } else if (value == null && settingDefault != null) {
      out.print(settingDefault);
    } else {
      out.print(value);
    }
    out.print("&nbsp;<a href=\"javascript: edit('" + objectnum + "');\">[edit]</a>");
    out.println("</span>");
    out.print("<span id='ipt_" + objectnum + "' style='display: none;'>");
    out.print("<form method='post'>");
    switch(setting.getType()) {
      case Component.Setting.TYPE_INTEGER:
          break;
      case Component.Setting.TYPE_BOOLEAN:
          break;
      case Component.Setting.TYPE_DOMAIN:
          break;
      case Component.Setting.TYPE_STRING:
          out.print("<input type='text' />");
          break;
    }
    out.print("<input type='submit' value='save' />");
    out.print("</form>");
    out.println("</span>");
    return value;
  }
%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="component" jspvar="component" />
<mm:import externid="setting" id="settingname" jspvar="settingname" />

<script type="text/javascript">
  function edit(onum) {
    var theSpan = document.getElementById("val_" + onum);
    theSpan.style.display = "none";
    var theInput = document.getElementById("ipt_" + onum);
    theInput.style.display = "inline";
  }
</script>

      <di:hasrole role="systemadministrator">
        <mm:node number="$component">
          <h1><mm:field name="name" /></h1>
          Setting name: <mm:write referid="settingname" />
          <mm:import jspvar="cname"><mm:field name="name" /></mm:import>
          <%
            Component comp = Component.getComponent(cname);
            if (comp != null) {
              Component.Setting setting = (Component.Setting)comp.getSettings().get(settingname);
              Vector scopes = setting.getScope();
            %>
            <hr />
            <h2>Waarden</h2>
            Default: 
            <% if (scopes.contains("component")) { 
               String componentValue = printSetting(out, setting, comp, cloud, component, null);
               %> 
               <% if (scopes.contains("providers")) { %>
                <ul>
                <mm:relatednodes type="providers" role="settingrel">
                  <li>
                  <mm:import jspvar="n_provider" vartype="String"><mm:field name="number" /></mm:import>
                  <mm:field name="name"/>:
                  <%
                    String providerValue = printSetting(out, setting, comp, cloud, n_provider, componentValue);
                  %>
                  <ul>
                  <mm:relatednodes type="educations">
                    <mm:import id="show_education" reset="true">false</mm:import>
                    <mm:relatedcontainer path="settingrel,components">
                      <mm:constraint field="components.number" value="$component"/>
                      <mm:size>
                        <mm:isgreaterthan value="0">
                          <mm:import id="show_education" reset="true">true</mm:import>
                        </mm:isgreaterthan>
                      </mm:size>
                    </mm:relatedcontainer>
                    <mm:compare referid="show_education" value="true">
                      <li>
                      <mm:import jspvar="n_education" vartype="String"><mm:field name="number" /></mm:import>
                      <mm:field name="name"/>:
                      <%
                        String educationValue = printSetting(out, setting, comp, cloud, n_education, providerValue);
                      %>
                      <ul>
                      <mm:relatednodes type="classes">
                        <mm:import id="show_class" reset="true">false</mm:import>
                        <mm:relatedcontainer path="settingrel,components">
                          <mm:constraint field="components.number" value="$component"/>
                          <mm:size>
                            <mm:isgreaterthan value="0">
                              <mm:import id="show_class" reset="true">true</mm:import>
                            </mm:isgreaterthan>
                          </mm:size>
                        </mm:relatedcontainer>
                        <mm:compare referid="show_class" value="true">
                          <li>
                          <mm:import jspvar="n_class" vartype="String"><mm:field name="number" /></mm:import>
                          <mm:field name="name"/>:
                          <%
                            String classValue = printSetting(out, setting, comp, cloud, n_class, educationValue);
                          %>
                          </li>
                        </mm:compare>
                      </mm:relatednodes>
                      </ul>
                      </li>
                    </mm:compare>
                  </mm:relatednodes>
                  </ul>
                  </li>
                </mm:relatednodes>
                </ul>
              <% } %>
            <% } %>
          <% } %>
        </mm:node>
      </di:hasrole>

</mm:cloud>
</mm:content>
