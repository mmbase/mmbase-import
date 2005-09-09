<%--
  This template shows the personal workspace (in dutch: persoonlijke werkruimte).
--%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>



<mm:import externid="agree"/>
<mm:present referid="agree">
    <mm:node referid="education">
    <mm:field name="number" jspvar="educationNo">
       <% session.setAttribute(username+"_has_agreed_to_education_licence_"+educationNo,"YES"); %>
    </mm:field>
    </mm:node>
    <mm:import jspvar="redirectTo" escape="none"><mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$referids" escape="none"/></mm:import>
    <% response.sendRedirect(redirectTo.replaceAll("&amp;","&")); %>
</mm:present>

<mm:import externid="notagree"/>
<mm:present referid="notagree">
    <mm:import jspvar="redirectTo" reset="true" escape="none"><mm:treefile page="/index.jsp" objectlist="$includePath" referids="$referids" escape="none"/></mm:import>
    <% response.sendRedirect(redirectTo.replaceAll("&amp;","&")); %>
   
</mm:present>


<fmt:bundle basename="nl.didactor.component.drm.DRMMessageBundle">
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><fmt:message key="LICENSE" /></title>
  </mm:param>
</mm:treeinclude>

<div class="rows">

<div class="navigationbar">
<div class="titlebar">
      <fmt:message key="LICENSE" />
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
  </div>

  <div class="contentSubHeader">
  </div>

  <div class="contentBody">
    <mm:node referid="education">
        <mm:relatednodes type="licensetexts">
            <h1><mm:field name="title"/></h1>
            <mm:field name="text" escape="none"/>
        </mm:relatednodes>
    </mm:node>

  <form method="POST">
    <input type="submit" name="agree" value="<fmt:message key="AGREE"/>"> <input type="submit" name="notagree" value="<fmt:message key="NOTAGREE"/>">
  </form>

  </div>
</div>
</div>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</fmt:bundle>
</mm:cloud>
</mm:content>
