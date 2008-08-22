<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="nl.didactor.component.Component,java.util.*,org.mmbase.bridge.Cloud,javax.servlet.http.HttpServletRequest" %>
<%!
  public String printSetting(HttpServletRequest req, JspWriter out, Component.Setting setting, Component component, Cloud cloud, String objectnum, String settingDefault) throws java.io.IOException {
    String newValue = req.getParameter("realinput_" + objectnum);
    if (newValue != null) {
        component.setObjectSetting(setting.getName(), Integer.parseInt(objectnum), cloud, newValue);
    }
    String value = null;
    Object objectSetting = component.getObjectSetting(setting.getName(), Integer.parseInt(objectnum), cloud);
    if (objectSetting != null) {
      value = objectSetting.toString();
    }
    String editValue = "";
    out.print("<span id='val_" + objectnum + "'>");
    if (value == null && settingDefault == null) {
      out.print("<i><font color='#aaaaaa'>(empty)</font></i>");
    } else if (value == null && settingDefault != null) {
      out.print("<i><font color='#aaaaaa'>" + settingDefault + "</font></i>");
      value = settingDefault;
      editValue = settingDefault;
    } else {
      out.print(value);
      editValue = value;
    }
    out.print("&nbsp;<a href=\"javascript: edit('" + objectnum + "');\">[edit]</a>");
    out.println("</span>");
    out.print("<span id='ipt_" + objectnum + "' style='display: none;'>");
    switch(setting.getType()) {
      case Component.Setting.TYPE_INTEGER:
          out.print("<input type='text' id='input_" + objectnum + "' name='input_" + objectnum + "' width='10' value=\"" + editValue.replaceAll("\"", "&quot;") + "\" />");
          break;
      case Component.Setting.TYPE_BOOLEAN:
      case Component.Setting.TYPE_DOMAIN:
          out.print("<select id='input_" + objectnum + "' name='input_" + objectnum + "'>");
          String[] domain = setting.getDomain();
          String selected = "";
          for (int i=0; i<domain.length; i++) {
            if (domain[i].equals(editValue)) {
              selected = "selected=\"true\"";
            } else {
              selected = "";
            }
            out.print("  <option " + selected + " value=\"" + domain[i] + "\">" + domain[i] +  "</option>");
          }
          out.println("</select>");
          break;
      case Component.Setting.TYPE_STRING:
          out.print("<input type='text' id='input_" + objectnum + "' name='input_" + objectnum + "' width='100' value=\"" + editValue.replaceAll("\"", "&quot;") + "\" />");
          break;
    }
    out.print("<input type='submit' value='save' />");
    out.println("</span>");
    return value;
  }
%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="component" jspvar="component" />
<mm:import externid="setting" id="settingname" jspvar="settingname" />
<html>
  <head>
    <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
    <script type="text/javascript">
      function edit(onum) {
        var theSpan = document.getElementById("val_" + onum);
        theSpan.style.display = "none";
        var theInput = document.getElementById("ipt_" + onum);
        theInput.style.display = "inline";
        var theInputField = document.getElementById("input_" + onum);
        theInputField.name = "realinput_" + onum;
      }
    </script>
  </head>
  <body>
    <div style="margin-left: 10px">
      <di:hasrole role="systemadministrator">
        <form method="post" action="editsetting.jsp">
        <input type="hidden" name="component" value="<mm:write referid="component" />" />
        <input type="hidden" name="setting" value="<mm:write referid="settingname" />" />
        <mm:node number="$component">
          <h1><mm:field name="name" /></h1>
          Setting name: <mm:write referid="settingname" />
          <mm:import jspvar="cname"><mm:field name="name" /></mm:import>
          <%
            Component comp = Component.getComponent(cname);
            if (comp != null) {
              Component.Setting setting = (Component.Setting)comp.getSettings().get(settingname);
              List scopes = setting.getScope();
            %>
            <hr />
            <h2>Waarden</h2>
            Default: 
            <% if (scopes.contains("component")) { 
               String componentValue = printSetting(request, out, setting, comp, cloud, component, null);
               %> 
               <% if (scopes.contains("providers")) { %>
                <ul>
                <mm:relatednodes type="providers" role="settingrel">
                  <li>
                  <mm:import jspvar="n_provider" vartype="String"><mm:field name="number" /></mm:import>
                  <mm:field name="name"/>:
                  <%
                    String providerValue = printSetting(request, out, setting, comp, cloud, n_provider, componentValue);
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
                        String educationValue = printSetting(request, out, setting, comp, cloud, n_education, providerValue);
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
                            String classValue = printSetting(request, out, setting, comp, cloud, n_class, educationValue);
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
        </form>
      </di:hasrole>
    </div>
  </body>
</html>
</mm:cloud>
</mm:content>
