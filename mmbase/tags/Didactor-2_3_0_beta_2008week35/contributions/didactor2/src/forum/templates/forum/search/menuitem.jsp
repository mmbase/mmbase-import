<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

<mm:import externid="search_component"/>

<mm:compare referid="search_component" value="forum">
    <option value="forum" selected="selected"><di:translate key="forum.forum" /></option>
</mm:compare>
<mm:compare referid="search_component" value="forum" inverse="true">
    <option value="forum"><di:translate key="forum.forum" /></option>
</mm:compare>

</mm:cloud>
</mm:content>
