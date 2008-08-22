<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@include file="/search/constraintBuilder.jsp"%>
<mm:import externid="search_query"/>
<% if ("".equals(request.getParameter("search_component")) || "faq".equals(request.getParameter("search_component"))) { %>

  
  <mm:list path="faqitems"  constraints="<%= searchConstraints("faqitems.question", request) %>">
    <tr>
      <td class="listItem"><di:translate key="faq.question" /></td> 
      <td class="listItem">
        <a href="<mm:treefile page="/faq/frontoffice/index.jsp"  objectlist="$includePath" referids="$referids">
                   <mm:param name="faqitems"><mm:field name="faqitems.number"/></mm:param>
                   <mm:param name="node"><mm:field name="faqitems.number"/></mm:param>
		             </mm:treefile>"><mm:field name="faqitems.question"/></a></td>
	  </tr>
	</mm:list>
  
 
  
  <mm:list path="faqitems" constraints="<%= searchConstraints("faqitems.answer", request) %>">
    <tr>
      <td class="listItem"><di:translate key="faq.answer" /></td> 
      <td class="listItem">
        <a href="<mm:treefile page="/faq/frontoffice/index.jsp" objectlist="$includePath" referids="$referids">
                   <mm:param name="faqitems"><mm:field name="faqitems.number"/></mm:param>
                    <mm:param name="node"><mm:field name="faqitems.number"/></mm:param>
                 </mm:treefile>"><mm:field name="faqitems.question"/></a></td>
    </tr>
  </mm:list>

<% } %>


</mm:cloud>
</mm:content>
