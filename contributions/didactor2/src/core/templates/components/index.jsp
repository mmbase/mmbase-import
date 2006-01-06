<%--
  This template shows all components in the current education
--%>

<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>



<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

   <%@include file="/shared/setImports.jsp" %>

   <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
      <mm:param name="extraheader">
         <!-- TODO translate -->
         <title><di:translate key="core.component" /></title>
      </mm:param>
   </mm:treeinclude>


<div class="rows">
   <div class="navigationbar">
      <div class="titlebar">
         <!-- TODO translate -->
         <img src="<mm:treefile write="true" page="/gfx/icon_addressbook.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" title="<di:translate key="core.component" />" alt="<di:translate key="core.component" />"/>
         <di:translate key="core.component" />
      </div>
   </div>

   <div class="folders">
      <div class="folderHeader">
         <!-- TODO translate -->
         Context
      </div>

      <div class="folderBody">
         <mm:node number="$provider" notfound="skip">
            <di:translate key="core.provider" /><mm:field name="name"/>
         </mm:node>

         </br>

         <mm:node number="$education" notfound="skip">
            <di:translate key="core.education" /><mm:field name="name"/>
         </mm:node>
      </div>
   </div>

   <div class="maincontent">
      <div class="contentHeader">Componenteditor</div>
      <%
         String sReturnURL = request.getRequestURL().toString();
      %>
      <mm:import id="components_show_cockpit" reset="true">true</mm:import>
      <mm:import id="link_to_main" reset="true"><mm:treefile page="/components/index.jsp" objectlist="$includePath" referids="$referids" /></mm:import>
      <%@include file="body.jsp"%>
   </div>

</div>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:cloud>
</mm:content>

