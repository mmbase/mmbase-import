<%--
  This template shows all components in the current education
--%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">


<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <!-- TODO translate -->
    <title><fmt:message key="COMPONENT" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import id="linkedlist" jspvar="linkedlist" vartype="List"/>

<mm:node number="$provider">
  <mm:relatednodes type="components">
    <mm:remove referid="objectnumber"/>
    <mm:import id="objectnumber" jspvar="objectnumber"><mm:field name="number"/></mm:import>
    <%
      linkedlist.add( objectnumber );
    %>
  </mm:relatednodes>
</mm:node>

<mm:node number="$education" notfound="skip">
  <mm:relatednodes type="components">
    <mm:remove referid="objectnumber"/>
    <mm:import id="objectnumber" jspvar="objectnumber"><mm:field name="number"/></mm:import>
    <%
      linkedlist.add( objectnumber );
    %>
  </mm:relatednodes>
</mm:node>


<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <!-- TODO translate -->
    <img src="<mm:treefile write="true" page="/gfx/icon_addressbook.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" alt="<fmt:message key="COMPONENT" />"/>
    <fmt:message key="COMPONENT" />
  </div>
</div>

<div class="folders">
  <div class="folderHeader">
    <!-- TODO translate -->
    Context
  </div>
  <div class="folderBody">
    <mm:node number="$provider" notfound="skip">
      <fmt:message key="PROVIDER" /><mm:field name="name"/>
    </mm:node>
    </br>
    <mm:node number="$education" notfound="skip">
      <fmt:message key="EDUCATION" /><mm:field name="name"/>
    </mm:node>
  </div>
</div>

<div class="maincontent">

  <div class="contentHeader">
    Componenteditor
  </div>

  <div class="contentSubHeader">
    <a href="<mm:treefile page="/components/editcomponent.jsp" objectlist="$includePath" referids="$referids">
               <mm:param name="component">-1</mm:param>
               <mm:param name="callerpage">/components/index.jsp</mm:param>
             </mm:treefile>"><fmt:message key="NEWCOMPONENT" /></a>

  </div>

  <div class="contentBody">

      <mm:listnodescontainer type="components">
        <mm:constraint field="number" referid="linkedlist" operator="IN"/>

        <di:table maxitems="10">

	      <di:row>
	        <di:headercell>Level</di:headercell>
	        <di:headercell sortfield="name" default="true"><fmt:message key="COMPONENT" /></di:headercell>
	        <di:headercell sortfield="classname"><fmt:message key="CLASSNAME" /></di:headercell>
	      </di:row>

          <mm:listnodes>

            <di:row>
              <mm:remove referid="link"/>
			  <mm:import id="link"><a href="<mm:treefile page="/components/editcomponent.jsp" objectlist="$includePath" referids="$referids">
                                              <mm:param name="component"><mm:field name="number"/></mm:param>
                                              <mm:param name="callerpage">/components/index.jsp</mm:param>
			                                </mm:treefile>">
			  </mm:import>
              <di:cell>
                <mm:relatednodes type="providers" max="1">
                  <mm:write referid="link" escape="none"/><mm:nodeinfo type="type"/></a>
                </mm:relatednodes>
				<mm:relatednodes type="educations" max="1">
				  <mm:write referid="link" escape="none"/><mm:nodeinfo type="type"/></a>
				</mm:relatednodes>
              </di:cell>
              <di:cell><mm:write referid="link" escape="none"/><mm:field name="name"/></a></di:cell>
              <di:cell><mm:write referid="link" escape="none"/><mm:field name="classname"/></a></di:cell>
            </di:row>

          </mm:listnodes>

        </di:table>

      </mm:listnodescontainer>
  </div>
</div>
</div>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:cloud>
</mm:content>
