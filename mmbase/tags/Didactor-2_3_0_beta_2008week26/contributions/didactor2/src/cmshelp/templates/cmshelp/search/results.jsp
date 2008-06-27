<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@include file="/search/constraintBuilder.jsp"%>
<mm:import externid="search_query"/>
<% if ("".equals(request.getParameter("search_component")) || "cmshelp".equals(request.getParameter("search_component"))) { %>

  
  <mm:list path="simplecontents"  constraints="<%= searchConstraints("simplecontents.title", request) %>">
    <tr>
      <td class="listItem"><di:translate key="cmshelp.title" /></td> 
      <td class="listItem">
        <a href="<mm:treefile page="/cmshelp/frontoffice/index.jsp" objectlist="$includePath" referids="$referids">
                   <mm:param name="simplecontents"><mm:field name="simplecontents.number"/></mm:param>
                 </mm:treefile>"><mm:field name="simplecontents.title"/></a></td>
    </tr>
  </mm:list>
  
  <mm:list path="simplecontents"  constraints="<%= searchConstraints("simplecontents.title", request) %>">
    <tr>
      <td class="listItem"><di:translate key="cmshelp.abstract" /></td> 
      <td class="listItem">
        <a href="<mm:treefile page="/cmshelp/frontoffice/index.jsp" objectlist="$includePath" referids="$referids">
                   <mm:param name="simplecontents"><mm:field name="simplecontents.number"/></mm:param>
                 </mm:treefile>"><mm:field name="simplecontents.abstract"/></a></td>
    </tr>
  </mm:list>
  
  
  <mm:list path="simplecontents"  constraints="<%= searchConstraints("simplecontents.abstract", request) %>">
    <tr>
      <td class="listItem"><di:translate key="cmshelp.body" /></td> 
      <td class="listItem">
        <a href="<mm:treefile page="/cmshelp/frontoffice/index.jsp" objectlist="$includePath" referids="$referids">
                   <mm:param name="simplecontents"><mm:field name="simplecontents.number"/></mm:param>
                 </mm:treefile>"><mm:field name="simplecontents.body"/></a></td>
    </tr>
  </mm:list>
  
  
  
  

<% } %>


</mm:cloud>
</mm:content>
