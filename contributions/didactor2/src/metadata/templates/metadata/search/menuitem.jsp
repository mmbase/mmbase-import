<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<mm:import externid="search_component"/>
<mm:compare referid="search_component" value="metadata">
    <option value="metadata" selected="selected"><di:translate id="metadata">Opleiding</di:translate></option>
</mm:compare>
<mm:compare referid="search_component" value="metadata" inverse="true">
    <option value="metadata"><di:translate id="metadata">Opleiding</di:translate></option>
</mm:compare>

