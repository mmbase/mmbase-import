<%--
  This template adds a new component to a provider or education.
--%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <!-- TODO translate -->
    <title><fmt:message key="EDITCOMPONENT" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="component"/>
<mm:import externid="callerpage"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>

<mm:import externid="componentname"/>
<!-- TODO check whether a value is given for level -->
<mm:import externid="level"/>


<%-- Check if the create button is pressed --%>
<mm:import id="action1text"><fmt:message key="CREATE" /></mm:import>
<mm:compare referid="action1" referid2="action1text">
  <mm:compare referid="component" value="-1">
    <mm:listnodescontainer type="components">
  	  <mm:constraint field="name" referid="componentname"/>

	  <mm:listnodes>
	    <mm:remove referid="component"/>
	    <mm:field id="component" name="number" write="false"/>
	  </mm:listnodes>

    </mm:listnodescontainer>

    <%-- related component to provider or education --%>
    <mm:createrelation role="settingrel" source="level" destination="component"/>

  </mm:compare>

  <mm:redirect referids="$referids,component" page="$callerpage"/>
</mm:compare>


<%-- Check if the back button is pressed --%>
<mm:import id="action2text"><fmt:message key="BACK" /></mm:import>
<mm:compare referid="action2" referid2="action2text">
  <mm:redirect referids="$referids,component" page="$callerpage"/>
</mm:compare>


<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
  </div>
</div>

<div class="folders">
  <div class="folderHeader">
  </div>
  <div class="folderBody">
  </div>
</div>

<div class="mainContent">

  <div class="contentHeader">
    <!-- TODO translate -->
    <fmt:message key="EDITCOMPONENT" />
  </div>

  <div class="contentSubHeader">
    <mm:compare referid="component" value="-1" inverse="true">
      <!-- TODO translate -->
      <a href="<mm:treefile page="/components/deletecomponent.jsp" objectlist="$includePath" referids="$referids">
	                 <mm:param name="component"><mm:write referid="component"/></mm:param>
	                 <mm:param name="callerpage"><mm:write referid="callerpage"/></mm:param>
               </mm:treefile>">delete component</a>
    </mm:compare>
  </div>

  <div class="contentBody">

    <%-- Show the form --%>
    <form name="editcomponentform" method="post" action="<mm:treefile page="/components/editcomponent.jsp" objectlist="$includePath" referids="$referids"/>">


      <table class="Font">

        <!-- TODO show only components not yet in education or provider -->
        <mm:compare referid="component" value="-1">
          <mm:listnodes type="components" orderby="name">
            <mm:first><tr><td><select name="componentname"></mm:first>
            <option><mm:field name="name"/></option>
            <mm:last></select></td><td/></tr></mm:last>
          </mm:listnodes>
        </mm:compare>

        <mm:compare referid="component" value="-1" inverse="true">

          <mm:node number="$component" notfound="skip">
            <mm:fieldlist nodetype="components" fields="name,classname">
              <tr>
              <td><mm:fieldinfo type="guiname"/></td>
              <td><mm:fieldinfo type="value"/></td>
              </tr>
            </mm:fieldlist>
          </mm:node>

        </mm:compare>

        <!-- TODO check if the right level is given for a specific component -->
        <tr><td><input type="radio" name="level" value="<mm:write referid="provider"/>">provider</input></td><td/></tr>
        <tr><td><input type="radio" name="level" value="<mm:write referid="education"/>">education</input></td><td/></tr>

      </table>
      <p/>

      <input type="hidden" name="component" value="<mm:write referid="component"/>"/>
      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>

      <mm:compare referid="component" value="-1">
        <input class="formbutton" type="submit" name="action1" value="<fmt:message key="CREATE" />"/>
      </mm:compare>
      <mm:compare referid="component" value="-1" inverse="true">
        <input class="formbutton" type="submit" name="action1" value="<fmt:message key="UPDATE" />"/>
      </mm:compare>

      <input class="formbutton" type="submit" name="action2" value="<fmt:message key="BACK" />"/>

    </form>

  </div>
</div>
</div>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:cloud>
</mm:content>
