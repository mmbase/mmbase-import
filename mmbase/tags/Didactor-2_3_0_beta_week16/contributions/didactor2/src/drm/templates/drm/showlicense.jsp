<%--
  This template shows the personal workspace (in dutch: persoonlijke werkruimte).
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
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


<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="drm.license" /></title>
  </mm:param>
</mm:treeinclude>

<div class="rows">

<div class="navigationbar">
<div class="titlebar">
      <di:translate key="drm.license" />
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
    <input type="submit" name="agree" value="<di:translate key="drm.agree" />"> <input type="submit" name="notagree" value="<di:translate key="drm.notagree" />">
  </form>

  </div>
</div>
</div>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
