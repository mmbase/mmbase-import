<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<mm:import externid="search_component"/>
<mm:compare referid="search_component" value="core">
    <option value="core" selected="selected"><di:translate key="core.news" /></option>
</mm:compare>
<mm:compare referid="search_component" value="core" inverse="true">
    <option value="core"><di:translate key="core.news" /></option>
</mm:compare>

