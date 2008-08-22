<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

<mm:import externid="search_component"/>

<mm:compare referid="search_component" value="address">
    <option value="address" selected="selected"><di:translate key="address.address" /></option>
</mm:compare>

<mm:compare referid="search_component" value="address" inverse="true">
    <option value="address"><di:translate key="address.address" /></option>
</mm:compare>

</mm:cloud>
</mm:content>
