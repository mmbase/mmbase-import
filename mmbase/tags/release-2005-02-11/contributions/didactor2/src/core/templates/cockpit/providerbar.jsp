<%--
  Show all components that are related to the current provider. 
  There are several 'default' components that will be shown in the
  standard layout: as hyperlinks from left to right. All other
  components that are directly related to the provider object will
  be placed in a dropdown box.

--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud jspvar="cloud" method="asis">
<%@include file="/shared/setImports.jsp" %>

<div style="display:none;">
    <mm:treeinclude write="true" page="/shared/onlineReporter.jsp" objectlist="$includePath" referids="$referids" />
</div>
 

<div class="providerMenubar" style="white-space: nowrap">
  <mm:import id="providerbaritems" vartype="list">search,pop,address,agenda,portfolio,email</mm:import>
  
  <%-- first show all the items in a predefined order --%>
  <mm:stringlist referid="providerbaritems">
    <mm:import id="pname"><mm:write /></mm:import>
    <mm:node number="$provider">
      <mm:relatedcontainer path="settingrel,components">
        <mm:constraint field="components.name" referid="pname" />
        <mm:related>
          <mm:treeinclude page="/$pname/cockpit/menuitem.jsp" objectlist="$includePath" referids="$referids">
            <mm:param name="name"><mm:write referid="pname" /></mm:param>
            <mm:param name="number"><mm:field name="number" /></mm:param>
            <mm:param name="type">div</mm:param>
            <mm:param name="scope">provider</mm:param>
          </mm:treeinclude>
        </mm:related>
      </mm:relatedcontainer>
    </mm:node>
    <mm:remove referid="pname" />
  </mm:stringlist>

  <%-- then show all the other components in a dropdown box --%>
  <mm:node number="$provider">
    <mm:related path="settingrel,components">
      <mm:first>
        <mm:import id="extraComponentsHeader">
          <div class="menuItemExtra">
            <script type="text/javascript">
              function gotourl() {
                var url = document.getElementById("extracomponents").value;
                if (url != "-")
                  document.location = url;
              }
            </script>
            <form name="switchpageform" action="<mm:treefile page="/cockpit/switchpage.jsp" objectlist="$includePath" referids="$referids" />">
            <select name="extracomponents" onchange="gotourl();" id="extracomponents">
            <option value="-">extra functionaliteiten</option>
        </mm:import>
      </mm:first>
      <mm:node element="components">
        <mm:field id="name" name="name" write="false" />
        <mm:compare referid="name" valueset="$providerbaritems" inverse="true">
          <mm:import jspvar="cpitem" vartype="String">
          <mm:treeinclude page="/$name/cockpit/menuitem.jsp" objectlist="$includePath" referids="$referids">
            <mm:param name="name"><mm:field name="name" /></mm:param>
            <mm:param name="number"><mm:field name="number" /></mm:param>
            <mm:param name="type">option</mm:param>
            <mm:param name="scope">provider</mm:param>
          </mm:treeinclude>
          </mm:import>
          <% if (!cpitem.trim().equals("")) { 
               if (cpitem.indexOf("<option") > -1) {
                %>
                <%-- we will only write the <select> box in case there is really an <option> for it --%>        
                <mm:write referid="extraComponentsHeader" />  
                <%=cpitem%>
                <mm:remove referid="extraComponentsHeader" />
                <mm:import id="extraComponentsHeader" />
                <% 
               } else { %>
                <%-- otherwise just print the item, some components use this to put other stuff in the header --%>
                  <%= cpitem %><%
               }       
            } %>
        </mm:compare>
        <mm:remove referid="mayshow" />
        <mm:remove referid="name" />
      </mm:node>
      <mm:last>
        <mm:isempty referid="extraComponentsHeader">
          </select>
          <input id="gobutton" type="submit" name="go" value="go" />
          </form>
          <script type="text/javascript">
            document.getElementById("gobutton").style.display = "none";
          </script>  
        </div>  
        </mm:isempty>
      </mm:last>
    </mm:related>
  </mm:node>
  <mm:remove referid="providerbaritems" />
</div>
</mm:cloud>
