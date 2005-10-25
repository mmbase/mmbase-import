<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<mm:import externid="search_component"/>
<mm:compare referid="search_component" value="agenda">
    <option value="agenda" selected="selected"><di:translate key="agenda.calendar" /></option>
</mm:compare>
<mm:compare referid="search_component" value="agenda" inverse="true">
    <option value="agenda"><di:translate key="agenda.calendar" /></option>
</mm:compare>
