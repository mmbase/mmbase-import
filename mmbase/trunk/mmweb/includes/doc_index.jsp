<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/html" escaper="inline">
<mm:cloud>
<%@include file="/includes/getids.jsp" %>
<%@include file="/includes/header.jsp" %>
<mm:import externid="docnr"/>
<mm:import externid="project"/>
<td>

<mm:notpresent referid="docnr">
  <mm:node number="$page">
   <mm:relatednodes type="articles">
     <h3><mm:field name="title"/></h3>
     <mm:field name="body" escape="p"/>
   </mm:relatednodes>
   <mm:relatednodes type="documentation" orderby="title" directions="down"  id="docnr">
     <mm:first><ul></mm:first>
     <li><a href="<mm:url referids="portal,page,docnr" />"><mm:field name="title"/></a></li>
     <mm:last></ul></mm:last>
   </mm:relatednodes>
  </mm:node>
</mm:notpresent>

<mm:present referid="docnr">
   <mm:import externid="backtemplate" />
   <mm:isempty referid="backtemplate"><mm:remove referid="backtemplate" /></mm:isempty>
   <mm:node referid="docnr">
     <h4><a href="<mm:url referids="portal,page,backtemplate?@template,project?" />">Back to overview</a></h4>
     <h3><mm:field name="title"/></h3>
     <mm:field name="body" escape="p"/>
   </mm:node>
</mm:present>

</td>
<%@include file="/includes/footer.jsp" %>
</mm:cloud>
</mm:content>